package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;
import utils.ApiHelper;

import static org.hamcrest.Matchers.*;

@Epic("Murabaha API")
@Feature("Murabaha Product Management")
public class BarqMurabahaTests extends BaseTest {

    // ✅ Get Murabaha Packages
    @Test
    @Story("Fetch Murabaha Packages")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that Murabaha packages can be retrieved successfully")
    public void testGetMurabahaPackagesSuccess() {
        ApiHelper.createRequestWithToken()
                .get("/v1/murabaha/packages/index")
                .then()
                .statusCode(200)
                .body("data.packages", notNullValue());
    }

    @Test
    @Story("Fetch Murabaha Packages - Invalid Token")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that fetching Murabaha packages fails with an invalid token")
    public void testGetMurabahaPackagesInvalidToken() {
        ApiHelper.createRequestWithExpiredToken()
                .get("/v1/murabaha/packages/index")
                .then()
                .statusCode(401)
                .body("code", equalTo("invalid_token"))
                .body("message", equalTo("Invalid access token"));
    }

    // ✅ Create Deposit Order
    @Test
    @Story("Create Deposit Order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a deposit order can be created successfully")
    public void testCreateDepositOrderSuccess() {
        ApiHelper.createRequestWithToken()
                .contentType(ContentType.JSON)
                .body("{\"package_statement_id\": 1, \"amount\": 1000}")
                .post("/v1/murabaha/deposit-order/create")
                .then()
                .statusCode(200)
                .body("message", equalTo("Deposit order created successfully"));
    }

    @Test
    @Story("Create Deposit Order - Unauthorized")
    @Severity(SeverityLevel.NORMAL)
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
    @Description("Verify that deposit orders can be listed successfully")
    public void testListDepositOrdersSuccess() {
        ApiHelper.createRequestWithToken()
                .get("/v1/murabaha/deposit-order/index")
                .then()
                .statusCode(200)
                .body("data.orders", notNullValue());
    }
}
