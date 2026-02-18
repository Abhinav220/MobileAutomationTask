package com.saucelab.factory;

import com.saucelab.config.ConfigLoader;
import com.microsoft.playwright.*;
import java.util.Properties;

/**
 * Factory class to initialize and manage Playwright browser instances, contexts, and pages
 * using ThreadLocal for parallel execution.
 */
public class PlaywrightFactory {

    private static final ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();

    public static Playwright getPlaywright() { return tlPlaywright.get(); }
    public static Browser getBrowser() { return tlBrowser.get(); }
    public static BrowserContext getBrowserContext() { return tlBrowserContext.get(); }
    public static Page getPage() { return tlPage.get(); }

    public static void setPlaywright(Playwright playwright) { tlPlaywright.set(playwright); }
    public static void setBrowser(Browser browser) { tlBrowser.set(browser); }
    public static void setPage(Page page) { tlPage.set(page); }
    public static void setBrowserContext(BrowserContext context) { tlBrowserContext.set(context); }

    /**
     * Opens a new tab and returns the Page object.
     */
    public static Page newTab() {
        BrowserContext context = getBrowserContext();
        if (context == null) {
            throw new IllegalStateException("BrowserContext is null. initBrowser() must run before opening a new tab.");
        }
        Page newPage = context.newPage();
        setPage(newPage);
        return newPage;
    }

    /**
     * Switches to a specific page.
     */
    public static void switchTo(Page targetPage) {
        if (targetPage == null) throw new IllegalArgumentException("Target page is null");
        setPage(targetPage);
        try { targetPage.bringToFront(); } catch (Exception ignored) {}
    }

    /**
     * Closes the current page.
     */
    public static void closeCurrentPage() {
        Page p = getPage();
        if (p != null && !p.isClosed()) {
            p.close();
        }
    }

    /**
     * Initializes the browser with configurations from config.properties.
     */
    public Page initBrowser(Properties prop) {
        if (getPage() != null) {
            System.out.println("[PlaywrightFactory] Page already exists for thread " + 
                    Thread.currentThread().getId() + ". Skipping browser creation.");
            return getPage();
        }

        String browserName = ConfigLoader.getBrowserName().trim().toLowerCase();

        System.out.println("=== BROWSER LAUNCH TRACKING ===");
        System.out.println("Thread ID: " + Thread.currentThread().getId());
        System.out.println("Thread Name: " + Thread.currentThread().getName());
        System.out.println("Launching browser: " + browserName);

        ensurePlaywright();
        ensureBrowser(browserName);
        createContextAndPage();

        String url = ConfigLoader.getWebAppUrl();
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing web app URL in config.properties.");
        }

        System.out.println("=== NAVIGATING TO URL ===");
        System.out.println("Thread ID: " + Thread.currentThread().getId());
        System.out.println("Navigating to URL: " + url);
        getPage().navigate(url);

        System.out.println("=== BROWSER INITIALIZATION COMPLETED ===");
        System.out.println("Thread ID: " + Thread.currentThread().getId());
        System.out.println("Browser ready for test execution");

        return getPage();
    }

    private void ensurePlaywright() {
        if (getPlaywright() == null) {
            tlPlaywright.set(Playwright.create());
            System.out.println("[PlaywrightFactory] Created Playwright for thread " + Thread.currentThread().getId());
        }
    }

    private void ensureBrowser(String browserName) {
        if (getBrowser() != null) return;

        boolean isHeadless = ConfigLoader.isHeadlessMode();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(isHeadless);

        switch (browserName) {
            case "chromium":
                tlBrowser.set(getPlaywright().chromium().launch(options));
                break;
            case "chrome":
                options.setChannel("chrome");
                tlBrowser.set(getPlaywright().chromium().launch(options));
                break;
            case "edge":
                options.setChannel("msedge");
                tlBrowser.set(getPlaywright().chromium().launch(options));
                break;
            case "firefox":
                tlBrowser.set(getPlaywright().firefox().launch(options));
                break;
            case "safari":
            case "webkit":
                tlBrowser.set(getPlaywright().webkit().launch(options));
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }

        System.out.println("[PlaywrightFactory] Browser instance created for thread " + Thread.currentThread().getId());
    }

    private void createContextAndPage() {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        
        // Set viewport size if configured
        String viewportSize = ConfigLoader.getViewportSize();
        if (viewportSize != null && !viewportSize.isEmpty()) {
            String[] dimensions = viewportSize.split("x");
            if (dimensions.length == 2) {
                try {
                    int width = Integer.parseInt(dimensions[0].trim());
                    int height = Integer.parseInt(dimensions[1].trim());
                    contextOptions.setViewportSize(width, height);
                } catch (NumberFormatException e) {
                    System.err.println("[PlaywrightFactory] Invalid viewport size format: " + viewportSize);
                }
            }
        }

        if (getBrowserContext() == null) {
            tlBrowserContext.set(getBrowser().newContext(contextOptions));
        }
        if (getPage() == null) {
            tlPage.set(getBrowserContext().newPage());
        }
    }

    public Properties initProp() {
        Properties prop = new Properties();
        try {
            java.io.FileInputStream ip = new java.io.FileInputStream("src/test/resources/config.properties");
            prop.load(ip);
            System.out.println("Loaded config properties from: src/test/resources/config.properties");
        } catch (java.io.IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
        }
        return prop;
    }

    /**
     * Cleans up browser resources.
     */
    public static void cleanup() {
        System.out.println("=== BROWSER CLEANUP TRACKING ===");
        System.out.println("Thread ID: " + Thread.currentThread().getId());
        System.out.println("Thread Name: " + Thread.currentThread().getName());

        try {
            Page page = getPage();
            if (page != null) {
                try { page.close(); } catch (Exception e) { 
                    System.err.println("Error closing page: " + e.getMessage()); 
                }
            }

            BrowserContext context = getBrowserContext();
            if (context != null) {
                try { context.close(); } catch (Exception e) { 
                    System.err.println("Error closing browser context: " + e.getMessage()); 
                }
            }

            Browser browser = getBrowser();
            if (browser != null) {
                try { browser.close(); } catch (Exception e) { 
                    System.err.println("Error closing browser: " + e.getMessage()); 
                }
            }

            Playwright playwright = getPlaywright();
            if (playwright != null) {
                try { playwright.close(); } catch (Exception e) { 
                    System.err.println("Error closing playwright: " + e.getMessage()); 
                }
            }

            System.out.println("=== CLEANUP COMPLETED ===");
            System.out.println("Thread ID: " + Thread.currentThread().getId());

        } finally {
            tlPage.remove();
            tlBrowserContext.remove();
            tlBrowser.remove();
            tlPlaywright.remove();
        }
    }

    /**
     * Clears ThreadLocal references.
     */
    public static void clearThreadLocals() {
        System.out.println("Clearing ThreadLocal references for Thread ID: " + Thread.currentThread().getId());
        tlPage.remove();
        tlBrowserContext.remove();
        tlBrowser.remove();
        tlPlaywright.remove();
    }
}
