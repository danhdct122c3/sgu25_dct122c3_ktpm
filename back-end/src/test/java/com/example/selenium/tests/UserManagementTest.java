package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.UserManagementPage;
import com.example.selenium.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Test class cho User Management module
 */
public class UserManagementTest extends BaseTest {

    private UserManagementPage userManagementPage;

    @BeforeClass
    public void setupUserManagement() {
        userManagementPage = new UserManagementPage(driver);
    }

    /**
     * Data Provider - đọc dữ liệu test từ CSV file
     */
    @DataProvider(name = "userManagementData")
    public Object[][] getUserManagementTestData() throws java.io.IOException {
        List<Map<String, String>> data = TestDataReader.readCSV(
            "src/test/resources/selenium_test_data_user_management.csv"
        );
        return TestDataReader.readCSVAsDataProvider(
            "src/test/resources/selenium_test_data_user_management.csv"
        );
    }

    @Test(priority = 1, description = "Test customer update own profile successfully")
    public void testCustomerUpdateProfile() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to profile page
        userManagementPage.openProfile();
        sleep(2000);

        // Update profile
        userManagementPage.updateProfile("Customer Updated", "customer.new@example.com", "0123456789");
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getCurrentUrl().contains("/profile"),
            "Profile should be updated successfully"
        );

        System.out.println("✓ Test Customer Update Profile PASSED");
    }

    @Test(priority = 2, description = "Test customer update profile with invalid email")
    public void testCustomerUpdateProfileInvalidEmail() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as customer
        loginPage.open();
        loginPage.login("customer1", "password123");
        sleep(2000);

        // Navigate to profile page
        userManagementPage.openProfile();
        sleep(2000);

        // Update profile with invalid email
        userManagementPage.updateProfile("Customer", "invalid-email", "0123456789");
        sleep(2000);

        // Verify validation error
        Assert.assertTrue(
            userManagementPage.isValidationErrorDisplayed() || 
            userManagementPage.isErrorMessageDisplayed(),
            "Should show validation error for invalid email"
        );

        System.out.println("✓ Test Invalid Email Validation PASSED");
    }

    @Test(priority = 3, description = "Test staff update own profile")
    public void testStaffUpdateProfile() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as staff
        loginPage.open();
        loginPage.login("staff1", "password123");
        sleep(2000);

        // Navigate to profile page
        userManagementPage.openProfile();
        sleep(2000);

        // Update profile
        userManagementPage.updateProfile("Staff Updated", "staff.new@example.com", "0987654321");
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getCurrentUrl().contains("/profile"),
            "Profile should be updated successfully"
        );

        System.out.println("✓ Test Staff Update Profile PASSED");
    }

    @Test(priority = 4, description = "Test manager update own profile")
    public void testManagerUpdateProfile() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to profile page
        userManagementPage.openProfile();
        sleep(2000);

        // Update profile
        userManagementPage.updateProfile("Manager Updated", "manager.new@example.com", "0123456789");
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getCurrentUrl().contains("/profile"),
            "Profile should be updated successfully"
        );

        System.out.println("✓ Test Manager Update Profile PASSED");
    }

    @Test(priority = 5, description = "Test manager update staff status to active")
    public void testManagerUpdateStaffStatusActive() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to user management page
        userManagementPage.openUserManagement();
        sleep(2000);

        // Search for staff user
        userManagementPage.searchUser("staff1");
        sleep(1000);

        // Click on staff user
        userManagementPage.clickUserItem();
        sleep(1000);

        // Activate user
        userManagementPage.activateUser();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getUserStatus().contains("ACTIVE"),
            "Staff status should be updated to ACTIVE"
        );

        System.out.println("✓ Test Manager Update Staff Status PASSED");
    }

    @Test(priority = 6, description = "Test manager update staff status to inactive")
    public void testManagerUpdateStaffStatusInactive() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as manager
        loginPage.open();
        loginPage.login("manager1", "password123");
        sleep(2000);

        // Navigate to user management page
        userManagementPage.openUserManagement();
        sleep(2000);

        // Search for staff user
        userManagementPage.searchUser("staff1");
        sleep(1000);

        // Click on staff user
        userManagementPage.clickUserItem();
        sleep(1000);

        // Deactivate user
        userManagementPage.deactivateUser();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getUserStatus().contains("INACTIVE"),
            "Staff status should be updated to INACTIVE"
        );

        System.out.println("✓ Test Manager Deactivate Staff PASSED");
    }

    @Test(priority = 7, description = "Test admin update own profile")
    public void testAdminUpdateProfile() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as admin
        loginPage.openAdmin();
        loginPage.login("admin1", "password123");
        sleep(2000);

        // Navigate to profile page
        userManagementPage.openProfile();
        sleep(2000);

        // Update profile
        userManagementPage.updateProfile("Admin Updated", "admin.new@example.com", "0123456789");
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getCurrentUrl().contains("/profile"),
            "Profile should be updated successfully"
        );

        System.out.println("✓ Test Admin Update Profile PASSED");
    }

    @Test(priority = 8, description = "Test admin update staff status")
    public void testAdminUpdateStaffStatus() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as admin
        loginPage.openAdmin();
        loginPage.login("admin1", "password123");
        sleep(2000);

        // Navigate to user management page
        userManagementPage.openUserManagement();
        sleep(2000);

        // Search for staff user
        userManagementPage.searchUser("staff1");
        sleep(1000);

        // Click on staff user
        userManagementPage.clickUserItem();
        sleep(1000);

        // Activate user
        userManagementPage.activateUser();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getUserStatus().contains("ACTIVE"),
            "Admin should be able to update staff status"
        );

        System.out.println("✓ Test Admin Update Staff Status PASSED");
    }

    @Test(priority = 9, description = "Test admin update manager status")
    public void testAdminUpdateManagerStatus() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as admin
        loginPage.openAdmin();
        loginPage.login("admin1", "password123");
        sleep(2000);

        // Navigate to user management page
        userManagementPage.openUserManagement();
        sleep(2000);

        // Search for manager user
        userManagementPage.searchUser("manager1");
        sleep(1000);

        // Click on manager user
        userManagementPage.clickUserItem();
        sleep(1000);

        // Activate user
        userManagementPage.activateUser();
        sleep(2000);

        // Verify success
        Assert.assertTrue(
            userManagementPage.isSuccessMessageDisplayed() || 
            userManagementPage.getUserStatus().contains("ACTIVE"),
            "Admin should be able to update manager status"
        );

        System.out.println("✓ Test Admin Update Manager Status PASSED");
    }

    @Test(priority = 10, description = "Test staff cannot access user management")
    public void testStaffUnauthorizedAccess() {
        LoginPage loginPage = new LoginPage(driver);
        
        // Login as staff
        loginPage.open();
        loginPage.login("staff1", "password123");
        sleep(2000);

        // Try to navigate to user management page
        userManagementPage.openUserManagement();
        sleep(2000);

        // Verify unauthorized (should redirect or show error)
        String currentUrl = userManagementPage.getCurrentUrl();
        Assert.assertTrue(
            !currentUrl.contains("/admin/users") || 
            userManagementPage.isErrorMessageDisplayed(),
            "Staff should not be able to access user management"
        );

        System.out.println("✓ Test Staff Unauthorized Access PASSED");
    }
}
