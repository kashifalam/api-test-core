package com.org.apitest.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Immutable environment configuration loaded from YAML.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EnvironmentConfig(
        String environment,
        ServicesConfig services,
        DatabasesConfig databases,
        RedisConfig redis,
        AuthConfig auth) {

    private static final int DEFAULT_MAX_POOL_SIZE = 5;
    private static final int DEFAULT_REDIS_PORT = 6379;
    private static final int DEFAULT_REDIS_DATABASE = 0;
    private static final String DEFAULT_REDIS_HOST = "localhost";

    public EnvironmentConfig {
        services = services != null ? services : new ServicesConfig(null, null);
        databases = databases != null ? databases : new DatabasesConfig(null);
        redis = redis != null ? redis : new RedisConfig(DEFAULT_REDIS_HOST, DEFAULT_REDIS_PORT, null,
                DEFAULT_REDIS_DATABASE);
        auth = auth != null ? auth : new AuthConfig(null, null, null, null);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServicesConfig(ServiceEndpoint order, ServiceEndpoint payment) {
        public ServicesConfig {
            order = order != null ? order : new ServiceEndpoint(null);
            payment = payment != null ? payment : new ServiceEndpoint(null);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServiceEndpoint(String baseUrl) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DatabasesConfig(JdbcConfig orderDb) {
        public DatabasesConfig {
            orderDb = orderDb != null ? orderDb : new JdbcConfig(null, null, null, DEFAULT_MAX_POOL_SIZE);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JdbcConfig(String jdbcUrl, String username, String password, int maxPoolSize) {
        public JdbcConfig {
            if (maxPoolSize <= 0) {
                maxPoolSize = DEFAULT_MAX_POOL_SIZE;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RedisConfig(String host, int port, String password, int database) {
        public RedisConfig {
            if (host == null || host.isBlank()) {
                host = DEFAULT_REDIS_HOST;
            }
            if (port <= 0) {
                port = DEFAULT_REDIS_PORT;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AuthConfig(String tokenUrl, String clientId, String clientSecret, String scope) {
    }
}
