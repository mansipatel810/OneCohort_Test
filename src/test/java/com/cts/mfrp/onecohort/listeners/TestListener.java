package com.cts.mfrp.onecohort.listeners;

import org.testng.ITestListener; // 1. Add this import
import org.testng.ITestResult;

// 2. Add "implements ITestListener" to your class declaration
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // Your logic here
    }

    // You can override other methods like onTestSuccess, onTestFailure, etc.
}