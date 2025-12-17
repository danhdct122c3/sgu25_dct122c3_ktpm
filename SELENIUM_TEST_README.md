# Selenium Test Suite

## 📁 Cấu trúc đã tạo

```
back-end/src/test/java/com/example/selenium/
├── BasePage.java                    # Base Page Object - common methods
├── pages/
│   ├── LoginPage.java              # Login page objects & methods
│   ├── HomePage.java               # Home page objects & methods
│   ├── ProductPage.java            # Product detail page
│   ├── CartPage.java               # Shopping cart page
│   └── CheckoutPage.java           # Checkout page
├── utils/
│   ├── DriverFactory.java          # WebDriver initialization
│   └── TestDataReader.java         # CSV reader utility
└── tests/
    ├── BaseTest.java               # Base test class với setup/teardown
    ├── AuthenticationTest.java     # 10 test cases cho Login/Logout
    ├── ProductTest.java            # 12 test cases cho Products
    ├── CartTest.java               # 12 test cases cho Cart
    └── CheckoutTest.java           # 10 test cases cho Checkout
```

## 🎯 Test Cases Summary

### 1. **AuthenticationTest** (10 tests)
- ✅ Login thành công Admin
- ✅ Login thành công Customer
- ✅ Login thất bại - Username không tồn tại
- ✅ Login thất bại - Sai password
- ✅ Login thất bại - Username trống
- ✅ Login thất bại - Password trống
- ✅ Remember Me checkbox
- ✅ Show/Hide password
- ✅ Logout
- ✅ Data-driven test từ CSV

### 2. **ProductTest** (12 tests)
- ✅ Hiển thị danh sách sản phẩm
- ✅ Tìm kiếm sản phẩm
- ✅ Tìm kiếm sản phẩm không tồn tại
- ✅ Xem chi tiết sản phẩm
- ✅ Chọn size
- ✅ Tăng/giảm số lượng
- ✅ Kiểm tra stock status
- ✅ Thêm vào giỏ hàng
- ✅ Filter theo brand
- ✅ Filter theo giá
- ✅ Sắp xếp sản phẩm
- ✅ Image carousel

### 3. **CartTest** (12 tests)
- ✅ Xem giỏ hàng
- ✅ Hiển thị thông tin sản phẩm
- ✅ Tính tổng tiền
- ✅ Tăng số lượng trong giỏ
- ✅ Giảm số lượng trong giỏ
- ✅ Xóa sản phẩm
- ✅ Apply mã giảm giá hợp lệ
- ✅ Apply mã giảm giá không hợp lệ
- ✅ Tiếp tục mua hàng
- ✅ Proceed to checkout
- ✅ Giỏ hàng trống
- ✅ Xóa tất cả sản phẩm

### 4. **CheckoutTest** (10 tests)
- ✅ Mở trang checkout
- ✅ Hiển thị order summary
- ✅ Checkout thành công COD
- ✅ Checkout với thông tin trống
- ✅ Checkout với số điện thoại không hợp lệ
- ✅ Chọn phương thức VNPay
- ✅ Thêm ghi chú đơn hàng
- ✅ Quay lại giỏ hàng
- ✅ Data-driven test từ CSV
- ✅ Checkout với giỏ hàng trống

**Tổng: 44 test cases**

## 🚀 Chạy Tests

### 1. Cài dependencies
```bash
cd back-end
mvn clean install
```

### 2. Chạy tất cả tests
```bash
mvn test
```

### 3. Chạy test class cụ thể
```bash
# Chạy authentication tests
mvn test -Dtest=AuthenticationTest

# Chạy product tests
mvn test -Dtest=ProductTest

# Chạy cart tests
mvn test -Dtest=CartTest

# Chạy checkout tests
mvn test -Dtest=CheckoutTest
```

### 4. Chạy test method cụ thể
```bash
mvn test -Dtest=AuthenticationTest#testLoginSuccessfulAdmin
```

