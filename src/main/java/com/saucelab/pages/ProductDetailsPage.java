package com.saucelab.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Page Object for the Product Details Screen of SauceLabs Demo App.
 * 
 * Handles:
 * - Product details display
 * - Add to Cart functionality
 * - Back navigation
 */
public class ProductDetailsPage extends BasePage {
    
    // Locators for Product Details Page elements
    // Using %s placeholder for dynamic product name
    private static final String PRODUCT_NAME_XPATH = "//android.widget.TextView[@text='%s']";
    private final By productPrice = AppiumBy.accessibilityId("test-Price");
    private final By productDescription = AppiumBy.accessibilityId("test-Description");
    private final By addToCartButton = AppiumBy.accessibilityId("test-ADD TO CART");
    private final By removeButton = AppiumBy.accessibilityId("test-REMOVE");
    private final By backButton = AppiumBy.accessibilityId("test-BACK TO PRODUCTS");
    private final By cartIcon = AppiumBy.accessibilityId("test-Cart");
    
    // Alternative locators
    private final By addToCartButtonAlt = By.xpath("//*[@content-desc='test-ADD TO CART']");
    
    // Store the product name for validation
    private String expectedProductName;
    
    public ProductDetailsPage() {
        super();
        System.out.println("[DETAILS PAGE] Initialized");
    }
    
    /**
     * Sets the expected product name for validation.
     * @param productName The product name to expect
     * @return ProductDetailsPage instance for chaining
     */
    public ProductDetailsPage withExpectedProduct(String productName) {
        this.expectedProductName = productName;
        return this;
    }
    
    /**
     * Checks if the Product Details page is displayed for a specific product.
     * @param productName The product name to look for
     * @return true if product name is visible
     */
    public boolean isProductDetailsPageDisplayed(String productName) {
        System.out.println("[DETAILS PAGE] Checking if Product Details page is displayed for: " + productName);
        By productNameLocator = By.xpath(String.format(PRODUCT_NAME_XPATH, productName));
        return isDisplayed(productNameLocator);
    }
    
    /**
     * Checks if the Product Details page is displayed using stored product name.
     * @return true if product name is visible
     */
    public boolean isProductDetailsPageDisplayed() {
        if (expectedProductName != null) {
            return isProductDetailsPageDisplayed(expectedProductName);
        }
        // Fallback - check if Add to Cart button is visible (we're on details page)
        System.out.println("[DETAILS PAGE] Checking if Product Details page is displayed (using Add to Cart button)");
        return isDisplayed(addToCartButton);
    }
    
    /**
     * Gets the product name from the details page.
     * @param productName The expected product name to locate
     * @return The product name text
     */
    public String getProductName(String productName) {
        By productNameLocator = By.xpath(String.format(PRODUCT_NAME_XPATH, productName));
        String name = getText(productNameLocator);
        System.out.println("[DETAILS PAGE] Product name: " + name);
        return name;
    }
    
    /**
     * Gets the product name using stored expected name.
     * @return The product name
     */
    public String getProductName() {
        if (expectedProductName != null) {
            return getProductName(expectedProductName);
        }
        throw new IllegalStateException("No expected product name set. Use withExpectedProduct() or getProductName(String)");
    }
    
    /**
     * Gets the product price from the details page.
     * @return The product price (e.g., "$29.99")
     */
    public String getProductPrice() {
        String price = getText(productPrice);
        System.out.println("[DETAILS PAGE] Product price: " + price);
        return price;
    }
    
    /**
     * Gets the product description.
     * @return The product description text
     */
    public String getProductDescription() {
        String description = getText(productDescription);
        System.out.println("[DETAILS PAGE] Product description: " + description);
        return description;
    }
    
    /**
     * Taps the Add to Cart button.
     * @return ProductDetailsPage instance for method chaining
     */
    public ProductDetailsPage addToCart() {
        System.out.println("[DETAILS PAGE] Adding product to cart");
        try {
            click(addToCartButton);
        } catch (Exception e) {
            click(addToCartButtonAlt);
        }
        System.out.println("[DETAILS PAGE] Product added to cart successfully");
        return this;
    }
    
    /**
     * Checks if the Add to Cart button is displayed.
     * @return true if Add to Cart button is visible
     */
    public boolean isAddToCartButtonDisplayed() {
        return isDisplayed(addToCartButton);
    }
    
    /**
     * Checks if the Remove button is displayed (item already in cart).
     * @return true if Remove button is visible
     */
    public boolean isRemoveButtonDisplayed() {
        return isDisplayed(removeButton);
    }
    
    /**
     * Taps the Remove button to remove item from cart.
     * @return ProductDetailsPage instance
     */
    public ProductDetailsPage removeFromCart() {
        System.out.println("[DETAILS PAGE] Removing product from cart");
        click(removeButton);
        return this;
    }
    
    /**
     * Goes back to the Products list.
     * @return ProductsPage instance
     */
    public ProductsPage goBackToProducts() {
        System.out.println("[DETAILS PAGE] Going back to Products page");
        click(backButton);
        return new ProductsPage();
    }
    
    /**
     * Navigates to the Cart page.
     * @return CartPage instance
     */
    public CartPage goToCart() {
        System.out.println("[DETAILS PAGE] Navigating to Cart");
        click(cartIcon);
        return new CartPage();
    }
}
