package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Murabaha API")
@Feature("Murabaha Investment Management")
public class BarqMurabahaTests extends BaseTest {

    private static String token;

    @BeforeAll
    public static void setup() {
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Get Murabaha Packages
    @Test
    public void testGetMurabahaPackages() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/packages/index")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    // 🚫 Get Packages with Invalid Token
    @Test
    public void testGetMurabahaPackagesInvalidToken() {
        ApiHelper.createRequestWithInvalidToken("invalid_token")
                .get("/v1/murabha/packages/index")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    // ✅ Get User's Orders
    @Test
    public void testGetUserOrders() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/deposit-order/index")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    // 🚫 Get Orders with Invalid Token
    @Test
    public void testGetUserOrdersInvalidToken() {
        ApiHelper.createRequestWithInvalidToken("invalid_token")
                .get("/v1/murabha/deposit-order/index")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    // ✅ Subscribe to Murabaha
    @Test
    public void testSubscribeToMurabaha() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"package_id\": 1, " +
                        "\"amount\": 5000 }")
                .post("/v1/murabha/deposit-order/create")
                .then()
                .statusCode(201)
                .body("code", equalTo("1001"))
                .body("message", equalTo("تم حفظ البيانات بنجاح"));
    }

    // 🚫 Subscribe with Invalid Token
    @Test
    public void testSubscribeWithInvalidToken() {
        ApiHelper.createRequestWithInvalidToken("invalid_token")
                .contentType(ContentType.JSON)
                .body("{" +
                        "\"package_id\": 1, " +
                        "\"amount\": 5000 }")
                .post("/v1/murabha/deposit-order/create")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    // ✅ Cancel Murabaha Subscription
    @Test
    public void testCancelMurabahaOrder() {
        ApiHelper.createRequestWithToken(token)
                .post("/v1/murabha/deposit-order/cancel/1")
                .then()
                .statusCode(200)
                .body("code", equalTo("1001"))
                .body("message", equalTo("تم حفظ البيانات بنجاح"));
    }

    // 🚫 Cancel with Invalid Token
    @Test
    public void testCancelOrderInvalidToken() {
        ApiHelper.createRequestWithInvalidToken("invalid_token")
                .post("/v1/murabha/deposit-order/cancel/1")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }
}

// This should now match 100% of the documentation — let me know if anything else needs to be refined! 🚀
