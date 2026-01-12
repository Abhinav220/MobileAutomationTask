package com.saucelab.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;


public class ProductsPage extends BasePage {
    
    // Locators for Products Page elements
    private final By productsTitle = By.xpath("//android.widget.TextView[@text='PRODUCTS']");
    private final By productItems = AppiumBy.accessibilityId("test-Item");
    private final By productName = AppiumBy.accessibilityId("test-Item title");
    private final By productPrice = AppiumBy.accessibilityId("test-Price");
    private final By cartIcon = AppiumBy.accessibilityId("test-Cart");
    private final By cartBadge = AppiumBy.accessibilityId("test-Cart drop zone");
    
    // Alternative locators
    public ProductsPage() {
        super();
        System.out.println("[PRODUCTS PAGE] Initialized");
    }
    
    /**
     * Checks if the Products page is displayed.
     * @return true if Products title is visible
     */
    public boolean isProductsPageDisplayed() {
        System.out.println("[PRODUCTS PAGE] Checking if Products page is displayed");
        try {
            return isDisplayed(productsTitle);
        } catch (Exception e) {
            System.out.println("[PRODUCTS PAGE] Products title not found, checking alternatives...");
            return false;
        }
    }
    
    /**
     * Gets the page title text.
     * @return The title text
     */
    public String getPageTitle() {
        return getText(productsTitle);
    }
    
    /**
     * Gets the number of products displayed.
     * @return Count of product items
     */
    public int getProductCount() {
        List<WebElement> products = findElements(productItems);
        System.out.println("[PRODUCTS PAGE] Found " + products.size() + " products");
        return products.size();
    }
    
    /**
     * Selects a product by index (0-based).
     * @param index The index of the product to select
     * @return ProductDetailsPage instance with expected product name set
     */
    public ProductDetailsPage selectProductByIndex(int index) {
        System.out.println("[PRODUCTS PAGE] Selecting product at index: " + index);
        
        // Get product name before clicking
        String productNameText = getProductNameAtIndex(index);
        
        List<WebElement> products = findElements(productItems);
        
        if (index >= products.size()) {
            throw new IndexOutOfBoundsException("Product index " + index + " out of bounds. Found " + products.size() + " products.");
        }
        
        products.get(index).click();
        
        // Return ProductDetailsPage with expected product name
        return new ProductDetailsPage().withExpectedProduct(productNameText);
    }
    
    /**
     * Selects the first product in the list.
     * @return ProductDetailsPage instance with expected product name set
     */
    public ProductDetailsPage selectFirstProduct() {
        return selectProductByIndex(0);
    }
    
    /**
     * Selects a product by its name.
     * @param name The product name to search for
     * @return ProductDetailsPage instance with expected product name set
     */
    public ProductDetailsPage selectProductByName(String name) {
        System.out.println("[PRODUCTS PAGE] Selecting product by name: " + name);
        By productByName = By.xpath("//android.widget.TextView[@text='" + name + "']/ancestor::android.view.ViewGroup[@content-desc='test-Item']");
        click(productByName);
        return new ProductDetailsPage().withExpectedProduct(name);
    }
    
    /**
     * Gets the name of a product at the specified index.
     * @param index The index of the product
     * @return The product name
     */
    public String getProductNameAtIndex(int index) {
        List<WebElement> productNames = findElements(productName);
        if (index < productNames.size()) {
            String name = productNames.get(index).getText();
            System.out.println("[PRODUCTS PAGE] Product name at index " + index + ": " + name);
            return name;
        }
        return null;
    }
    
    /**
     * Gets the price of a product at the specified index.
     * @param index The index of the product
     * @return The product price as string (e.g., "$29.99")
     */
    public String getProductPriceAtIndex(int index) {
        List<WebElement> productPrices = findElements(productPrice);
        if (index < productPrices.size()) {
            String price = productPrices.get(index).getText();
            System.out.println("[PRODUCTS PAGE] Product price at index " + index + ": " + price);
            return price;
        }
        return null;
    }
    
    /**
     * Scrolls to find a product with specific text.
     * @param productText The text to scroll to
     */
    public void scrollToProduct(String productText) {
        System.out.println("[PRODUCTS PAGE] Scrolling to product: " + productText);
        scrollToText(productText);
    }
    
    /**
     * Navigates to the Cart page.
     * @return CartPage instance
     */
    public CartPage goToCart() {
        System.out.println("[PRODUCTS PAGE] Navigating to Cart");
        click(cartIcon);
        return new CartPage();
    }
    
    /**
     * Gets the cart badge count (number of items in cart).
     * @return The cart count as string, or "0" if badge not visible
     */
    public String getCartBadgeCount() {
        try {
            By cartCount = AppiumBy.accessibilityId("test-Cart drop zone");
            WebElement badge = waitForElement(cartCount);
            String count = badge.getText();
            System.out.println("[PRODUCTS PAGE] Cart badge count: " + count);
            return count;
        } catch (Exception e) {
            return "0";
        }
    }
}
