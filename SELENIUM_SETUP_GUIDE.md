# Hướng Dẫn Setup Selenium Testing

## 1. Cài Đặt Môi Trường

### 1.1. Cài đặt Java JDK
```bash
# Kiểm tra Java version
java -version

# Cần Java 11 trở lên
```

### 1.2. Cài đặt Maven (nếu dùng Java)
```bash
# Kiểm tra Maven
mvn -version
```

### 1.3. Hoặc cài Python + pip (nếu dùng Python)
```bash
# Kiểm tra Python
python --version

# Cài Selenium cho Python
pip install selenium
pip install pytest
pip install pytest-html
```

## 2. Cài Đặt WebDriver

### 2.1. Tự động với WebDriverManager (Recommended)

**Java:**
```xml
<!-- Thêm vào pom.xml -->
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.6.2</version>
</dependency>
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.16.1</version>
</dependency>
```

**Python:**
```bash
pip install webdriver-manager
```

### 2.2. Hoặc download thủ công
- ChromeDriver: https://chromedriver.chromium.org/
- GeckoDriver (Firefox): https://github.com/mozilla/geckodriver/releases
- EdgeDriver: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/

## 3. Cấu Trúc Thư Mục Test

```
selenium-tests/
├── config/
│   ├── test.properties          # Cấu hình test (base URL, timeout, etc.)
│   └── browsers.properties      # Cấu hình browser
├── data/
│   ├── login_data.csv           # Test data đăng nhập
│   ├── product_data.csv         # Test data sản phẩm
│   └── checkout_data.csv        # Test data checkout
├── pages/                       # Page Object Model
│   ├── BasePage.java/py
│   ├── LoginPage.java/py
│   ├── HomePage.java/py
│   ├── ProductPage.java/py
│   ├── CartPage.java/py
│   └── CheckoutPage.java/py
├── tests/                       # Test cases
│   ├── AuthenticationTest.java/py
│   ├── ProductTest.java/py
│   ├── CartTest.java/py
│   └── CheckoutTest.java/py
├── utils/                       # Utilities
│   ├── DriverFactory.java/py    # WebDriver initialization
│   ├── ConfigReader.java/py     # Đọc config
│   ├── CSVReader.java/py        # Đọc test data
│   └── Screenshot.java/py       # Chụp màn hình khi fail
└── reports/                     # Test reports
    ├── screenshots/
    └── html-reports/
```

## 4. Page Object Model (POM) Pattern

### 4.1. BasePage (Java)
```java
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }
    
    protected void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }
    
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 4.2. LoginPage (Java)
```java
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {
    
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
    
    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }
    
    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }
    
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    public void clickRegister() {
        click(registerLink);
    }
    
    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }
}
```

### 4.3. BasePage (Python)
```python
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

class BasePage:
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)
    
    def click(self, locator):
        element = self.wait.until(EC.element_to_be_clickable(locator))
        element.click()
    
    def type(self, locator, text):
        element = self.wait.until(EC.visibility_of_element_located(locator))
        element.clear()
        element.send_keys(text)
    
    def get_text(self, locator):
        element = self.wait.until(EC.visibility_of_element_located(locator))
        return element.text
    
    def is_displayed(self, locator):
        try:
            element = self.driver.find_element(*locator)
            return element.is_displayed()
        except:
            return False
```

### 4.4. LoginPage (Python)
```python
from selenium.webdriver.common.by import By
from pages.base_page import BasePage

class LoginPage(BasePage):
    # Locators
    USERNAME_INPUT = (By.ID, "username")
    PASSWORD_INPUT = (By.ID, "password")
    LOGIN_BUTTON = (By.XPATH, "//button[@type='submit']")
    ERROR_MESSAGE = (By.CLASS_NAME, "error-message")
    REGISTER_LINK = (By.LINK_TEXT, "Register")
    
    def __init__(self, driver):
        super().__init__(driver)
    
    def login(self, username, password):
        self.type(self.USERNAME_INPUT, username)
        self.type(self.PASSWORD_INPUT, password)
        self.click(self.LOGIN_BUTTON)
    
    def get_error_message(self):
        return self.get_text(self.ERROR_MESSAGE)
    
    def click_register(self):
        self.click(self.REGISTER_LINK)
    
    def is_login_button_displayed(self):
        return self.is_displayed(self.LOGIN_BUTTON)
