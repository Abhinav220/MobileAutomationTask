# SauceLab Automation Project

A comprehensive automation project for the SauceLabs Demo App using **Appium** (for mobile) and **Playwright** (for web browser) with TestNG. **Supports Android, iOS, and Web Browser platforms with Xray and ChainTest reporting integration.**

## 📋 Project Overview

This project automates an end-to-end shopping flow in the SauceLabs Demo app across multiple platforms:

### Mobile Testing (Android & iOS)
1. **Login** - Enter credentials and verify successful login
2. **Product Selection** - Browse and select a product
3. **Add to Cart** - Add product to cart and verify
4. **Cart Validation** - Verify item details in cart
5. **Bonus Features** - Remove from cart, price validation

### Web Browser Testing (Playwright)
1. **Login** - Enter credentials and verify successful login
2. **Product Selection** - Browse and select a product
3. **Add to Cart** - Add product to cart and verify
4. **Cart Validation** - Verify item details in cart
5. **Bonus Features** - Remove from cart, price validation

## 🏗️ Project Structure

```
SauceLab_automation/
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
├── src/
│   ├── main/java/com/saucelab/
│   │   ├── config/
│   │   │   └── ConfigLoader.java              # Configuration loader
│   │   ├── config/
│   │   │   └── ConfigLoader.java              # Configuration loader
│   │   ├── driver/
│   │   │   └── DriverManager.java             # Appium driver management
│   │   ├── factory/
│   │   │   └── PlaywrightFactory.java         # Playwright browser factory
│   │   ├── listeners/
│   │   │   ├── XrayListener.java              # Xray test result listener
│   │   │   └── ChainTestReporter.java         # ChainTest report generator
│   │   ├── utils/
│   │   │   └── XrayLogger.java                # Xray integration utility
│   │   ├── annotations/
│   │   │   └── XrayKey.java                   # Xray test key annotation
│   │   ├── pages/
│   │   │   ├── BasePage.java                  # Mobile base page
│   │   │   ├── LoginPage.java                 # Mobile login page object
│   │   │   ├── ProductsPage.java              # Mobile products page object
│   │   │   ├── ProductDetailsPage.java        # Mobile product details page object
│   │   │   └── CartPage.java                  # Mobile cart page object
│   │   └── pages/web/
│   │       ├── WebBasePage.java               # Web base page
│   │       ├── WebLoginPage.java              # Web login page object
│   │       ├── WebProductsPage.java           # Web products page object
│   │       ├── WebProductDetailsPage.java     # Web product details page object
│   │       └── WebCartPage.java                # Web cart page object
│   └── test/
│       ├── java/com/saucelab/tests/
│       │   ├── ShoppingFlowTest.java          # Mobile test class
│       │   └── WebShoppingFlowTest.java       # Web browser test class
│       └── resources/
│           ├── config.properties              # Test configuration
│           └── testng.xml                     # TestNG suite configuration
```

## 🛠️ Prerequisites

### Common Requirements
1. **Java JDK 11+** installed
2. **Maven** installed
3. **Appium Server** installed and running

### For Android Testing
4. **Android SDK** with emulator or real device
5. **SauceLabs Demo App** (APK) installed on device/emulator
   - Download APK: https://github.com/saucelabs/sample-app-mobile/releases

### For iOS Testing
4. **Xcode** installed (macOS only)
5. **iOS Simulator** or real iOS device
6. **SauceLabs Demo App** (APP or IPA) for iOS
   - Download iOS app: https://github.com/saucelabs/sample-app-mobile/releases
   - Or build from source using Xcode

### For Web Browser Testing
4. **Playwright** browsers installed (automatically installed via Maven)
5. **Internet connection** for accessing SauceLabs demo website

## ⚙️ Configuration

Edit `src/test/resources/config.properties`:

### Android Configuration

```properties
# Platform Selection
platform=Android

# Appium Server
appium.server.url=http://127.0.0.1:4723

# Android Device Configuration
android.device.name=emulator-5554
android.platform.version=16
android.automation.name=UiAutomator2

# Android App Configuration
app.path=src/main/resources/app/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk
app.package=com.swaglabsmobileapp
app.activity=com.swaglabsmobileapp.SplashActivity

# Test Credentials
test.username=standard_user
test.password=secret_sauce

# Wait Configuration (in seconds)
implicit.wait=10
explicit.wait=15
```

### iOS Configuration

```properties
# Platform Selection
platform=iOS

# Appium Server
appium.server.url=http://127.0.0.1:4723

# iOS Device Configuration
ios.device.name=iPhone 14
ios.platform.version=16.0
ios.bundle.id=com.saucelabs.SwagLabsMobileApp
ios.udid=                    # Optional: Leave empty for simulator, set UDID for real device
ios.auto.accept.alerts=true
ios.auto.dismiss.alerts=false

# iOS App Configuration
ios.app.path=src/main/resources/app/SwagLabs.app

# Test Credentials
test.username=standard_user
test.password=secret_sauce

# Wait Configuration (in seconds)
implicit.wait=10
explicit.wait=15
```

