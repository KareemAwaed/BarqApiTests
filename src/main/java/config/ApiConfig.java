package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApiConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties");
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url", "https://apisix-gateway-barq-dev.awaed.cloud"); // Add default URL
    }

    public static String getApiKey() {
        return properties.getProperty("api.key");
    }

    public static String getSignature() {
        return properties.getProperty("api.signature");
    }
}
