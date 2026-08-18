package com.org.apitest.http;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Supplier;

public final class RetryPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(RetryPolicy.class);
    private static final int MAX_ATTEMPTS = 3;
    private static final long BACKOFF_MS = 500;
    private static final Set<Integer> RETRYABLE_STATUS = Set.of(429, 502, 503, 504);

    private RetryPolicy() {
    }

    public static Response execute(Supplier<Response> action) {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                Response response = action.get();
                if (attempt < MAX_ATTEMPTS && RETRYABLE_STATUS.contains(response.getStatusCode())) {
                    LOG.warn("Retryable status {} on attempt {}/{}", response.getStatusCode(), attempt, MAX_ATTEMPTS);
                    sleep(BACKOFF_MS * attempt);
                    continue;
                }
                return response;
            } catch (RuntimeException e) {
                lastException = e;
                if (attempt == MAX_ATTEMPTS) {
                    throw e;
                }
                LOG.warn("Request failed on attempt {}/{}: {}", attempt, MAX_ATTEMPTS, e.getMessage());
                sleep(BACKOFF_MS * attempt);
            }
        }
        throw lastException != null ? lastException : new IllegalStateException("Retry exhausted");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", e);
        }
    }
}
