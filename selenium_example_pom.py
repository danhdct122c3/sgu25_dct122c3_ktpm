"""
Page Object Model (POM) Example
Tổ chức code theo mô hình POM để dễ maintain và reuse
"""

from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


# ============= BASE PAGE =============
class BasePage:
    """Base page chứa các methods dùng chung"""
    
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)
    
    def find_element(self, locator):
        """Tìm element với explicit wait"""
        return self.wait.until(EC.presence_of_element_located(locator))
    
    def click(self, locator):
        """Click element"""
        element = self.wait.until(EC.element_to_be_clickable(locator))
        element.click()
    
    def type(self, locator, text):
        """Nhập text vào input"""
        element = self.find_element(locator)
        element.clear()
        element.send_keys(text)
    
    def get_text(self, locator):
        """Lấy text của element"""
        element = self.find_element(locator)
        return element.text
    
    def is_displayed(self, locator):
        """Kiểm tra element có hiển thị không"""
        try:
            element = self.find_element(locator)
            return element.is_displayed()
        except:
            return False
    
    def get_current_url(self):
        """Lấy URL hiện tại"""
        return self.driver.current_url
    
    def navigate_to(self, url):
        """Điều hướng đến URL"""
        self.driver.get(url)


# ============= LOGIN PAGE =============
class LoginPage(BasePage):
    """Page Object cho Login Page"""
    
    # Locators
    USERNAME_INPUT = (By.ID, "username")
    PASSWORD_INPUT = (By.ID, "password")
    LOGIN_BUTTON = (By.XPATH, "//button[@type='submit']")
    ERROR_MESSAGE = (By.CLASS_NAME, "error-message")
    REGISTER_LINK = (By.LINK_TEXT, "Register")
    REMEMBER_CHECKBOX = (By.ID, "remember-me")
    SHOW_PASSWORD_BTN = (By.CLASS_NAME, "toggle-password")
    
    def __init__(self, driver, base_url):
        super().__init__(driver)
        self.base_url = base_url
    
    def open(self):
        """Mở login page"""
        self.navigate_to(f"{self.base_url}/login")
    
    def login(self, username, password):
        """Đăng nhập"""
        self.type(self.USERNAME_INPUT, username)
        self.type(self.PASSWORD_INPUT, password)
        self.click(self.LOGIN_BUTTON)
    
    def get_error_message(self):
        """Lấy error message"""
        return self.get_text(self.ERROR_MESSAGE)
    
    def is_error_displayed(self):
        """Kiểm tra error có hiển thị không"""
        return self.is_displayed(self.ERROR_MESSAGE)
    
    def click_register_link(self):
        """Click vào link Register"""
        self.click(self.REGISTER_LINK)
    
    def check_remember_me(self):
        """Check Remember Me checkbox"""
        self.click(self.REMEMBER_CHECKBOX)
    
    def toggle_password_visibility(self):
        """Show/Hide password"""
        self.click(self.SHOW_PASSWORD_BTN)


# ============= HOME PAGE =============
class HomePage(BasePage):
    """Page Object cho Home Page"""
    
    # Locators
    USER_MENU = (By.CLASS_NAME, "user-menu")
    LOGOUT_BUTTON = (By.XPATH, "//button[text()='Logout']")
    SEARCH_INPUT = (By.ID, "search")
    SEARCH_BUTTON = (By.CLASS_NAME, "search-btn")
    PRODUCT_CARDS = (By.CLASS_NAME, "product-card")
    CART_ICON = (By.CLASS_NAME, "cart-icon")
    CART_BADGE = (By.CLASS_NAME, "cart-badge")
    
    def __init__(self, driver, base_url):
        super().__init__(driver)
        self.base_url = base_url
    
    def is_user_logged_in(self):
        """Kiểm tra user đã login chưa"""
        return self.is_displayed(self.USER_MENU)
    
    def logout(self):
        """Đăng xuất"""
        self.click(self.USER_MENU)
        self.click(self.LOGOUT_BUTTON)
    
    def search_product(self, keyword):
        """Tìm kiếm sản phẩm"""
        self.type(self.SEARCH_INPUT, keyword)
        self.click(self.SEARCH_BUTTON)
    
    def get_product_count(self):
        """Đếm số sản phẩm hiển thị"""
        products = self.driver.find_elements(*self.PRODUCT_CARDS)
        return len(products)
    
    def click_cart_icon(self):
        """Click vào giỏ hàng"""
        self.click(self.CART_ICON)
    
    def get_cart_count(self):
        """Lấy số lượng sản phẩm trong giỏ"""
        badge = self.find_element(self.CART_BADGE)
        return int(badge.text)


