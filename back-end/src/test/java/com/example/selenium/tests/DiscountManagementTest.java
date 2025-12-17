package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.DiscountManagementPage;
import com.example.selenium.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Test class cho Discount Management module
 */
public class DiscountManagementTest extends BaseTest {

    private DiscountManagementPage discountPage;

    @BeforeClass
    public void setupDiscountManagement() {
        discountPage = new DiscountManagementPage(driver);
    }

    /**
     * Data Provider - đọc dữ liệu test từ CSV file
     */
    @DataProvider(name = "discountManagementData")
    public Object[][] getDiscountManagementTestData() throws java.io.IOException {
        List<Map<String, String>> data = TestDataReader.readCSV(
            "src/test/resources/selenium_test_data_discount_management.csv"
        );
        return TestDataReader.readCSVAsDataProvider(
            "src/test/resources/selenium_test_data_discount_management.csv"
        );
    }

    @Test(priority = 1, description = "Test manager create percentage discount")
    public void testManagerCreatePercentageDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click new discount button
        discountPage.clickNewDiscount();
        sleep(1000);

        // Create discount
        discountPage.createDiscount(
            "SUMMER2024", 
            "Summer Sale", 
            "PERCENTAGE", 
            "20", 
            "100000", 
            "50000", 
            "2024-06-01", 
            "2024-08-31", 
            "100"
        );
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            discountPage.isSuccessMessageDisplayed() || 
            discountPage.isDiscountListDisplayed(),
            "Discount should be created successfully"
        );

        System.out.println("✓ Test Manager Create Percentage Discount PASSED");
    }

    @Test(priority = 2, description = "Test manager create fixed amount discount")
    public void testManagerCreateFixedAmountDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click new discount button
        discountPage.clickNewDiscount();
        sleep(1000);

        // Create discount
        discountPage.createDiscount(
            "FIXED50K", 
            "Fixed 50k discount", 
            "FIXED_AMOUNT", 
            "50000", 
            "200000", 
            "", 
            "2024-01-01", 
            "2024-12-31", 
            "50"
        );
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            discountPage.isSuccessMessageDisplayed() || 
            discountPage.isDiscountListDisplayed(),
            "Fixed amount discount should be created successfully"
        );

        System.out.println("✓ Test Manager Create Fixed Amount Discount PASSED");
    }

    @Test(priority = 3, description = "Test manager create discount with duplicate code")
    public void testManagerCreateDiscountDuplicateCode() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click new discount button
        discountPage.clickNewDiscount();
        sleep(1000);

        // Try to create discount with duplicate code
        discountPage.createDiscount(
            "SUMMER2024", 
            "Duplicate code", 
            "PERCENTAGE", 
            "10", 
            "50000", 
            "", 
            "2024-01-01", 
            "2024-12-31", 
            ""
        );
        sleep(2000);

        // Verify error message
        Assert.assertTrue(
            discountPage.isErrorMessageDisplayed(),
            "Should show error for duplicate code"
        );

        System.out.println("✓ Test Duplicate Code Validation PASSED");
    }

    @Test(priority = 4, description = "Test manager create discount with invalid date range")
    public void testManagerCreateDiscountInvalidDateRange() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click new discount button
        discountPage.clickNewDiscount();
        sleep(1000);

        // Try to create discount with invalid date range
        discountPage.createDiscount(
            "INVALID", 
            "Invalid dates", 
            "PERCENTAGE", 
            "10", 
            "50000", 
            "", 
            "2024-12-31", 
            "2024-01-01", 
            ""
        );
        sleep(2000);

        // Verify validation error
        Assert.assertTrue(
            discountPage.isValidationErrorDisplayed() || 
            discountPage.isErrorMessageDisplayed(),
            "Should show error for invalid date range"
        );

        System.out.println("✓ Test Invalid Date Range Validation PASSED");
    }

    @Test(priority = 5, description = "Test manager view all discounts")
    public void testManagerViewDiscounts() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Verify discount list is displayed
        Assert.assertTrue(
            discountPage.isDiscountListDisplayed(),
            "Discount list should be displayed"
        );

        System.out.println("✓ Test Manager View Discounts PASSED");
    }

    @Test(priority = 6, description = "Test manager search discount by code")
    public void testManagerSearchDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Search for discount
        discountPage.searchDiscount("SUMMER2024");
        sleep(2000);

        // Verify search results
        Assert.assertTrue(
            discountPage.isDiscountListDisplayed(),
            "Search results should be displayed"
        );

        System.out.println("✓ Test Manager Search Discount PASSED");
    }

    @Test(priority = 7, description = "Test manager update discount")
    public void testManagerUpdateDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click on first discount
        discountPage.clickFirstDiscount();
        sleep(1000);

        // Update discount
        discountPage.updateDiscount(
            "UPDATED2024", 
            "Updated discount", 
            "PERCENTAGE", 
            "25", 
            "150000", 
            "75000", 
            "2024-06-01", 
            "2024-09-30", 
            "200"
        );
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            discountPage.isSuccessMessageDisplayed(),
            "Discount should be updated successfully"
        );

        System.out.println("✓ Test Manager Update Discount PASSED");
    }

    @Test(priority = 8, description = "Test manager activate discount")
    public void testManagerActivateDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click on first discount
        discountPage.clickFirstDiscount();
        sleep(1000);

        // Activate discount
        discountPage.activateDiscount();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            discountPage.isSuccessMessageDisplayed() || 
            discountPage.getDiscountStatus().contains("ACTIVE"),
            "Discount should be activated"
        );

        System.out.println("✓ Test Manager Activate Discount PASSED");
    }

    @Test(priority = 9, description = "Test manager deactivate discount")
    public void testManagerDeactivateDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click on first discount
        discountPage.clickFirstDiscount();
        sleep(1000);

        // Deactivate discount
        discountPage.deactivateDiscount();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            discountPage.isSuccessMessageDisplayed() || 
            discountPage.getDiscountStatus().contains("INACTIVE"),
            "Discount should be deactivated"
        );

        System.out.println("✓ Test Manager Deactivate Discount PASSED");
    }

    @Test(priority = 10, description = "Test manager delete discount")
    public void testManagerDeleteDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Click on first discount
        discountPage.clickFirstDiscount();
        sleep(1000);

        // Delete discount
        discountPage.deleteDiscount();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            discountPage.isSuccessMessageDisplayed(),
            "Discount should be deleted successfully"
        );

        System.out.println("✓ Test Manager Delete Discount PASSED");
    }

    @Test(priority = 11, description = "Test staff cannot create discount")
    public void testStaffCannotCreateDiscount() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as staff
        loginPage.open();
        loginPage.login("staff1", "password123");
        sleep(2000);

        // Try to navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Verify unauthorized (should redirect or not show create button)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            !currentUrl.contains("/admin/discounts") || 
            !discountPage.isCreateButtonDisplayed() ||
            discountPage.isErrorMessageDisplayed(),
            "Staff should not be able to create discount"
        );

        System.out.println("✓ Test Staff Unauthorized Access PASSED");
    }

    @Test(priority = 12, description = "Test customer cannot access discount management")
    public void testCustomerCannotAccessDiscountManagement() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Try to navigate to discount management page
        discountPage.open();
        sleep(2000);

        // Verify unauthorized (should redirect or show error)
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            !currentUrl.contains("/admin/discounts") || 
            discountPage.isErrorMessageDisplayed(),
            "Customer should not be able to access discount management"
        );

        System.out.println("✓ Test Customer Unauthorized Access PASSED");
    }
}