### 5. Chạy với browser khác
```bash
# Chrome (default)
mvn test

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge

# Headless mode
mvn test -Dheadless=true
```

### 6. Chạy với TestNG XML
Tạo file `testng.xml`:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Selenium Test Suite">
    <test name="All Tests">
        <classes>
            <class name="com.example.selenium.tests.AuthenticationTest"/>
            <class name="com.example.selenium.tests.ProductTest"/>
            <class name="com.example.selenium.tests.CartTest"/>
            <class name="com.example.selenium.tests.CheckoutTest"/>
        </classes>
    </test>
</suite>
```

Chạy:
```bash
mvn test -DsuiteXmlFile=testng.xml
```

## 📊 Test Reports

### 1. Maven Surefire Report
```bash
mvn surefire-report:report
```
Report: `target/site/surefire-report.html`

### 2. TestNG Report
Report tự động tạo tại: `target/surefire-reports/index.html`

### 3. Screenshots
Khi test fail, screenshot tự động lưu tại: `target/screenshots/`

## ⚙️ Configuration

### System Properties
```bash
# Browser
-Dbrowser=chrome|firefox|edge

# Headless
-Dheadless=true|false

# Base URL
-DbaseUrl=http://localhost:3000
```

### Trong code
Edit `BaseTest.java` hoặc `DriverFactory.java`:
```java
protected String baseUrl = "http://localhost:3000";
```

## 📝 Data-Driven Testing

Test sử dụng CSV files:
- `selenium_test_data_login.csv` - Login test data
- `selenium_test_data_products.csv` - Product test data
- `selenium_test_data_cart.csv` - Cart test data
- `selenium_test_data_checkout.csv` - Checkout test data

Copy các file CSV này vào thư mục gốc project.

## 🎨 Page Object Model (POM)

Tất cả tests sử dụng POM pattern:
- **Pages**: Chứa locators và methods của từng page
- **Tests**: Chỉ chứa test logic, không có locators
- **Utils**: Utilities như DriverFactory, TestDataReader

### Example Usage:
```java
LoginPage loginPage = new LoginPage(driver);
loginPage.open();
loginPage.login("admin", "admin123");

HomePage homePage = new HomePage(driver);
Assert.assertTrue(homePage.isUserLoggedIn());
```

## 🔧 Troubleshooting

### ChromeDriver version mismatch
WebDriverManager tự động download đúng version. Nếu vẫn lỗi:
```bash
# Xóa cache
rm -rf ~/.cache/selenium
```

### Element not found
- Tăng implicit wait trong `DriverFactory.java`
- Thêm explicit wait trong methods
- Kiểm tra locators có đúng không

### Test chạy chậm
- Giảm `sleep()` time
- Dùng explicit wait thay vì sleep
- Chạy headless mode

### Port 3000 không có frontend
Đảm bảo frontend đang chạy:
```bash
cd front-end
npm start
```

## 📦 Maven Dependencies

Đã thêm vào `pom.xml`:
```xml
<!-- Selenium WebDriver -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.16.1</version>
</dependency>

<!-- WebDriverManager -->
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.6.2</version>
</dependency>

<!-- TestNG -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.9.0</version>
</dependency>
```

## 🎯 Next Steps

1. **Update locators** trong các Page classes theo UI thực tế
2. **Thêm test cases** nếu cần
3. **Integrate với CI/CD** (GitHub Actions, Jenkins)
4. **Setup parallel execution** cho faster tests
5. **Add logging** với Log4j hoặc SLF4J

## 🚦 CI/CD Integration

### GitHub Actions Example:
```yaml
name: Selenium Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
    
    - name: Run Selenium Tests
      run: |
        cd back-end
        mvn clean test -Dheadless=true
    
    - name: Upload Screenshots
      uses: actions/upload-artifact@v2
      if: failure()
      with:
        name: screenshots
        path: back-end/target/screenshots/
```

---

**Happy Testing! 🎉**
