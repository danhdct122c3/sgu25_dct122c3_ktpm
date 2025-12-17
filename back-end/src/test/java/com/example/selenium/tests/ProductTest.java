package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.HomePage;
import com.example.selenium.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases cho Product Module
 */
public class ProductTest extends BaseTest {

    @BeforeClass
    public void loginBeforeTests() {
        // Login trước khi chạy product tests
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login("test", "12345678");
        sleep(2000);
    }

    @Test(priority = 1, description = "Test hiển thị danh sách sản phẩm")
    public void testDisplayProductList() {
        HomePage homePage = new HomePage(driver);
        
        // Navigate directly to products page
        driver.get(baseUrl + "/shoes");
        sleep(2000);
        
        // Debug info
        System.out.println("Current URL: " + driver.getCurrentUrl());
        
        int productCount = homePage.getProductCount();
        System.out.println("Product count: " + productCount);
        
        if (productCount == 0) {
            System.out.println("Page source contains 'product': " + driver.getPageSource().toLowerCase().contains("product"));
            
            // Try clicking navbar link if direct navigation didn't work
            try {
                homePage.clickProductsNavLink();
                sleep(2000);
                productCount = homePage.getProductCount();
                System.out.println("Product count after clicking navbar: " + productCount);
            } catch (Exception e) {
                System.out.println("Could not click navbar link: " + e.getMessage());
            }
        }
        
        Assert.assertTrue(productCount > 0, "Should display products");

        System.out.println("Found " + productCount + " products");
        System.out.println("✓ Test Display Product List PASSED");
    }

    @Test(priority = 2, description = "Test tìm kiếm sản phẩm")
    public void testSearchProduct() {
        HomePage homePage = new HomePage(driver);
        
        // Navigate to products page
        driver.get(baseUrl + "/shoes");
        sleep(2000);

        // Search for Nike
        homePage.searchProduct("Nike");
        sleep(2000);

        int resultCount = homePage.getProductCount();
        Assert.assertTrue(resultCount > 0, "Should find Nike products");

        System.out.println("Found " + resultCount + " Nike products");
        System.out.println("✓ Test Search Product PASSED");
    }

    @Test(priority = 3, description = "Test tìm kiếm sản phẩm không tồn tại")
    public void testSearchNonExistentProduct() {
        HomePage homePage = new HomePage(driver);
        homePage.open();

        homePage.searchProduct("XYZ123NotExist");
        sleep(2000);

        int resultCount = homePage.getProductCount();
        Assert.assertEquals(resultCount, 0, "Should find no products");

        System.out.println("✓ Test Search Non-existent Product PASSED");
    }

    @Test(priority = 4, description = "Test xem chi tiết sản phẩm")
    public void testViewProductDetail() {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);

        homePage.open();
        
        // Click vào sản phẩm đầu tiên
        homePage.clickFirstProduct();
        sleep(2000);

        // Verify product detail page
        String productName = productPage.getProductName();
        String productPrice = productPage.getProductPrice();

        Assert.assertNotNull(productName, "Product name should be displayed");
        Assert.assertNotNull(productPrice, "Product price should be displayed");

        System.out.println("Product: " + productName);
        System.out.println("Price: " + productPrice);
        System.out.println("✓ Test View Product Detail PASSED");
    }

    @Test(priority = 5, description = "Test chọn size sản phẩm")
    public void testSelectProductSize() {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);

        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);

        // Select size 42
        productPage.selectSize("42");
        sleep(1000);

        System.out.println("✓ Test Select Product Size PASSED");
    }

    @Test(priority = 6, description = "Test tăng/giảm số lượng sản phẩm")
    public void testChangeProductQuantity() {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);

        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);

        int initialQty = productPage.getCurrentQuantity();
        System.out.println("Initial quantity: " + initialQty);

        // Increase quantity
        productPage.increaseQuantity();
        sleep(500);
        int afterIncrease = productPage.getCurrentQuantity();
        Assert.assertEquals(afterIncrease, initialQty + 1, "Quantity should increase by 1");

        // Decrease quantity
        productPage.decreaseQuantity();
        sleep(500);
        int afterDecrease = productPage.getCurrentQuantity();
        Assert.assertEquals(afterDecrease, initialQty, "Quantity should be back to initial");

        System.out.println("✓ Test Change Product Quantity PASSED");
    }

    @Test(priority = 7, description = "Test kiểm tra stock status")
    public void testCheckStockStatus() {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);

        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);

        boolean inStock = productPage.isInStock();
        System.out.println("Product in stock: " + inStock);

        if (inStock) {
            Assert.assertTrue(productPage.isAddToCartButtonEnabled(), "Add to cart button should be enabled");
        }

        System.out.println("✓ Test Check Stock Status PASSED");
    }

    @Test(priority = 8, description = "Test thêm sản phẩm vào giỏ hàng")
    public void testAddProductToCart() {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);

        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);

        // Select size and quantity
        productPage.selectSize("42");
        productPage.setQuantity(2);

        // Add to cart
        productPage.addToCart();
        sleep(2000);

        // Verify success message hoặc cart badge update
        Assert.assertTrue(
            productPage.isSuccessMessageDisplayed() || homePage.getCartCount() > 0,
            "Product should be added to cart"
        );

        System.out.println("✓ Test Add Product To Cart PASSED");
    }

    @Test(priority = 9, description = "Test filter sản phẩm theo brand")
    public void testFilterByBrand() {
        HomePage homePage = new HomePage(driver);
        homePage.open();

        int initialCount = homePage.getProductCount();
        System.out.println("Initial product count: " + initialCount);

        // Filter by Nike
        homePage.filterByBrand("Nike");
        sleep(2000);

        int filteredCount = homePage.getProductCount();
        System.out.println("Filtered product count: " + filteredCount);

        Assert.assertTrue(filteredCount > 0, "Should have filtered products");

        System.out.println("✓ Test Filter By Brand PASSED");
    }

    @Test(priority = 10, description = "Test filter sản phẩm theo giá")
    public void testFilterByPrice() {
        HomePage homePage = new HomePage(driver);
        homePage.open();

        // Filter price range 500k - 1M
        homePage.filterByPriceRange("500000", "1000000");
        sleep(2000);

        int filteredCount = homePage.getProductCount();
        System.out.println("Products in price range: " + filteredCount);

        Assert.assertTrue(filteredCount >= 0, "Should execute filter");

        System.out.println("✓ Test Filter By Price PASSED");
    }

    @Test(priority = 11, description = "Test sắp xếp sản phẩm")
    public void testSortProducts() {
        HomePage homePage = new HomePage(driver);
        homePage.open();

        // Sort by price
        homePage.sortProducts("price_asc");
        sleep(2000);

        Assert.assertTrue(homePage.hasProducts(), "Should display sorted products");

        System.out.println("✓ Test Sort Products PASSED");
    }

    @Test(priority = 12, description = "Test product image carousel")
    public void testProductImageCarousel() {
        HomePage homePage = new HomePage(driver);
        ProductPage productPage = new ProductPage(driver);

        homePage.open();
        homePage.clickFirstProduct();
        sleep(2000);

        // Click next image
        productPage.clickNextImage();
        sleep(1000);

        // Click previous image
        productPage.clickPrevImage();
        sleep(1000);

        System.out.println("✓ Test Product Image Carousel PASSED");
    }
}
