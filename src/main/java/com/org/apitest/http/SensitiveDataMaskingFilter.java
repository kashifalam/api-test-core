package com.org.apitest.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs HTTP traffic with sensitive fields masked.
 */
public final class SensitiveDataMaskingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SensitiveDataMaskingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        LOG.info(">>> {} {}", requestSpec.getMethod(), requestSpec.getURI());
        Object requestBody = requestSpec.getBody();
        if (requestBody != null) {
            LOG.debug("Request body: {}", mask(String.valueOf(requestBody)));
        }
        Response response = ctx.next(requestSpec, responseSpec);
        LOG.info("<<< Status: {}", response.getStatusCode());
        LOG.debug("Response body: {}", mask(response.asPrettyString()));
        return response;
    }

    static String mask(String input) {
        if (input == null) {
            return null;
        }
        String masked = input;
        for (MaskRule rule : MaskRule.values()) {
            masked = rule.apply(masked);
        }
        return masked;
    }
}
