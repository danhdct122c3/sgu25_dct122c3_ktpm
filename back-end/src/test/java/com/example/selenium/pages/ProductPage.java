package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object cho Product Detail Page
 */
public class ProductPage extends BasePage {

    // Locators
    @FindBy(className = "product-name")
    private WebElement productName;

    @FindBy(className = "product-price")
    private WebElement productPrice;

    // Size buttons are found dynamically by text
    
    @FindBy(className = "quantity-display")
    private WebElement quantityDisplay;

    @FindBy(className = "increase-qty")
    private WebElement increaseQtyButton;

    @FindBy(className = "decrease-qty")
    private WebElement decreaseQtyButton;

    @FindBy(xpath = "//button[contains(text(),'Thêm vào giỏ hàng')]")
    private WebElement addToCartButton;

    @FindBy(className = "stock-status")
    private WebElement stockStatus;

    @FindBy(className = "success-message")
    private WebElement successMessage;

    @FindBy(className = "product-image")
    private WebElement productImage;

    @FindBy(className = "next-image")
    private WebElement nextImageButton;

    @FindBy(className = "prev-image")
    private WebElement prevImageButton;

    public ProductPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Lấy tên sản phẩm
     */
    public String getProductName() {
        return getText(productName);
    }

    /**
     * Lấy giá sản phẩm
     */
    public String getProductPrice() {
        return getText(productPrice);
    }

    /**
     * Chọn size (click vào button size)
     */
    public void selectSize(String size) {
        try {
            // Wait for size buttons to be present
            wait.until(driver -> {
                java.util.List<WebElement> sizeButtons = driver.findElements(
                    By.xpath("//button[contains(@class,'w-10 h-10')]"));
                return sizeButtons.size() > 0;
            });
            
            // Find and click the size button (more flexible matching)
            WebElement sizeButton = wait.until(
                driver -> driver.findElement(
                    By.xpath("//button[contains(@class,'w-10 h-10') and normalize-space(text())='" + size + "']")
                )
            );
            
            click(sizeButton);
            
            // Wait a bit after clicking
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            
        } catch (Exception e) {
            System.err.println("Error selecting size: " + size);
            System.err.println("Available sizes: ");
            
            // Print available sizes for debugging
            java.util.List<WebElement> allSizes = driver.findElements(
                By.xpath("//button[contains(@class,'w-10 h-10')]"));
            for (WebElement btn : allSizes) {
                System.err.println("  - '" + btn.getText() + "'");
            }
            throw e;
        }
    }

    /**
     * Set số lượng (bằng cách click button tăng/giảm)
     */
    public void setQuantity(int quantity) {
        try {
            // Wait for quantity controls to be visible
            wait.until(driver -> increaseQtyButton.isDisplayed() && decreaseQtyButton.isDisplayed());
            
            int currentQty = getCurrentQuantity();
            System.out.println("Current quantity: " + currentQty + ", Target: " + quantity);
            
            if (quantity > currentQty) {
                // Click tăng
                for (int i = 0; i < (quantity - currentQty); i++) {
                    click(increaseQtyButton);
                    try { Thread.sleep(500); } catch (InterruptedException e) {}
                }
            } else if (quantity < currentQty) {
                // Click giảm
                for (int i = 0; i < (currentQty - quantity); i++) {
                    click(decreaseQtyButton);
                    try { Thread.sleep(500); } catch (InterruptedException e) {}
                }
            }
            
            System.out.println("Final quantity: " + getCurrentQuantity());
        } catch (Exception e) {
            System.err.println("Error setting quantity to: " + quantity);
            throw e;
        }
    }

    /**
     * Tăng số lượng
     */
    public void increaseQuantity() {
        click(increaseQtyButton);
    }

    /**
     * Giảm số lượng
     */
    public void decreaseQuantity() {
        click(decreaseQtyButton);
    }

    /**
     * Lấy số lượng hiện tại (đọc từ text hiển thị)
     */
    public int getCurrentQuantity() {
        String qty = getText(quantityDisplay).trim();
        return Integer.parseInt(qty);
    }

    /**
     * Thêm vào giỏ hàng
     */
    public void addToCart() {
        try {
            // Wait for add to cart button to be clickable
            wait.until(driver -> addToCartButton.isDisplayed() && addToCartButton.isEnabled());
            
            System.out.println("Clicking 'Add to Cart' button...");
            click(addToCartButton);
            
            // Wait for the action to complete
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            
            System.out.println("Product added to cart successfully");
        } catch (Exception e) {
            System.err.println("Error adding product to cart");
            throw e;
        }
    }

    /**
     * Kiểm tra còn hàng không
     */
    public boolean isInStock() {
        String status = getText(stockStatus);
        return status.contains("In Stock") || status.contains("Còn hàng");
    }

    /**
     * Kiểm tra success message có hiển thị không
     */
    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(successMessage);
    }

    /**
     * Lấy success message
     */
    public String getSuccessMessage() {
        return getText(successMessage);
    }

    /**
     * Click ảnh tiếp theo
     */
    public void clickNextImage() {
        click(nextImageButton);
    }

    /**
     * Click ảnh trước đó
     */
    public void clickPrevImage() {
        click(prevImageButton);
    }

    /**
     * Kiểm tra Add to Cart button có enabled không
     */
    public boolean isAddToCartButtonEnabled() {
        return addToCartButton.isEnabled();
    }
}
