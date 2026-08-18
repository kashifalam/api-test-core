package com.org.apitest.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CleanupRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupRegistry.class);
    private static final ThreadLocal<Deque<Runnable>> CLEANUPS = ThreadLocal.withInitial(ArrayDeque::new);

    private CleanupRegistry() {
    }

    public static void register(Runnable cleanup) {
        CLEANUPS.get().push(cleanup);
    }

    public static void runAll() {
        Deque<Runnable> cleanups = CLEANUPS.get();
        while (!cleanups.isEmpty()) {
            Runnable cleanup = cleanups.pop();
            try {
                cleanup.run();
            } catch (Exception e) {
                LOG.warn("Cleanup task failed: {}", e.getMessage());
            }
        }
    }

    public static void clear() {
        CLEANUPS.get().clear();
        CLEANUPS.remove();
    }
}
