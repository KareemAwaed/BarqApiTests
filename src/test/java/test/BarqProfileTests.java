package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Profile API")
@Feature("User Profile Management")
public class BarqProfileTests extends BaseTest {

    private static String token;

    @BeforeAll
    public static void setup() {
        // Generate a valid token before running the tests
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Fetch User Profile Successfully
    @Test
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

    // 🚫 User Not Found
    @Test
    public void testFetchUserProfileNotFound() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/user/profile?user_id=non_existent")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"));
    }

    // ✅ Fetch Profile with Signature
    @Test
    public void testFetchUserProfileWithSignature() {
        ApiHelper.createRequestWithTokenAndSignature(token)
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    // 🚫 Fetch Profile without Signature (optional)
    @Test
    public void testFetchUserProfileWithoutSignature() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    // 🚫 Fetch Profile with Invalid Token
    @Test
    public void testFetchUserProfileInvalidToken() {
        ApiHelper.createRequestWithInvalidToken(token)
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }
}
