package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;
import org.junit.jupiter.api.Tag;

import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Authentication")
public class BarqAuthTests extends BaseTest {

    private static final String REGISTERED_NIN = ApiHelper.getTestData().getValidUser().getNin();
    private static final String REGISTERED_MOBILE = ApiHelper.getTestData().getValidUser().getMobile();
    private static final String INVALID_NIN = ApiHelper.getTestData().getInvalidUser().getNin();
    private static final String INVALID_MOBILE = ApiHelper.getTestData().getInvalidUser().getMobile();

    // ✅ Check Existing User — Smoke & Regression
    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Check existing user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an existing user can be checked successfully")
    public void testExistingUser() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body(ApiHelper.createDynamicPayload(REGISTERED_NIN, REGISTERED_MOBILE))
                .post("/v1/auth/check-user")
                .then()
                .statusCode(200)
                .body("code", equalTo("existing_account"))
                .body("data.exist", equalTo(true))
                .body("message", equalTo("User already exists."));
    }

    // ✅ Login with valid credentials — Smoke & Regression
    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Login with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can log in with valid NIN, mobile, password, and DOB")
    public void testLoginWithValidCredentials() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"" + REGISTERED_NIN + "\", " +
                        "\"mobile\": \"" + REGISTERED_MOBILE + "\", " +
                        "\"password\": \"1111\", " +
                        "\"dob\": \"1404-11-29\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(200)
                .body("code", equalTo("login_success"))
                .body("message", equalTo("Login successful"))
                .body("data.access_token", notNullValue());
    }

    // 🚫 Login with non-existent user — Regression Only
    @Test
    @Tag("regression")
    @Story("Login with non-existent user")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when trying to log in with a non-existent user")
    public void testLoginWithNonExistentUser() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body(ApiHelper.createDynamicPayload(INVALID_NIN, INVALID_MOBILE))
                .post("/v1/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"))
                .body("message", equalTo("User not found"));
    }

    // 🚫 Invalid Password — Regression Only
    @Test
    @Tag("regression")
    @Story("Login with invalid password")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that login fails with invalid password")
    public void testLoginWithInvalidPassword() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"" + REGISTERED_NIN + "\", " +
                        "\"mobile\": \"" + REGISTERED_MOBILE + "\", " +
                        "\"password\": \"wrongpass\", " +
                        "\"dob\": \"1404-11-29\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_password"))
                .body("message", equalTo("Invalid password"));
    }
}

