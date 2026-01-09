package com.saucelab.tests;

import com.saucelab.config.ConfigLoader;
import com.saucelab.driver.DriverManager;
import com.saucelab.pages.*;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * End-to-End Shopping Flow Test for SauceLabs Demo App.
 * 
 * Test Flow:
 * 1. Login with valid credentials
 * 2. Verify Products page is displayed
 * 3. Select a product and view details
 * 4. Add product to cart
 * 5. Navigate to cart and verify item
 * 6. Remove from cart and verify empty
 * 7. Price validation across pages
 * 
 * @author Abhinav Singh
 */
public class ShoppingFlowTest {
    
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private ProductDetailsPage detailsPage;
    private CartPage cartPage;
    
    // Variables to store product info for validation
    private String selectedProductName;
    private String selectedProductPrice;
    
    @BeforeClass
    public void setUp() {
        System.out.println("\n========================================");
        System.out.println("  SAUCELAB MOBILE AUTOMATION TEST");
        System.out.println("========================================\n");
        
        // Initialize driver
        DriverManager.initDriver();
        System.out.println("[SETUP] Test setup completed\n");
    }
    
    @AfterClass
    public void tearDown() {
        System.out.println("\n[TEARDOWN] Cleaning up...");
        DriverManager.quitDriver();
        System.out.println("[TEARDOWN] Test teardown completed\n");
    }
    
    // ==================== PART 1: LOGIN ====================
    
    @Test(priority = 1, description = "Login with valid credentials and verify Products page")
    public void testLogin() {
        System.out.println("\n--- TEST: Login ---");
        
        // Initialize Login Page
        loginPage = new LoginPage();
        
        // Get credentials from config
        String username = ConfigLoader.getTestUsername();
        String password = ConfigLoader.getTestPassword();
        
        System.out.println("[TEST] Logging in with username: " + username);
        
        // Perform login
        productsPage = loginPage.login(username, password);
        
        // ASSERTION: Verify Products page is displayed
        Assert.assertTrue(productsPage.isProductsPageDisplayed(), 
            "ASSERTION FAILED: Products page should be displayed after login");
        
        System.out.println("[TEST] ✓ Login successful - Products page is displayed");
        System.out.println("[TEST] Page title: " + productsPage.getPageTitle());
    }
    
    // ==================== PART 2: PRODUCT LIST ====================
    
    @Test(priority = 2, dependsOnMethods = "testLogin", 
          description = "Select a product from the list and open details")
    public void testSelectProduct() {
        System.out.println("\n--- TEST: Select Product ---");
        
        // Store product info before selecting (for later validation)
        selectedProductName = productsPage.getProductNameAtIndex(0);
        selectedProductPrice = productsPage.getProductPriceAtIndex(0);
        
        System.out.println("[TEST] Selected product: " + selectedProductName);
        System.out.println("[TEST] Selected price: " + selectedProductPrice);
        
        // Select the first product (this sets expectedProductName in ProductDetailsPage)
        detailsPage = productsPage.selectFirstProduct();
        
        // ASSERTION: Verify Product Details page is displayed with correct product name
        Assert.assertTrue(detailsPage.isProductDetailsPageDisplayed(selectedProductName), 
            "ASSERTION FAILED: Product Details page should be displayed with product: " + selectedProductName);
        
        // ASSERTION: Verify product name matches
        String detailsName = detailsPage.getProductName(selectedProductName);
        Assert.assertEquals(detailsName, selectedProductName, 
            "ASSERTION FAILED: Product name on details page should match selected product");
        
        System.out.println("[TEST] ✓ Product details page displayed correctly");
        System.out.println("[TEST] Product name verified: " + detailsName);
    }
    
    // ==================== PART 3: ADD TO CART ====================
    
    @Test(priority = 3, dependsOnMethods = "testSelectProduct", 
          description = "Add product to cart and verify in cart")
    public void testAddToCart() {
        System.out.println("\n--- TEST: Add to Cart ---");
        
        // Add product to cart
        detailsPage.addToCart();
        
        // ASSERTION: Remove button should appear (indicates item was added)
        Assert.assertTrue(detailsPage.isRemoveButtonDisplayed(), 
            "ASSERTION FAILED: Remove button should be displayed after adding to cart");
        
        System.out.println("[TEST] ✓ Product added to cart - Remove button visible");
        
        // Navigate to cart
        cartPage = detailsPage.goToCart();
        
        // ASSERTION: Cart page is displayed
        Assert.assertTrue(cartPage.isCartPageDisplayed(), 
            "ASSERTION FAILED: Cart page should be displayed");
        
        // ASSERTION: Cart has 1 item
        int cartCount = cartPage.getCartItemCount();
        Assert.assertEquals(cartCount, 1, 
            "ASSERTION FAILED: Cart should have exactly 1 item");
        
        // ASSERTION: Product is in cart (using product name xpath)
        Assert.assertTrue(cartPage.isProductInCart(selectedProductName), 
            "ASSERTION FAILED: Product '" + selectedProductName + "' should be in cart");
        
        System.out.println("[TEST] ✓ Cart verified - Contains: " + selectedProductName);
        System.out.println("[TEST] ✓ Cart count: " + cartCount);
    }
    
    // ==================== BONUS A: REMOVE FROM CART ====================
    
    @Test(priority = 4, dependsOnMethods = "testAddToCart", 
          description = "BONUS: Remove item from cart and verify empty")
    public void testRemoveFromCart() {
        System.out.println("\n--- TEST (BONUS A): Remove from Cart ---");
        
        // Remove the item
        cartPage.removeFirstItem();
        
        // Wait a moment for UI update
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        // ASSERTION: Cart should be empty
        Assert.assertTrue(cartPage.isCartEmpty(), 
            "ASSERTION FAILED: Cart should be empty after removing item");
        
        System.out.println("[TEST] ✓ Item removed - Cart is now empty");
    }
    
    // ==================== BONUS B: PRICE VALIDATION ====================
    
    @Test(priority = 5, dependsOnMethods = "testRemoveFromCart", 
          description = "BONUS: Add item again and validate price across pages")
    public void testPriceValidation() {
        System.out.println("\n--- TEST (BONUS B): Price Validation ---");
        
        // Go back to products and add item again
        productsPage = cartPage.continueShopping();
        
        // Get price from product list
        String listPrice = productsPage.getProductPriceAtIndex(0);
        System.out.println("[TEST] Price on Products page: " + listPrice);
        
        // Go to details
        detailsPage = productsPage.selectFirstProduct();
        String detailsPrice = detailsPage.getProductPrice();
        System.out.println("[TEST] Price on Details page: " + detailsPrice);
        
        // ASSERTION: Price should match on list and details
        Assert.assertEquals(detailsPrice, listPrice, 
            "ASSERTION FAILED: Price should be same on Products and Details pages");
        
        // Add to cart
        detailsPage.addToCart();
        
        // Go to cart
        cartPage = detailsPage.goToCart();
        String cartPrice = cartPage.getFirstItemPrice();
        System.out.println("[TEST] Price on Cart page: " + cartPrice);
        
        // ASSERTION: Price should match in cart
        Assert.assertEquals(cartPrice, listPrice, 
            "ASSERTION FAILED: Price should be same in Cart as on Products page");
        
        System.out.println("[TEST] ✓ Price validated across all pages: " + listPrice);
    }

}
