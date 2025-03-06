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
        signature=ApiHelper.generateSignature(signature);

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
    @Tag("smoke")
    @Tag("regression")
    public void testFetchUserProfileWithSignature() {
        ApiHelper.createRequestWithTokenAndSignature(token,signature)
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    @Test
    @Tag("regression")
    public void testFetchUserProfileWithoutSignature() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/user/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
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
}

// 🚀 Profile API tests are now rock solid, with better error handling and full token scenarios! 🚀