```

## 5. Test Case Example

### 5.1. AuthenticationTest (Java with TestNG)
```java
package tests;

import org.testng.annotations.*;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.HomePage;
import utils.DriverFactory;

public class AuthenticationTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private HomePage homePage;
    
    @BeforeClass
    public void setUp() {
        driver = DriverFactory.getDriver("chrome");
        driver.get("http://localhost:3000/login");
    }
    
    @Test(priority = 1)
    public void testLoginWithValidCredentials() {
        loginPage = new LoginPage(driver);
        loginPage.login("admin", "admin123");
        
        homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isUserLoggedIn());
    }
    
    @Test(priority = 2)
    public void testLoginWithInvalidCredentials() {
        loginPage = new LoginPage(driver);
        loginPage.login("wronguser", "wrongpass");
        
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertEquals(errorMsg, "Invalid username or password");
    }
    
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### 5.2. test_authentication.py (Python with pytest)
```python
import pytest
from selenium import webdriver
from pages.login_page import LoginPage
from pages.home_page import HomePage

class TestAuthentication:
    
    @pytest.fixture(autouse=True)
    def setup(self):
        self.driver = webdriver.Chrome()
        self.driver.get("http://localhost:3000/login")
        self.driver.maximize_window()
        yield
        self.driver.quit()
    
    def test_login_with_valid_credentials(self):
        login_page = LoginPage(self.driver)
        login_page.login("admin", "admin123")
        
        home_page = HomePage(self.driver)
        assert home_page.is_user_logged_in()
    
    def test_login_with_invalid_credentials(self):
        login_page = LoginPage(self.driver)
        login_page.login("wronguser", "wrongpass")
        
        error_msg = login_page.get_error_message()
        assert error_msg == "Invalid username or password"
```

## 6. WebDriver Factory

### 6.1. DriverFactory.java
```java
package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
    
    public static WebDriver getDriver(String browserName) {
        WebDriver driver;
        
        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Browser not supported: " + browserName);
        }
        
        driver.manage().window().maximize();
        return driver;
    }
}
```

### 6.2. driver_factory.py
```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager

class DriverFactory:
    
    @staticmethod
    def get_driver(browser_name="chrome"):
        if browser_name.lower() == "chrome":
            service = Service(ChromeDriverManager().install())
            driver = webdriver.Chrome(service=service)
        elif browser_name.lower() == "firefox":
            service = Service(GeckoDriverManager().install())
            driver = webdriver.Firefox(service=service)
        else:
            raise ValueError(f"Browser not supported: {browser_name}")
        
        driver.maximize_window()
        return driver
```

## 7. Chạy Test

### 7.1. Java với Maven
```bash
# Chạy tất cả tests
mvn clean test

# Chạy test cụ thể
mvn test -Dtest=AuthenticationTest

# Chạy với browser khác
mvn test -Dbrowser=firefox

# Generate report
mvn surefire-report:report
```

### 7.2. Python với pytest
```bash
# Chạy tất cả tests
pytest

# Chạy test cụ thể
pytest tests/test_authentication.py

# Chạy với HTML report
pytest --html=reports/report.html

# Chạy parallel
pytest -n 4

# Chạy với markers
pytest -m smoke
```

## 8. Best Practices

### 8.1. Locator Strategy Priority
1. **ID** - Nhanh nhất, unique
2. **Name** - Khá nhanh
3. **CSS Selector** - Linh hoạt, nhanh
4. **XPath** - Mạnh nhất nhưng chậm hơn
5. **Link Text / Partial Link Text** - Chỉ cho links
6. **Class Name** - Tránh nếu không unique
7. **Tag Name** - Tránh sử dụng

### 8.2. Wait Strategies
```java
// Implicit Wait - áp dụng cho tất cả elements
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

// Explicit Wait - chờ điều kiện cụ thể
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("element")));

// Fluent Wait - custom polling
Wait<WebDriver> wait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofSeconds(5))
    .ignoring(NoSuchElementException.class);
```

