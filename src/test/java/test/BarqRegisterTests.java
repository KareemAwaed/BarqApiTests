package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Registration API")
@Feature("User Registration Management")
public class BarqRegisterTests extends BaseTest {

    private static String token;

    @BeforeAll
    public static void setup() {
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Register New User Success
    @Test
    public void testRegisterNewUserSuccess() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"national_id\": \"1234567890\", " +
                        "\"georgian_birth_date\": \"1990-01-01\", " +
                        "\"mobile\": \"+966512345678\", " +
                        "\"id_expiration_date\": \"2030-01-01\", " +
                        "\"id_issue_date\": \"2010-01-01\", " +
                        "\"id_issue_place_code\": \"Riyadh\", " +
                        "\"first_name_ar\": \"أحمد\", " +
                        "\"first_name_en\": \"Ahmed\", " +
                        "\"father_name_ar\": \"محمد\", " +
                        "\"father_name_en\": \"Mohammed\", " +
                        "\"terms_and_conditions\": true, " +
                        "\"terms_and_conditions_version\": \"v1\"}")
                .post("/v1/auth/register")
                .then()
                .statusCode(200)
                .body("code", equalTo("partner-new-user-register"))
                .body("message", equalTo("Successfully registered"))
                .body("data.user_id", notNullValue());
    }

    // 🚫 Register Old User - Already Synced
    @Test
    public void testRegisterOldUserAlreadySynced() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"nin\": \"1234567890\", " +
                        "\"terms_and_conditions\": true, " +
                        "\"terms_and_conditions_version\": \"v1\"}")
                .post("/v1/auth/old-user/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("user_already_synced"))
                .body("message", equalTo("user already synced with this partner"));
    }

    // 🚫 Register New User - Invalid NIN
    @Test
    public void testRegisterNewUserInvalidNIN() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"national_id\": \"invalidNIN\", " +
                        "\"georgian_birth_date\": \"1990-01-01\", " +
                        "\"mobile\": \"+966512345678\", " +
                        "\"terms_and_conditions\": true, " +
                        "\"terms_and_conditions_version\": \"v1\"}")
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_nin"))
                .body("message", equalTo("Invalid National ID"));
    }

    // 🚫 Register New User - Missing Terms
    @Test
    public void testRegisterNewUserMissingTerms() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"national_id\": \"1234567890\", " +
                        "\"mobile\": \"+966512345678\", " +
                        "\"terms_and_conditions\": false, " +
                        "\"terms_and_conditions_version\": \"v1\"}")
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("terms_not_accepted"))
                .body("message", equalTo("Terms and conditions must be accepted"));
    }

    // 🚫 Register New User - Missing Fields
    @Test
    public void testRegisterNewUserMissingFields() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("Required fields are missing"));
    }
}

