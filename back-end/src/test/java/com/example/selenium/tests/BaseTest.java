package com.example.selenium.tests;

import com.example.selenium.utils.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base Test class - Chứa setup/teardown common cho tất cả test classes
 */
public class BaseTest {

    protected WebDriver driver;
    protected String baseUrl = "http://localhost:3000";

    @BeforeClass
    public void setUp() {
        System.out.println("=== Setting up WebDriver ===");
        driver = DriverFactory.createDriver();
        System.out.println("WebDriver created successfully");
    }

    @AfterClass
    public void tearDown() {
        System.out.println("=== Tearing down WebDriver ===");
        DriverFactory.quitDriver(driver);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        // Chụp screenshot nếu test fail
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }
    }

    /**
     * Chụp screenshot và lưu vào reports/screenshots
     */
    protected void takeScreenshot(String testName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File source = screenshot.getScreenshotAs(OutputType.FILE);
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String screenshotPath = "target/screenshots/" + fileName;
            
            File destination = new File(screenshotPath);
            destination.getParentFile().mkdirs();
            
            FileHandler.copy(source, destination);
            System.out.println("Screenshot saved: " + screenshotPath);
        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    /**
     * Sleep/wait for debugging
     */
    protected void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
