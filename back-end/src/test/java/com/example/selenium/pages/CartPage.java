package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object cho Cart Page
 */
public class CartPage extends BasePage {

    // Locators
    @FindBy(className = "cart-item")
    private List<WebElement> cartItems;

    @FindBy(className = "item-name")
    private List<WebElement> itemNames;

    @FindBy(className = "item-price")
    private List<WebElement> itemPrices;

    @FindBy(className = "item-quantity")
    private List<WebElement> itemQuantities;

    @FindBy(className = "item-size")
    private List<WebElement> itemSizes;

    @FindBy(className = "increase-qty")
    private List<WebElement> increaseButtons;

    @FindBy(className = "decrease-qty")
    private List<WebElement> decreaseButtons;

    @FindBy(className = "remove-item")
    private List<WebElement> removeButtons;

    @FindBy(className = "total-price")
    private WebElement totalPrice;

    @FindBy(className = "subtotal")
    private WebElement subtotal;

    @FindBy(className = "discount-input")
    private WebElement discountInput;

    @FindBy(className = "apply-discount")
    private WebElement applyDiscountButton;

    @FindBy(className = "discount-message")
    private WebElement discountMessage;

    @FindBy(className = "checkout-btn")
    private WebElement checkoutButton;

    @FindBy(className = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(className = "empty-cart")
    private WebElement emptyCartMessage;

    @FindBy(className = "clear-cart")
    private WebElement clearCartButton;

    public CartPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang giỏ hàng
     */
    public void open() {
        navigateTo(baseUrl + "/cart");
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ
     */
    public int getCartItemsCount() {
        return cartItems.size();
    }

    /**
     * Lấy tên sản phẩm theo index
     */
    public String getItemName(int index) {
        if (index < itemNames.size()) {
            return getText(itemNames.get(index));
        }
        return "";
    }

    /**
     * Lấy giá sản phẩm theo index
     */
    public String getItemPrice(int index) {
        if (index < itemPrices.size()) {
            return getText(itemPrices.get(index));
        }
        return "";
    }

    /**
     * Lấy số lượng sản phẩm theo index
     */
    public int getItemQuantity(int index) {
        if (index < itemQuantities.size()) {
            String qty = getText(itemQuantities.get(index));
            return Integer.parseInt(qty);
        }
        return 0;
    }

    /**
     * Tăng số lượng sản phẩm
     */
    public void increaseQuantity(int index) {
        if (index < increaseButtons.size()) {
            click(increaseButtons.get(index));
        }
    }

    /**
     * Giảm số lượng sản phẩm
     */
    public void decreaseQuantity(int index) {
        if (index < decreaseButtons.size()) {
            click(decreaseButtons.get(index));
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    public void removeItem(int index) {
        if (index < removeButtons.size()) {
            click(removeButtons.get(index));
        }
    }

    /**
     * Lấy tổng tiền
     */
    public String getTotalPrice() {
        return getText(totalPrice);
    }

    /**
     * Lấy tổng tiền dạng số
     */
    public double getTotalPriceAsNumber() {
        String price = getText(totalPrice);
        // Remove currency symbols and convert to number
        price = price.replaceAll("[^0-9.]", "");
        return Double.parseDouble(price);
    }

    /**
     * Apply mã giảm giá
     */
    public void applyDiscountCode(String code) {
        type(discountInput, code);
        click(applyDiscountButton);
    }

    /**
     * Lấy discount message
     */
    public String getDiscountMessage() {
        return getText(discountMessage);
    }

    /**
     * Kiểm tra discount có được apply không
     */
    public boolean isDiscountApplied() {
        return isDisplayed(discountMessage);
    }

    /**
     * Tiếp tục mua hàng
     */
    public void continueShopping() {
        click(continueShoppingButton);
    }

    /**
     * Proceed to checkout
     */
    public void proceedToCheckout() {
        click(checkoutButton);
    }

    /**
     * Kiểm tra giỏ hàng trống
     */
    public boolean isCartEmpty() {
        return isDisplayed(emptyCartMessage);
    }

    /**
     * Xóa tất cả sản phẩm
     */
    public void clearCart() {
        click(clearCartButton);
    }

    /**
     * Kiểm tra checkout button có enabled không
     */
    public boolean isCheckoutButtonEnabled() {
        return checkoutButton.isEnabled();
    }
}
