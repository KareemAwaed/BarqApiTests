package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Authentication")
public class BarqAuthTests extends BaseTest {

    private static final String REGISTERED_NIN = "2054312802";
    private static final String REGISTERED_MOBILE = "+966538772716";
    private static final String REGISTERED_PASSWORD = "1111";
    private static final String REGISTERED_DOB = "1404-11-29";

    // ✅ Check Existing User
    @Test
    @Story("Check existing user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an existing user can be checked successfully")
    public void testExistingUser() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"" + REGISTERED_NIN + "\", \"mobile\": \"" + REGISTERED_MOBILE + "\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(200)
                .body("code", equalTo("existing_account"))
                .body("data.exist", equalTo(true))
                .body("message", equalTo("User already exists."));
    }

    // ✅ Login with valid credentials
    @Test
    @Story("Login with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can log in with valid NIN, mobile, password, and DOB")
    public void testLoginWithValidCredentials() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"" + REGISTERED_NIN + "\", \"mobile\": \"" + REGISTERED_MOBILE + "\", \"password\": \"" + REGISTERED_PASSWORD + "\", \"dob\": \"" + REGISTERED_DOB + "\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(200)
                .body("code", equalTo("login_success"))
                .body("message", equalTo("Login successful"))
                .body("data.access_token", notNullValue());
    }

    // 🚫 User Not Found
    @Test
    @Story("Login with non-existent user")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when trying to log in with a non-existent user")
    public void testLoginWithNonExistentUser() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"9999999999\", \"mobile\": \"+966512345678\", \"password\": \"wrongpass\", \"dob\": \"1400-01-01\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"))
                .body("message", equalTo("User not found"));
    }

    // 🚫 Invalid Password
    @Test
    @Story("Login with invalid password")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that login fails with invalid password")
    public void testLoginWithInvalidPassword() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"" + REGISTERED_NIN + "\", \"mobile\": \"" + REGISTERED_MOBILE + "\", \"password\": \"wrongpass\", \"dob\": \"" + REGISTERED_DOB + "\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_password"))
                .body("message", equalTo("Invalid password"));
    }

    // 🚫 Missing API Key
    @Test
    @Story("Handle missing API key")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when API key is missing")
    public void testMissingApiKey() {
        ApiHelper.createRequestWithoutApiKey()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"" + REGISTERED_NIN + "\", \"mobile\": \"" + REGISTERED_MOBILE + "\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }

    // 🚫 Missing NIN and Mobile
    @Test
    @Story("Check User - Missing Fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify response when NIN and mobile are missing")
    public void testCheckUserMissingFields() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("nin and mobile are required"));
    }
}

/*Here’s what’s covered:

✅ Check Existing User
✅ Login with valid credentials
🚫 Login with non-existent user
🚫 Invalid password
🚫 Missing API key
🚫 Missing NIN and Mobile*/