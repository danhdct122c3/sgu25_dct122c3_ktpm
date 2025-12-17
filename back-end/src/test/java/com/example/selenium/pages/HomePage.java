package com.example.selenium.pages;

import com.example.selenium.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object cho Home Page
 */
public class HomePage extends BasePage {

    // Locators
    @FindBy(className = "user-menu")
    private WebElement userMenu;

    @FindBy(xpath = "//button[contains(text(),'Logout')]")
    private WebElement logoutButton;

    @FindBy(xpath = "//input[@placeholder='Tìm kiếm...']")
    private WebElement searchInput;

    @FindBy(className = "search-btn")
    private WebElement searchButton;

    @FindBy(css = ".rounded-lg.border.bg-card.text-card-foreground")
    private List<WebElement> productCards;

    @FindBy(className = "cart-icon")
    private WebElement cartIcon;

    @FindBy(className = "cart-badge")
    private WebElement cartBadge;

    @FindBy(className = "brand-filter")
    private WebElement brandFilter;

    @FindBy(className = "price-min")
    private WebElement priceMinInput;

    @FindBy(className = "price-max")
    private WebElement priceMaxInput;

    @FindBy(className = "filter-btn")
    private WebElement filterButton;

    @FindBy(id = "sort-select")
    private WebElement sortDropdown;

    @FindBy(xpath = "//a[contains(text(),'Products') or contains(text(),'Sản phẩm') or contains(@href,'/products')]")
    private WebElement productsNavLink;

    public HomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Mở trang home
     */
    public void open() {
        navigateTo(baseUrl + "/");
    }

    /**
     * Kiểm tra user đã login chưa
     */
    public boolean isUserLoggedIn() {
        return isDisplayed(userMenu);
    }

    /**
     * Đăng xuất
     */
    public void logout() {
        click(userMenu);
        click(logoutButton);
    }

    /**
     * Tìm kiếm sản phẩm (tự động tìm khi gõ)
     */
    public void searchProduct(String keyword) {
        type(searchInput, keyword);
        // Không cần click button, trang tự động tìm kiếm
    }

    /**
     * Lấy số lượng sản phẩm hiển thị
     */
    public int getProductCount() {
        return productCards.size();
    }

    /**
     * Click vào button "Xem chi tiết" của sản phẩm đầu tiên
     */
    public void clickFirstProduct() {
        // Find first "Xem chi tiết" button within product cards
        WebElement firstViewButton = driver.findElement(
            By.xpath("//button[contains(text(),'Xem chi tiết')]")
        );
        click(firstViewButton);
    }

    /**
     * Click vào giỏ hàng
     */
    public void clickCartIcon() {
        click(cartIcon);
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ
     */
    public int getCartCount() {
        try {
            String count = getText(cartBadge);
            return Integer.parseInt(count);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Filter theo brand
     */
    public void filterByBrand(String brand) {
        click(brandFilter);
        // Select brand from dropdown
        // Implementation depends on your UI
    }

    /**
     * Filter theo khoảng giá
     */
    public void filterByPriceRange(String minPrice, String maxPrice) {
        if (minPrice != null && !minPrice.isEmpty()) {
            type(priceMinInput, minPrice);
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            type(priceMaxInput, maxPrice);
        }
        click(filterButton);
    }

    /**
     * Sắp xếp sản phẩm
     */
    public void sortProducts(String sortBy) {
        click(sortDropdown);
        // Select sort option
        // Implementation depends on your UI
    }

    /**
     * Kiểm tra có sản phẩm nào không
     */
    public boolean hasProducts() {
        return getProductCount() > 0;
    }

    /**
     * Click vào Products link trong navbar
     */
    public void clickProductsNavLink() {
        click(productsNavLink);
    }
}
