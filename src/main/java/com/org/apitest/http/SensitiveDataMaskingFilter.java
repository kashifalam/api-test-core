package com.org.apitest.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class SensitiveDataMaskingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SensitiveDataMaskingFilter.class);
    private static final String MASK = "****";

    private static final Pattern[] SENSITIVE_PATTERNS = {
            Pattern.compile("(\"password\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\"client_secret\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\"ssn\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(Authorization:\\s*Bearer\\s+)(\\S+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\"email\"\\s*:\\s*\")([^\"@]+)(@[^\"]+)(\")", Pattern.CASE_INSENSITIVE)
    };

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        LOG.info(">>> {} {}", requestSpec.getMethod(), requestSpec.getURI());
        if (requestSpec.getBody() != null) {
            LOG.debug("Request body: {}", mask(String.valueOf(requestSpec.getBody())));
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
        for (Pattern pattern : SENSITIVE_PATTERNS) {
            if (pattern.pattern().contains("email")) {
                masked = pattern.matcher(masked).replaceAll("$1" + MASK + "$3$4");
            } else if (pattern.pattern().contains("Authorization")) {
                masked = pattern.matcher(masked).replaceAll("$1" + MASK);
            } else {
                masked = pattern.matcher(masked).replaceAll("$1" + MASK + "$3");
            }
        }
        return masked;
    }
}
