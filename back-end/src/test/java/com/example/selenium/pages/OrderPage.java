package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object cho Order Management Page
 */
public class OrderPage extends BasePage {

    // Order List Locators
    @FindBy(className = "order-list")
    private WebElement orderList;

    @FindBy(className = "order-item")
    private List<WebElement> orderItems;

    @FindBy(className = "order-id")
    private WebElement orderId;

    @FindBy(className = "order-status")
    private WebElement orderStatus;

    @FindBy(className = "order-total")
    private WebElement orderTotal;

    @FindBy(className = "order-date")
    private WebElement orderDate;

    // Order Details Locators
    @FindBy(className = "order-details")
    private WebElement orderDetails;

    @FindBy(className = "order-items-list")
    private WebElement orderItemsList;

    @FindBy(className = "shipping-address")
    private WebElement shippingAddress;

    @FindBy(className = "payment-method")
    private WebElement paymentMethod;

    // Action Buttons
    @FindBy(xpath = "//button[contains(text(),'View Details') or contains(text(),'Xem chi tiết')]")
    private WebElement viewDetailsButton;

    @FindBy(xpath = "//button[contains(text(),'Cancel Order') or contains(text(),'Hủy đơn')]")
    private WebElement cancelOrderButton;

    @FindBy(xpath = "//button[contains(text(),'Reorder') or contains(text(),'Đặt lại')]")
    private WebElement reorderButton;

    @FindBy(xpath = "//button[contains(text(),'Track Shipment') or contains(text(),'Theo dõi')]")
    private WebElement trackShipmentButton;

    @FindBy(xpath = "//button[contains(text(),'View Invoice') or contains(text(),'Xem hóa đơn')]")
    private WebElement viewInvoiceButton;

    // Order Status Update (Staff/Manager/Admin)
    @FindBy(id = "order-status-select")
    private WebElement orderStatusSelect;

    @FindBy(xpath = "//button[contains(text(),'Update Status') or contains(text(),'Cập nhật')]")
    private WebElement updateStatusButton;

    // Search and Filter
    @FindBy(id = "search-order")
    private WebElement searchOrderInput;

    @FindBy(id = "filter-status")
    private WebElement filterStatusSelect;

    @FindBy(id = "filter-date-from")
    private WebElement dateFromInput;

    @FindBy(id = "filter-date-to")
    private WebElement dateToInput;

    @FindBy(xpath = "//button[contains(text(),'Search') or contains(text(),'Tìm kiếm')]")
    private WebElement searchButton;

    @FindBy(xpath = "//button[contains(text(),'Filter') or contains(text(),'Lọc')]")
    private WebElement filterButton;

    // Statistics and Reports (Manager/Admin)
    @FindBy(className = "order-statistics")
    private WebElement orderStatistics;

    @FindBy(xpath = "//button[contains(text(),'Export Report') or contains(text(),'Xuất báo cáo')]")
    private WebElement exportReportButton;

    @FindBy(xpath = "//button[contains(text(),'View Analytics') or contains(text(),'Xem phân tích')]")
    private WebElement viewAnalyticsButton;

    // Messages
    @FindBy(className = "success-message")
    private WebElement successMessage;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(className = "empty-orders")
    private WebElement emptyOrdersMessage;

    // Confirmation Dialog
    @FindBy(xpath = "//button[contains(text(),'Confirm') or contains(text(),'Xác nhận')]")
    private WebElement confirmButton;

    @FindBy(xpath = "//button[contains(text(),'Cancel') or contains(text(),'Hủy bỏ')]")
    private WebElement cancelDialogButton;

    public OrderPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang order history (Customer)
     */
    public void openOrderHistory() {
        navigateTo(baseUrl + "/orders");
    }

    /**
     * Mở trang order management (Staff/Manager/Admin)
     */
    public void openOrderManagement() {
        navigateTo(baseUrl + "/admin/orders");
    }

    /**
     * Mở chi tiết đơn hàng
     */
    public void openOrderDetails(String orderId) {
        navigateTo(baseUrl + "/orders/" + orderId);
    }

    /**
     * Click vào đơn hàng đầu tiên trong list
     */
    public void clickFirstOrder() {
        if (!orderItems.isEmpty()) {
            click(orderItems.get(0));
        }
    }

    /**
     * Click View Details button
     */
    public void clickViewDetails() {
        click(viewDetailsButton);
    }

    /**
     * Cancel order
     */
    public void cancelOrder() {
        click(cancelOrderButton);
        try {
            Thread.sleep(500);
            // Confirm if dialog appears
            click(confirmButton);
        } catch (Exception e) {
            // No confirmation dialog
        }
    }

    /**
     * Reorder from order history
     */
    public void reorder() {
        click(reorderButton);
    }

    /**
     * Track shipment
     */
    public void trackShipment() {
        click(trackShipmentButton);
    }

    /**
     * View invoice
     */
    public void viewInvoice() {
        click(viewInvoiceButton);
    }

    /**
     * Update order status (Staff/Manager/Admin)
     */
    public void updateOrderStatus(String status) {
        Select statusDropdown = new Select(orderStatusSelect);
        statusDropdown.selectByValue(status);
        click(updateStatusButton);
    }

    /**
     * Search order by ID or customer
     */
    public void searchOrder(String searchTerm) {
        type(searchOrderInput, searchTerm);
        click(searchButton);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filter orders by status
     */
    public void filterByStatus(String status) {
        Select statusFilter = new Select(filterStatusSelect);
        statusFilter.selectByValue(status);
        click(filterButton);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filter orders by date range
     */
    public void filterByDateRange(String dateFrom, String dateTo) {
        type(dateFromInput, dateFrom);
        type(dateToInput, dateTo);
        click(filterButton);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Export report
     */
    public void exportReport() {
        click(exportReportButton);
    }

    /**
     * View analytics
     */
    public void viewAnalytics() {
        click(viewAnalyticsButton);
    }

    /**
     * Get number of orders in list
     */
    public int getOrderCount() {
        return orderItems.size();
    }

    /**
     * Get order status
     */
    public String getOrderStatus() {
        return getText(orderStatus);
    }

    /**
     * Get order total
     */
    public String getOrderTotal() {
        return getText(orderTotal);
    }

    /**
     * Get order date
     */
    public String getOrderDate() {
        return getText(orderDate);
    }

    /**
     * Check if order list is displayed
     */
    public boolean isOrderListDisplayed() {
        return isDisplayed(orderList);
    }

    /**
     * Check if order details is displayed
     */
    public boolean isOrderDetailsDisplayed() {
        return isDisplayed(orderDetails);
    }

    /**
     * Check if empty orders message is displayed
     */
    public boolean isEmptyOrdersDisplayed() {
        return isDisplayed(emptyOrdersMessage);
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
     * Check if cancel button is displayed
     */
    public boolean isCancelButtonDisplayed() {
        return isDisplayed(cancelOrderButton);
    }

    /**
     * Check if reorder button is displayed
     */
    public boolean isReorderButtonDisplayed() {
        return isDisplayed(reorderButton);
    }

    /**
     * Check if update status button is displayed (Staff/Manager/Admin)
     */
    public boolean isUpdateStatusButtonDisplayed() {
        return isDisplayed(updateStatusButton);
    }

    /**
     * Check if statistics is displayed (Manager/Admin)
     */
    public boolean isStatisticsDisplayed() {
        return isDisplayed(orderStatistics);
    }

    /**
     * Get current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
