package com.saucelab.driver;

import com.saucelab.config.ConfigLoader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Driver Manager for handling Appium Android and iOS driver lifecycle.
 * Implements singleton pattern with ThreadLocal for parallel execution support.
 */
public class DriverManager {
    
    private static ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    
    private DriverManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initializes the AppiumDriver (Android or iOS) with configurations from config.properties.
     * Platform is determined by the "platform" property in config.properties.
     */
    public static void initDriver() {
        if (driverThreadLocal.get() == null) {
            if (ConfigLoader.isAndroid()) {
                initAndroidDriver();
            } else if (ConfigLoader.isIOS()) {
                initIOSDriver();
            } else {
                throw new RuntimeException("Unsupported platform: " + ConfigLoader.getPlatform());
            }
        }
    }
    
    /**
     * Initializes the AndroidDriver with configurations from config.properties.
     * Uses setApp capability to install and launch the APK from the specified path.
     */
    private static void initAndroidDriver() {
        System.out.println("[DRIVER] Initializing Android Driver...");
        
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(ConfigLoader.getDeviceName());
        options.setPlatformName(ConfigLoader.getPlatformName());
        options.setPlatformVersion(ConfigLoader.getPlatformVersion());
        options.setAutomationName(ConfigLoader.getAutomationName());
        
        // Use setApp capability to install APK from file path
        String appPath = ConfigLoader.getAbsoluteAppPath();
        if (appPath != null && !appPath.isEmpty()) {
            File apkFile = new File(appPath);
            if (apkFile.exists()) {
                System.out.println("[DRIVER] Using APK file: " + appPath);
                options.setApp(appPath);
            } else {
                System.err.println("[DRIVER] APK file not found: " + appPath);
                throw new RuntimeException("APK file not found: " + appPath);
            }
        } else {
            // Fallback to app package/activity if no APK path specified
            System.out.println("[DRIVER] No APK path specified, using app package/activity");
            options.setAppPackage(ConfigLoader.getAppPackage());
            options.setAppActivity(ConfigLoader.getAppActivity());
        }
        
        // Use wildcard for app wait activity to handle any activity
        options.setAppWaitActivity("*");
        
        // Additional useful options
        options.setNoReset(false);  // Reset app state before each session
        options.setFullReset(false); // Don't uninstall app
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        options.setAutoGrantPermissions(true); // Auto grant app permissions
        options.setAppWaitDuration(Duration.ofSeconds(30)); // Wait up to 30s for app to launch
        
        try {
            URL appiumServerUrl = new URL(ConfigLoader.getAppiumServerUrl());
            System.out.println("[DRIVER] Connecting to Appium server: " + appiumServerUrl);
            
            AndroidDriver driver = new AndroidDriver(appiumServerUrl, options);
            
            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigLoader.getImplicitWait())
            );
            
            driverThreadLocal.set(driver);
            
            // Initialize WebDriverWait for explicit waits
            WebDriverWait wait = new WebDriverWait(driver, 
                Duration.ofSeconds(ConfigLoader.getExplicitWait()));
            waitThreadLocal.set(wait);
            
