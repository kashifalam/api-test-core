package com.org.apitest.http;

import com.org.apitest.config.ConfigManager;
import com.org.apitest.config.EnvironmentConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;

/**
 * Provides and caches OAuth2 client-credentials access tokens.
 */
public final class AuthTokenProvider {

    private static final long DEFAULT_EXPIRES_SECONDS = 3600;
    private static final long EXPIRY_SAFETY_MARGIN_SECONDS = 60;

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
        EnvironmentConfig.AuthConfig auth = ConfigManager.get().auth();
        if (auth.tokenUrl() == null || auth.tokenUrl().isBlank()) {
            return new AuthTokenProvider(null, null, null, null);
        }
        return new AuthTokenProvider(
                auth.tokenUrl(),
                auth.clientId(),
                auth.clientSecret(),
                auth.scope());
    }

    public Optional<String> getToken() {
        if (tokenUrl == null || tokenUrl.isBlank()) {
            return Optional.empty();
        }
        if (Instant.now().isBefore(tokenExpiry) && cachedToken.get() != null) {
            return Optional.of(cachedToken.get());
        }
        synchronized (this) {
            if (Instant.now().isBefore(tokenExpiry) && cachedToken.get() != null) {
                return Optional.of(cachedToken.get());
            }
            try {
                Response response = given()
                        .contentType(ContentType.URLENC)
                        .formParams(Map.of(
                                "grant_type", "client_credentials",
                                "client_id", clientId,
                                "client_secret", clientSecret,
                                "scope", scope != null ? scope : ""))
                        .post(tokenUrl)
                        .then()
                        .extract()
                        .response();

                if (response.getStatusCode() != 200) {
                    throw new AuthenticationException(
                            "Token request failed with status " + response.getStatusCode());
                }

                String token = response.path("access_token");
                if (token == null || token.isBlank()) {
                    throw new AuthenticationException("Token response missing access_token");
                }

                Integer expiresIn = response.path("expires_in");
                long ttlSeconds = expiresIn != null && expiresIn > EXPIRY_SAFETY_MARGIN_SECONDS
                        ? expiresIn - EXPIRY_SAFETY_MARGIN_SECONDS
                        : DEFAULT_EXPIRES_SECONDS;

                cachedToken.set(token);
                tokenExpiry = Instant.now().plusSeconds(ttlSeconds);
                return Optional.of(token);
            } catch (AuthenticationException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new AuthenticationException("Failed to obtain access token", e);
            }
        }
    }
}
