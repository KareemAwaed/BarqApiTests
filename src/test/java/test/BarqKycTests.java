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
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Fetch KYC Questions Successfully
    @Test
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

    // ✅ Submit KYC Answers Successfully
    @Test
    @Story("Submit KYC Answers")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC answers can be submitted successfully")
    public void testSubmitKYCAnswersSuccess() {
        String requestBody = "{" +
                "    \"answers\": [" +
                "        {\"question_slug\": \"social_status\", \"answer\": \"married\", \"reset\": false}," +
                "        {\"question_slug\": \"num_family_member\", \"answer\": \"2\", \"reset\": false}," +
                "        {\"question_slug\": \"educational_lvl\", \"answer\": \"university\", \"reset\": false}" +
                "    ]" +
                "}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(200)
                .body("code", equalTo("retail_kyc_answers_sent"))
                .body("message", equalTo("Retail KYC answers sent successfully"));
    }

    // 🚫 Submit KYC Answers - Invalid Answer
    @Test
    @Story("Submit KYC Answers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting an invalid answer returns an error")
    public void testSubmitInvalidKYCAnswer() {
        String requestBody = "{" +
                "    \"answers\": [" +
                "        {\"question_slug\": \"social_status\", \"answer\": \"invalid_option\", \"reset\": false}" +
                "    ]" +
                "}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/profile/retail-kyc/send-answers")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_answer"))
                .body("message", equalTo("Invalid answer provided for question"));
    }

    // ✅ Handle Webhook for KYC Status
    @Test
    @Story("Handle KYC Status Webhook")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the system handles the KYC status webhook correctly")
    public void testKYCStatusWebhook() {
        String requestBody = "{" +
                "    \"user_id\": \"173120407660036096\"," +
                "    \"retail_kyc_status\": \"APPROVED\"" +
                "}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/partner-sync_kyc_status-endpoint")
                .then()
                .statusCode(200);
    }
}

/*✅ Fetch KYC Questions Successfully — /v1/profile/retail-kyc
🚫 Fetch KYC Questions with Invalid Token — Invalid token handling.
✅ Submit KYC Answers Successfully — /v1/profile/retail-kyc/send-answers
🚫 Submit KYC Answers with Invalid Answer — Invalid answer validation.
🚫 Submit KYC Answers without Authorization — Missing token handling.
🚫 Submit Empty KYC Answers — Missing answers validation.
✅ Handle KYC Status Webhook — /partner-sync_kyc_status-endpoint for status updates.
🚫 Handle Invalid KYC Status in Webhook — Invalid status handling.*/