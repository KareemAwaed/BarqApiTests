package base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://apisix-gateway-barq-dev.awaed.co";

        // Add Allure listener to capture API request and response details
        RestAssured.filters(new AllureRestAssured());
    }
}


