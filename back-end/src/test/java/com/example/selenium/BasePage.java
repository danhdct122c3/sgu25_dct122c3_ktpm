package com.example.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base Page - Chứa các methods dùng chung cho tất cả Page Objects
 */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl = "http://localhost:3000";

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Click vào element với explicit wait
     */
    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * Nhập text vào input field
     */
    protected void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
        
        // Đợi một chút sau khi clear
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        element.sendKeys(text);
        
        // Đợi và verify text đã được nhập
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Verify giá trị đã nhập đúng chưa
        String actualValue = element.getAttribute("value");
        if (!actualValue.equals(text)) {
            System.out.println("WARNING: Expected '" + text + "' but got '" + actualValue + "'");
            // Thử nhập lại
            element.clear();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            element.sendKeys(text);
        }
    }

    /**
     * Lấy text của element
     */
    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }

    /**
     * Kiểm tra element có hiển thị không
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Đợi element hiển thị
     */
    protected void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Lấy URL hiện tại
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Navigate đến URL
     */
    public void navigateTo(String url) {
        driver.get(url);
    }

    /**
     * Đợi URL chứa text
     */
    protected void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }
}
