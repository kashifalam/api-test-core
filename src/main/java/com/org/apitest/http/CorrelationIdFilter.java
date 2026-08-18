package com.org.apitest.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationIdFilter implements Filter {

    public static final String CORRELATION_HEADER = "X-Correlation-Id";
    public static final String TRACE_HEADER = "traceparent";

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
        requestSpec.header(TRACE_HEADER, "00-" + correlationId.replace("-", "") + "-" + correlationId.replace("-", "").substring(0, 16) + "-01");
        return ctx.next(requestSpec, responseSpec);
    }
}
