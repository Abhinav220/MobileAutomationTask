package com.saucelab.pages.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.saucelab.config.ConfigLoader;

/**
 * Base Page class for web browser testing using Playwright.
 * Provides reusable methods for all web page objects.
 */
public abstract class WebBasePage {
    
    protected final Page page;
    
    public WebBasePage(Page page) {
        this.page = page;
    }
    
    /**
     * Gets the active Playwright Page instance.
     */
    public Page getPage() {
        return page;
    }
    
    /**
     * Clicks an element using default timeout.
     */
    protected void click(String selector) {
        click(selector, ConfigLoader.getBrowserTimeout());
    }
    
    /**
     * Clicks an element with explicit timeout.
     */
    protected void click(String selector, int timeoutMillis) {
        try {
            waitForSelector(selector, timeoutMillis);
            page.locator(selector).click();
            System.out.println("[WEB PAGE] Clicked element: " + selector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to click element: " + selector, e);
        }
    }
    
    /**
     * Fills an input field with text using default timeout.
     */
    protected void fill(String selector, String value) {
        fill(selector, value, ConfigLoader.getBrowserTimeout());
    }
    
    /**
     * Fills an input field with text with explicit timeout.
     */
    protected void fill(String selector, String value, int timeoutMillis) {
        try {
            waitForSelector(selector, timeoutMillis);
            page.locator(selector).fill(value);
            System.out.println("[WEB PAGE] Filled '" + value + "' into: " + selector);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fill field: " + selector, e);
        }
    }
    
    /**
     * Gets text content of an element using default timeout.
     */
    protected String getText(String selector) {
        return getText(selector, ConfigLoader.getBrowserTimeout());
    }
    
    /**
     * Gets text content of an element with explicit timeout.
     */
    protected String getText(String selector, int timeoutMillis) {
        try {
            waitForSelector(selector, timeoutMillis);
            String text = page.locator(selector).textContent();
            System.out.println("[WEB PAGE] Got text '" + text + "' from: " + selector);
            return text != null ? text.trim() : "";
        } catch (Exception e) {
            throw new RuntimeException("Failed to get text from: " + selector, e);
        }
    }
    
    /**
     * Checks if an element is visible using default timeout.
     */
    protected boolean isVisible(String selector) {
        return isVisible(selector, ConfigLoader.getBrowserTimeout());
    }
    
    /**
     * Checks if an element is visible with explicit timeout.
     */
    protected boolean isVisible(String selector, int timeoutMillis) {
        try {
            waitForSelector(selector, timeoutMillis);
            boolean visible = page.locator(selector).first().isVisible();
            System.out.println("[WEB PAGE] Element visible: " + visible + " - " + selector);
            return visible;
        } catch (Exception e) {
            System.out.println("[WEB PAGE] Element not visible: " + selector);
            return false;
        }
    }
    
    /**
     * Waits for an element to be visible using default timeout.
     */
    protected void waitForSelector(String selector) {
        waitForSelector(selector, ConfigLoader.getBrowserTimeout());
    }
    
    /**
     * Waits for an element to be visible with explicit timeout.
     */
    protected void waitForSelector(String selector, int timeoutMillis) {
        try {
            page.waitForSelector(selector, 
                new Page.WaitForSelectorOptions()
                    .setTimeout(timeoutMillis)
                    .setState(WaitForSelectorState.VISIBLE));
        } catch (Exception e) {
            throw new RuntimeException("Element not found: " + selector, e);
        }
    }
    
    /**
     * Gets a Locator for the given selector.
     */
    protected Locator getLocator(String selector) {
        return page.locator(selector);
    }
    
    /**
     * Gets all Locators matching the selector.
     */
    protected java.util.List<Locator> getAllLocators(String selector) {
        return page.locator(selector).all();
    }
    
    /**
     * Gets the count of elements matching the selector.
     */
    protected int getElementCount(String selector) {
        return page.locator(selector).count();
    }
    
    /**
     * Navigates to a URL.
     */
    protected void navigate(String url) {
        System.out.println("[WEB PAGE] Navigating to: " + url);
        page.navigate(url);
    }
    
    /**
     * Gets the current page URL.
     */
    protected String getCurrentUrl() {
        return page.url();
    }
    
    /**
     * Gets the page title.
     */
    protected String getTitle() {
        return page.title();
    }
    
    /**
     * Waits for page to load.
     */
    public void waitForLoadState() {
        page.waitForLoadState();
    }
    
    /**
     * Takes a screenshot.
     */
    protected void takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
    }
}
