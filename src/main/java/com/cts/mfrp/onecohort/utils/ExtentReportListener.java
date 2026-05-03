package com.cts.mfrp.onecohort.utils;

import com.aventstack.extentreports.Status;
import com.cts.mfrp.onecohort.constants.AppConstants;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportListener implements ITestListener {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        ExtentManager.createTest(testName, description != null ? description : "");
        ExtentManager.getTest().info("Test started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test passed.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentManager.getTest().log(Status.FAIL, result.getThrowable());
        captureAndAttachScreenshot(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }

    private void captureAndAttachScreenshot(ITestResult result) {
        try {
            Object instance = result.getInstance();
            WebDriver driver = (WebDriver) instance.getClass()
                    .getMethod("getDriver")
                    .invoke(instance);

            if (driver == null) return;

            String timestamp = LocalDateTime.now().format(FMT);
            String fileName = AppConstants.SCREENSHOT_PATH
                    + result.getName() + "_" + timestamp + ".png";

            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.createDirectories(Paths.get(AppConstants.SCREENSHOT_PATH));
            Files.write(Paths.get(fileName), bytes);

            ExtentManager.getTest()
                    .addScreenCaptureFromPath("../../" + fileName, "Failure Screenshot");
        } catch (IOException | ReflectiveOperationException e) {
            ExtentManager.getTest().warning("Screenshot failed: " + e.getMessage());
        }
    }
}
