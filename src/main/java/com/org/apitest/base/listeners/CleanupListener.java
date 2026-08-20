package com.org.apitest.base.listeners;

import com.org.apitest.data.CleanupRegistry;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * Runs registered cleanup tasks after each test method.
 */
public class CleanupListener implements IInvokedMethodListener {

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            CleanupRegistry.runAll();
            CleanupRegistry.clear();
        }
    }
}
