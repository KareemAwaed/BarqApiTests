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
}

