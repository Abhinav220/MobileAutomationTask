package com.saucelab.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;


public class LoginPage extends BasePage {
    
    // Locators for Login Page elements
    private final By usernameField = AppiumBy.accessibilityId("test-Username");
    private final By passwordField = AppiumBy.accessibilityId("test-Password");
    private final By loginButton = AppiumBy.accessibilityId("test-LOGIN");
    private final By errorMessage = AppiumBy.accessibilityId("test-Error message");
    
    public LoginPage() {
        super();
        System.out.println("[LOGIN PAGE] Initialized");
    }
    
    /**
     * Enters username into the username field.
     * @param username The username to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterUsername(String username) {
        System.out.println("[LOGIN PAGE] Entering username: " + username);
        type(usernameField, username);
        return this;
    }
    
    /**
     * Enters password into the password field.
     * @param password The password to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterPassword(String password) {
        System.out.println("[LOGIN PAGE] Entering password: ****");
        type(passwordField, password);
        return this;
    }
    
    /**
     * Taps the Login button.
     * @return ProductsPage instance after successful login
     */
    public ProductsPage tapLogin() {
        System.out.println("[LOGIN PAGE] Tapping Login button");
        click(loginButton);
        return new ProductsPage();
    }
    
    /**
     * Performs complete login flow with provided credentials.
     * @param username The username to login with
     * @param password The password to login with
     * @return ProductsPage instance after successful login
     */
    public ProductsPage login(String username, String password) {
        System.out.println("[LOGIN PAGE] Performing login with username: " + username);
        enterUsername(username);
        enterPassword(password);
        return tapLogin();
    }
}
