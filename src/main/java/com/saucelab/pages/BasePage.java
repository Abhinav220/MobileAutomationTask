package com.saucelab.pages;

import com.saucelab.driver.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Base Page class containing common methods for all page objects.
 * Provides utility methods for waiting, clicking, typing, and element interactions.
 */
public abstract class BasePage {
    
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = DriverManager.getWait();
    }
    
    /**
     * Waits for element to be visible and returns it.
     */
    protected WebElement waitForElement(By locator) {
        System.out.println("[PAGE] Waiting for element: " + locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Waits for element to be clickable and returns it.
     */
    protected WebElement waitForClickable(By locator) {
        System.out.println("[PAGE] Waiting for clickable: " + locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    /**
     * Clicks on an element after waiting for it to be clickable.
     */
    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        System.out.println("[PAGE] Clicking: " + locator);
        element.click();
    }
    
    /**
     * Types text into an element after clearing it.
     */
    protected void type(By locator, String text) {
        WebElement element = waitForElement(locator);
        System.out.println("[PAGE] Typing '" + text + "' into: " + locator);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Gets the text of an element.
     */
    protected String getText(By locator) {
        WebElement element = waitForElement(locator);
        String text = element.getText();
        System.out.println("[PAGE] Got text '" + text + "' from: " + locator);
        return text;
    }
    
    /**
     * Checks if an element is displayed.
     */
    protected boolean isDisplayed(By locator) {
        try {
            WebElement element = waitForElement(locator);
            boolean displayed = element.isDisplayed();
            System.out.println("[PAGE] Element displayed: " + displayed + " - " + locator);
            return displayed;
        } catch (Exception e) {
            System.out.println("[PAGE] Element not found: " + locator);
            return false;
        }
    }
    
    /**
     * Finds all elements matching the locator.
     */
    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }
    
    /**
     * Scrolls down to find an element using UiScrollable.
     */
    protected void scrollToText(String text) {
        System.out.println("[PAGE] Scrolling to text: " + text);
        driver.findElement(AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(" +
            "new UiSelector().textContains(\"" + text + "\"))"));
    }
    
    /**
     * Scrolls down the page.
     */
    protected void scrollDown() {
        System.out.println("[PAGE] Scrolling down...");
        driver.findElement(AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
    }
    
    /**
     * Gets an attribute value from an element.
     */
    protected String getAttribute(By locator, String attribute) {
        WebElement element = waitForElement(locator);
        return element.getAttribute(attribute);
    }
}
