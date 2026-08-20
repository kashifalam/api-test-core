package com.org.apitest.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.MDC;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Injects correlation and W3C traceparent headers for distributed tracing.
 */
public final class CorrelationIdFilter implements Filter {

    public static final String CORRELATION_HEADER = "X-Correlation-Id";
    public static final String TRACE_HEADER = "traceparent";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        String correlationId = MDC.get(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            MDC.put(CORRELATION_HEADER, correlationId);
        }
        requestSpec.header(CORRELATION_HEADER, correlationId);
        requestSpec.header(TRACE_HEADER, buildTraceParent());
        return ctx.next(requestSpec, responseSpec);
    }

    static String buildTraceParent() {
        return "00-" + randomHex(16) + "-" + randomHex(8) + "-01";
    }

    private static String randomHex(int byteCount) {
        byte[] bytes = new byte[byteCount];
        RANDOM.nextBytes(bytes);
        StringBuilder hex = new StringBuilder(byteCount * 2);
        for (byte value : bytes) {
            hex.append(String.format("%02x", value));
        }
        return hex.toString();
    }
}
