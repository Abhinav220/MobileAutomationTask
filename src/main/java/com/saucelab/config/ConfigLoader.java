package com.saucelab.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration loader for reading properties from config.properties file.
 */
public class ConfigLoader {
    
    private static Properties properties;
    private static final String CONFIG_PATH = "src/test/resources/config.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            System.out.println("[CONFIG] Loaded configuration from: " + CONFIG_PATH);
        } catch (IOException e) {
            System.err.println("[CONFIG] Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException("Could not load configuration file", e);
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    // Convenience methods for common properties
    public static String getAppiumServerUrl() {
        return getProperty("appium.server.url", "http://127.0.0.1:4723");
    }
    
    public static String getDeviceName() {
        return getProperty("android.device.name", "emulator-5554");
    }
    
    public static String getPlatformName() {
        return getProperty("android.platform.name", "Android");
    }
    
    public static String getPlatformVersion() {
        return getProperty("android.platform.version", "13");
    }
    
    public static String getAutomationName() {
        return getProperty("android.automation.name", "UiAutomator2");
    }
    
    public static String getAppPackage() {
        return getProperty("app.package", "com.swaglabsmobileapp");
    }
    
    public static String getAppActivity() {
        return getProperty("app.activity", "com.swaglabsmobileapp.MainActivity");
    }
    
    public static String getAppPath() {
        return getProperty("app.path", "src/main/resources/app/Android.SauceLabs.Mobile.Sample.app.2.7.1.apk");
    }
    
    /**
     * Gets the absolute path to the APK file.
     * Converts relative path to absolute path based on user.dir
     */
    public static String getAbsoluteAppPath() {
        String appPath = getAppPath();
        if (appPath == null || appPath.isEmpty()) {
            return null;
        }
        // If already absolute path, return as is
        if (appPath.startsWith("/") || appPath.contains(":")) {
            return appPath;
        }
        // Convert relative path to absolute
        return System.getProperty("user.dir") + "/" + appPath;
    }
    
    public static String getTestUsername() {
        return getProperty("test.username", "standard_user");
    }
    
    public static String getTestPassword() {
        return getProperty("test.password", "secret_sauce");
    }
    
    public static int getImplicitWait() {
        return getIntProperty("implicit.wait", 10);
    }
    
    public static int getExplicitWait() {
        return getIntProperty("explicit.wait", 15);
    }
}
