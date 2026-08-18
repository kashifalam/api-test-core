package com.org.apitest.http;

import com.org.apitest.config.ConfigManager;
import com.org.apitest.config.EnvironmentConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AuthTokenProvider {

    private static final long TOKEN_TTL_SECONDS = 3000;

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String scope;

    private final AtomicReference<String> cachedToken = new AtomicReference<>();
    private volatile Instant tokenExpiry = Instant.EPOCH;

    public AuthTokenProvider(String tokenUrl, String clientId, String clientSecret, String scope) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    public static AuthTokenProvider fromConfig() {
        EnvironmentConfig.AuthConfig auth = ConfigManager.get().getAuth();
        if (auth.getTokenUrl() == null || auth.getTokenUrl().isBlank()) {
            return new AuthTokenProvider(null, null, null, null);
        }
        return new AuthTokenProvider(
                auth.getTokenUrl(),
                auth.getClientId(),
                auth.getClientSecret(),
                auth.getScope()
        );
    }

    public String getToken() {
        if (tokenUrl == null || tokenUrl.isBlank()) {
            return null;
        }
        if (Instant.now().isBefore(tokenExpiry) && cachedToken.get() != null) {
            return cachedToken.get();
        }
        synchronized (this) {
            if (Instant.now().isBefore(tokenExpiry) && cachedToken.get() != null) {
                return cachedToken.get();
            }
            Response response = io.restassured.RestAssured.given()
                    .contentType(ContentType.URLENC)
                    .formParams(Map.of(
                            "grant_type", "client_credentials",
                            "client_id", clientId,
                            "client_secret", clientSecret,
                            "scope", scope != null ? scope : ""
                    ))
                    .post(tokenUrl)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            String token = response.path("access_token");
            cachedToken.set(token);
            tokenExpiry = Instant.now().plusSeconds(TOKEN_TTL_SECONDS);
            return token;
        }
    }
}
