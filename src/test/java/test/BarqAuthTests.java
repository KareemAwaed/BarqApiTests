package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Authentication")
public class BarqAuthTests extends BaseTest {  // Extend BaseTest

    @Test
    @Story("Check existing user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an existing user can be checked successfully")
    public void testExistingUser() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\n  \"nin\": \"2054312802\",\n  \"mobile\": \"+966538772716\"\n}")
                .when()
                .post("/v1/auth/check-user")
                .then()
                .statusCode(200)
                .body("code", equalTo("existing_account"))
                .body("data.exist", equalTo(true))
                .body("message", equalTo("User already exists."));
    }

    @Test
    @Story("Handle missing API key")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when API key is missing")
    public void testMissingApiKey() {
        given()
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\n  \"nin\": \"2054312802\",\n  \"mobile\": \"+966538772716\"\n}")
                .when()
                .post("/v1/auth/check-user")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }

    @Test
    @Story("Handle invalid signature")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when signature is invalid")
    public void testInvalidSignature() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "invalid_signature")
                .contentType(ContentType.JSON)
                .body("{\n  \"nin\": \"2054312802\",\n  \"mobile\": \"+966538772716\"\n}")
                .when()
                .post("/v1/auth/check-user")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }
}
