package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Registration API")
@Feature("Old User Registration")
public class BarqRegisterTests extends BaseTest {

    private static String token;

    @BeforeAll
    public static void setup() {
        // Generate a valid token before running the tests
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Successful Registration
    @Test
    @Story("Old User Sync Success")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify successful registration of an old user")
    public void testOldUserSyncSuccess() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"terms_and_conditions\": true, \"terms_and_conditions_version\": \"1.1\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    // ⚠️ User Already Synced
    @Test
    @Story("Old User Already Synced")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when user is already synced")
    public void testOldUserAlreadySynced() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_already_synced"))
                .body("message", equalTo("User already synced with this partner"));
    }

    // ❌ Validation Error (Empty Body)
    @Test
    @Story("Validation Error")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify registration fails with empty request body")
    public void testOldUserValidationError() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("id_number is required"));
    }

    // 🔍 User Not Found
    @Test
    @Story("User Not Found")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify registration fails for a non-existent user")
    public void testOldUserNotFound() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312803\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"))
                .body("message", equalTo("User not found"));
    }

    // 🚫 Missing Terms and Conditions
    @Test
    @Story("Missing Terms and Conditions")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify registration fails without accepting terms")
    public void testRegisterMissingTerms() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("terms_not_accepted"))
                .body("message", equalTo("Terms and conditions must be accepted"));
    }

    // 🟠 NIN with Minimum Digits
    @Test
    @Story("Short NIN")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify registration fails with short NIN")
    public void testRegisterShortNin() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"12345\", \"terms_and_conditions\": true, \"terms_and_conditions_version\": \"1.1\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_nin_format"))
                .body("message", equalTo("NIN must be exactly 10 digits"));
    }

    // 🛡️ Edge Case: Excessively Long NIN
    @Test
    @Story("Excessively Long NIN")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify registration fails with a NIN longer than 10 digits")
    public void testRegisterLongNin() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"12345678901234567890\", \"terms_and_conditions\": true, \"terms_and_conditions_version\": \"1.1\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_nin_format"))
                .body("message", equalTo("NIN must be exactly 10 digits"));
    }
}
