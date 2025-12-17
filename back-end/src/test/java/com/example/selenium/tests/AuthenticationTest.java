package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.HomePage;
import com.example.selenium.utils.TestDataReader;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * Test cases cho Authentication Module
 */
public class AuthenticationTest extends BaseTest {

    @Test(priority = 1, description = "Test đăng nhập thành công với Admin")
    public void testLoginSuccessfulAdmin() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        // Open admin login page
        loginPage.openAdmin();

        // Login
        loginPage.login("admin", "12345678");

        // Wait for redirect
        sleep(3000);

        // Debug: Print current URL
        System.out.println("Current URL after login: " + driver.getCurrentUrl());
        System.out.println("Page title: " + driver.getTitle());
        System.out.println("Page source contains 'admin': " + driver.getPageSource().contains("admin"));

        // Verify redirect
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin"), "Should redirect to admin page");

        // Verify user logged in (check if NOT on login page anymore)
        Assert.assertFalse(driver.getCurrentUrl().contains("/login"), "Should not be on login page");

        System.out.println("✓ Test Login Admin PASSED");
    }

    @Test(priority = 2, description = "Test đăng nhập thành công với Customer")
    public void testLoginSuccessfulCustomer() {
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage = new HomePage(driver);

        loginPage.open();
        loginPage.login("test", "12345678");

        sleep(2000);
//        Assert.assertTrue(homePage.isUserLoggedIn(), "Customer should be logged in");
        Assert.assertTrue(driver.getCurrentUrl().equals(baseUrl + "/") || 
                         driver.getCurrentUrl().contains("/shop"), 
                         "Customer should redirect to home or shop page");

        System.out.println("✓ Test Login Customer PASSED");
    }

    @Test(priority = 3, description = "Test đăng nhập thất bại - Username không tồn tại")
    public void testLoginFailedInvalidUsername() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.login("nonexistuser", "password123");

        sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Chờ alert xuất hiện
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        // Lấy nội dung alert
        String alertText = alert.getText();
        System.out.println("Alert text: " + alertText);

        // Assert nội dung lỗi
        Assert.assertTrue(
                alertText.contains("Invalid") ||
                        alertText.contains("Tên đăng nhập hoặc mật khẩu không đúng"),
                "Should show invalid credentials alert"
        );

        System.out.println("✓ Test Login Failed - Invalid Username PASSED");
    }



}
