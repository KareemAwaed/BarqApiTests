package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Authentication & Token Management")
public class BarqAuthTests extends BaseTest {

    // ✅ Login Tests
    @Test
    @Story("User Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful login")
    public void testLoginSuccess() {
        ApiHelper.createRequest()
                .body("{\"username\": \"testuser\", \"password\": \"password123\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue());
    }

    @Test
    @Story("User Login - Invalid Credentials")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify login fails with invalid credentials")
    public void testLoginInvalidCredentials() {
        ApiHelper.createRequest()
                .body("{\"username\": \"invalid\", \"password\": \"wrongpass\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Invalid username or password"));
    }

    @Test
    @Story("User Login - Missing API Key")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login fails when API key is missing")
    public void testLoginMissingApiKey() {
        ApiHelper.createRequestWithoutApiKey()
                .body("{\"username\": \"testuser\", \"password\": \"password123\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("API key is missing"));
    }

    @Test
    @Story("User Login - Expired Token")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login fails with expired token")
    public void testLoginExpiredToken() {
        ApiHelper.createRequestWithExpiredToken()
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Token has expired"));
    }

    // ✅ Token Management Tests
    @Test
    @Story("Token Refresh")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify token can be refreshed")
    public void testRefreshTokenSuccess() {
        ApiHelper.createRequest()
                .body("{\"refresh_token\": \"valid_refresh_token\"}")
                .post("/v1/auth/refresh-token")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue());
    }

    @Test
    @Story("Token Refresh - Invalid Session")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify token refresh fails with invalid session ID")
    public void testRefreshTokenInvalidSession() {
        ApiHelper.createRequest()
                .body("{\"refresh_token\": \"invalid_token\"}")
                .post("/v1/auth/refresh-token")
                .then()
                .statusCode(401)
                .body("message", equalTo("Invalid session ID"));
    }

    @Test
    @Story("Logout")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify user logout invalidates the token")
    public void testLogoutSuccess() {
        ApiHelper.createRequestWithToken()
                .post("/v1/auth/logout")
                .then()
                .statusCode(200)
                .body("message", equalTo("Logged out successfully"));
    }
}
