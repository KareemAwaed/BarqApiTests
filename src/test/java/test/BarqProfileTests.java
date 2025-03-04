package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Profile API")
@Feature("User Profile Management")
public class BarqProfileTests extends BaseTest {

    // ✅ Fetch User Profile
    @Test
    @Story("Fetch User Profile")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the user's profile can be fetched successfully")
    public void testFetchUserProfileSuccess() {
        ApiHelper.createRequestWithToken()
                .get("/v1/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue())
                .body("data.full_name", notNullValue());
    }

    @Test
    @Story("Fetch User Profile - Invalid Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that profile fetch fails with an invalid token")
    public void testFetchUserProfileInvalidToken() {
        ApiHelper.createRequestWithExpiredToken()
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
        ApiHelper.createRequestWithToken()
                .contentType(ContentType.JSON)
                .body("{\"accepted\": true, \"version\": \"1.1\"}")
                .post("/v1/profile/terms")
                .then()
                .statusCode(200)
                .body("message", equalTo("Terms accepted successfully"));
    }

    @Test
    @Story("Accept Terms - Unauthorized")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that accepting terms fails without authorization")
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
}
