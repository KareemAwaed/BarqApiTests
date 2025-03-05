package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("KYC API")
@Feature("User KYC Management")
public class BarqKycTests extends BaseTest {

    private static String token;

    @BeforeAll
    public static void setup() {
        // Generate a valid token before running the tests
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Fetch KYC Questions Successfully
    @Test
    @Story("Fetch KYC Questions")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC questions can be fetched successfully")
    public void testGetKycQuestionsSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("data.questions", notNullValue());
    }

    // 🚫 Fetch KYC Questions with Invalid Token
    @Test
    @Story("Fetch KYC Questions - Invalid Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that fetching KYC questions fails with an invalid token")
    public void testGetKycQuestionsInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    // ✅ Submit KYC Answers Successfully
    @Test
    @Story("Submit KYC Answers")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC answers can be submitted successfully")
    public void testSubmitKycAnswersSuccess() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"answers\": [{\"question_id\": 1, \"answer\": \"Yes\"}, {\"question_id\": 2, \"answer\": \"No\"}]}" )
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("message", equalTo("KYC answers submitted successfully"));
    }

    // 🚫 Submit KYC Answers without Authorization
    @Test
    @Story("Submit KYC Answers without Authorization")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that submitting KYC answers fails without authorization")
    public void testSubmitKycAnswersUnauthorized() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"answers\": [{\"question_id\": 1, \"answer\": \"Yes\"}, {\"question_id\": 2, \"answer\": \"No\"}]}" )
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }

    // 🟠 Submit KYC with Missing Answers
    @Test
    @Story("Submit KYC - Missing Answers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting KYC answers fails with missing answers")
    public void testSubmitKycMissingAnswers() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"answers\": []}")
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(400)
                .body("code", equalTo("missing_answers"))
                .body("message", equalTo("At least one answer is required"));
    }
}
