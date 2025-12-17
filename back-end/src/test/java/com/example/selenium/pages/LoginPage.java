package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object cho Login Page
 */
public class LoginPage extends BasePage {

    // Locators
    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(linkText = "Register")
    private WebElement registerLink;

    @FindBy(id = "remember-me")
    private WebElement rememberCheckbox;

    @FindBy(className = "toggle-password")
    private WebElement showPasswordButton;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang login (customer)
     */
    public void open() {
        navigateTo(baseUrl + "/login");
    }
    
    /**
     * Mở trang login admin
     */
    public void openAdmin() {
        navigateTo(baseUrl + "/admin/login");
    }

    /**
     * Đăng nhập với username và password
     */
    public void login(String username, String password) {
        System.out.println("Attempting login with username: " + username);
        
        // Wait for page to be fully loaded
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Type username
        type(usernameInput, username);
        System.out.println("Username entered: " + usernameInput.getAttribute("value"));
        
        // Type password
        type(passwordInput, password);
        System.out.println("Password entered (length): " + passwordInput.getAttribute("value").length());
        
        // Wait before clicking submit
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Click login button
        click(loginButton);
        System.out.println("Login button clicked");
    }

    /**
     * Lấy error message
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Kiểm tra error message có hiển thị không
     */
    public boolean isErrorDisplayed() {
        try {
            // Try to find error message with explicit wait
            boolean displayed = isDisplayed(errorMessage);
            System.out.println("Error message displayed: " + displayed);
            if (displayed) {
                System.out.println("Error message text: " + errorMessage.getText());
            }
            return displayed;
        } catch (Exception e) {
            System.out.println("Error message element not found, checking page source...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page source contains 'error': " + driver.getPageSource().contains("error"));
            System.out.println("Page source contains 'Invalid': " + driver.getPageSource().contains("Invalid"));
            System.out.println("Page source contains 'không đúng': " + driver.getPageSource().contains("không đúng"));
            
            // Print a snippet of the page source around any error text
            String pageSource = driver.getPageSource();
            int errorIndex = pageSource.toLowerCase().indexOf("error");
            if (errorIndex > 0) {
                int start = Math.max(0, errorIndex - 100);
                int end = Math.min(pageSource.length(), errorIndex + 200);
                System.out.println("Page source near 'error': " + pageSource.substring(start, end));
            }
            
            return false;
        }
    }

    /**
     * Click vào link Register
     */
    public void clickRegisterLink() {
        click(registerLink);
    }

    /**
     * Check Remember Me checkbox
     */
    public void checkRememberMe() {
        if (!rememberCheckbox.isSelected()) {
            click(rememberCheckbox);
        }
    }

    /**
     * Kiểm tra Remember Me checkbox đã được chọn chưa
     */
    public boolean isRememberMeChecked() {
        return rememberCheckbox.isSelected();
    }

    /**
     * Click nút Show/Hide password
     */
    public void togglePasswordVisibility() {
        click(showPasswordButton);
    }

    /**
     * Lấy type của password input
     */
    public String getPasswordInputType() {
        return passwordInput.getAttribute("type");
    }

    /**
     * Kiểm tra login button có hiển thị không
     */
    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }
}
