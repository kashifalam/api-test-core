package com.org.apitest.base;

import com.org.apitest.data.DataIsolationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for API tests. TestNG listeners are registered in testng.xml.
 */
public abstract class BaseApiTest {

    protected String testRunId;

    @BeforeMethod(alwaysRun = true)
    public void baseSetUp() {
        testRunId = DataIsolationContext.init();
    }

    @AfterMethod(alwaysRun = true)
    public void baseTearDown() {
        DataIsolationContext.clear();
    }

    protected static String env() {
        return System.getProperty("env", "qa");
    }
}
