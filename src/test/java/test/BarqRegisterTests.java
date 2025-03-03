package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Registration")
public class BarqRegisterTests extends BaseTest {

    @Test
    @Story("Old User Sync Success")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an old user can be synced successfully")
    public void testOldUserSyncSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"terms_and_conditions\": true, \"terms_and_conditions_version\": \"1.1\"}")
                .when()
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    @Test
    @Story("Handle already synced user")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when trying to sync an already synced user")
    public void testOldUserAlreadySynced() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\"}")
                .when()
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_already_synced"))
                .body("message", equalTo("user already synced with this partner"));
    }

    @Test
    @Story("Handle validation errors")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify response when request body is empty")
    public void testOldUserValidationError() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("id_number is required"));
    }

    @Test
    @Story("Handle user not found")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when user is not found")
    public void testOldUserNotFound() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312803\"}")
                .when()
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"))
                .body("message", equalTo("user not found"));
    }

    @Test
    @Story("Handle missing API key")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify response when API key is missing")
    public void testMissingApiKey() {
        given()
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\"}")
                .when()
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }

    @Test
    @Story("Handle invalid signature")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify response when signature is invalid")
    public void testInvalidSignature() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "invalid_signature")
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\"}")
                .when()
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }
}