# ============= PRODUCT PAGE =============
class ProductPage(BasePage):
    """Page Object cho Product Detail Page"""
    
    # Locators
    PRODUCT_NAME = (By.CLASS_NAME, "product-name")
    PRODUCT_PRICE = (By.CLASS_NAME, "product-price")
    SIZE_DROPDOWN = (By.ID, "size-select")
    QUANTITY_INPUT = (By.ID, "quantity")
    INCREASE_QTY_BTN = (By.CLASS_NAME, "increase-qty")
    DECREASE_QTY_BTN = (By.CLASS_NAME, "decrease-qty")
    ADD_TO_CART_BTN = (By.CLASS_NAME, "add-to-cart")
    STOCK_STATUS = (By.CLASS_NAME, "stock-status")
    
    def __init__(self, driver, base_url):
        super().__init__(driver)
        self.base_url = base_url
    
    def get_product_name(self):
        """Lấy tên sản phẩm"""
        return self.get_text(self.PRODUCT_NAME)
    
    def get_product_price(self):
        """Lấy giá sản phẩm"""
        price_text = self.get_text(self.PRODUCT_PRICE)
        # Extract số từ text (ví dụ: "1,000,000 VND" -> 1000000)
        return price_text.replace(",", "").replace(" VND", "")
    
    def select_size(self, size):
        """Chọn size"""
        from selenium.webdriver.support.ui import Select
        dropdown = self.find_element(self.SIZE_DROPDOWN)
        select = Select(dropdown)
        select.select_by_visible_text(str(size))
    
    def set_quantity(self, quantity):
        """Set số lượng"""
        qty_input = self.find_element(self.QUANTITY_INPUT)
        qty_input.clear()
        qty_input.send_keys(str(quantity))
    
    def increase_quantity(self):
        """Tăng số lượng"""
        self.click(self.INCREASE_QTY_BTN)
    
    def decrease_quantity(self):
        """Giảm số lượng"""
        self.click(self.DECREASE_QTY_BTN)
    
    def add_to_cart(self):
        """Thêm vào giỏ hàng"""
        self.click(self.ADD_TO_CART_BTN)
    
    def is_in_stock(self):
        """Kiểm tra còn hàng"""
        status = self.get_text(self.STOCK_STATUS)
        return "In Stock" in status


# ============= CART PAGE =============
class CartPage(BasePage):
    """Page Object cho Cart Page"""
    
    # Locators
    CART_ITEMS = (By.CLASS_NAME, "cart-item")
    ITEM_NAME = (By.CLASS_NAME, "item-name")
    ITEM_PRICE = (By.CLASS_NAME, "item-price")
    ITEM_QUANTITY = (By.CLASS_NAME, "item-quantity")
    REMOVE_BTN = (By.CLASS_NAME, "remove-item")
    TOTAL_PRICE = (By.CLASS_NAME, "total-price")
    CHECKOUT_BTN = (By.CLASS_NAME, "checkout-btn")
    EMPTY_CART_MSG = (By.CLASS_NAME, "empty-cart")
    
    def __init__(self, driver, base_url):
        super().__init__(driver)
        self.base_url = base_url
    
    def open(self):
        """Mở cart page"""
        self.navigate_to(f"{self.base_url}/cart")
    
    def get_cart_items_count(self):
        """Đếm số sản phẩm trong giỏ"""
        items = self.driver.find_elements(*self.CART_ITEMS)
        return len(items)
    
    def remove_item(self, index=0):
        """Xóa sản phẩm khỏi giỏ"""
        remove_buttons = self.driver.find_elements(*self.REMOVE_BTN)
        if index < len(remove_buttons):
            remove_buttons[index].click()
    
    def get_total_price(self):
        """Lấy tổng tiền"""
        total = self.get_text(self.TOTAL_PRICE)
        return total.replace(",", "").replace(" VND", "")
    
    def proceed_to_checkout(self):
        """Tiếp tục checkout"""
        self.click(self.CHECKOUT_BTN)
    
    def is_cart_empty(self):
        """Kiểm tra giỏ hàng trống"""
        return self.is_displayed(self.EMPTY_CART_MSG)


