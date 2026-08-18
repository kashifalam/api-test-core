package com.org.apitest.base;

import com.org.apitest.base.listeners.ConfigListener;
import com.org.apitest.base.listeners.CleanupListener;
import com.org.apitest.base.listeners.TraceIdListener;
import com.org.apitest.config.ConfigManager;
import com.org.apitest.data.CleanupRegistry;
import com.org.apitest.data.DataIsolationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners({ConfigListener.class, TraceIdListener.class, CleanupListener.class})
public abstract class BaseApiTest {

    protected String testRunId;

    @BeforeMethod(alwaysRun = true)
    public void baseSetUp() {
        testRunId = DataIsolationContext.init();
    }

    @AfterMethod(alwaysRun = true)
    public void baseTearDown() {
        CleanupRegistry.runAll();
        CleanupRegistry.clear();
        DataIsolationContext.clear();
    }

    protected static String env() {
        return System.getProperty("env", "qa");
    }

    protected static String serviceBaseUrl(String service) {
        return switch (service) {
            case "order" -> ConfigManager.get().getServices().getOrder().getBaseUrl();
            case "payment" -> ConfigManager.get().getServices().getPayment().getBaseUrl();
            default -> throw new IllegalArgumentException("Unknown service: " + service);
        };
    }
}
