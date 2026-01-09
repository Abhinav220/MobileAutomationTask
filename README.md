# SauceLab Mobile Automation Project

A sample mobile automation project for the SauceLabs Demo App using Appium and TestNG.

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

1. **Java JDK 11+** installed
2. **Maven** installed
3. **Android SDK** with emulator or real device
4. **Appium Server** installed and running
5. **SauceLabs Demo App** installed on device/emulator
   - Download APK: https://github.com/saucelabs/sample-app-mobile/releases

## ⚙️ Configuration

Edit `src/test/resources/config.properties`:

```properties
# Appium Server
appium.server.url=http://127.0.0.1:4723

# Device Configuration
android.device.name=emulator-5554
android.platform.version=13

# App Configuration
app.package=com.swaglabsmobileapp
app.activity=com.swaglabsmobileapp.MainActivity

# Test Credentials
test.username=standard_user
test.password=secret_sauce
```

## 🚀 Running Tests

### 1. Start Appium Server

```bash
appium
```

### 2. Start Android Emulator (or connect real device)

```bash
emulator -avd <your_avd_name>
```

### 3. Run Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=ShoppingFlowTest

# Run with custom device
mvn clean test -Dandroid.device.name=Pixel_6_API_33
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

## 📚 Key Concepts Demonstrated

1. **Page Object Model (POM)** - Each screen has its own page class
2. **Driver Management** - ThreadLocal for parallel execution support
3. **Configuration Management** - External properties file
4. **Method Chaining** - Fluent API design in page objects
5. **Test Dependencies** - Tests execute in order using TestNG
6. **Reusable Components** - BasePage with common methods

## 🔧 Troubleshooting

### App Not Found
```
Ensure app is installed: adb shell pm list packages | grep saucelabs
```

### Element Not Found
```
Use Appium Inspector to verify locators
```

### Driver Timeout
```
Increase implicit.wait and explicit.wait in config.properties
```

## 📄 License

This is a training project for learning mobile automation.
