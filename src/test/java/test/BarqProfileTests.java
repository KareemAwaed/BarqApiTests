package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Profile API")
@Feature("User Profile Management")
public class BarqProfileTests extends BaseTest {

    @Test
    @Story("Fetch user profile")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that user profile data can be fetched successfully")
    public void testFetchUserProfileSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .when()
                .get("/v1/profile")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    @Test
    @Story("Handle invalid access token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when an invalid access token is used to fetch the profile")
    public void testFetchUserProfileInvalidToken() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer invalid_access_token")
                .when()
                .get("/v1/profile")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    @Test
    @Story("Accept terms and conditions")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that terms and conditions can be accepted successfully")
    public void testAcceptTermsSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .contentType(ContentType.JSON)
                .body("{\"accepted\": true, \"version\": \"1.1\"}")
                .when()
                .post("/v1/profile/terms")
                .then()
                .statusCode(200)
                .body("message", equalTo("Terms accepted successfully"));
    }

    @Test
    @Story("Accept terms without authorization")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when accepting terms without an authorization token")
    public void testAcceptTermsUnauthorized() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"accepted\": true, \"version\": \"1.1\"}")
                .when()
                .post("/v1/profile/terms")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }
}
