package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Authentication")
public class BarqAuthTests extends BaseTest {

    // ✅ Check Existing User
    @Test
    @Story("Check existing user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an existing user can be checked successfully")
    public void testExistingUser() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"mobile\": \"+966538772716\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(200)
                .body("code", equalTo("existing_account"))
                .body("data.exist", equalTo(true))
                .body("message", equalTo("User already exists."));
    }

    // 🚫 Missing API Key
    @Test
    @Story("Handle missing API key")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when API key is missing")
    public void testMissingApiKey() {
        ApiHelper.createRequestWithoutApiKey()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"mobile\": \"+966538772716\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }

    // 🚫 Invalid Signature
    @Test
    @Story("Handle invalid signature")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when signature is invalid")
    public void testInvalidSignature() {
        ApiHelper.createRequestWithInvalidSignature()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"mobile\": \"+966538772716\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(401)
                .body("code", equalTo("ims.authentication_failed"))
                .body("message", equalTo("Authentication failed"));
    }

    // 🚫 Missing NIN and Mobile
    @Test
    @Story("Check User - Missing Fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify response when NIN and mobile are missing")
    public void testCheckUserMissingFields() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("nin and mobile are required"));
    }

    // 🟠 Invalid NIN Format
    @Test
    @Story("Check User - Invalid NIN Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when NIN is invalid format")
    public void testCheckUserInvalidNinFormat() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"abc123\", \"mobile\": \"+966538772716\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_nin_format"))
                .body("message", equalTo("NIN must be numeric and 10 digits"));
    }

    // 🟠 Invalid Mobile Format
    @Test
    @Story("Check User - Invalid Mobile Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when mobile number format is invalid")
    public void testCheckUserInvalidMobileFormat() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"mobile\": \"123456\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_mobile_format"))
                .body("message", equalTo("Mobile number must start with +9665 and be 13 digits long"));
    }

    // 🧩 Verify Response Structure
    @Test
    @Story("Verify response structure")
    @Severity(SeverityLevel.MINOR)
    @Description("Ensure the response structure matches the expected format")
    public void testResponseStructure() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"mobile\": \"+966538772716\"}")
                .post("/v1/auth/check-user")
                .then()
                .statusCode(200)
                .body("code", notNullValue())
                .body("data", notNullValue())
                .body("message", notNullValue());
    }
}
