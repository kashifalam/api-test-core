package com.org.apitest.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        log.info(">>> {} {}", requestSpec.getMethod(), requestSpec.getURI());
        if (requestSpec.getBody() != null) {
            log.debug("Request body: {}", SensitiveDataMasker.mask(String.valueOf(requestSpec.getBody())));
        }
        Response response = ctx.next(requestSpec, responseSpec);
        log.info("<<< {} {} -> {}", requestSpec.getMethod(), requestSpec.getURI(), response.getStatusCode());
        log.debug("Response body: {}", SensitiveDataMasker.mask(response.getBody().asString()));
        return response;
    }
}