            System.out.println("[DRIVER] Android Driver initialized successfully");
            System.out.println("[DRIVER] Device: " + ConfigLoader.getDeviceName());
            System.out.println("[DRIVER] App: " + (appPath != null ? appPath : ConfigLoader.getAppPackage()));
            
        } catch (MalformedURLException e) {
            System.err.println("[DRIVER] Invalid Appium server URL: " + e.getMessage());
            throw new RuntimeException("Failed to initialize Android driver", e);
        }
    }
    
    /**
     * Initializes the IOSDriver with configurations from config.properties.
     * Uses setApp capability to install and launch the iOS app from the specified path.
     */
    private static void initIOSDriver() {
        System.out.println("[DRIVER] Initializing iOS Driver...");
        
        XCUITestOptions options = new XCUITestOptions();
        options.setDeviceName(ConfigLoader.getIOSDeviceName());
        options.setPlatformVersion(ConfigLoader.getIOSPlatformVersion());
        options.setAutomationName(ConfigLoader.getAutomationName());
        
        // Set UDID if provided
        String udid = ConfigLoader.getIOSUdid();
        if (udid != null && !udid.isEmpty()) {
            options.setUdid(udid);
            System.out.println("[DRIVER] Using UDID: " + udid);
        }
        
        // Use setApp capability to install iOS app from file path
        String appPath = ConfigLoader.getAbsoluteAppPath();
        if (appPath != null && !appPath.isEmpty()) {
            File appFile = new File(appPath);
            if (appFile.exists()) {
                System.out.println("[DRIVER] Using iOS app file: " + appPath);
                options.setApp(appPath);
            } else {
                System.err.println("[DRIVER] iOS app file not found: " + appPath);
                // For iOS, we can also use bundle ID if app is already installed
                String bundleId = ConfigLoader.getIOSBundleId();
                if (bundleId != null && !bundleId.isEmpty()) {
                    System.out.println("[DRIVER] Using bundle ID instead: " + bundleId);
                    options.setBundleId(bundleId);
                } else {
                    throw new RuntimeException("iOS app file not found and no bundle ID provided: " + appPath);
                }
            }
        } else {
            // Fallback to bundle ID if no app path specified
            String bundleId = ConfigLoader.getIOSBundleId();
            if (bundleId != null && !bundleId.isEmpty()) {
                System.out.println("[DRIVER] No app path specified, using bundle ID: " + bundleId);
                options.setBundleId(bundleId);
            } else {
                throw new RuntimeException("No iOS app path or bundle ID specified");
            }
        }
        
        // iOS-specific options
        options.setNoReset(false);  // Reset app state before each session
        options.setFullReset(false); // Don't uninstall app
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        options.setAutoAcceptAlerts(ConfigLoader.getIOSAutoAcceptAlerts());
        options.setAutoDismissAlerts(ConfigLoader.getIOSAutoDismissAlerts());
        options.setWdaLaunchTimeout(Duration.ofSeconds(60)); // Wait up to 60s for WebDriverAgent to launch
        
        try {
            URL appiumServerUrl = new URL(ConfigLoader.getAppiumServerUrl());
            System.out.println("[DRIVER] Connecting to Appium server: " + appiumServerUrl);
            
            IOSDriver driver = new IOSDriver(appiumServerUrl, options);
            
            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigLoader.getImplicitWait())
            );
            
            driverThreadLocal.set(driver);
            
            // Initialize WebDriverWait for explicit waits
            WebDriverWait wait = new WebDriverWait(driver, 
                Duration.ofSeconds(ConfigLoader.getExplicitWait()));
            waitThreadLocal.set(wait);
            
            System.out.println("[DRIVER] iOS Driver initialized successfully");
            System.out.println("[DRIVER] Device: " + ConfigLoader.getIOSDeviceName());
            System.out.println("[DRIVER] Platform Version: " + ConfigLoader.getIOSPlatformVersion());
            System.out.println("[DRIVER] App: " + (appPath != null ? appPath : ConfigLoader.getIOSBundleId()));
            
        } catch (MalformedURLException e) {
            System.err.println("[DRIVER] Invalid Appium server URL: " + e.getMessage());
            throw new RuntimeException("Failed to initialize iOS driver", e);
        }
    }
    
    /**
     * Gets the current AppiumDriver instance (Android or iOS).
     * @return AppiumDriver instance
     */
    public static AppiumDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            throw new IllegalStateException("Driver not initialized. Call initDriver() first.");
        }
        return driverThreadLocal.get();
    }
    
    /**
     * Gets the current AndroidDriver instance (if platform is Android).
     * @return AndroidDriver instance
     * @throws IllegalStateException if platform is not Android
     */
    public static AndroidDriver getAndroidDriver() {
        AppiumDriver driver = getDriver();
        if (driver instanceof AndroidDriver) {
            return (AndroidDriver) driver;
        }
        throw new IllegalStateException("Driver is not an AndroidDriver. Current platform: " + ConfigLoader.getPlatform());
    }
    
    /**
     * Gets the current IOSDriver instance (if platform is iOS).
     * @return IOSDriver instance
     * @throws IllegalStateException if platform is not iOS
     */
    public static IOSDriver getIOSDriver() {
        AppiumDriver driver = getDriver();
        if (driver instanceof IOSDriver) {
            return (IOSDriver) driver;
        }
        throw new IllegalStateException("Driver is not an IOSDriver. Current platform: " + ConfigLoader.getPlatform());
    }
    
    /**
     * Gets the WebDriverWait instance for explicit waits.
     * @return WebDriverWait instance
     */
    public static WebDriverWait getWait() {
        if (waitThreadLocal.get() == null) {
            throw new IllegalStateException("Wait not initialized. Call initDriver() first.");
        }
        return waitThreadLocal.get();
    }
    
    /**
     * Checks if the driver is initialized.
     * @return true if driver is initialized
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Quits the driver and cleans up resources.
     */
    public static void quitDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                String platform = ConfigLoader.getPlatform();
                System.out.println("[DRIVER] Quitting " + platform + " Driver...");
                driver.quit();
                System.out.println("[DRIVER] " + platform + " Driver quit successfully");
            } catch (Exception e) {
                System.err.println("[DRIVER] Error quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
                waitThreadLocal.remove();
            }
        }
    }
}
