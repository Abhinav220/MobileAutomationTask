package com.saucelab.pages.web;

import com.microsoft.playwright.Page;

/**
 * Page Object for SauceLabs Demo Web Product Details Page.
 */
public class WebProductDetailsPage extends WebBasePage {
    
    // Locators
    private final String productName = ".inventory_details_name";
    private final String productPrice = ".inventory_details_price";
    private final String productDescription = ".inventory_details_desc";
    private final String addToCartButton = "button[data-test*='add-to-cart']";
    private final String removeButton = "button[data-test*='remove']";
    private final String backButton = "[data-test='back-to-products']";
    private final String cartIcon = ".shopping_cart_link";
    
    // Store the expected product name for validation
    private String expectedProductName;
    
    public WebProductDetailsPage(Page page) {
        super(page);
        System.out.println("[WEB DETAILS PAGE] Initialized");
    }
    
    /**
     * Sets the expected product name for validation.
     */
    public WebProductDetailsPage withExpectedProduct(String productName) {
        this.expectedProductName = productName;
        return this;
    }
    
    /**
     * Checks if the Product Details page is displayed.
     */
    public boolean isProductDetailsPageDisplayed() {
        System.out.println("[WEB DETAILS PAGE] Checking if Product Details page is displayed");
        return isVisible(productName) || isVisible(addToCartButton);
    }
    
    /**
     * Gets the product name from the details page.
     */
    public String getProductName() {
        String name = getText(productName);
        System.out.println("[WEB DETAILS PAGE] Product name: " + name);
        return name;
    }
    
    /**
     * Gets the product price from the details page.
     */
    public String getProductPrice() {
        String price = getText(productPrice);
        System.out.println("[WEB DETAILS PAGE] Product price: " + price);
        return price;
    }
    
    /**
     * Gets the product description.
     */
    public String getProductDescription() {
        String description = getText(productDescription);
        System.out.println("[WEB DETAILS PAGE] Product description: " + description);
        return description;
    }
    
    /**
     * Clicks the Add to Cart button.
     */
    public WebProductDetailsPage addToCart() {
        System.out.println("[WEB DETAILS PAGE] Adding product to cart");
        click(addToCartButton);
        System.out.println("[WEB DETAILS PAGE] Product added to cart successfully");
        return this;
    }
    
    /**
     * Checks if the Add to Cart button is displayed.
     */
    public boolean isAddToCartButtonDisplayed() {
        return isVisible(addToCartButton);
    }
    
    /**
     * Checks if the Remove button is displayed.
     */
    public boolean isRemoveButtonDisplayed() {
        return isVisible(removeButton);
    }
    
    /**
     * Clicks the Remove button.
     */
    public WebProductDetailsPage removeFromCart() {
        System.out.println("[WEB DETAILS PAGE] Removing product from cart");
        click(removeButton);
        return this;
    }
    
    /**
     * Navigates back to Products page.
     */
    public WebProductsPage goBackToProducts() {
        System.out.println("[WEB DETAILS PAGE] Going back to Products");
        click(backButton);
        return new WebProductsPage(page);
    }
    
    /**
     * Navigates to the Cart page.
     */
    public WebCartPage goToCart() {
        System.out.println("[WEB DETAILS PAGE] Navigating to Cart");
        click(cartIcon);
        return new WebCartPage(page);
    }
}
