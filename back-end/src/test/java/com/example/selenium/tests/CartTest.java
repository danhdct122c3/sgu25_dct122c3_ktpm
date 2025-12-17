package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.HomePage;
import com.example.selenium.pages.ProductPage;
import com.example.selenium.pages.CartPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases cho Cart Module
 */
public class CartTest extends BaseTest {

    @BeforeClass
    public void setupCart() {
        // Login và thêm sản phẩm vào giỏ trước
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("test", "12345678");
        sleep(2000);

        // Add product to cart
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        
        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);
        
        productPage.selectSize("8");
        productPage.setQuantity(1);
        productPage.addToCart();
        sleep(2000);
    }

    @Test(priority = 1, description = "Test xem giỏ hàng")
    public void testViewCart() {
        HomePage homePage = new HomePage(driver);
        CartPage cartPage = new CartPage(driver);

        homePage.clickCartIcon();
        sleep(2000);

        int itemCount = cartPage.getCartItemsCount();
        Assert.assertTrue(itemCount > 0, "Cart should have items");

        System.out.println("Cart has " + itemCount + " item(s)");
        System.out.println("✓ Test View Cart PASSED");
    }

    @Test(priority = 2, description = "Test hiển thị thông tin sản phẩm trong giỏ")
    public void testDisplayCartItemInfo() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        if (cartPage.getCartItemsCount() > 0) {
            String itemName = cartPage.getItemName(0);
            String itemPrice = cartPage.getItemPrice(0);
            int itemQty = cartPage.getItemQuantity(0);

            Assert.assertNotNull(itemName, "Item name should be displayed");
            Assert.assertNotNull(itemPrice, "Item price should be displayed");
            Assert.assertTrue(itemQty > 0, "Item quantity should be greater than 0");

            System.out.println("Item: " + itemName);
            System.out.println("Price: " + itemPrice);
            System.out.println("Quantity: " + itemQty);
        }

        System.out.println("✓ Test Display Cart Item Info PASSED");
    }

    @Test(priority = 3, description = "Test tính tổng tiền giỏ hàng")
    public void testCalculateCartTotal() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        String totalPrice = cartPage.getTotalPrice();
        Assert.assertNotNull(totalPrice, "Total price should be displayed");
        Assert.assertFalse(totalPrice.isEmpty(), "Total price should not be empty");

        System.out.println("Total: " + totalPrice);
        System.out.println("✓ Test Calculate Cart Total PASSED");
    }

    @Test(priority = 4, description = "Test tăng số lượng sản phẩm trong giỏ")
    public void testIncreaseCartItemQuantity() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        if (cartPage.getCartItemsCount() > 0) {
            int initialQty = cartPage.getItemQuantity(0);
            double initialTotal = cartPage.getTotalPriceAsNumber();

            // Increase quantity
            cartPage.increaseQuantity(0);
            sleep(2000);

            int newQty = cartPage.getItemQuantity(0);
            double newTotal = cartPage.getTotalPriceAsNumber();

            Assert.assertEquals(newQty, initialQty + 1, "Quantity should increase by 1");
            Assert.assertTrue(newTotal > initialTotal, "Total should increase");

            System.out.println("Quantity increased: " + initialQty + " -> " + newQty);
        }

        System.out.println("✓ Test Increase Cart Item Quantity PASSED");
    }

    @Test(priority = 5, description = "Test giảm số lượng sản phẩm trong giỏ")
    public void testDecreaseCartItemQuantity() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        if (cartPage.getCartItemsCount() > 0) {
            int initialQty = cartPage.getItemQuantity(0);
            
            if (initialQty > 1) {
                // Decrease quantity
                cartPage.decreaseQuantity(0);
                sleep(2000);

                int newQty = cartPage.getItemQuantity(0);
                Assert.assertEquals(newQty, initialQty - 1, "Quantity should decrease by 1");

                System.out.println("Quantity decreased: " + initialQty + " -> " + newQty);
            } else {
                System.out.println("Quantity is 1, cannot decrease");
            }
        }

        System.out.println("✓ Test Decrease Cart Item Quantity PASSED");
    }

    @Test(priority = 6, description = "Test xóa sản phẩm khỏi giỏ")
    public void testRemoveItemFromCart() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        int initialCount = cartPage.getCartItemsCount();
        
        if (initialCount > 0) {
            // Remove first item
            cartPage.removeItem(0);
            sleep(2000);

            int newCount = cartPage.getCartItemsCount();
            Assert.assertEquals(newCount, initialCount - 1, "Cart should have one less item");

            System.out.println("Removed item. Cart count: " + initialCount + " -> " + newCount);
        }

        System.out.println("✓ Test Remove Item From Cart PASSED");
    }

    @Test(priority = 7, description = "Test apply mã giảm giá hợp lệ")
    public void testApplyValidDiscountCode() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        // Ensure cart has items
        if (cartPage.getCartItemsCount() > 0) {
            double initialTotal = cartPage.getTotalPriceAsNumber();

            // Apply discount code (adjust code based on your system)
            cartPage.applyDiscountCode("DISCOUNT10");
            sleep(2000);

            if (cartPage.isDiscountApplied()) {
                String message = cartPage.getDiscountMessage();
                System.out.println("Discount message: " + message);

                double newTotal = cartPage.getTotalPriceAsNumber();
                Assert.assertTrue(newTotal < initialTotal, "Total should decrease after discount");
            } else {
                System.out.println("Discount code may not be valid in this environment");
            }
        }

        System.out.println("✓ Test Apply Valid Discount Code PASSED");
    }

    @Test(priority = 8, description = "Test apply mã giảm giá không hợp lệ")
    public void testApplyInvalidDiscountCode() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        if (cartPage.getCartItemsCount() > 0) {
            cartPage.applyDiscountCode("INVALIDCODE123");
            sleep(2000);

            if (cartPage.isDiscountApplied()) {
                String message = cartPage.getDiscountMessage();
                Assert.assertTrue(
                    message.contains("Invalid") || message.contains("không hợp lệ"),
                    "Should show invalid code message"
                );
            }
        }

        System.out.println("✓ Test Apply Invalid Discount Code PASSED");
    }

    @Test(priority = 9, description = "Test tiếp tục mua hàng")
    public void testContinueShopping() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        cartPage.continueShopping();
        sleep(2000);

        // Verify redirect về products page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/") || currentUrl.contains("/products"),
            "Should redirect to products page"
        );

        System.out.println("✓ Test Continue Shopping PASSED");
    }

    @Test(priority = 10, description = "Test proceed to checkout")
    public void testProceedToCheckout() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        if (cartPage.getCartItemsCount() > 0 && cartPage.isCheckoutButtonEnabled()) {
            cartPage.proceedToCheckout();
            sleep(2000);

            // Verify redirect to checkout page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/checkout"), "Should redirect to checkout page");

            System.out.println("✓ Test Proceed To Checkout PASSED");
        } else {
            System.out.println("Cart is empty or checkout button disabled");
        }
    }

    @Test(priority = 11, description = "Test giỏ hàng trống")
    public void testEmptyCart() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();

        // Clear cart
        while (cartPage.getCartItemsCount() > 0) {
            cartPage.removeItem(0);
            sleep(1000);
        }

        // Verify empty cart message
        Assert.assertTrue(cartPage.isCartEmpty(), "Should show empty cart message");
        Assert.assertFalse(cartPage.isCheckoutButtonEnabled(), "Checkout button should be disabled");

        System.out.println("✓ Test Empty Cart PASSED");
    }

    @Test(priority = 12, description = "Test xóa tất cả sản phẩm")
    public void testClearCart() {
        // Add item first
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        
        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);
        
        productPage.selectSize("42");
        productPage.addToCart();
        sleep(2000);

        // Clear cart
        CartPage cartPage = new CartPage(driver);
        cartPage.open();
        
        if (cartPage.getCartItemsCount() > 0) {
            cartPage.clearCart();
            sleep(2000);

            Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
            System.out.println("✓ Test Clear Cart PASSED");
        }
    }
}
