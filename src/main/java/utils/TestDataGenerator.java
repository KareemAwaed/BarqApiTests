package utils;

import java.util.Random;

public class TestDataGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Generates a valid 10-digit NIN.
     * @return A valid NIN string.
     */
    public static String generateValidNin() {
        return "205" + (100000 + RANDOM.nextInt(900000));
    }

    /**
     * Generates a valid mobile number starting with +9665.
     * @return A valid mobile number string.
     */
    public static String generateValidMobileNumber() {
        StringBuilder mobileNumber = new StringBuilder("+9665");
        for (int i = 0; i < 8; i++) {
            mobileNumber.append(RANDOM.nextInt(10));
        }
        return mobileNumber.toString();
    }
}

// Example usage:
// System.out.println(TestDataGenerator.generateValidNin());
// System.out.println(TestDataGenerator.generateValidMobileNumber());

// Let me know, and we’ll update your test files to use this dynamically! 🚀
