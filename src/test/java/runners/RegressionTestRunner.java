package runners;

import org.junit.platform.suite.api.*;

@Suite
@SelectPackages("test")
@IncludeTags("regression")
@SuiteDisplayName("Regression Test Suite")
public class RegressionTestRunner {
}