## 🚀 Running Tests

### 1. Start Appium Server

```bash
appium
```

### 2. Start Device/Simulator

#### For Android:
```bash
# Start Android Emulator
emulator -avd <your_avd_name>

# Or connect real device via USB
adb devices
```

#### For iOS:
```bash
# List available simulators
xcrun simctl list devices

# Boot a simulator (if not already running)
open -a Simulator

# Or use command line
xcrun simctl boot "iPhone 14"
```

### 3. Configure Platform

Edit `src/test/resources/config.properties` and set:
- `platform=Android` for Android tests
- `platform=iOS` for iOS tests

### 4. Run Tests

#### Mobile Tests
```bash
# Run all mobile tests (uses platform from config.properties)
mvn clean test -Dtest=ShoppingFlowTest

# Run Android tests only (via TestNG suite)
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml -Dtest=Android Tests

# Run iOS tests only (via TestNG suite)
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml -Dtest=iOS Tests
```

#### Web Browser Tests
```bash
# Run web browser tests
mvn clean test -Dtest=WebShoppingFlowTest

# Run all tests (mobile + web)
mvn clean test
```

## 📝 Test Cases

| Test | Description | Assertions |
|------|-------------|------------|
| `testLogin` | Login with valid credentials | Products page displayed |
| `testSelectProduct` | Select product from list | Details page displayed, name matches |
| `testAddToCart` | Add product to cart | Remove button visible, cart count = 1, item name matches |
| `testRemoveFromCart` | Remove item from cart | Cart is empty |
| `testPriceValidation` | Validate price across pages | Price same on all pages |

## 🎯 Technical Requirements Met

✅ **Implicit Wait** - Configured in DriverManager  
✅ **Explicit Wait** - WebDriverWait in BasePage methods  
✅ **3+ Assertions** - Multiple assertions in each test  
✅ **Page Object Model** - Separate page classes  
✅ **Emulator/Real Device** - Configurable in properties  
✅ **Cross-Platform Support** - Android, iOS, and Web Browser automation  
✅ **Playwright Integration** - Modern browser automation with Playwright  
✅ **Xray Integration** - Automatic test result reporting to Xray Cloud  
✅ **ChainTest Reporting** - Beautiful HTML test reports with ChainTest  

## 📚 Key Concepts Demonstrated

1. **Page Object Model (POM)** - Each screen has its own page class
2. **Driver Management** - ThreadLocal for parallel execution support, supports Android, iOS, and Web
3. **Configuration Management** - External properties file with platform-specific settings
4. **Method Chaining** - Fluent API design in page objects
5. **Test Dependencies** - Tests execute in order using TestNG
6. **Reusable Components** - BasePage with common methods, platform-aware scrolling
7. **Cross-Platform Automation** - Single codebase for Android, iOS, and Web Browser
8. **Playwright Factory** - ThreadLocal-based browser management for parallel web testing
9. **Xray Integration** - Automatic test result reporting to Xray Cloud with test execution tracking
10. **ChainTest Reporting** - Professional HTML test reports with automatic Xray attachment

## 🔧 Troubleshooting

### Android Issues

#### App Not Found
```
Ensure app is installed: adb shell pm list packages | grep saucelabs
```

#### Device Not Connected
```
Check device: adb devices
Restart ADB: adb kill-server && adb start-server
```

### iOS Issues

#### Simulator Not Starting
```
List simulators: xcrun simctl list devices
Boot simulator: xcrun simctl boot "iPhone 14"
```

#### WebDriverAgent Build Issues
```
Clean and rebuild WebDriverAgent:
cd /usr/local/lib/node_modules/appium/node_modules/appium-xcuitest-driver/WebDriverAgent
xcodebuild -project WebDriverAgent.xcodeproj -scheme WebDriverAgentRunner -destination 'id=<UDID>' test
```

#### Real Device Issues
- Ensure device is trusted and developer mode is enabled
- Set `ios.udid` in config.properties to your device UDID
- Check device UDID: `xcrun xctrace list devices`

### Common Issues

#### Element Not Found
```
Use Appium Inspector to verify locators
For iOS, check accessibility identifiers and predicates
```

#### Driver Timeout
```
Increase implicit.wait and explicit.wait in config.properties
For iOS, increase wdaLaunchTimeout in DriverManager
```

#### Platform-Specific Scrolling
```
Android uses UiScrollable, iOS uses W3C Actions API
BasePage handles platform differences automatically
```

### Web Browser Issues

#### Playwright Browsers Not Installed
```
Run: mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
Or: npx playwright install
```

#### Browser Launch Fails
```
Check browser.name in config.properties (chromium, chrome, firefox, webkit)
Ensure headless mode is set correctly: browser.headless=false
```

#### Element Not Found in Web Tests
```
Use browser developer tools to verify selectors
Check if element is in iframe (may need frame handling)
Verify viewport size matches expected layout
```

