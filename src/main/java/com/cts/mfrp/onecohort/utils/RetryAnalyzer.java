package com.cts.mfrp.onecohort.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY = 2;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            ExtentManager.getTest()
                    .warning("Retrying test [" + result.getName() + "] — attempt " + retryCount + " of " + MAX_RETRY);
            return true;
        }
        return false;
    }
}
