package com.org.apitest.http;

import com.org.apitest.config.ServiceKey;
import com.org.apitest.config.ServiceRegistry;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RestAssured wrapper providing auth, retries, correlation IDs, and request logging.
 */
public final class HttpClientFacade {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientFacade.class);

    private final String baseUri;
    private final AuthTokenProvider authTokenProvider;
    private final CorrelationIdFilter correlationIdFilter = new CorrelationIdFilter();
    private final SensitiveDataMaskingFilter sensitiveDataMaskingFilter = new SensitiveDataMaskingFilter();

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
                .filter(correlationIdFilter)
                .filter(sensitiveDataMaskingFilter)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .relaxedHTTPSValidation();

        authTokenProvider.getToken().ifPresent(token -> spec.header("Authorization", "Bearer " + token));
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

    public static HttpClientFacade forService(ServiceKey serviceKey) {
        return new HttpClientFacade(ServiceRegistry.baseUrl(serviceKey));
    }
}
