package com.cts.mfrp.onecohort.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.cts.mfrp.onecohort.constants.AppConstants;

public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(AppConstants.REPORT_PATH);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("OneCohort Test Report");
            spark.config().setReportName("Selenium TestNG Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "OneCohort");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Browser", ConfigReader.getBrowser());
            extent.setSystemInfo("Base URL", ConfigReader.getBaseUrl());
        }
        return extent;
    }

    public static void createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        testThreadLocal.set(test);
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void removeTest() {
        testThreadLocal.remove();
    }

    public static void flush() {
        if (extent != null) extent.flush();
    }
}
