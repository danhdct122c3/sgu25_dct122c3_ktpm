package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object cho Discount Management Page
 */
public class DiscountManagementPage extends BasePage {

    // Discount Form Locators
    @FindBy(id = "discount-code")
    private WebElement discountCodeInput;

    @FindBy(id = "discount-description")
    private WebElement descriptionInput;

    @FindBy(id = "discount-type")
    private WebElement discountTypeSelect;

    @FindBy(id = "discount-value")
    private WebElement discountValueInput;

    @FindBy(id = "min-order-value")
    private WebElement minOrderValueInput;

    @FindBy(id = "max-discount-amount")
    private WebElement maxDiscountAmountInput;

    @FindBy(id = "start-date")
    private WebElement startDateInput;

    @FindBy(id = "end-date")
    private WebElement endDateInput;

    @FindBy(id = "usage-limit")
    private WebElement usageLimitInput;

    @FindBy(xpath = "//button[contains(text(),'Create') or contains(text(),'Tạo mã')]")
    private WebElement createButton;

    @FindBy(xpath = "//button[contains(text(),'Update') or contains(text(),'Cập nhật')]")
    private WebElement updateButton;

    @FindBy(xpath = "//button[contains(text(),'Delete') or contains(text(),'Xóa')]")
    private WebElement deleteButton;

    // Search and Filter Locators
    @FindBy(id = "search-discount")
    private WebElement searchInput;

    @FindBy(id = "filter-status")
    private WebElement statusFilterSelect;

    @FindBy(className = "discount-list")
    private WebElement discountList;

    @FindBy(className = "discount-item")
    private WebElement discountItem;

    @FindBy(className = "discount-code")
    private WebElement discountCodeText;

    // Status Toggle Buttons
    @FindBy(xpath = "//button[contains(text(),'Activate') or contains(text(),'Kích hoạt')]")
    private WebElement activateButton;

    @FindBy(xpath = "//button[contains(text(),'Deactivate') or contains(text(),'Vô hiệu hóa')]")
    private WebElement deactivateButton;

    @FindBy(className = "status-badge")
    private WebElement statusBadge;

    // Messages
    @FindBy(className = "success-message")
    private WebElement successMessage;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(className = "validation-error")
    private WebElement validationError;

    // Navigation
    @FindBy(xpath = "//button[contains(text(),'New Discount') or contains(text(),'Tạo mã mới')]")
    private WebElement newDiscountButton;

    public DiscountManagementPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang discount management
     */
    public void open() {
        navigateTo(baseUrl + "/admin/discounts");
    }

    /**
     * Click New Discount button
     */
    public void clickNewDiscount() {
        click(newDiscountButton);
    }

    /**
     * Create new discount
     */
    public void createDiscount(String code, String description, String type, String value, 
                               String minOrder, String maxDiscount, String startDate, 
                               String endDate, String usageLimit) {
        if (code != null && !code.isEmpty()) {
            type(discountCodeInput, code);
        }
        if (description != null && !description.isEmpty()) {
            type(descriptionInput, description);
        }
        if (type != null && !type.isEmpty()) {
            Select typeDropdown = new Select(discountTypeSelect);
            typeDropdown.selectByValue(type);
        }
        if (value != null && !value.isEmpty()) {
            type(discountValueInput, value);
        }
        if (minOrder != null && !minOrder.isEmpty()) {
            type(minOrderValueInput, minOrder);
        }
        if (maxDiscount != null && !maxDiscount.isEmpty()) {
            type(maxDiscountAmountInput, maxDiscount);
        }
        if (startDate != null && !startDate.isEmpty()) {
            type(startDateInput, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            type(endDateInput, endDate);
        }
        if (usageLimit != null && !usageLimit.isEmpty()) {
            type(usageLimitInput, usageLimit);
        }
        
        click(createButton);
    }

    /**
     * Update existing discount
     */
    public void updateDiscount(String code, String description, String type, String value, 
                               String minOrder, String maxDiscount, String startDate, 
                               String endDate, String usageLimit) {
        if (code != null && !code.isEmpty()) {
            discountCodeInput.clear();
            type(discountCodeInput, code);
        }
        if (description != null && !description.isEmpty()) {
            descriptionInput.clear();
            type(descriptionInput, description);
        }
        if (type != null && !type.isEmpty()) {
            Select typeDropdown = new Select(discountTypeSelect);
            typeDropdown.selectByValue(type);
        }
        if (value != null && !value.isEmpty()) {
            discountValueInput.clear();
            type(discountValueInput, value);
        }
        if (minOrder != null && !minOrder.isEmpty()) {
            minOrderValueInput.clear();
            type(minOrderValueInput, minOrder);
        }
        if (maxDiscount != null && !maxDiscount.isEmpty()) {
            maxDiscountAmountInput.clear();
            type(maxDiscountAmountInput, maxDiscount);
        }
        if (startDate != null && !startDate.isEmpty()) {
            startDateInput.clear();
            type(startDateInput, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateInput.clear();
            type(endDateInput, endDate);
        }
        if (usageLimit != null && !usageLimit.isEmpty()) {
            usageLimitInput.clear();
            type(usageLimitInput, usageLimit);
        }
        
        click(updateButton);
    }

    /**
     * Search discount by code
     */
    public void searchDiscount(String code) {
        type(searchInput, code);
        try {
            Thread.sleep(500); // Wait for search results
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filter discounts by status
     */
    public void filterByStatus(String status) {
        Select statusFilter = new Select(statusFilterSelect);
        statusFilter.selectByValue(status);
    }

    /**
     * Click on first discount item
     */
    public void clickFirstDiscount() {
        click(discountItem);
    }

    /**
     * Delete discount
     */
    public void deleteDiscount() {
        click(deleteButton);
        // Confirm deletion if there's a confirmation dialog
        try {
            Thread.sleep(500);
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert present
        }
    }

    /**
     * Activate discount
     */
    public void activateDiscount() {
        click(activateButton);
    }

    /**
     * Deactivate discount
     */
    public void deactivateDiscount() {
        click(deactivateButton);
    }

    /**
     * Get discount status
     */
    public String getDiscountStatus() {
        return getText(statusBadge);
    }

    /**
     * Get discount code text
     */
    public String getDiscountCode() {
        return getText(discountCodeText);
    }

    /**
     * Check if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(successMessage);
    }

    /**
     * Get success message
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
     * Get error message
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
     * Get validation error
     */
    public String getValidationError() {
        return getText(validationError);
    }

    /**
     * Check if discount list is displayed
     */
    public boolean isDiscountListDisplayed() {
        return isDisplayed(discountList);
    }

    /**
     * Check if create button is displayed
     */
    public boolean isCreateButtonDisplayed() {
        return isDisplayed(createButton);
    }
}
