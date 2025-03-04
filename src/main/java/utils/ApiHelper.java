package utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.filter.log.LogDetail;
import io.qameta.allure.restassured.AllureRestAssured;

public class ApiHelper {

    public static RequestSpecification createRequest() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .log().ifValidationFails(LogDetail.ALL);
    }

    public static RequestSpecification createRequestWithoutApiKey() {
        return createRequest();
    }

    public static RequestSpecification createRequestWithExpiredToken() {
        return createRequest().header("Authorization", "Bearer expired_token");
    }

    public static RequestSpecification createRequestWithToken() {
        return createRequest().header("Authorization", "Bearer valid_token");
    }
}
