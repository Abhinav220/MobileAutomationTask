package com.saucelab.driver;

import com.saucelab.config.ConfigLoader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Driver Manager for handling Appium Android driver lifecycle.
 * Implements singleton pattern with ThreadLocal for parallel execution support.
 */
public class DriverManager {
    
    private static ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    
    private DriverManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initializes the AndroidDriver with configurations from config.properties.
     * Uses setApp capability to install and launch the APK from the specified path.
     */
    public static void initDriver() {
        if (driverThreadLocal.get() == null) {
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
                throw new RuntimeException("Failed to initialize driver", e);
            }
        }
    }
    
    /**
     * Gets the current AndroidDriver instance.
     * @return AndroidDriver instance
     */
    public static AndroidDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            throw new IllegalStateException("Driver not initialized. Call initDriver() first.");
        }
        return driverThreadLocal.get();
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
        AndroidDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                System.out.println("[DRIVER] Quitting Android Driver...");
                driver.quit();
                System.out.println("[DRIVER] Android Driver quit successfully");
            } catch (Exception e) {
                System.err.println("[DRIVER] Error quitting driver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
                waitThreadLocal.remove();
            }
        }
    }
}
