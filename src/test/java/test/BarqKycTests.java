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
    @Tag("regression")
    @Story("Submit Duplicate KYC Answers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting duplicate answers for the same question returns an error")
    public void testSubmitDuplicateKYCAnswers() {
        String requestBody = "{\"answers\": [\n  { \"question_id\": 1, \"answer\": \"Yes\" },\n  { \"question_id\": 1, \"answer\": \"No\" }\n]}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("duplicate_question_id"))
                .body("message", equalTo("Duplicate answers for the same question ID"));
    }

    @Test
    @Tag("regression")
    @Story("Submit Partial KYC Answers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting incomplete KYC answers returns an error")
    public void testSubmitPartialKYCAnswers() {
        String requestBody = "{\"answers\": [{ \"question_id\": 1, \"answer\": \"Yes\" }]}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("incomplete_answers"))
                .body("message", equalTo("Some required questions are missing"));
    }

    @Test
    @Tag("regression")
    @Story("Submit Invalid Question ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting an invalid question ID returns an error")
    public void testSubmitInvalidQuestionId() {
        String requestBody = "{\"answers\": [{ \"question_id\": 9999, \"answer\": \"Yes\" }]}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_question_id"))
                .body("message", equalTo("Invalid question ID provided"));
    }

    @Test
    @Tag("regression")
    @Story("Submit KYC Answers with Invalid Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting answers with an invalid format returns an error")
    public void testSubmitInvalidFormatKYCAnswers() {
        String requestBody = "{\"answers\": [{ \"question_id\": 1, \"answer\": 12345 }]}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_answer_format"))
                .body("message", equalTo("Answer must be a string"));
    }
    @Test
    @Tag("regression")
    @Story("Submit KYC Answers with Expired Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting KYC answers with an expired token returns an error")
    public void testSubmitKYCAnswersExpiredToken() {
        String requestBody = ApiHelper.loadTestDataFile("kyc-valid-answers.json");

        ApiHelper.createRequestWithExpiredToken()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(401)
                .body("code", equalTo("token_expired"))
                .body("message", equalTo("Access token has expired"));
    }

    @Test
    @Tag("regression")
    @Story("Submit KYC Answers with Large Payload")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting an excessively large payload returns an error")
    public void testSubmitKYCAnswersLargePayload() {
        String requestBody = ApiHelper.generateLargePayload();

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(413)
                .body("code", equalTo("payload_too_large"))
                .body("message", equalTo("Request payload is too large"));
    }
    @Test
    @Tag("regression")
    public void testSubmitKYCAnswersWithUnauthorizedAccess() {
        ApiHelper.createRequestWithoutToken()
                .contentType(ContentType.JSON)
                .body(ApiHelper.loadTestDataFile("kyc-valid-answers.json"))
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"));
    }

}
