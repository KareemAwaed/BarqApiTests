package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Terms & Conditions API")
@Feature("User Terms & Conditions Management")
public class BarqTermsAndConditionsTests extends BaseTest {

    private static String token;
    private static String latestTermsVersion;

    @BeforeAll
    public static void setup() {
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");

        // Fetch latest terms and conditions version
        latestTermsVersion = ApiHelper.createRequestWithToken(token)
                .post("/v1/profile/terms-and-conditions/latest")
                .then()
                .statusCode(200)
                .extract().path("data.version");
    }

    // ✅ Get Latest Terms & Conditions
    @Test
    public void testGetLatestTermsAndConditions() {
        ApiHelper.createRequestWithToken(token)
                .post("/v1/profile/terms-and-conditions/latest")
                .then()
                .statusCode(200)
                .body("data.version", notNullValue())
                .body("data.links.en", notNullValue())
                .body("data.links.ar", notNullValue());
    }

    // 🚫 Get Latest Terms without Token
    @Test
    public void testGetLatestTermsWithoutToken() {
        ApiHelper.createRequest()
                .post("/v1/profile/terms-and-conditions/latest")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"));
    }

    // ✅ Accept Terms & Conditions Success
    @Test
    public void testAcceptTermsAndConditionsSuccess() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"terms_and_conditions_version\": \"" + latestTermsVersion + "\"}" )
                .post("/v1/profile-actions/accept-terms-and-conditions/approve")
                .then()
                .statusCode(200)
                .body("message", equalTo("Terms and conditions accepted successfully"));
    }

    // 🚫 Accept Terms & Conditions - Invalid Version
    @Test
    public void testAcceptTermsAndConditionsInvalidVersion() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"terms_and_conditions_version\": \"invalid_version\"}")
                .post("/v1/profile-actions/accept-terms-and-conditions/approve")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_terms_version"))
                .body("message", equalTo("Invalid or outdated terms version provided"));
    }

    // 🚫 Accept Terms without Token
    @Test
    public void testAcceptTermsWithoutToken() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"terms_and_conditions_version\": \"" + latestTermsVersion + "\"}" )
                .post("/v1/profile-actions/accept-terms-and-conditions/approve")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"));
    }
}

// This now fully covers **Terms & Conditions** — including missing tokens, invalid versions, and all necessary success scenarios. 🚀
