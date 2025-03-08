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

    // 🚫 Login with expired token
    @Test
    @Tag("regression")
    @Story("Login with expired token")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that login fails with an expired token")
    public void testLoginWithExpiredToken() {
        ApiHelper.createRequestWithExpiredToken()
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("code", equalTo("token_expired"))
                .body("message", equalTo("Token has expired"));
    }

    // 🚫 Login with partially correct credentials
    @Test
    @Tag("regression")
    @Story("Login with partially correct credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that login fails with valid NIN but incorrect DOB")
    public void testLoginWithPartiallyCorrectCredentials() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"" + REGISTERED_NIN + "\", " +
                        "\"mobile\": \"" + REGISTERED_MOBILE + "\", " +
                        "\"password\": \"1111\", " +
                        "\"dob\": \"9999-12-31\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_dob"))
                .body("message", equalTo("Invalid date of birth"));
    }

    // 🚫 Exceedingly long NIN or mobile number
    @Test
    @Tag("regression")
    @Story("Login with excessively long NIN or mobile number")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that login fails with excessively long NIN or mobile number")
    public void testLoginWithExcessivelyLongNinOrMobile() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"12345678901234567890\", " +
                        "\"mobile\": \"+966512345678901234567890\", " +
                        "\"password\": \"1111\", " +
                        "\"dob\": \"1404-11-29\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data_format"))
                .body("message", equalTo("NIN and mobile number must follow the correct length"));
    }

    @Test
    @Tag("regression")
    public void testLoginWithoutRequiredFields() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo("missing_required_fields"));
    }

    @Test
    @Tag("regression")
    public void testLoginWithIncorrectPassword() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"" + REGISTERED_NIN + "\", \"password\": \"wrongPassword\"}")
                .post("/v1/auth/login")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_credentials"));
    }


}
