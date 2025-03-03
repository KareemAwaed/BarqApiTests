package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Profile API")
@Feature("KYC Management")
public class BarqKycTests extends BaseTest {

    @Test
    @Story("Retrieve KYC questions")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC questions can be retrieved successfully")
    public void testGetKycQuestionsSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .when()
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("data.questions", notNullValue());
    }

    @Test
    @Story("Handle invalid access token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when an invalid access token is used")
    public void testGetKycQuestionsInvalidToken() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer invalid_access_token")
                .when()
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    @Test
    @Story("Submit KYC answers")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC answers can be submitted successfully")
    public void testSubmitKycAnswersSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .contentType(ContentType.JSON)
                .body("{\"answers\": [{\"question_id\": 1, \"answer\": \"Yes\"}, {\"question_id\": 2, \"answer\": \"No\"}]}")
                .when()
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("message", equalTo("KYC answers submitted successfully"));
    }

    @Test
    @Story("Submit KYC answers without authorization")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when submitting KYC answers without an authorization token")
    public void testSubmitKycAnswersUnauthorized() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"answers\": [{\"question_id\": 1, \"answer\": \"Yes\"}, {\"question_id\": 2, \"answer\": \"No\"}]}")
                .when()
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }
}
