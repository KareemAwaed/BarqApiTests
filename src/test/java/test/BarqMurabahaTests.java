package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
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
        token = ApiHelper.loginAndGetToken(
                ApiHelper.getTestData().getValidUser().getNin(),
                ApiHelper.getTestData().getValidUser().getMobile()
        );
    }

    // ✅ Get Murabaha Packages
    @Test
    @Tag("smoke")
    @Tag("regression")
    public void testGetMurabahaPackages() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/packages/index")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    // ⛔ Get Packages with Invalid Token
    @Test
    @Tag("regression")
    public void testGetMurabahaPackagesInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/murabha/packages/index")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    // ✅ Get User's Orders
    @Test
    @Tag("smoke")
    @Tag("regression")
    public void testGetUserOrders() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/deposit-order/index")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    // ⛔ Get Orders with Invalid Token
    @Test
    @Tag("regression")
    public void testGetUserOrdersInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/murabha/deposit-order/index")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    // ✅ Subscribe to Murabaha
    @Test
    @Tag("smoke")
    @Tag("regression")
    public void testSubscribeToMurabaha() {
        String requestBody = ApiHelper.loadTestDataFile("murabaha-subscribe.json");

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/murabha/deposit-order/create")
                .then()
                .statusCode(201)
                .body("code", equalTo("1001"))
                .body("message", equalTo("تم حفظ البيانات بنجاح"));
    }

    // ⛔ Subscribe with Invalid Token
    @Test
    @Tag("regression")
    public void testSubscribeWithInvalidToken() {
        String requestBody = ApiHelper.loadTestDataFile("murabaha-subscribe.json");

        ApiHelper.createRequestWithInvalidToken()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/murabha/deposit-order/create")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }

    // ✅ Cancel Murabaha Subscription
    @Test
    @Tag("smoke")
    @Tag("regression")
    public void testCancelMurabahaOrder() {
        ApiHelper.createRequestWithToken(token)
                .post("/v1/murabha/deposit-order/cancel/1")
                .then()
                .statusCode(200)
                .body("code", equalTo("1001"))
                .body("message", equalTo("تم حفظ البيانات بنجاح"));
    }

    // ⛔ Cancel with Invalid Token
    @Test
    @Tag("regression")
    public void testCancelOrderInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .post("/v1/murabha/deposit-order/cancel/1")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"));
    }
    // ⛔ Get Packages with Expired Token
    @Test
    @Tag("regression")
    public void testGetMurabahaPackagesExpiredToken() {
        ApiHelper.createRequestWithExpiredToken()
                .get("/v1/murabha/packages/index")

                .then()
                .statusCode(401)
                .body("code", equalTo("token_expired"))
                .body("message", equalTo("Access token has expired"));
    }

    // 🚫 Create Order with Invalid Package ID
    @Test
    @Tag("regression")
    public void testCreateOrderInvalidPackageId() {
        String requestBody = "{\"package_statement_id\": 9999, \"amount\": 1000}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/murabha/deposit-order/create")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_package_id"))
                .body("message", equalTo("Package ID is invalid"));
    }

    // 🚫 Create Order with Excessive Amount
    @Test
    @Tag("regression")
    public void testCreateOrderExcessiveAmount() {
        String requestBody = "{\"package_statement_id\": 1, \"amount\": 1000000000}";

        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/v1/murabha/deposit-order/create")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_amount"))
                .body("message", equalTo("Amount exceeds the allowed maximum limit"));
    }

    // 🚫 Fetch Order Details with Non-existent ID
    @Test
    @Tag("regression")
    public void testFetchOrderDetailsNonExistentId() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/deposit-order/show/9999")
                .then()
                .statusCode(404)
                .body("code", equalTo("order_not_found"))
                .body("message", equalTo("Order not found"));
    }
    @Test
    @Tag("regression")
    public void testCancelSubscriptionAlreadyCanceledOrder() {
        ApiHelper.createRequestWithToken(token)
                .post("/v1/murabha/deposit-order/cancel/1")
                .then()
                .statusCode(400)
                .body("code", equalTo("order_already_canceled"));
    }
    @Test
    @Tag("regression")
    public void testFetchOrderListWithInvalidParameters() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/deposit-order/index?page=-1&limit=2000")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_parameters"));
    }

    @Test
    @Tag("regression")
    public void testMurabahaPackageWithInvalidQueryParams() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabha/packages/index?sort=unknown")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_query_parameters"));
    }
}

