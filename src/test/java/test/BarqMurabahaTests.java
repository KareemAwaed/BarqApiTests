package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Murabaha API")
@Feature("Murabaha Management")
public class BarqMurabahaTests extends BaseTest {

    private static String token;

    @BeforeAll
    public static void setup() {
        // Generate a valid token before running the tests
        token = ApiHelper.loginAndGetToken("2054312802", "+966538772716");
    }

    // ✅ Get Murabaha Packages Successfully
    @Test
    @Story("Get Murabaha Packages")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that Murabaha packages can be fetched successfully")
    public void testGetMurabahaPackagesSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabaha/packages/index")
                .then()
                .statusCode(200)
                .body("data.packages", notNullValue());
    }

    // 🚫 Get Packages with Invalid Token
    @Test
    @Story("Get Murabaha Packages - Invalid Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that fetching Murabaha packages fails with an invalid token")
    public void testGetMurabahaPackagesInvalidToken() {
        ApiHelper.createRequestWithInvalidToken()
                .get("/v1/murabaha/packages/index")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    // ✅ Create Deposit Order Successfully
    @Test
    @Story("Create Deposit Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a deposit order can be created successfully")
    public void testCreateDepositOrderSuccess() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{\"package_statement_id\": 1, \"amount\": 1000}")
                .post("/v1/murabaha/deposit-order/create")
                .then()
                .statusCode(200)
                .body("message", equalTo("Deposit order created successfully"));
    }

    // 🚫 Create Order with Missing Fields
    @Test
    @Story("Create Deposit Order - Missing Fields")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that creating a deposit order fails with missing fields")
    public void testCreateDepositOrderMissingFields() {
        ApiHelper.createRequestWithToken(token)
                .contentType(ContentType.JSON)
                .body("{}")
                .post("/v1/murabaha/deposit-order/create")
                .then()
                .statusCode(400)
                .body("code", equalTo("invalid_data"))
                .body("message", equalTo("package_statement_id and amount are required"));
    }

    // 🚫 Create Order without Authorization
    @Test
    @Story("Create Deposit Order without Authorization")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that creating a deposit order fails without authorization")
    public void testCreateDepositOrderUnauthorized() {
        ApiHelper.createRequest()
                .contentType(ContentType.JSON)
                .body("{\"package_statement_id\": 1, \"amount\": 1000}")
                .post("/v1/murabaha/deposit-order/create")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }

    // ✅ List Deposit Orders
    @Test
    @Story("List Deposit Orders")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that deposit orders can be listed")
    public void testListDepositOrdersSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabaha/deposit-order/index")
                .then()
                .statusCode(200)
                .body("data.orders", notNullValue());
    }

    // ✅ Show Deposit Order Details
    @Test
    @Story("Show Deposit Order Details")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that deposit order details can be fetched")
    public void testShowDepositOrderDetailsSuccess() {
        ApiHelper.createRequestWithToken(token)
                .get("/v1/murabaha/deposit-order/show/1")
                .then()
                .statusCode(200)
                .body("data.order_id", equalTo(1));
    }

    // ✅ Cancel Deposit Order
    @Test
    @Story("Cancel Deposit Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a deposit order can be canceled")
    public void testCancelDepositOrderSuccess() {
        ApiHelper.createRequestWithToken(token)
                .post("/v1/murabaha/deposit-order/cancel/1")
                .then()
                .statusCode(200)
                .body("message", equalTo("Deposit order canceled successfully"));
    }
}
