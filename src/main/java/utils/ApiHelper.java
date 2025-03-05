package utils;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiHelper {

    private static final String API_KEY = "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN";
    private static final String API_SIGNATURE = "951b182d313601589ebea1f2be1ec8686213c1f262e202547afab74420fd5b5f";

    // Base method to create a request with API key and signature
    private static RequestSpecification createBaseRequest(String apiKey, String signature) {
        return RestAssured.given()
                .header("X-API-KEY", apiKey)
                .header("X-SIGNATURE", signature);
    }

    // Standard valid request
    public static RequestSpecification createRequest() {
        return createBaseRequest(API_KEY, API_SIGNATURE);
    }

    // Valid token request
    public static RequestSpecification createRequestWithToken(String token) {
        return createRequest().header("Authorization", "Bearer " + token);
    }

    // Invalid signature request
    public static RequestSpecification createRequestWithInvalidSignature() {
        return createBaseRequest(API_KEY, "invalid_signature");
    }

    // No API key request
    public static RequestSpecification createRequestWithoutApiKey() {
        return RestAssured.given().header("X-SIGNATURE", "valid_signature");
    }

    // Invalid token request
    public static RequestSpecification createRequestWithInvalidToken() {
        return createRequest().header("Authorization", "Bearer invalid_token");
    }

    // Expired token request
    public static RequestSpecification createRequestWithExpiredToken() {
        return createRequest().header("Authorization", "Bearer expired_token");
    }

    // No headers request
    public static RequestSpecification createRequestWithoutHeaders() {
        return RestAssured.given();
    }

    // Limited permissions request
    public static RequestSpecification createRequestWithLimitedPermissions() {
        return createBaseRequest("limited_api_key", "valid_signature");
    }

    // Generate random valid mobile number
    public static String generateRandomMobile() {
        return "+9665" + (int)(Math.random() * 100000000);
    }

    // Generate random NIN (e.g., 10 digits)
    public static String generateRandomNin() {
        return String.format("%010d", (int)(Math.random() * 10000000000L));
    }

    // Login and get access token
    public static String loginAndGetToken(String nin, String mobile) {
        User user = new User(nin, mobile);  // Use POJO for payload
        Response response = createRequest()
                .contentType("application/json")
                .body(new Gson().toJson(user))  // Serialize POJO to JSON
                .post("/v1/auth/login");

        if (response.statusCode() == 200) {
            return response.jsonPath().getString("data.access_token");
        } else {
            throw new RuntimeException("Failed to get access token: " + response.getBody().asString());
        }
    }

    // Dynamic payload creation (POJO-based)
    public static String createDynamicPayload(String nin, String mobile) {
        User user = new User(nin, mobile);
        return new Gson().toJson(user);
    }

    // User POJO class
    public static class User {
        private String nin;
        private String mobile;

        public User(String nin, String mobile) {
            this.nin = nin;
            this.mobile = mobile;
        }

        public String getNin() {
            return nin;
        }

        public void setNin(String nin) {
            this.nin = nin;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }
}
