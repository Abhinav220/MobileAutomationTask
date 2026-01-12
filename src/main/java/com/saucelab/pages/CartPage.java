package com.saucelab.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;


public class CartPage extends BasePage {
    
    // Locators for Cart Page elements
    private final By cartTitle = By.xpath("//android.widget.TextView[@text='YOUR CART']");
    private final By cartItems = AppiumBy.accessibilityId("test-Item");
    private final By cartItemName = AppiumBy.accessibilityId("test-Item title");
    private final By cartItemPrice = AppiumBy.accessibilityId("test-Price");
    private final By cartItemQuantity = AppiumBy.accessibilityId("test-Amount");
    private final By removeButton = AppiumBy.accessibilityId("test-REMOVE");
    private final By continueShoppingButton = AppiumBy.accessibilityId("test-CONTINUE SHOPPING");
    private final By checkoutButton = AppiumBy.accessibilityId("test-CHECKOUT");
    
    public CartPage() {
        super();
        System.out.println("[CART PAGE] Initialized");
    }
    
    /**
     * Checks if the Cart page is displayed.
     * @return true if Cart title is visible
     */
    public boolean isCartPageDisplayed() {
        System.out.println("[CART PAGE] Checking if Cart page is displayed");
        try {
            return isDisplayed(cartTitle);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the number of items in the cart.
     * @return Count of cart items
     */
    public int getCartItemCount() {
        List<WebElement> items = findElements(cartItems);
        int count = items.size();
        System.out.println("[CART PAGE] Cart item count: " + count);
        return count;
    }
    
    /**
     * Checks if the cart is empty.
     * @return true if cart has no items
     */
    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }
    
    /**
     * Gets the name of the first item in cart.
     * Uses xpath to find text element within cart item
     * @return The item name
     */
    public String getFirstItemName() {
        // Try accessibility ID first
        List<WebElement> itemNames = findElements(cartItemName);
        if (!itemNames.isEmpty()) {
            String name = itemNames.get(0).getText();
            if (name != null && !name.isEmpty()) {
                System.out.println("[CART PAGE] First item name: " + name);
                return name;
            }
        }
        
        // Fallback: Look for any TextView within cart item
        try {
            By textViewLocator = By.xpath("//android.view.ViewGroup[@content-desc='test-Item']//android.widget.TextView");
            List<WebElement> textViews = findElements(textViewLocator);
            for (WebElement tv : textViews) {
                String text = tv.getText();
                // Skip price and quantity, return the product name
                if (text != null && !text.isEmpty() && !text.startsWith("$") && !text.matches("\\d+")) {
                    System.out.println("[CART PAGE] First item name (from TextView): " + text);
                    return text;
                }
            }
        } catch (Exception e) {
            System.out.println("[CART PAGE] Could not find item name from TextView: " + e.getMessage());
        }
        
        System.out.println("[CART PAGE] Could not find item name");
        return null;
    }
    
    /**
     * Validates that a specific product is in the cart using xpath with product name.
     * @param productName The expected product name
     * @return true if the product is found in cart
     */
    public boolean isProductInCart(String productName) {
        System.out.println("[CART PAGE] Checking if product is in cart: " + productName);
        By productLocator = By.xpath(String.format("//android.widget.TextView[@text='%s']", productName));
        return isDisplayed(productLocator);
    }
    
    /**
     * Gets the name of an item at the specified index.
     * @param index The index of the item (0-based)
     * @return The item name
     */
    public String getItemNameAtIndex(int index) {
        List<WebElement> itemNames = findElements(cartItemName);
        if (index < itemNames.size()) {
            String name = itemNames.get(index).getText();
            System.out.println("[CART PAGE] Item name at index " + index + ": " + name);
            return name;
        }
        return null;
    }
    
    /**
     * Gets the price of the first item in cart.
     * @return The item price (e.g., "$29.99")
     */
    public String getFirstItemPrice() {
        List<WebElement> itemPrices = findElements(cartItemPrice);
        if (!itemPrices.isEmpty()) {
            String price = itemPrices.get(0).getText();
            System.out.println("[CART PAGE] First item price: " + price);
            return price;
        }
        return null;
    }
    
    /**
     * Gets the price of an item at the specified index.
     * @param index The index of the item (0-based)
     * @return The item price
     */
    public String getItemPriceAtIndex(int index) {
        List<WebElement> itemPrices = findElements(cartItemPrice);
        if (index < itemPrices.size()) {
            String price = itemPrices.get(index).getText();
            System.out.println("[CART PAGE] Item price at index " + index + ": " + price);
            return price;
        }
        return null;
    }
    
    /**
     * Removes the first item from the cart.
     * @return CartPage instance
     */
    public CartPage removeFirstItem() {
        System.out.println("[CART PAGE] Removing first item from cart");
        List<WebElement> removeButtons = findElements(removeButton);
        if (!removeButtons.isEmpty()) {
            removeButtons.get(0).click();
            System.out.println("[CART PAGE] Item removed successfully");
        } else {
            System.out.println("[CART PAGE] No remove button found");
        }
        return this;
    }
    
    /**
     * Removes an item at the specified index.
     * @param index The index of the item to remove (0-based)
     * @return CartPage instance
     */
    public CartPage removeItemAtIndex(int index) {
        System.out.println("[CART PAGE] Removing item at index: " + index);
        List<WebElement> removeButtons = findElements(removeButton);
        if (index < removeButtons.size()) {
            removeButtons.get(index).click();
            System.out.println("[CART PAGE] Item at index " + index + " removed");
        }
        return this;
    }
    
    /**
     * Taps Continue Shopping to go back to Products.
     * @return ProductsPage instance
     */
    public ProductsPage continueShopping() {
        System.out.println("[CART PAGE] Continuing shopping");
        click(continueShoppingButton);
        return new ProductsPage();
    }
    
    /**
     * Taps Checkout button to proceed with purchase.
     * Note: Checkout flow is not implemented in this basic version.
     */
    public void checkout() {
        System.out.println("[CART PAGE] Proceeding to checkout");
        click(checkoutButton);
    }
    
    /**
     * Validates that a specific item is in the cart.
     * @param expectedName The expected item name
     * @return true if item is found in cart
     */
    public boolean isItemInCart(String expectedName) {
        List<WebElement> itemNames = findElements(cartItemName);
        for (WebElement item : itemNames) {
            if (item.getText().equals(expectedName)) {
                System.out.println("[CART PAGE] Found item in cart: " + expectedName);
                return true;
            }
        }
        System.out.println("[CART PAGE] Item not found in cart: " + expectedName);
        return false;
    }
}
