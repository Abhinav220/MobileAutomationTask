# SauceLab Mobile Automation Project

A sample mobile automation project for the SauceLabs Demo App using Appium and TestNG. **Supports both Android and iOS platforms.**

## 📋 Project Overview

This project automates an end-to-end shopping flow in the SauceLabs Demo mobile app:

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
│   │   ├── driver/
│   │   │   └── DriverManager.java             # Appium driver management
│   │   └── pages/
│   │       ├── BasePage.java                  # Base page with common methods
│   │       ├── LoginPage.java                 # Login screen page object
│   │       ├── ProductsPage.java              # Products list page object
│   │       ├── ProductDetailsPage.java        # Product details page object
│   │       └── CartPage.java                  # Cart page object
│   └── test/
│       ├── java/com/saucelab/tests/
│       │   └── ShoppingFlowTest.java          # Main test class
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

```bash
# Run all tests (uses platform from config.properties)
mvn clean test

# Run specific test class
mvn clean test -Dtest=ShoppingFlowTest

# Run Android tests only (via TestNG suite)
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml -Dtest=Android Tests

# Run iOS tests only (via TestNG suite)
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml -Dtest=iOS Tests
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
✅ **Cross-Platform Support** - Android and iOS automation  

## 📚 Key Concepts Demonstrated

1. **Page Object Model (POM)** - Each screen has its own page class
2. **Driver Management** - ThreadLocal for parallel execution support, supports both Android and iOS
3. **Configuration Management** - External properties file with platform-specific settings
4. **Method Chaining** - Fluent API design in page objects
5. **Test Dependencies** - Tests execute in order using TestNG
6. **Reusable Components** - BasePage with common methods, platform-aware scrolling
7. **Cross-Platform Automation** - Single codebase for Android and iOS

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

## 📄 License

This is a training project for learning mobile automation.
