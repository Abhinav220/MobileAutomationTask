package com.saucelab.pages;

import com.saucelab.config.ConfigLoader;
import com.saucelab.driver.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Base Page class containing common methods for all page objects.
 * Provides utility methods for waiting, clicking, typing, and element interactions.
 * Supports both Android and iOS platforms.
 */
public abstract class BasePage {
    
    protected AppiumDriver driver;
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
     * Scrolls down to find an element by text.
     * Uses platform-specific scrolling methods.
     */
    protected void scrollToText(String text) {
        System.out.println("[PAGE] Scrolling to text: " + text);
        if (ConfigLoader.isAndroid()) {
            // Android: Use UiScrollable
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(" +
                "new UiSelector().textContains(\"" + text + "\"))"));
        } else if (ConfigLoader.isIOS()) {
            // iOS: Use predicate string to find element by text
            try {
                driver.findElement(AppiumBy.iOSNsPredicateString("name CONTAINS '" + text + "' OR label CONTAINS '" + text + "'"));
            } catch (Exception e) {
                // If element not found, perform a scroll gesture
                scrollDown();
                // Try again after scroll
                try {
                    driver.findElement(AppiumBy.iOSNsPredicateString("name CONTAINS '" + text + "' OR label CONTAINS '" + text + "'"));
                } catch (Exception e2) {
                    System.out.println("[PAGE] Element with text '" + text + "' not found after scrolling");
                }
            }
        }
    }
    
    /**
     * Scrolls down the page.
     * Uses platform-specific scrolling methods.
     */
    protected void scrollDown() {
        System.out.println("[PAGE] Scrolling down...");
        if (ConfigLoader.isAndroid()) {
            // Android: Use UiScrollable
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
        } else if (ConfigLoader.isIOS()) {
            // iOS: Use W3C Actions API for scrolling
            scrollIOS(Direction.DOWN);
        }
    }
    
    /**
     * Scrolls up the page.
     * Uses platform-specific scrolling methods.
     */
    protected void scrollUp() {
        System.out.println("[PAGE] Scrolling up...");
        if (ConfigLoader.isAndroid()) {
            // Android: Use UiScrollable
            driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollBackward()"));
        } else if (ConfigLoader.isIOS()) {
            // iOS: Use W3C Actions API for scrolling
            scrollIOS(Direction.UP);
        }
    }
    
    /**
     * Enum for scroll directions.
     */
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    
    /**
     * Performs iOS scrolling using W3C Actions API.
     * @param direction The direction to scroll
     */
    private void scrollIOS(Direction direction) {
        Dimension size = driver.manage().window().getSize();
        Point start = new Point(size.width / 2, size.height / 2);
        Point end = new Point(start.x, start.y);
        
        switch (direction) {
            case UP:
                end.y = (int) (size.height * 0.25);
                break;
            case DOWN:
                end.y = (int) (size.height * 0.75);
                break;
            case LEFT:
                end.x = (int) (size.width * 0.25);
                break;
            case RIGHT:
                end.x = (int) (size.width * 0.75);
                break;
        }
        
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1);
        scroll.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
        scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        scroll.addAction(new Pause(finger, Duration.ofMillis(200)));
        scroll.addAction(finger.createPointerMove(Duration.ofMillis(300), PointerInput.Origin.viewport(), end.x, end.y));
        scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(scroll));
    }
    
    /**
     * Gets an attribute value from an element.
     */
    protected String getAttribute(By locator, String attribute) {
        WebElement element = waitForElement(locator);
        return element.getAttribute(attribute);
    }
    
    /**
     * Creates a platform-aware XPath locator for text matching.
     * Android: uses android.widget.TextView
     * iOS: uses XCUIElementTypeStaticText
     */
    protected By getTextLocator(String text) {
        if (ConfigLoader.isAndroid()) {
            return By.xpath("//android.widget.TextView[@text='" + text + "']");
        } else if (ConfigLoader.isIOS()) {
            return By.xpath("//XCUIElementTypeStaticText[@name='" + text + "' or @label='" + text + "']");
        }
        throw new RuntimeException("Unsupported platform: " + ConfigLoader.getPlatform());
    }
    
    /**
     * Creates a platform-aware XPath locator for text containing.
     * Android: uses android.widget.TextView
     * iOS: uses XCUIElementTypeStaticText
     */
    protected By getTextContainsLocator(String text) {
        if (ConfigLoader.isAndroid()) {
            return By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
        } else if (ConfigLoader.isIOS()) {
            return By.xpath("//XCUIElementTypeStaticText[contains(@name, '" + text + "') or contains(@label, '" + text + "')]");
        }
        throw new RuntimeException("Unsupported platform: " + ConfigLoader.getPlatform());
    }
}
