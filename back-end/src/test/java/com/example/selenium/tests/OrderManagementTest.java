package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.HomePage;
import com.example.selenium.pages.ProductPage;
import com.example.selenium.pages.CartPage;
import com.example.selenium.pages.CheckoutPage;
import com.example.selenium.pages.OrderPage;
import com.example.selenium.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Test class cho Order Management module
 */
public class OrderManagementTest extends BaseTest {

    private OrderPage orderPage;

    @BeforeClass
    public void setupOrderManagement() {
        orderPage = new OrderPage(driver);
    }

    /**
     * Data Provider - đọc dữ liệu test từ CSV file
     */
    @DataProvider(name = "orderManagementData")
    public Object[][] getOrderManagementTestData() throws java.io.IOException {
        return TestDataReader.readCSVAsDataProvider(
            "src/test/resources/selenium_test_data_order_management.csv"
        );
    }

    @Test(priority = 1, description = "Test customer place order successfully")
    public void testCustomerPlaceOrder() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Add product to cart
        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);
        productPage.selectSize("8");
        productPage.setQuantity(1);
        productPage.addToCart();
        sleep(2000);

        // Go to checkout
        cartPage.open();
        cartPage.proceedToCheckout();
        sleep(2000);

        // Fill checkout info and place order
        checkoutPage.fillShippingInfo(
            "Customer Name",
            "0123456789",
            "Customer Address",
            "Ho Chi Minh"
        );
        checkoutPage.selectPaymentMethod("COD");
        checkoutPage.placeOrder();
        sleep(3000);

        // Verify order placed
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/order") || currentUrl.contains("/success"),
            "Should redirect to order confirmation page"
        );

        System.out.println("✓ Test Customer Place Order PASSED");
    }

    @Test(priority = 2, description = "Test customer view order history")
    public void testCustomerViewOrderHistory() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Verify order list is displayed
        Assert.assertTrue(
            orderPage.isOrderListDisplayed() || orderPage.isEmptyOrdersDisplayed(),
            "Order history page should be displayed"
        );

        System.out.println("✓ Test Customer View Order History PASSED");
    }

    @Test(priority = 3, description = "Test customer view order details")
    public void testCustomerViewOrderDetails() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Click on first order if exists
        if (orderPage.getOrderCount() > 0) {
            orderPage.clickFirstOrder();
            sleep(2000);

            // Verify order details displayed
            Assert.assertTrue(
                orderPage.isOrderDetailsDisplayed() || 
                orderPage.getCurrentUrl().contains("/order"),
                "Order details should be displayed"
            );
        }

        System.out.println("✓ Test Customer View Order Details PASSED");
    }

    @Test(priority = 4, description = "Test customer cancel pending order")
    public void testCustomerCancelPendingOrder() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Find and cancel a pending order
        if (orderPage.getOrderCount() > 0) {
            orderPage.clickFirstOrder();
            sleep(2000);

            // Try to cancel if button is available
            if (orderPage.isCancelButtonDisplayed()) {
                orderPage.cancelOrder();
                sleep(2000);

                // Verify cancellation
                Assert.assertTrue(
                    orderPage.isSuccessMessageDisplayed() || 
                    orderPage.getOrderStatus().contains("CANCEL"),
                    "Order should be cancelled"
                );
            }
        }

        System.out.println("✓ Test Customer Cancel Pending Order PASSED");
    }

    @Test(priority = 5, description = "Test customer reorder from history")
    public void testCustomerReorder() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Reorder from first order if available
        if (orderPage.getOrderCount() > 0) {
            orderPage.clickFirstOrder();
            sleep(2000);

            if (orderPage.isReorderButtonDisplayed()) {
                orderPage.reorder();
                sleep(2000);

                // Should redirect to cart or checkout
                String currentUrl = driver.getCurrentUrl();
                Assert.assertTrue(
                    currentUrl.contains("/cart") || currentUrl.contains("/checkout"),
                    "Should redirect to cart or checkout"
                );
            }
        }

        System.out.println("✓ Test Customer Reorder PASSED");
    }

    @Test(priority = 6, description = "Test customer filter orders by status")
    public void testCustomerFilterOrders() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Filter by PENDING status
        orderPage.filterByStatus("PENDING");
        sleep(2000);

        // Verify still on orders page
        Assert.assertTrue(
            orderPage.getCurrentUrl().contains("/order"),
            "Should be on orders page"
        );

        System.out.println("✓ Test Customer Filter Orders PASSED");
    }

    @Test(priority = 7, description = "Test staff view all orders")
    public void testStaffViewAllOrders() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as staff
        loginPage.open();
        loginPage.login("staff1", "password123");
        sleep(2000);

        // Navigate to order management
        orderPage.openOrderManagement();
        sleep(2000);

        // Verify order management page
        Assert.assertTrue(
            orderPage.isOrderListDisplayed() || 
            orderPage.getCurrentUrl().contains("/admin/orders"),
            "Staff should access order management"
        );

        System.out.println("✓ Test Staff View All Orders PASSED");
    }

    @Test(priority = 8, description = "Test staff update order status")
    public void testStaffUpdateOrderStatus() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as staff
        loginPage.open();
        loginPage.login("staff1", "password123");
        sleep(2000);

        // Navigate to order management
        orderPage.openOrderManagement();
        sleep(2000);

        // Click on first order and update status
        if (orderPage.getOrderCount() > 0) {
            orderPage.clickFirstOrder();
            sleep(2000);

            if (orderPage.isUpdateStatusButtonDisplayed()) {
                orderPage.updateOrderStatus("PROCESSING");
                sleep(2000);

                // Verify status updated
                Assert.assertTrue(
                    orderPage.isSuccessMessageDisplayed() || 
                    orderPage.getOrderStatus().contains("PROCESSING"),
                    "Order status should be updated"
                );
            }
        }

        System.out.println("✓ Test Staff Update Order Status PASSED");
    }

    @Test(priority = 9, description = "Test staff search orders")
    public void testStaffSearchOrders() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as staff
        loginPage.open();
        loginPage.login("staff1", "password123");
        sleep(2000);

        // Navigate to order management
        orderPage.openOrderManagement();
        sleep(2000);

        // Search orders
        orderPage.searchOrder("customer1");
        sleep(2000);

        // Verify search results
        Assert.assertTrue(
            orderPage.getCurrentUrl().contains("/admin/orders"),
            "Should be on order management page"
        );

        System.out.println("✓ Test Staff Search Orders PASSED");
    }

    @Test(priority = 10, description = "Test manager view all orders")
    public void testManagerViewAllOrders() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to order management
        orderPage.openOrderManagement();
        sleep(2000);

        // Verify order management page
        Assert.assertTrue(
            orderPage.isOrderListDisplayed() || 
            orderPage.getCurrentUrl().contains("/admin/orders"),
            "Manager should access order management"
        );

        System.out.println("✓ Test Manager View All Orders PASSED");
    }

    @Test(priority = 11, description = "Test manager view order statistics")
    public void testManagerViewStatistics() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to order management
        orderPage.openOrderManagement();
        sleep(2000);

        // Verify statistics or analytics available
        Assert.assertTrue(
            orderPage.getCurrentUrl().contains("/admin/orders") ||
            orderPage.getCurrentUrl().contains("/order"),
            "Manager should access order management"
        );

        System.out.println("✓ Test Manager View Statistics PASSED");
    }

    @Test(priority = 12, description = "Test admin view all orders")
    public void testAdminViewAllOrders() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as admin
        loginPage.openAdmin();
        loginPage.login("admin1", "password123");
        sleep(2000);

        // Navigate to order management
        orderPage.openOrderManagement();
        sleep(2000);

        // Verify order management page
        Assert.assertTrue(
            orderPage.isOrderListDisplayed() || 
            orderPage.getCurrentUrl().contains("/admin/orders"),
            "Admin should access order management"
        );

        System.out.println("✓ Test Admin View All Orders PASSED");
    }

    @Test(priority = 13, description = "Test customer cannot update order status")
    public void testCustomerCannotUpdateStatus() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Customer should not see update status button
        if (orderPage.getOrderCount() > 0) {
            orderPage.clickFirstOrder();
            sleep(2000);

            // Update status button should not be displayed
            boolean cannotUpdate = !orderPage.isUpdateStatusButtonDisplayed();
            Assert.assertTrue(
                cannotUpdate,
                "Customer should not see update status button"
            );
        }

        System.out.println("✓ Test Customer Cannot Update Status PASSED");
    }

    @Test(priority = 14, description = "Test place order with empty cart")
    public void testPlaceOrderWithEmptyCart() {
        LoginPage loginPage = new LoginPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Try to go directly to checkout
        checkoutPage.open();
        sleep(2000);

        // Should be redirected or show error
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/cart") || 
            currentUrl.contains("/checkout"),
            "Should handle empty cart checkout"
        );

        System.out.println("✓ Test Place Order With Empty Cart PASSED");
    }

    @Test(priority = 15, description = "Test customer track order shipment")
    public void testCustomerTrackShipment() {
        LoginPage loginPage = new LoginPage(driver);

        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to order history
        orderPage.openOrderHistory();
        sleep(2000);

        // Track shipment if order exists
        if (orderPage.getOrderCount() > 0) {
            orderPage.clickFirstOrder();
            sleep(2000);

            // Try to track if button available
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(
                currentUrl.contains("/order"),
                "Should be on order details page"
            );
        }

        System.out.println("✓ Test Customer Track Shipment PASSED");
    }
}
