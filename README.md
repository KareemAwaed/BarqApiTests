# Barq API Test Automation Framework 🚀

Welcome to the **Barq API Test Automation Framework**! This project is designed to automate API testing for the Barq platform, covering authentication, registration, KYC, profile management, and Murabaha operations.

## 📂 Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── config/                   # API URLs, tokens, and properties
│   │   ├── utils/                    # Utility classes (e.g., SignatureGenerator)
│   │   └── models/                   # POJOs for request/response bodies
│   └── resources/                    # Config and test data files
│
├── test/
│   ├── java/
│   │   ├── base/                    # BaseTest for setup/teardown
│   │   ├── tests/
│   │   │   ├── auth/                # Authentication test cases
│   │   │   ├── register/            # User registration test cases
│   │   │   ├── profile/             # Profile API test cases
│   │   │   ├── kyc/                 # KYC API test cases
│   │   │   └── murabaha/            # Murabaha API test cases
│   │   └── runners/                 # Test runners (e.g., smoke, regression)
│   └── resources/                   # Log and test data configs
│
├── pom.xml                          # Maven dependencies & plugins
└── README.md                        # Project documentation (this file)
```

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/BarqApiTests.git
cd BarqApiTests
```

### 2. Set Up the Environment
Create a `config.properties` file in `src/main/resources/`:
```
base.url=https://apisix-gateway-barq-dev.awaed.co
api.key=your-api-key
api.signature=your-api-signature
```

### 3. Build and Run Tests
```bash
mvn clean test
```

### 4. Generate Test Reports
- **Surefire Report:**
```bash
mvn surefire-report:report
```
- **Allure Report:**
```bash
mvn allure:serve
```

## 🔧 Branching Strategy
- **main** → Stable production-ready code
- **feature/** → New features or tests (e.g., `feature/add-auth-tests`)
- **bugfix/** → Fixes for existing tests (e.g., `bugfix/fix-login-error`)
- **hotfix/** → Urgent fixes directly into `main`

Example branch creation:
```bash
git checkout -b feature/add-kyc-tests
git push -u origin feature/add-kyc-tests
```

## 👥 Contribution Guidelines
1. **Fork the repository**
2. **Create a branch** for your feature or fix
3. **Write clean, well-documented code**
4. **Add or update tests** to cover your changes
5. **Submit a pull request (PR)** with a clear description

PR example:
```
Title: Add API tests for user registration
Description: Added positive and negative test cases for the /register endpoint
```

## 📄 Test plan : https://www.notion.so/awaed-tech/Barq-Test-Plan-1b92bd88c0648078b859d8fc215ab2de?pvs=4
This project is licensed under the MIT License.



