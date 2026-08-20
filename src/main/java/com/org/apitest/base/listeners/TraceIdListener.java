package com.org.apitest.base.listeners;

import com.org.apitest.http.CorrelationIdFilter;
import org.slf4j.MDC;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.util.UUID;

/**
 * Sets a correlation ID in MDC before each test method for log correlation.
 */
public class TraceIdListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            MDC.put(CorrelationIdFilter.CORRELATION_HEADER, UUID.randomUUID().toString());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            MDC.remove(CorrelationIdFilter.CORRELATION_HEADER);
        }
    }
}
