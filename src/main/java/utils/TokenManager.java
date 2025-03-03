package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static config.ApiConfig.getApiKey;
import static config.ApiConfig.getBaseUrl;

public class TokenManager {

    private static String accessToken;

    public static String getAccessToken() {
        if (accessToken == null || accessToken.isEmpty()) {
            generateAccessToken();
        }
        return accessToken;
    }

    private static void generateAccessToken() {
        Response response = RestAssured.given()
                .header("X-API-KEY", getApiKey())
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"testuser\", \"password\": \"testpass\" }")
                .post(getBaseUrl() + "/v1/auth/login");

        accessToken = response.jsonPath().getString("data.token");
    }

    public static void clearToken() {
        accessToken = null;
    }
}
