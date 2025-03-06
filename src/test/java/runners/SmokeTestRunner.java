package runners;

import org.junit.platform.suite.api.*;

@Suite
@SelectPackages("test")
@IncludeTags("smoke")
@SuiteDisplayName("Smoke Test Suite")
public class SmokeTestRunner {
}
