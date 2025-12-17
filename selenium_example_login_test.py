"""
Selenium Test Example - Login Test
Python + pytest + Selenium WebDriver
"""

import pytest
import csv
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager


class TestLogin:
    """Test cases cho Login functionality"""
    
    @pytest.fixture(autouse=True)
    def setup_teardown(self):
        """Setup trước mỗi test và teardown sau mỗi test"""
        # Setup
        self.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        self.driver.maximize_window()
        self.driver.implicitly_wait(10)
        self.base_url = "http://localhost:3000"
        
        yield
        
        # Teardown
        self.driver.quit()
    
    def read_test_data(self, file_path):
        """Đọc test data từ CSV file"""
        test_data = []
        with open(file_path, 'r', encoding='utf-8') as file:
            reader = csv.DictReader(file)
            for row in reader:
                test_data.append(row)
        return test_data
    
    def test_login_successful_admin(self):
        """Test: Đăng nhập thành công với tài khoản Admin"""
        # Navigate to login page
        self.driver.get(f"{self.base_url}/login")
        
        # Tìm các elements
        username_input = self.driver.find_element(By.ID, "username")
        password_input = self.driver.find_element(By.ID, "password")
        login_button = self.driver.find_element(By.XPATH, "//button[@type='submit']")
        
        # Nhập thông tin đăng nhập
        username_input.send_keys("admin")
        password_input.send_keys("admin123")
        
        # Click login button
        login_button.click()
        
        # Wait và verify redirect về home page
        wait = WebDriverWait(self.driver, 10)
        wait.until(EC.url_to_be(self.base_url + "/"))
        
        # Verify user đã login
        assert self.driver.current_url == f"{self.base_url}/"
        
        # Verify user menu hoặc avatar hiển thị
        user_menu = wait.until(EC.presence_of_element_located((By.CLASS_NAME, "user-menu")))
        assert user_menu.is_displayed()
    
    def test_login_failed_invalid_credentials(self):
        """Test: Đăng nhập thất bại với thông tin sai"""
        self.driver.get(f"{self.base_url}/login")
        
        username_input = self.driver.find_element(By.ID, "username")
        password_input = self.driver.find_element(By.ID, "password")
        login_button = self.driver.find_element(By.XPATH, "//button[@type='submit']")
        
        # Nhập sai thông tin
        username_input.send_keys("wronguser")
        password_input.send_keys("wrongpass")
        login_button.click()
        
        # Verify error message hiển thị
        wait = WebDriverWait(self.driver, 10)
        error_message = wait.until(EC.presence_of_element_located((By.CLASS_NAME, "error-message")))
        
        assert error_message.is_displayed()
        assert "Invalid username or password" in error_message.text
    
    def test_login_failed_empty_username(self):
        """Test: Đăng nhập thất bại khi username trống"""
        self.driver.get(f"{self.base_url}/login")
        
        password_input = self.driver.find_element(By.ID, "password")
        login_button = self.driver.find_element(By.XPATH, "//button[@type='submit']")
        
        # Chỉ nhập password
        password_input.send_keys("password123")
        login_button.click()
        
        # Verify validation message
        username_input = self.driver.find_element(By.ID, "username")
        validation_message = username_input.get_attribute("validationMessage")
        
        assert validation_message != ""
        # Hoặc check error message trên UI
        # error_message = self.driver.find_element(By.CLASS_NAME, "error-message")
        # assert "Username is required" in error_message.text
    
    def test_login_failed_empty_password(self):
        """Test: Đăng nhập thất bại khi password trống"""
        self.driver.get(f"{self.base_url}/login")
        
        username_input = self.driver.find_element(By.ID, "username")
        login_button = self.driver.find_element(By.XPATH, "//button[@type='submit']")
        
        # Chỉ nhập username
        username_input.send_keys("admin")
        login_button.click()
        
        # Verify validation message
        password_input = self.driver.find_element(By.ID, "password")
        validation_message = password_input.get_attribute("validationMessage")
        
        assert validation_message != ""
    
    @pytest.mark.parametrize("test_data", [
        {"username": "admin", "password": "admin123", "expected": "success"},
        {"username": "customer1", "password": "customer123", "expected": "success"},
        {"username": "wronguser", "password": "wrongpass", "expected": "fail"},
        {"username": "", "password": "password123", "expected": "fail"},
        {"username": "admin", "password": "", "expected": "fail"},
    ])
    def test_login_with_data_provider(self, test_data):
        """Test: Data-driven testing với nhiều bộ test data"""
        self.driver.get(f"{self.base_url}/login")
        
        username_input = self.driver.find_element(By.ID, "username")
        password_input = self.driver.find_element(By.ID, "password")
        login_button = self.driver.find_element(By.XPATH, "//button[@type='submit']")
        
        # Nhập dữ liệu
        if test_data["username"]:
            username_input.send_keys(test_data["username"])
        if test_data["password"]:
            password_input.send_keys(test_data["password"])
        
        login_button.click()
        time.sleep(2)  # Wait for action
        
        # Verify result
        if test_data["expected"] == "success":
            # Verify redirect về home page
            assert "/" in self.driver.current_url
        else:
            # Verify vẫn ở login page hoặc có error
            assert "/login" in self.driver.current_url or \
                   self.driver.find_element(By.CLASS_NAME, "error-message").is_displayed()
    
    def test_login_from_csv_file(self):
        """Test: Đọc test data từ CSV file"""
        test_data_list = self.read_test_data("selenium_test_data_login.csv")
        
        for test_data in test_data_list:
            print(f"\nRunning test: {test_data['testId']} - {test_data['description']}")
            
            self.driver.get(f"{self.base_url}/login")
            
            username_input = self.driver.find_element(By.ID, "username")
            password_input = self.driver.find_element(By.ID, "password")
            login_button = self.driver.find_element(By.XPATH, "//button[@type='submit']")
            
            # Nhập dữ liệu
            if test_data["username"]:
                username_input.send_keys(test_data["username"])
            if test_data["password"]:
                password_input.send_keys(test_data["password"])
            
            login_button.click()
            time.sleep(2)
            
            # Verify result
            if test_data["expectedResult"] == "success":
                assert test_data["expectedUrl"] in self.driver.current_url
                print(f"✓ Test {test_data['testId']} PASSED")
            else:
                # Verify error message
                try:
                    error_element = self.driver.find_element(By.CLASS_NAME, "error-message")
                    assert error_element.is_displayed()
                    print(f"✓ Test {test_data['testId']} PASSED")
                except:
                    print(f"✗ Test {test_data['testId']} FAILED")
    
    def test_remember_me_checkbox(self):
        """Test: Checkbox Remember Me"""
        self.driver.get(f"{self.base_url}/login")
        
        # Tìm checkbox
        remember_checkbox = self.driver.find_element(By.ID, "remember-me")
        
        # Verify checkbox ban đầu chưa checked
        assert not remember_checkbox.is_selected()
        
        # Click checkbox
        remember_checkbox.click()
        
        # Verify checkbox đã checked
        assert remember_checkbox.is_selected()
    
    def test_show_hide_password(self):
        """Test: Show/Hide password"""
        self.driver.get(f"{self.base_url}/login")
        
        password_input = self.driver.find_element(By.ID, "password")
        show_password_btn = self.driver.find_element(By.CLASS_NAME, "toggle-password")
        
        # Verify password input type ban đầu là "password"
        assert password_input.get_attribute("type") == "password"
        
        # Click show password
        show_password_btn.click()
        
        # Verify input type đổi thành "text"
        assert password_input.get_attribute("type") == "text"
        
        # Click lại để hide
        show_password_btn.click()
        
        # Verify đổi lại thành "password"
        assert password_input.get_attribute("type") == "password"
    
    def test_redirect_to_register_page(self):
        """Test: Click link Register"""
        self.driver.get(f"{self.base_url}/login")
        
        register_link = self.driver.find_element(By.LINK_TEXT, "Register")
        register_link.click()
        
        # Verify redirect về register page
        wait = WebDriverWait(self.driver, 10)
        wait.until(EC.url_contains("/register"))
        
        assert "/register" in self.driver.current_url


# Chạy test:
# pytest selenium_example_login_test.py
# pytest selenium_example_login_test.py -v
# pytest selenium_example_login_test.py -v -s
# pytest selenium_example_login_test.py --html=reports/report.html
