package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object cho User Management Page
 */
public class UserManagementPage extends BasePage {

    // Profile Update Form Locators
    @FindBy(id = "fullName")
    private WebElement fullNameInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "phone")
    private WebElement phoneInput;

    @FindBy(xpath = "//button[contains(text(),'Update Profile') or contains(text(),'Cập nhật')]")
    private WebElement updateProfileButton;

    // User Management Locators (for Manager/Admin)
    @FindBy(id = "search-user")
    private WebElement searchUserInput;

    @FindBy(className = "user-list")
    private WebElement userList;

    @FindBy(className = "user-item")
    private WebElement userItem;

    @FindBy(xpath = "//button[contains(text(),'Active') or contains(text(),'Kích hoạt')]")
    private WebElement activateButton;

    @FindBy(xpath = "//button[contains(text(),'Inactive') or contains(text(),'Vô hiệu')]")
    private WebElement deactivateButton;

    @FindBy(className = "status-badge")
    private WebElement statusBadge;

    // Success/Error Messages
    @FindBy(className = "success-message")
    private WebElement successMessage;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(className = "validation-error")
    private WebElement validationError;

    // Navigation
    @FindBy(linkText = "Profile")
    private WebElement profileLink;

    @FindBy(linkText = "User Management")
    private WebElement userManagementLink;

    public UserManagementPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang profile
     */
    public void openProfile() {
        navigateTo(baseUrl + "/profile");
    }

    /**
     * Mở trang user management (Manager/Admin)
     */
    public void openUserManagement() {
        navigateTo(baseUrl + "/admin/users");
    }

    /**
     * Update profile
     */
    public void updateProfile(String fullName, String email, String phone) {
        if (fullName != null && !fullName.isEmpty()) {
            type(fullNameInput, fullName);
        }
        if (email != null && !email.isEmpty()) {
            type(emailInput, email);
        }
        if (phone != null && !phone.isEmpty()) {
            type(phoneInput, phone);
        }
        click(updateProfileButton);
    }

    /**
     * Search user by username
     */
    public void searchUser(String username) {
        type(searchUserInput, username);
        try {
            Thread.sleep(500); // Wait for search results
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Click on user item in list
     */
    public void clickUserItem() {
        click(userItem);
    }

    /**
     * Update user status to Active
     */
    public void activateUser() {
        click(activateButton);
    }

    /**
     * Update user status to Inactive
     */
    public void deactivateUser() {
        click(deactivateButton);
    }

    /**
     * Get user status
     */
    public String getUserStatus() {
        return getText(statusBadge);
    }

    /**
     * Check if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(successMessage);
    }

    /**
     * Get success message text
     */
    public String getSuccessMessage() {
        return getText(successMessage);
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Get error message text
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Check if validation error is displayed
     */
    public boolean isValidationErrorDisplayed() {
        return isDisplayed(validationError);
    }

    /**
     * Get validation error text
     */
    public String getValidationError() {
        return getText(validationError);
    }

    /**
     * Navigate to profile page via menu
     */
    public void clickProfileLink() {
        click(profileLink);
    }

    /**
     * Navigate to user management page via menu
     */
    public void clickUserManagementLink() {
        click(userManagementLink);
    }

    /**
     * Check if update button is displayed
     */
    public boolean isUpdateButtonDisplayed() {
        return isDisplayed(updateProfileButton);
    }

    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