## 📊 Xray and ChainTest Reporting

### Xray Integration

The project includes automatic Xray Cloud integration for test result reporting.

#### Configuration

Edit `src/test/resources/config.properties`:

```properties
# Enable Xray integration
xray.enabled=true

# Xray Cloud API credentials
xray.client.id=your_client_id
xray.client.secret=your_client_secret

# Xray API endpoints (defaults provided)
xray.auth.endpoint=https://xray.cloud.getxray.app/api/oauth/token
xray.execution.endpoint=https://xray.cloud.getxray.app/api/v2/import/execution

# Test Execution Key (optional - will create new if not provided)
xray.exec.id=SAUCE-123

# Jira Configuration (for report attachments)
jira.url=https://your-jira-instance.atlassian.net
jira.email=your-email@example.com
jira.token=your-api-token
```

#### Using XrayKey Annotation

Annotate test methods or classes with `@XrayKey` to link tests to Xray:

```java
import com.saucelab.annotations.XrayKey;

@XrayKey("SAUCE-123")
@Test
public void testLogin() {
    // Test implementation
}
```

#### Running Tests with Xray

```bash
# Run tests with Xray enabled
mvn clean test

# Override execution ID via system property
mvn clean test -DexecId=SAUCE-456

# Disable Xray for a run
mvn clean test -Dxray.enabled=false
```

### ChainTest Reporting

ChainTest automatically generates beautiful HTML test reports.

#### Features

- **Automatic Report Generation** - Reports generated in `target/chaintest/Index.html`
- **Xray Integration** - Reports automatically attached to Xray test executions
- **Test Statistics** - Comprehensive test statistics and summaries
- **Visual Reports** - Professional HTML reports with charts and graphs

#### Viewing Reports

After test execution, open the ChainTest report:

```bash
# Open the report in browser
open target/chaintest/Index.html

# Or on Linux
xdg-open target/chaintest/Index.html
```

#### Report Location

- **Main Report**: `target/chaintest/Index.html`
- **Resources**: `target/chaintest/resources/`

### Xray Troubleshooting

#### Authentication Fails
```
Verify xray.client.id and xray.client.secret in config.properties
Check Xray Cloud API credentials in Xray settings
```

#### Test Results Not Reported
```
Ensure @XrayKey annotation is present on test methods
Check xray.enabled=true in config.properties
Verify test execution key is set (xray.exec.id)
```

#### Report Attachment Fails
```
Verify Jira credentials (jira.url, jira.email, jira.token)
Check network connectivity to Jira instance
Ensure test execution key exists in Jira
```

## 🔐 Credential Encryption

The project supports encrypted storage of sensitive credentials (Xray and Jira) using AES-GCM encryption.

### Encrypting Credentials

Use the `CredentialEncryptor` utility to encrypt your credentials:

```bash
# Compile the project first
mvn clean compile

# Run the encryption utility
mvn exec:java -Dexec.mainClass="com.saucelab.utils.CredentialEncryptor"

# Or directly with Java
java -cp target/classes com.saucelab.utils.CredentialEncryptor
```

The utility will prompt you for:
- Encryption secret key (or use default)
- Xray Client ID
- Xray Client Secret
- Jira Email
- Jira API Token

It will output encrypted values that you can add to `config.properties`.

### Configuration

Edit `src/test/resources/config.properties`:

```properties
# Encryption Configuration
encryption.secret.key=SauceLabAutomation

# Use encrypted credentials (recommended)
xray.client.id.encrypted=<encrypted_value>
xray.client.secret.encrypted=<encrypted_value>
jira.email.encrypted=<encrypted_value>
jira.token.encrypted=<encrypted_value>

# OR use plain text (not recommended for production)
# xray.client.id=your_client_id
# xray.client.secret=your_client_secret
# jira.email=your-email@example.com
# jira.token=your-api-token
```

### Security Best Practices

1. **Use Encrypted Credentials** - Always use `.encrypted` properties in production
2. **Change Default Secret Key** - Update `encryption.secret.key` to a custom value
3. **Don't Commit Secrets** - Add `config.properties` to `.gitignore` if it contains secrets
4. **Rotate Keys Regularly** - Change encryption keys periodically

### Programmatic Encryption

You can also encrypt credentials programmatically:

```java
import com.saucelab.utils.EncryptionUtils;

String secretKey = "YourSecretKey";
String plainText = "your-credential";
String encrypted = EncryptionUtils.encrypt(plainText, secretKey);
String decrypted = EncryptionUtils.decrypt(encrypted, secretKey);
```

### Encryption Algorithm

- **Algorithm**: AES-GCM (Galois/Counter Mode)
- **Key Size**: 128 bits (AES-128)
- **IV Length**: 96 bits (12 bytes)
- **Tag Length**: 128 bits (16 bytes)
- **Encoding**: Base64 for storage

## 📄 License

This is a training project for learning mobile automation.
