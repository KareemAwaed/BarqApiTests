package config;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;

public class TestDataLoader {

    public static <T> T loadTestData(String filePath, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load test data from file: " + filePath);
        }
    }
}
