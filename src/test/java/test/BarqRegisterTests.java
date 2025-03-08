package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Registration API")
@Feature("User Registration Management")
public class BarqRegisterTests extends BaseTest {

    private static String token;
    private static ApiHelper.User validUser;
    private static ApiHelper.User invalidUser;

    @BeforeAll
    public static void setup() {
        validUser = ApiHelper.getTestData().getValidUser();
        invalidUser = ApiHelper.getTestData().getInvalidUser();
        token = ApiHelper.loginAndGetToken(validUser.getNin(), validUser.getMobile());
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    public void testRegisterNewUserSuccess() {
        String requestBody = ApiHelper.loadTestDataFile("register-valid-user.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/auth/register")
                .then()
                .statusCode(200)
                .body("code", equalTo("partner-new-user-register"))
                .body("message", equalTo("Successfully registered"))
                .body("data.user_id", notNullValue());
    }

    @Test
    @Tag("regression")
    public void testRegisterOldUserAlreadySynced() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"" + validUser.getNin() + "\", " +
                        "\"terms_and_conditions\": true, " +
                        "\"terms_and_conditions_version\": \"v1\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_already_synced"))
                .body("message", equalTo("user already synced with this partner"));
    }

    @Test
    @Tag("regression")
    public void testRegisterNewUserInvalidNIN() {
        String requestBody = ApiHelper.loadTestDataFile("register-invalid-nin.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_nin"))
                .body("message", equalTo("Invalid National ID"));
    }

    @Test
    @Tag("regression")
    public void testRegisterNewUserMissingTerms() {
        String requestBody = ApiHelper.loadTestDataFile("register-missing-terms.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("terms_not_accepted"))
                .body("message", equalTo("Terms and conditions must be accepted"));
    }

    @Test
    @Tag("regression")
    public void testRegisterNewUserMissingFields() {
        String requestBody = ApiHelper.loadTestDataFile("register-missing-fields.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("Required fields are missing"));
    }

    // New test cases based on recommendations

    @Test
    @Tag("regression")
    public void testRegisterWithExpiredToken() {
        ApiHelper.createRequestWithExpiredToken()
                .contentType(ContentType.JSON)
                .body(ApiHelper.loadTestDataFile("register-valid-user.json"))
                .post("/v1/auth/register")
                .then()
                .statusCode(401)
                .body("code", equalTo("token_expired"))
                .body("message", equalTo("Access token has expired"));
    }

    @Test
    @Tag("regression")
    public void testRegisterWithInvalidTermsVersion() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"" + validUser.getNin() + "\", " +
                        "\"terms_and_conditions\": true, " +
                        "\"terms_and_conditions_version\": \"invalid_version\"}")
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_terms_version"))
                .body("message", equalTo("Invalid terms and conditions version"));
    }

    @Test
    @Tag("regression")
    public void testRegisterWithExcessivePayload() {
        String requestBody = ApiHelper.generateLargePayload();

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/auth/register")
                .then()
                .statusCode(413)
                .body("code", equalTo("payload_too_large"))
                .body("message", equalTo("Request payload is too large"));
    }

    @Test
    @Tag("regression")
    public void testRegisterWithMobileAlreadyExists() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(ApiHelper.loadTestDataFile("register-duplicate-mobile.json"))
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("mobile_already_exists"));
    }
    @Test
    @Tag("regression")
    public void testRegisterWithInvalidIdExpirationDate() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(ApiHelper.loadTestDataFile("register-invalid-expiration-date.json"))
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_expiration_date"));
    }
    @Test
    @Tag("regression")
    public void testRegisterWithUnsupportedMethod() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/auth/register")
                .then()
                .statusCode(405)
                .body("code", equalTo("method_not_allowed"));
    }
}
