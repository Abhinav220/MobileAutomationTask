package com.saucelab.pages.web;

import com.microsoft.playwright.Page;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for SauceLabs Demo Web Products Page.
 */
public class WebProductsPage extends WebBasePage {
    
    // Locators
    private final String productsTitle = ".title";
    private final String productItems = ".inventory_item";
    private final String productName = ".inventory_item_name";
    private final String productPrice = ".inventory_item_price";
    private final String addToCartButton = "button[data-test*='add-to-cart']";
    private final String removeButton = "button[data-test*='remove']";
    private final String cartIcon = ".shopping_cart_link";
    private final String cartBadge = ".shopping_cart_badge";
    
    public WebProductsPage(Page page) {
        super(page);
        System.out.println("[WEB PRODUCTS PAGE] Initialized");
    }
    
    /**
     * Checks if the Products page is displayed.
     */
    public boolean isProductsPageDisplayed() {
        System.out.println("[WEB PRODUCTS PAGE] Checking if Products page is displayed");
        try {
            return isVisible(productsTitle);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the page title text.
     */
    public String getPageTitle() {
        return getText(productsTitle);
    }
    
    /**
     * Gets the number of products displayed.
     */
    public int getProductCount() {
        int count = getElementCount(productItems);
        System.out.println("[WEB PRODUCTS PAGE] Found " + count + " products");
        return count;
    }
    
    /**
     * Gets the name of a product at the specified index.
     */
    public String getProductNameAtIndex(int index) {
        List<String> names = getAllLocators(productName).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        if (index < names.size()) {
            String name = names.get(index);
            System.out.println("[WEB PRODUCTS PAGE] Product name at index " + index + ": " + name);
            return name;
        }
        return null;
    }
    
    /**
     * Gets the price of a product at the specified index.
     */
    public String getProductPriceAtIndex(int index) {
        List<String> prices = getAllLocators(productPrice).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        if (index < prices.size()) {
            String price = prices.get(index);
            System.out.println("[WEB PRODUCTS PAGE] Product price at index " + index + ": " + price);
            return price;
        }
        return null;
    }
    
    /**
     * Clicks on a product by index to view details.
     */
    public WebProductDetailsPage clickProductByIndex(int index) {
        System.out.println("[WEB PRODUCTS PAGE] Clicking product at index: " + index);
        
        String productNameText = getProductNameAtIndex(index);
        List<com.microsoft.playwright.Locator> products = getAllLocators(productName);
        
        if (index >= products.size()) {
            throw new IndexOutOfBoundsException("Product index " + index + " out of bounds. Found " + products.size() + " products.");
        }
        
        products.get(index).click();
        
        return new WebProductDetailsPage(page).withExpectedProduct(productNameText);
    }
    
    /**
     * Clicks the first product.
     */
    public WebProductDetailsPage clickFirstProduct() {
        return clickProductByIndex(0);
    }
    
    /**
     * Clicks Add to Cart button for a product by index.
     */
    public WebProductsPage addToCartByIndex(int index) {
        System.out.println("[WEB PRODUCTS PAGE] Adding product to cart at index: " + index);
        List<com.microsoft.playwright.Locator> addButtons = getAllLocators(addToCartButton);
        if (index < addButtons.size()) {
            addButtons.get(index).click();
        }
        return this;
    }
    
    /**
     * Navigates to the Cart page.
     */
    public WebCartPage goToCart() {
        System.out.println("[WEB PRODUCTS PAGE] Navigating to Cart");
        click(cartIcon);
        return new WebCartPage(page);
    }
    
    /**
     * Gets the cart badge count.
     */
    public String getCartBadgeCount() {
        try {
            if (isVisible(cartBadge)) {
                String count = getText(cartBadge);
                System.out.println("[WEB PRODUCTS PAGE] Cart badge count: " + count);
                return count;
            }
        } catch (Exception e) {
            // Badge not visible means cart is empty
        }
        return "0";
    }
}
