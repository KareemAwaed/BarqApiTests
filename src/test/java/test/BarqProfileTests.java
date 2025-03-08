package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Profile API")
@Feature("User Profile Management")
public class BarqProfileTests extends BaseTest {

    private static String token;
    private static String signature;
    private static String validNin;
    private static String validMobile;

    @BeforeAll
    public static void setup() {
        validNin = ApiHelper.getTestData().getValidUser().getNin();
        validMobile = ApiHelper.getTestData().getValidUser().getMobile();
        token = ApiHelper.loginAndGetToken(validNin, validMobile);
        signature = ApiHelper.generateSignature(signature);

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Failed to fetch valid token for the test setup!");
        }
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    public void testFetchUserProfileSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue())
                .body("data.kyc_status", anyOf(equalTo("REQUIRED"), equalTo("PENDING"), equalTo("VERIFIED"), equalTo("REJECTED")))
                .body("data.terms_and_conditions_status", anyOf(equalTo("ACCEPTED"), equalTo("PENDING"), equalTo("REJECTED")))
                .body("data.terms_and_conditions_version", notNullValue())
                .body("data.user_type", anyOf(equalTo("RETAIL"), equalTo("CORPORATE")));
    }

    @Test
    @Tag("regression")
    public void testFetchUserProfileNotFound() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/user/profile?user_id=non_existent")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"));
    }

    @Test
    @Tag("regression")
    public void testFetchUserProfileExpiredToken() {
        ApiHelper.createRequestWithExpiredToken()
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("token_expired"))
                .body("message", equalTo("Access token has expired"));
    }

    @Test
    @Tag("regression")
    public void testUpdateProfileInvalidDob() {
        String requestBody = "{\"dob\": \"32-13-2023\"}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/user/update")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_date_format"))
                .body("message", equalTo("Date of birth must be in YYYY-MM-DD format"));
    }

    @Test
    @Tag("regression")
    public void testUpdateProfileMissingFields() {
        String requestBody = "{}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/user/update")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("Required fields are missing"));
    }

    @Test
    @Tag("regression")
    public void testUpdateProfileInvalidEmailFormat() {
        String requestBody = "{\"email\": \"invalid-email\"}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/user/update")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_email_format"))
                .body("message", equalTo("Invalid email format"));
    }

    @Test
    @Tag("regression")
    public void testUpdateProfileExcessivePayload() {
        String requestBody = ApiHelper.generateLargePayload();

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/user/update")
                .then()
                .statusCode(413)
                .body("code", equalTo("payload_too_large"))
                .body("message", equalTo("Payload size exceeds the allowed limit"));
    }

    @Test
    @Tag("regression")
    public void testFetchUserProfileInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    @Test
    @Tag("regression")
    public void testFetchUserProfileMissingToken() {
        ApiHelper.createRequestWithoutHeaders()
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
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
    public void testFetchProfileWithUnsupportedMethod() {
        ApiHelper.createRequestWithToken(token)
                .post("/v1/profile/user/profile")
                .then()
                .statusCode(405)
                .body("code", equalTo("method_not_allowed"));
    }

    @Test
    @Tag("regression")
    public void testUpdateProfileWithUnauthorizedAccess() {
        ApiHelper.createRequestWithoutToken()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"test@example.com\" }")
                .post("/v1/profile/user/update")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"));
    }
    @Test
    @Tag("regression")
    public void testFetchProfileWithUnsupportedMediaType() {
        ApiHelper.createRequestWithToken(token)
                .header("Content-Type", "application/xml")
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(415)
                .body("code", equalTo("unsupported_media_type"));
    }
}
