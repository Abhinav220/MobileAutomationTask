package com.saucelab.pages.web;

import com.microsoft.playwright.Page;

/**
 * Page Object for SauceLabs Demo Web Login Page.
 */
public class WebLoginPage extends WebBasePage {
    
    // Locators
    private final String usernameField = "#user-name";
    private final String passwordField = "#password";
    private final String loginButton = "#login-button";
    private final String errorMessage = "[data-test='error']";
    
    public WebLoginPage(Page page) {
        super(page);
        System.out.println("[WEB LOGIN PAGE] Initialized");
    }
    
    /**
     * Enters username into the username field.
     */
    public WebLoginPage enterUsername(String username) {
        System.out.println("[WEB LOGIN PAGE] Entering username: " + username);
        fill(usernameField, username);
        return this;
    }
    
    /**
     * Enters password into the password field.
     */
    public WebLoginPage enterPassword(String password) {
        System.out.println("[WEB LOGIN PAGE] Entering password: ****");
        fill(passwordField, password);
        return this;
    }
    
    /**
     * Clicks the Login button.
     */
    public WebProductsPage clickLogin() {
        System.out.println("[WEB LOGIN PAGE] Clicking Login button");
        click(loginButton);
        return new WebProductsPage(page);
    }
    
    /**
     * Performs complete login flow with provided credentials.
     */
    public WebProductsPage login(String username, String password) {
        System.out.println("[WEB LOGIN PAGE] Performing login with username: " + username);
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    /**
     * Checks if error message is displayed.
     */
    public boolean isErrorMessageDisplayed() {
        return isVisible(errorMessage);
    }
    
    /**
     * Gets the error message text.
     */
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return getText(errorMessage);
        }
        return "";
    }
}
