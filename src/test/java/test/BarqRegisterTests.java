package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Registration API")
@Feature("User Registration Management")
public class BarqRegisterTests extends BaseTest {

    // ✅ Old User Registration
    @Test
    @Story("Old User Sync")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an old user can be synced successfully")
    public void testOldUserSyncSuccess() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\", \"terms_and_conditions\": true, \"terms_and_conditions_version\": \"1.1\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(200)
                .body("data.user_id", notNullValue());
    }

    @Test
    @Story("Old User Already Synced")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when an old user is already synced")
    public void testOldUserAlreadySynced() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312802\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_already_synced"))
                .body("message", equalTo("User already synced with this partner"));
    }

    @Test
    @Story("Old User Registration Validation Error")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify validation error for empty registration body")
    public void testOldUserValidationError() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("id_number is required"));
    }

    @Test
    @Story("Old User Not Found")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when an old user is not found")
    public void testOldUserNotFound() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"nin\": \"2054312803\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_not_found"))
                .body("message", equalTo("User not found"));
    }
}
