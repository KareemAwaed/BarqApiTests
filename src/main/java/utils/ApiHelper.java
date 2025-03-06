package utils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ApiHelper {

    private static final String API_KEY = "eWuSEzDqE@bC@TanMP!pVAnCrTrCSCGN";
    private static final String SECRET_KEY = "h(fGfU2kf5pA9xx5Ea*CMJu6)DZ$V6Pg";

    private static TestData testData;

    static {
        loadTestData();
    }

    public static void loadTestData() {
        try (JsonReader reader = new JsonReader(new FileReader("src/test/resources/test-data.json"))) {
            testData = new Gson().fromJson(reader, TestData.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Test data file not found!", e);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw new RuntimeException("Failed to load test data", e);
        }
    }

    public static TestData getTestData() {
        return testData;
    }

    public static String generateSignature(String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private static RequestSpecification createBaseRequest(String apiKey, String signature) {
        return RestAssured.given()
                .header("X-API-KEY", apiKey)
                .header("X-SIGNATURE", signature);
    }

    public static RequestSpecification createRequest() {
        return createRequest(""); // Default to empty body
    }

    public static RequestSpecification createRequest(String body) {
        String signature = generateSignature(body);
        return createBaseRequest(API_KEY, signature);
    }

    public static RequestSpecification createRequestWithToken(String token) {
        return createRequestWithToken(token, ""); // Default to empty body
    }

    public static RequestSpecification createRequestWithToken(String token, String body) {
        return createRequest(body).header("Authorization", "Bearer " + token);
    }

    public static RequestSpecification createRequestWithTokenAndSignature(String token) {
        return createRequestWithTokenAndSignature(token, ""); // Default to empty body
    }

    public static RequestSpecification createRequestWithTokenAndSignature(String token, String body) {
        return RestAssured.given()
                .header("X-API-KEY", API_KEY)
                .header("X-SIGNATURE", generateSignature(body))
                .header("Authorization", "Bearer " + token);
    }

    public static RequestSpecification createRequestWithInvalidSignature() {
        return createBaseRequest(API_KEY, "invalid_signature");
    }

    public static RequestSpecification createRequestWithoutApiKey() {
        return createRequestWithoutApiKey(""); // Overloaded to handle no body
    }

    public static RequestSpecification createRequestWithoutApiKey(String body) {
        return RestAssured.given().header("X-SIGNATURE", generateSignature(body));
    }

    public static RequestSpecification createRequestWithInvalidToken(String body) {
        return createRequest(body).header("Authorization", "Bearer invalid_token");
    }

    public static RequestSpecification createRequestWithExpiredToken(String body) {
        return createRequest(body).header("Authorization", "Bearer expired_token");
    }

    public static RequestSpecification createRequestWithoutHeaders() {
        return RestAssured.given();
    }

    public static RequestSpecification createRequestWithLimitedPermissions(String body) {
        return createBaseRequest("limited_api_key", generateSignature(body));
    }

    public static String generateRandomMobile() {
        return "+9665" + (int)(Math.random() * 100000000);
    }

    public static String generateRandomNin() {
        return String.format("%010d", (int)(Math.random() * 10000000000L));
    }

    public static String loginAndGetToken(String nin, String mobile) {
        User user = new User(nin, mobile);
        String body = new Gson().toJson(user);
        Response response = createRequest(body)
                .contentType("application/json")
                .body(body)
                .post("/v1/auth/login");

        if (response.statusCode() == 200) {
            return response.jsonPath().getString("data.access_token");
        } else {
            throw new RuntimeException("Failed to get access token: " + response.getBody().asString());
        }
    }

    public static String createDynamicPayload(String nin, String mobile) {
        User user = new User(nin, mobile);
        return new Gson().toJson(user);
    }

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

    public static class TestData {
        private String registeredNumber;
        private String password;
        private String nin;
        private String dob;

        public String getRegisteredNumber() {
            return registeredNumber;
        }

        public String getPassword() {
            return password;
        }

        public String getNin() {
            return nin;
        }

        public String getDob() {
            return dob;
        }
    }
}
