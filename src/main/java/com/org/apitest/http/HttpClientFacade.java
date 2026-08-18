package com.org.apitest.http;

import com.org.apitest.config.ConfigManager;
import com.org.apitest.config.EnvironmentConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientFacade.class);

    private final String baseUri;
    private final AuthTokenProvider authTokenProvider;

    public HttpClientFacade(String baseUri) {
        this(baseUri, AuthTokenProvider.fromConfig());
    }

    public HttpClientFacade(String baseUri, AuthTokenProvider authTokenProvider) {
        this.baseUri = baseUri;
        this.authTokenProvider = authTokenProvider;
    }

    public RequestSpecification given() {
        RequestSpecification spec = RestAssured.given()
                .baseUri(baseUri)
                .filter(new CorrelationIdFilter())
                .filter(new SensitiveDataMaskingFilter())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .relaxedHTTPSValidation();

        String token = authTokenProvider.getToken();
        if (token != null && !token.isBlank()) {
            spec.header("Authorization", "Bearer " + token);
        }
        return spec;
    }

    public Response get(String path) {
        return RetryPolicy.execute(() -> {
            LOG.debug("GET {}{}", baseUri, path);
            return given().when().get(path).then().extract().response();
        });
    }

    public Response post(String path, Object body) {
        return RetryPolicy.execute(() -> {
            LOG.debug("POST {}{}", baseUri, path);
            return given().body(body).when().post(path).then().extract().response();
        });
    }

    public Response put(String path, Object body) {
        return RetryPolicy.execute(() -> {
            LOG.debug("PUT {}{}", baseUri, path);
            return given().body(body).when().put(path).then().extract().response();
        });
    }

    public Response delete(String path) {
        return RetryPolicy.execute(() -> {
            LOG.debug("DELETE {}{}", baseUri, path);
            return given().when().delete(path).then().extract().response();
        });
    }

    public static HttpClientFacade forService(String serviceKey) {
        EnvironmentConfig config = ConfigManager.get();
        return switch (serviceKey) {
            case "order" -> new HttpClientFacade(config.getServices().getOrder().getBaseUrl());
            case "payment" -> new HttpClientFacade(config.getServices().getPayment().getBaseUrl());
            default -> throw new IllegalArgumentException("Unknown service: " + serviceKey);
        };
    }
}
