package com.cts.mfrp.onecohort.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Minimal TestNG listener registered in testng.xml.
 * All screenshot and report logic is handled by ExtentReportListener.
 * This class must implement ITestListener so TestNG can cast it correctly.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[TestListener] Starting: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TestListener] PASSED : " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[TestListener] FAILED : " + result.getMethod().getMethodName()
                + " — " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TestListener] SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("[TestListener] Suite starting: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("[TestListener] Suite finished: " + context.getName()
                + " | Passed: " + context.getPassedTests().size()
                + " | Failed: " + context.getFailedTests().size()
                + " | Skipped: " + context.getSkippedTests().size());
    }
}