# ============= CHECKOUT PAGE =============
class CheckoutPage(BasePage):
    """Page Object cho Checkout Page"""
    
    # Locators
    FULLNAME_INPUT = (By.ID, "fullName")
    PHONE_INPUT = (By.ID, "phone")
    ADDRESS_INPUT = (By.ID, "address")
    CITY_INPUT = (By.ID, "city")
    PAYMENT_COD = (By.ID, "payment-cod")
    PAYMENT_VNPAY = (By.ID, "payment-vnpay")
    PLACE_ORDER_BTN = (By.CLASS_NAME, "place-order")
    ORDER_SUMMARY = (By.CLASS_NAME, "order-summary")
    
    def __init__(self, driver, base_url):
        super().__init__(driver)
        self.base_url = base_url
    
    def fill_shipping_info(self, fullname, phone, address, city):
        """Điền thông tin giao hàng"""
        self.type(self.FULLNAME_INPUT, fullname)
        self.type(self.PHONE_INPUT, phone)
        self.type(self.ADDRESS_INPUT, address)
        self.type(self.CITY_INPUT, city)
    
    def select_payment_method(self, method="COD"):
        """Chọn phương thức thanh toán"""
        if method == "COD":
            self.click(self.PAYMENT_COD)
        elif method == "VNPay":
            self.click(self.PAYMENT_VNPAY)
    
    def place_order(self):
        """Đặt hàng"""
        self.click(self.PLACE_ORDER_BTN)


# ============= TEST USING POM =============
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager


class TestWithPOM:
    """Test cases sử dụng Page Object Model"""
    
    @pytest.fixture(autouse=True)
    def setup_teardown(self):
        self.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        self.driver.maximize_window()
        self.base_url = "http://localhost:3000"
        yield
        self.driver.quit()
    
    def test_login_successful(self):
        """Test đăng nhập thành công"""
        # Khởi tạo page objects
        login_page = LoginPage(self.driver, self.base_url)
        home_page = HomePage(self.driver, self.base_url)
        
        # Test steps
        login_page.open()
        login_page.login("admin", "admin123")
        
        # Verify
        assert home_page.is_user_logged_in()
    
    def test_login_and_search_product(self):
        """Test đăng nhập và tìm kiếm sản phẩm"""
        login_page = LoginPage(self.driver, self.base_url)
        home_page = HomePage(self.driver, self.base_url)
        
        # Login
        login_page.open()
        login_page.login("customer1", "customer123")
        
        # Search
        home_page.search_product("Nike")
        
        # Verify có sản phẩm hiển thị
        assert home_page.get_product_count() > 0
    
    def test_add_product_to_cart(self):
        """Test thêm sản phẩm vào giỏ"""
        login_page = LoginPage(self.driver, self.base_url)
        home_page = HomePage(self.driver, self.base_url)
        product_page = ProductPage(self.driver, self.base_url)
        
        # Login
        login_page.open()
        login_page.login("customer1", "customer123")
        
        # Vào trang sản phẩm (giả sử click vào product đầu tiên)
        # ... code click product ...
        
        # Add to cart
        product_page.select_size(42)
        product_page.set_quantity(2)
        product_page.add_to_cart()
        
        # Verify cart badge cập nhật
        # assert home_page.get_cart_count() > 0
    
    def test_complete_checkout(self):
        """Test checkout hoàn chỉnh"""
        login_page = LoginPage(self.driver, self.base_url)
        cart_page = CartPage(self.driver, self.base_url)
        checkout_page = CheckoutPage(self.driver, self.base_url)
        
        # Login
        login_page.open()
        login_page.login("customer1", "customer123")
        
        # Giả sử đã có sản phẩm trong giỏ
        cart_page.open()
        cart_page.proceed_to_checkout()
        
        # Fill checkout form
        checkout_page.fill_shipping_info(
            "Nguyen Van A",
            "0901234567",
            "123 Le Loi",
            "Ho Chi Minh"
        )
        checkout_page.select_payment_method("COD")
        checkout_page.place_order()
        
        # Verify success message hoặc redirect


# Chạy test:
# pytest selenium_example_pom.py -v
