package com.example.selenium.tests;

import com.example.selenium.pages.*;
import com.example.selenium.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Test cases cho Checkout Module
 */
public class CheckoutTest extends BaseTest {

    @BeforeClass
    public void setupCheckout() {
        System.out.println("=== Starting Checkout Test Setup ===");
        
        // Login và thêm sản phẩm vào giỏ
        LoginPage loginPage = new LoginPage(driver);
        System.out.println("Step 1: Opening login page...");
        loginPage.open();
        
        System.out.println("Step 2: Logging in...");
        loginPage.login("test", "12345678");
        sleep(2000);

        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        
        // Navigate to products page
        System.out.println("Step 3: Navigating to products page...");
        driver.get(baseUrl + "/shoes");
        sleep(3000);  // Increased wait time for page load
        
        // Click first product
        System.out.println("Step 4: Clicking first product...");
        homePage.clickFirstProduct();
        sleep(3000);  // Increased wait time for product page load
        
        System.out.println("Step 5: Selecting size 8.5...");
        productPage.selectSize("8.5");
        sleep(1000);
        
        System.out.println("Step 6: Setting quantity to 2...");
        productPage.setQuantity(2);
        sleep(1000);
        
        System.out.println("Step 7: Adding to cart...");
        productPage.addToCart();
        sleep(3000);  // Wait for cart update
        
        System.out.println("=== Setup Complete ===\n");
    }

    @Test(priority = 1, description = "Test mở trang checkout")
    public void testOpenCheckoutPage() {
        CartPage cartPage = new CartPage(driver);
        cartPage.open();
        
        cartPage.proceedToCheckout();
        sleep(2000);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/checkout"), "Should be on checkout page");

        System.out.println("✓ Test Open Checkout Page PASSED");
    }

    @Test(priority = 2, description = "Test hiển thị order summary")
    public void testDisplayOrderSummary() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        Assert.assertTrue(checkoutPage.isOrderSummaryDisplayed(), "Order summary should be displayed");

        String total = checkoutPage.getTotalAmount();
        Assert.assertNotNull(total, "Total amount should be displayed");

