package base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    public static void setup() {
        String env = System.getProperty("env", "dev"); // Default to dev if no environment is provided
        String baseUrl = getBaseUrl(env);

        RestAssured.baseURI = baseUrl;

        // Add Allure listener to capture API request and response details
        RestAssured.filters(new AllureRestAssured());

        logger.info("Environment set to: " + env);
        logger.info("Base URI set to: " + baseUrl);
    }

    private static String getBaseUrl(String env) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            String url = properties.getProperty("base.url." + env);
            if (url == null) {
                throw new IllegalArgumentException("Environment '" + env + "' is not configured in config.properties");
            }
            return url;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load base URL for environment: " + env, e);
        }
    }
}
