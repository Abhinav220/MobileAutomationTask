package com.saucelab.pages.web;

import com.microsoft.playwright.Page;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for SauceLabs Demo Web Cart Page.
 */
public class WebCartPage extends WebBasePage {
    
    // Locators
    private final String cartTitle = ".title";
    private final String cartItems = ".cart_item";
    private final String cartItemName = ".inventory_item_name";
    private final String cartItemPrice = ".inventory_item_price";
    private final String cartItemQuantity = ".cart_quantity";
    private final String removeButton = "button[data-test*='remove']";
    private final String continueShoppingButton = "[data-test='continue-shopping']";
    private final String checkoutButton = "[data-test='checkout']";
    
    public WebCartPage(Page page) {
        super(page);
        System.out.println("[WEB CART PAGE] Initialized");
    }
    
    /**
     * Checks if the Cart page is displayed.
     */
    public boolean isCartPageDisplayed() {
        System.out.println("[WEB CART PAGE] Checking if Cart page is displayed");
        try {
            return isVisible(cartTitle);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the number of items in the cart.
     */
    public int getCartItemCount() {
        int count = getElementCount(cartItems);
        System.out.println("[WEB CART PAGE] Cart item count: " + count);
        return count;
    }
    
    /**
     * Checks if the cart is empty.
     */
    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }
    
    /**
     * Gets the name of the first item in cart.
     */
    public String getFirstItemName() {
        List<String> itemNames = getAllLocators(cartItemName).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        if (!itemNames.isEmpty()) {
            String name = itemNames.get(0);
            System.out.println("[WEB CART PAGE] First item name: " + name);
            return name;
        }
        return null;
    }
    
    /**
     * Gets the name of an item at the specified index.
     */
    public String getItemNameAtIndex(int index) {
        List<String> itemNames = getAllLocators(cartItemName).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        if (index < itemNames.size()) {
            String name = itemNames.get(index);
            System.out.println("[WEB CART PAGE] Item name at index " + index + ": " + name);
            return name;
        }
        return null;
    }
    
    /**
     * Gets the price of the first item in cart.
     */
    public String getFirstItemPrice() {
        List<String> itemPrices = getAllLocators(cartItemPrice).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        if (!itemPrices.isEmpty()) {
            String price = itemPrices.get(0);
            System.out.println("[WEB CART PAGE] First item price: " + price);
            return price;
        }
        return null;
    }
    
    /**
     * Gets the price of an item at the specified index.
     */
    public String getItemPriceAtIndex(int index) {
        List<String> itemPrices = getAllLocators(cartItemPrice).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        if (index < itemPrices.size()) {
            String price = itemPrices.get(index);
            System.out.println("[WEB CART PAGE] Item price at index " + index + ": " + price);
            return price;
        }
        return null;
    }
    
    /**
     * Validates that a specific product is in the cart.
     */
    public boolean isProductInCart(String productName) {
        System.out.println("[WEB CART PAGE] Checking if product is in cart: " + productName);
        List<String> itemNames = getAllLocators(cartItemName).stream()
            .map(locator -> locator.textContent())
            .collect(Collectors.toList());
        
        return itemNames.contains(productName);
    }
    
    /**
     * Removes the first item from the cart.
     */
    public WebCartPage removeFirstItem() {
        System.out.println("[WEB CART PAGE] Removing first item from cart");
        List<com.microsoft.playwright.Locator> removeButtons = getAllLocators(removeButton);
        if (!removeButtons.isEmpty()) {
            removeButtons.get(0).click();
            System.out.println("[WEB CART PAGE] Item removed successfully");
        }
        return this;
    }
    
    /**
     * Removes an item at the specified index.
     */
    public WebCartPage removeItemAtIndex(int index) {
        System.out.println("[WEB CART PAGE] Removing item at index: " + index);
        List<com.microsoft.playwright.Locator> removeButtons = getAllLocators(removeButton);
        if (index < removeButtons.size()) {
            removeButtons.get(index).click();
        }
        return this;
    }
    
    /**
     * Clicks Continue Shopping to go back to Products.
     */
    public WebProductsPage continueShopping() {
        System.out.println("[WEB CART PAGE] Continuing shopping");
        click(continueShoppingButton);
        return new WebProductsPage(page);
    }
    
    /**
     * Clicks Checkout button.
     */
    public void checkout() {
        System.out.println("[WEB CART PAGE] Proceeding to checkout");
        click(checkoutButton);
    }
}