### 8.3. Screenshot on Failure
```java
@AfterMethod
public void takeScreenshotOnFailure(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String dest = "reports/screenshots/" + result.getName() + ".png";
        FileUtils.copyFile(source, new File(dest));
    }
}
```

```python
@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    outcome = yield
    report = outcome.get_result()
    if report.when == "call" and report.failed:
        driver = item.funcargs.get('driver')
        if driver:
            driver.save_screenshot(f"reports/screenshots/{item.name}.png")
```

## 9. Configuration File

### test.properties
```properties
# Base URL
base.url=http://localhost:3000

# Browser
browser=chrome

# Timeout
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Test Data
test.data.path=data/

# Screenshots
screenshot.path=reports/screenshots/
take.screenshot.on.failure=true

# Reports
report.path=reports/html-reports/
```

## 10. Data-Driven Testing

### Đọc CSV File (Java)
```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() throws IOException {
    String csvFile = "data/login_data.csv";
    List<String[]> data = CSVReader.readCSV(csvFile);
    
    Object[][] testData = new Object[data.size()][2];
    for (int i = 0; i < data.size(); i++) {
        testData[i][0] = data.get(i)[0]; // username
        testData[i][1] = data.get(i)[1]; // password
    }
    return testData;
}

@Test(dataProvider = "loginData")
public void testLoginWithMultipleUsers(String username, String password) {
    loginPage.login(username, password);
    Assert.assertTrue(homePage.isUserLoggedIn());
}
```

### Đọc CSV File (Python)
```python
import csv
import pytest

def read_csv(file_path):
    with open(file_path, 'r') as file:
        reader = csv.DictReader(file)
        return list(reader)

@pytest.mark.parametrize("data", read_csv("data/login_data.csv"))
def test_login_with_multiple_users(setup, data):
    login_page = LoginPage(setup)
    login_page.login(data['username'], data['password'])
    
    home_page = HomePage(setup)
    assert home_page.is_user_logged_in()
```

## 11. Parallel Execution

### TestNG Parallel (testng.xml)
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Test Suite" parallel="tests" thread-count="3">
    <test name="Chrome Tests">
        <parameter name="browser" value="chrome"/>
        <classes>
            <class name="tests.AuthenticationTest"/>
            <class name="tests.ProductTest"/>
        </classes>
    </test>
    <test name="Firefox Tests">
        <parameter name="browser" value="firefox"/>
        <classes>
            <class name="tests.AuthenticationTest"/>
            <class name="tests.ProductTest"/>
        </classes>
    </test>
</suite>
```

### pytest Parallel
```bash
# Cài pytest-xdist
pip install pytest-xdist

# Chạy với 4 workers
pytest -n 4
```

## 12. CI/CD Integration

### GitHub Actions
```yaml
name: Selenium Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up Python
      uses: actions/setup-python@v2
      with:
        python-version: '3.9'
    
    - name: Install dependencies
      run: |
        pip install -r requirements.txt
    
    - name: Run Selenium tests
      run: |
        pytest --html=reports/report.html
    
    - name: Upload test results
      uses: actions/upload-artifact@v2
      if: always()
      with:
        name: test-results
        path: reports/
```

## 13. Troubleshooting

### Common Issues

1. **Element not found**
   - Tăng wait time
   - Kiểm tra locator đúng chưa
   - Element có trong iframe không?

2. **Stale element reference**
   - Re-find element sau khi page refresh
   - Dùng explicit wait

3. **Browser không mở**
   - Kiểm tra ChromeDriver version vs Chrome version
   - Dùng WebDriverManager

4. **Test chạy chậm**
   - Giảm implicit wait
   - Dùng CSS selector thay vì XPath
   - Chạy parallel

5. **Screenshot không chụp được**
   - Kiểm tra path tồn tại
   - Có quyền write vào folder không

## 14. Resources

- Selenium Documentation: https://www.selenium.dev/documentation/
- WebDriverManager: https://github.com/bonigarcia/webdrivermanager
- TestNG: https://testng.org/
- pytest: https://docs.pytest.org/
- Page Object Model: https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/