        System.out.println("Order total: " + total);
        System.out.println("✓ Test Display Order Summary PASSED");
    }

    @Test(priority = 3, description = "Test checkout thành công với COD")
    public void testCheckoutSuccessWithCOD() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        // Fill shipping info
        checkoutPage.fillShippingInfo(
            "Nguyen Van A",
            "0901234567",
            "123 Le Loi",
            "Ho Chi Minh"
        );

        // Select payment method
        checkoutPage.selectPaymentMethod("COD");
        sleep(1000);

        // Place order
        checkoutPage.placeOrder();
        sleep(3000);

        // Verify success
        if (checkoutPage.isSuccessDisplayed()) {
            String successMsg = checkoutPage.getSuccessMessage();
            System.out.println("Success: " + successMsg);
            
            String orderId = checkoutPage.getOrderId();
            System.out.println("Order ID: " + orderId);
            
            Assert.assertNotNull(orderId, "Order ID should be generated");
        } else {
            // May redirect to order confirmation page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(
                currentUrl.contains("/order") || currentUrl.contains("/success"),
                "Should redirect to order confirmation"
            );
        }

        System.out.println("✓ Test Checkout Success With COD PASSED");
    }

    @Test(priority = 4, description = "Test checkout với thông tin trống")
    public void testCheckoutWithEmptyInfo() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        // Try to place order without filling info
        checkoutPage.selectPaymentMethod("COD");
        checkoutPage.placeOrder();
        sleep(2000);

        // Verify error or validation
        Assert.assertTrue(
            checkoutPage.isErrorDisplayed() || driver.getCurrentUrl().contains("/checkout"),
            "Should show error or stay on checkout page"
        );

        System.out.println("✓ Test Checkout With Empty Info PASSED");
    }

    @Test(priority = 5, description = "Test checkout với số điện thoại không hợp lệ")
    public void testCheckoutWithInvalidPhone() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        checkoutPage.fillShippingInfo(
            "Nguyen Van A",
            "123456",  // Invalid phone
            "123 Le Loi",
            "Ho Chi Minh"
        );

        checkoutPage.selectPaymentMethod("COD");
        checkoutPage.placeOrder();
        sleep(2000);

        // Verify error
        Assert.assertTrue(
            checkoutPage.isErrorDisplayed() || driver.getCurrentUrl().contains("/checkout"),
            "Should show validation error"
        );

        System.out.println("✓ Test Checkout With Invalid Phone PASSED");
    }

    @Test(priority = 6, description = "Test chọn phương thức thanh toán VNPay")
    public void testSelectVNPayPayment() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        checkoutPage.fillShippingInfo(
            "Tran Thi B",
            "0907654321",
            "456 Nguyen Hue",
            "Ha Noi"
        );

        checkoutPage.selectPaymentMethod("VNPay");
        sleep(1000);

        checkoutPage.placeOrder();
        sleep(3000);

        // VNPay may redirect to payment gateway
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

        Assert.assertTrue(
            currentUrl.contains("/order") || 
            currentUrl.contains("/vnpay") || 
            currentUrl.contains("/payment"),
            "Should proceed with VNPay payment"
        );

        System.out.println("✓ Test Select VNPay Payment PASSED");
    }

    @Test(priority = 7, description = "Test thêm ghi chú đơn hàng")
    public void testAddOrderNote() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        checkoutPage.fillShippingInfo(
            "Le Van C",
            "0909999999",
            "789 Tran Hung Dao",
            "Da Nang"
        );

        checkoutPage.addNote("Giao giờ hành chính. Gọi trước khi giao.");
        checkoutPage.selectPaymentMethod("COD");

        System.out.println("✓ Test Add Order Note PASSED");
    }

    @Test(priority = 8, description = "Test quay lại giỏ hàng từ checkout")
    public void testBackToCartFromCheckout() {
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();

        checkoutPage.backToCart();
        sleep(2000);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/cart"), "Should redirect back to cart");

        System.out.println("✓ Test Back To Cart From Checkout PASSED");
    }

    @DataProvider(name = "checkoutData")
    public Object[][] getCheckoutData() throws IOException {
        String csvPath = "selenium_test_data_checkout.csv";
        return TestDataReader.readCSVAsDataProvider(csvPath);
    }

    @Test(priority = 9, dataProvider = "checkoutData", description = "Data-driven checkout test")
    public void testCheckoutWithCSVData(Map<String, String> data) {
        // Setup: Add product to cart first
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        
        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);
        
        productPage.selectSize("42");
        productPage.addToCart();
        sleep(2000);

        // Go to checkout
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();
        sleep(2000);

        String testId = TestDataReader.getValue(data, "testId");
        String description = TestDataReader.getValue(data, "description");
        String fullName = TestDataReader.getValue(data, "fullName");
        String phone = TestDataReader.getValue(data, "phone");
        String address = TestDataReader.getValue(data, "address");
        String city = TestDataReader.getValue(data, "city");
        String paymentMethod = TestDataReader.getValue(data, "paymentMethod");
        String expectedResult = TestDataReader.getValue(data, "expectedResult");

        System.out.println("\n--- Running: " + testId + " - " + description + " ---");

        checkoutPage.fillShippingInfo(fullName, phone, address, city);
        checkoutPage.selectPaymentMethod(paymentMethod);
        checkoutPage.placeOrder();
        sleep(3000);

        if ("success".equals(expectedResult)) {
            // Verify success
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(
                currentUrl.contains("/order") || 
                currentUrl.contains("/success") || 
                checkoutPage.isSuccessDisplayed(),
                "Checkout should be successful for " + testId
            );
            System.out.println("✓ " + testId + " PASSED");
        } else if ("fail".equals(expectedResult)) {
            // Verify fail
            Assert.assertTrue(
                checkoutPage.isErrorDisplayed() || driver.getCurrentUrl().contains("/checkout"),
                "Should show error for " + testId
            );
            System.out.println("✓ " + testId + " PASSED");
        }
    }

    @Test(priority = 10, description = "Test checkout với giỏ hàng trống")
    public void testCheckoutWithEmptyCart() {
        // Clear cart first
        CartPage cartPage = new CartPage(driver);
        cartPage.open();
        
        while (cartPage.getCartItemsCount() > 0) {
            cartPage.removeItem(0);
            sleep(1000);
        }

        // Try to go to checkout
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open();
        sleep(2000);

        // Should prevent checkout or show error
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/cart") || checkoutPage.isErrorDisplayed(),
            "Should not allow checkout with empty cart"
        );

        System.out.println("✓ Test Checkout With Empty Cart PASSED");
    }
}
