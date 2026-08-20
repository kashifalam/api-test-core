package com.org.apitest.data;

import java.util.UUID;

/**
 * Provides per-test isolation keys via ThreadLocal for parallel-safe test data.
 */
public final class DataIsolationContext {

    private static final ThreadLocal<String> TEST_RUN_ID = new ThreadLocal<>();

    private DataIsolationContext() {
    }

    public static String init() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        TEST_RUN_ID.set(id);
        return id;
    }

    public static String getTestRunId() {
        String id = TEST_RUN_ID.get();
        if (id == null) {
            throw new IllegalStateException("DataIsolationContext not initialized for this test");
        }
        return id;
    }

    public static String getKeyPrefix() {
        return "test:" + getTestRunId();
    }

    public static String uniqueEmail(String domain) {
        return "qa+" + getTestRunId() + "@" + domain;
    }

    public static void clear() {
        TEST_RUN_ID.remove();
    }
}
