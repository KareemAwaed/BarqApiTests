package test;

import io.restassured.http.ContentType;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import base.BaseTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Murabaha API")
@Feature("Murabaha Operations")
public class BarqMurabahaTests extends BaseTest {

    @Test
    @Story("Fetch Murabaha packages")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that Murabaha packages can be fetched successfully")
    public void testGetMurabahaPackagesSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .when()
                .get("/v1/murabaha/packages/index")
                .then()
                .statusCode(200)
                .body("data.packages", notNullValue());
    }

    @Test
    @Story("Fetch Murabaha package details")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that specific Murabaha package details can be fetched successfully")
    public void testGetMurabahaPackageDetailsSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .when()
                .get("/v1/murabaha/packages/show/1")
                .then()
                .statusCode(200)
                .body("data.package_id", equalTo(1));
    }

    @Test
    @Story("Create deposit order")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a deposit order can be created successfully")
    public void testCreateDepositOrderSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .contentType(ContentType.JSON)
                .body("{\"package_statement_id\": 1, \"amount\": 1000}")
                .when()
                .post("/v1/murabaha/deposit-order/create")
                .then()
                .statusCode(200)
                .body("message", equalTo("Deposit order created successfully"));
    }

    @Test
    @Story("List deposit orders")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that deposit orders can be listed successfully")
    public void testListDepositOrdersSuccess() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .header("Authorization", "Bearer valid_access_token")
                .when()
                .get("/v1/murabaha/deposit-order/index")
                .then()
                .statusCode(200)
                .body("data.orders", notNullValue());
    }

    @Test
    @Story("Create deposit order without authorization")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response when creating a deposit order without authorization token")
    public void testCreateDepositOrderUnauthorized() {
        given()
                .header("X-API-KEY", "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN")
                .header("X-SIGNATURE", "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f")
                .contentType(ContentType.JSON)
                .body("{\"package_statement_id\": 1, \"amount\": 1000}")
                .when()
                .post("/v1/murabaha/deposit-order/create")
                .then()
                .statusCode(401)
                .body("code", equalTo("unauthorized"))
                .body("message", equalTo("Authorization token is missing"));
    }
}
