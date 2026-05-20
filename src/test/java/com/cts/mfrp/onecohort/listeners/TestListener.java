package com.cts.mfrp.onecohort.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that logs test lifecycle events to standard output.
 * Registered in all suite XML files via {@code <listener class-name="...TestListener"/>}.
 *
 * Must implement {@link ITestListener} (or another TestNG listener interface);
 * an empty class causes a ClassCastException when TestNG tries to register it.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n[TestListener] ▶ Suite started : " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.printf("[TestListener] ■ Suite finished: %s  |  Passed=%d  Failed=%d  Skipped=%d%n",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[TestListener] → " + result.getTestClass().getName()
                + "#" + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TestListener] ✔ PASSED  : " + result.getName()
                + "  (" + elapsedMs(result) + " ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[TestListener] ✘ FAILED  : " + result.getName()
                + "  (" + elapsedMs(result) + " ms)");
        if (result.getThrowable() != null) {
            System.out.println("              Cause: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TestListener] ⊘ SKIPPED : " + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("[TestListener] ~ WITHIN_SUCCESS_PCT: " + result.getName());
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private static long elapsedMs(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
