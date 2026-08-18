package com.org.apitest.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class TestExecutionListener implements ISuiteListener, IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(TestExecutionListener.class);

    @Override
    public void onStart(ISuite suite) {
        log.info("Suite started: {} | env={} | groups={}",
                suite.getName(),
                System.getProperty("env", "qa"),
                System.getProperty("groups", "all"));
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        log.debug("Invoking: {}.{}", method.getTestMethod().getTestClass().getName(),
                method.getTestMethod().getMethodName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (testResult.getStatus() == ITestResult.FAILURE) {
            log.error("Test failed: {} - {}", testResult.getName(), testResult.getThrowable().getMessage());
        }
    }
}
