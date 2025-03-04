package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("KYC API")
@Feature("Know Your Customer (KYC) Management")
public class BarqKycTests extends BaseTest {

    // ✅ Get KYC Questions
    @Test
    @Story("Fetch KYC Questions")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that KYC questions can be retrieved successfully")
    public void testGetKycQuestionsSuccess() {
        ApiHelper.createRequestWithToken()
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("data.questions", notNullValue());
    }

    @Test
    @Story("Fetch KYC Questions - Invalid Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that fetching KYC questions fails with an invalid token")
    public void testGetKycQuestionsInvalidToken() {
        ApiHelper.createRequestWithExpiredToken()
                .get("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    // ✅ Submit KYC Answers
    @Test
    @Story("Submit KYC Answers")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that users can submit KYC answers successfully")
    public void testSubmitKycAnswersSuccess() {
        ApiHelper.createRequestWithToken()
                .contentType(ContentType.JSON)
                .body("{\"answers\": [{\"question_id\": 1, \"answer\": \"Yes\"}, {\"question_id\": 2, \"answer\": \"No\"}]}")
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(200)
                .body("message", equalTo("KYC answers submitted successfully"));
    }

    @Test
    @Story("Submit KYC Answers - Unauthorized")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that submitting KYC answers fails without authorization")
    public void testSubmitKycAnswersUnauthorized() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"answers\": [{\"question_id\": 1, \"answer\": \"Yes\"}, {\"question_id\": 2, \"answer\": \"No\"}]}")
                .post("/v1/profile/retail-kyc")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }
}
