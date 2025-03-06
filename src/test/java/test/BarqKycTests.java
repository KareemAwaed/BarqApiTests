package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("KYC API")
@Feature("User KYC Management")
public class BarqKycTests extends BaseTest {

    private static String token;
    private static String validNin;
    private static String validMobile;

    @BeforeAll
    public static void setup() {
        validNin = ApiHelper.getTestData().getValidUser().getNin();
        validMobile = ApiHelper.getTestData().getValidUser().getMobile();
        token = ApiHelper.loginAndGetToken(validNin, validMobile);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Fetch KYC Questions")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC questions can be fetched successfully")
    public void testFetchKYCQuestionsSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("code", equalTo("retail_kyc_questions_listed"))
                .body("message", equalTo("Get retail KYC questions list successfully"))
                .body("data", notNullValue());
    }

    @Test
    @Tag("regression")
    @Story("Fetch KYC Questions - Invalid Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify fetching KYC questions fails with an invalid token")
    public void testFetchKYCQuestionsInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Submit KYC Answers")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC answers can be submitted successfully")
    public void testSubmitKYCAnswersSuccess() {
        String requestBody = ApiHelper.loadTestDataFile("kyc-valid-answers.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(200)
                .body("code", equalTo("retail_kyc_answers_sent"))
                .body("message", equalTo("Retail KYC answers sent successfully"));
    }

    @Test
    @Tag("regression")
    @Story("Submit KYC Answers - Invalid Answer")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting an invalid answer returns an error")
    public void testSubmitInvalidKYCAnswer() {
        String requestBody = ApiHelper.loadTestDataFile("kyc-invalid-answers.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_answer"))
                .body("message", equalTo("Invalid answer provided for question"));
    }

    @Test
    @Tag("regression")
    @Story("Submit Empty KYC Answers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting empty KYC answers returns an error")
    public void testSubmitEmptyKYCAnswers() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"answers\": []}")
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("Answers array cannot be empty"));
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Handle KYC Status Webhook")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the system handles the KYC status webhook correctly")
    public void testKYCStatusWebhook() {
        String requestBody = ApiHelper.loadTestDataFile("kyc-status-webhook.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/partner-sync_kyc_status-endpoint")
                .then()
                .statusCode(200);
    }

    @Test
    @Tag("regression")
    @Story("Handle Invalid KYC Status in Webhook")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that handling invalid KYC status in webhook returns an error")
    public void testInvalidKYCStatusWebhook() {
        String requestBody = ApiHelper.loadTestDataFile("kyc-invalid-status-webhook.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/partner-sync_kyc_status-endpoint")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_status"))
                .body("message", equalTo("Invalid KYC status provided"));
    }
}

