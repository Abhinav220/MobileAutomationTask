package com.saucelab.listeners;

import com.saucelab.annotations.XrayKey;
import com.saucelab.utils.XrayLogger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TestNG listener to automatically report test results to Xray.
 */
public class XrayListener implements ITestListener {
    
    @Override
    public void onStart(ITestContext context) {
        XrayLogger.initializeTestExecutionKey();
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        // No action needed on test start
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String xrayKey = getXrayTestKey(result);
        if (xrayKey != null && !xrayKey.isEmpty()) {
            System.out.println("[XRAY] Test PASSED: " + result.getName() + " | Xray Key: " + xrayKey);
            XrayLogger.logTestExecution(xrayKey, "PASSED", null);
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String xrayKey = getXrayTestKey(result);
        if (xrayKey != null && !xrayKey.isEmpty()) {
            System.out.println("[XRAY] Test FAILED: " + result.getName() + " | Xray Key: " + xrayKey);
            String errorMessage = result.getThrowable() != null ? 
                    result.getThrowable().getMessage() : "Test failed";
            XrayLogger.logTestExecution(xrayKey, "FAILED", errorMessage);
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String xrayKey = getXrayTestKey(result);
        if (xrayKey != null && !xrayKey.isEmpty()) {
            System.out.println("[XRAY] Test SKIPPED: " + result.getName() + " | Xray Key: " + xrayKey);
            XrayLogger.logTestExecution(xrayKey, "SKIPPED", null);
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not used
    }
    
    @Override
    public void onFinish(ITestContext context) {
        // Attach ChainTest report if available
        String chainTestReport = "target/chaintest/Index.html";
        java.io.File reportFile = new java.io.File(chainTestReport);
        if (reportFile.exists()) {
            XrayLogger.attachReportToTestExecution(chainTestReport);
        }
    }
    
    /**
     * Extracts Xray test key from test method annotations or test name.
     */
    private String getXrayTestKey(ITestResult result) {
        try {
            Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
            if (testMethod != null) {
                // Check for @XrayKey annotation on the method
                if (testMethod.isAnnotationPresent(XrayKey.class)) {
                    XrayKey xrayKeyAnnotation = testMethod.getAnnotation(XrayKey.class);
                    return xrayKeyAnnotation.value();
                }
                
                // Check for @XrayKey annotation on the class
                Class<?> testClass = testMethod.getDeclaringClass();
                if (testClass.isAnnotationPresent(XrayKey.class)) {
                    XrayKey xrayKeyAnnotation = testClass.getAnnotation(XrayKey.class);
                    return xrayKeyAnnotation.value();
                }
            }
            
            // Try to extract from test name or description
            String testName = result.getName();
            String description = result.getMethod().getDescription();
            
            String xrayKey = extractXrayKeyFromText(testName);
            if (xrayKey != null) {
                return xrayKey;
            }
            
            xrayKey = extractXrayKeyFromText(description);
            if (xrayKey != null) {
                return xrayKey;
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("[XRAY] Error extracting Xray test key: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts Xray key from text using regex pattern.
     */
    private String extractXrayKeyFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        // Pattern to match Xray keys (e.g., SAUCE-123, TONIC-12345)
        Pattern pattern = Pattern.compile("([A-Z]+)-(\\d+)");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            return matcher.group(0);
        }
        
        return null;
    }
}
