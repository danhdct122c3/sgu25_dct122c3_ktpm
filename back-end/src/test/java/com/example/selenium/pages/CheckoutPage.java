package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object cho Checkout Page
 */
public class CheckoutPage extends BasePage {

    // Locators
    @FindBy(id = "fullName")
    private WebElement fullNameInput;

    @FindBy(id = "phone")
    private WebElement phoneInput;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "address")
    private WebElement addressInput;

    @FindBy(id = "city")
    private WebElement cityInput;

    @FindBy(id = "district")
    private WebElement districtInput;

    @FindBy(id = "ward")
    private WebElement wardInput;

    @FindBy(id = "note")
    private WebElement noteInput;

    @FindBy(id = "payment-cod")
    private WebElement paymentCOD;

    @FindBy(id = "payment-vnpay")
    private WebElement paymentVNPay;

    @FindBy(id = "payment-bank")
    private WebElement paymentBank;

    @FindBy(className = "place-order")
    private WebElement placeOrderButton;

    @FindBy(className = "back-to-cart")
    private WebElement backToCartButton;

    @FindBy(className = "order-summary")
    private WebElement orderSummary;

    @FindBy(className = "total-amount")
    private WebElement totalAmount;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(className = "success-message")
    private WebElement successMessage;

    @FindBy(className = "order-id")
    private WebElement orderId;

    public CheckoutPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang checkout
     */
    public void open() {
        navigateTo(baseUrl + "/checkout");
    }

    /**
     * Điền thông tin giao hàng
     */
    public void fillShippingInfo(String fullName, String phone, String address, String city) {
        type(fullNameInput, fullName);
        type(phoneInput, phone);
        type(addressInput, address);
        type(cityInput, city);
    }

    /**
     * Điền thông tin giao hàng đầy đủ
     */
    public void fillFullShippingInfo(String fullName, String phone, String email, 
                                      String address, String city, String district, String ward) {
        type(fullNameInput, fullName);
        type(phoneInput, phone);
        if (email != null && !email.isEmpty()) {
            type(emailInput, email);
        }
        type(addressInput, address);
        type(cityInput, city);
        if (district != null && !district.isEmpty()) {
            type(districtInput, district);
        }
        if (ward != null && !ward.isEmpty()) {
            type(wardInput, ward);
        }
    }

    /**
     * Thêm ghi chú đơn hàng
     */
    public void addNote(String note) {
        type(noteInput, note);
    }

    /**
     * Chọn phương thức thanh toán
     */
    public void selectPaymentMethod(String method) {
        switch (method.toUpperCase()) {
            case "COD":
                click(paymentCOD);
                break;
            case "VNPAY":
                click(paymentVNPay);
                break;
            case "BANK":
            case "BANK_TRANSFER":
                click(paymentBank);
                break;
        }
    }

    /**
     * Đặt hàng
     */
    public void placeOrder() {
        click(placeOrderButton);
    }

    /**
     * Quay lại giỏ hàng
     */
    public void backToCart() {
        click(backToCartButton);
    }

    /**
     * Lấy tổng tiền
     */
    public String getTotalAmount() {
        return getText(totalAmount);
    }

    /**
     * Kiểm tra có error message không
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    /**
     * Lấy error message
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /**
     * Kiểm tra có success message không
     */
    public boolean isSuccessDisplayed() {
        return isDisplayed(successMessage);
    }

    /**
     * Lấy success message
     */
    public String getSuccessMessage() {
        return getText(successMessage);
    }

    /**
     * Lấy order ID sau khi đặt hàng thành công
     */
    public String getOrderId() {
        return getText(orderId);
    }

    /**
     * Kiểm tra order summary có hiển thị không
     */
    public boolean isOrderSummaryDisplayed() {
        return isDisplayed(orderSummary);
    }

    /**
     * Kiểm tra place order button có enabled không
     */
    public boolean isPlaceOrderButtonEnabled() {
        return placeOrderButton.isEnabled();
    }
}
