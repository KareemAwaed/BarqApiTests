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
    @Story("Fetch User Profile")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user profile can be fetched successfully")
    public void testFetchUserProfileSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    // 🚫 Fetch Profile with Invalid Token
    @Test
    @Story("Fetch User Profile - Invalid Token")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that fetching profile fails with an invalid token")
    public void testFetchUserProfileInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    // ✅ Accept Terms and Conditions
    @Test
    @Story("Accept Terms and Conditions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that users can accept terms and conditions")
    public void testAcceptTermsSuccess() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"accepted\": true, \"version\": \"1.1\"}")
                .post("/v1/profile/terms")
                .then()
                .statusCode(200)
                .body("message", equalTo("Terms accepted successfully"));
    }

    // 🚫 Accept Terms without Authorization
    @Test
    @Story("Accept Terms without Authorization")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that accepting terms fails without an authorization token")
    public void testAcceptTermsUnauthorized() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"accepted\": true, \"version\": \"1.1\"}")
                .post("/v1/profile/terms")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }

    // 🟠 Invalid Date of Birth Format
    @Test
    @Story("Update Profile - Invalid DOB Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that updating profile fails with invalid DOB format")
    public void testUpdateProfileInvalidDob() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"dob\": \"32-13-2023\"}")
                .post("/v1/profile/update")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_date_format"))
                .body("message", equalTo("Date of birth must be in YYYY-MM-DD format"));
    }

    // 🚫 Fetch Profile without API Key
    @Test
    @Story("Fetch User Profile - Missing API Key")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that fetching profile fails without API key")
    public void testFetchUserProfileMissingApiKey() {
        ApiHelper.createRequestWithoutApiKey()
                .get("/v1/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }

    // 🟠 Verify Response Structure
    @Test
    @Story("Verify Profile Response Structure")
    @Severity(SeverityLevel.MINOR)
    @Description("Ensure the profile response structure matches the expected format")
    public void testProfileResponseStructure() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile")
                .then()
                .statusCode(200)
                .body("code", notNullValue())
                .body("data", notNullValue())
                .body("message", notNullValue());
    }
}
