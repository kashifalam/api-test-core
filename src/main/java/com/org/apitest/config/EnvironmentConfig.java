package com.org.apitest.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentConfig {

    private String environment;
    private ServicesConfig services = new ServicesConfig();
    private DatabasesConfig databases = new DatabasesConfig();
    private RedisConfig redis = new RedisConfig();
    private AuthConfig auth = new AuthConfig();

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public ServicesConfig getServices() {
        return services;
    }

    public void setServices(ServicesConfig services) {
        this.services = services;
    }

    public DatabasesConfig getDatabases() {
        return databases;
    }

    public void setDatabases(DatabasesConfig databases) {
        this.databases = databases;
    }

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServicesConfig {
        private ServiceEndpoint order = new ServiceEndpoint();
        private ServiceEndpoint payment = new ServiceEndpoint();

        public ServiceEndpoint getOrder() {
            return order;
        }

        public void setOrder(ServiceEndpoint order) {
            this.order = order;
        }

        public ServiceEndpoint getPayment() {
            return payment;
        }

        public void setPayment(ServiceEndpoint payment) {
            this.payment = payment;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ServiceEndpoint {
        private String baseUrl;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatabasesConfig {
        private JdbcConfig orderDb = new JdbcConfig();

        public JdbcConfig getOrderDb() {
            return orderDb;
        }

        public void setOrderDb(JdbcConfig orderDb) {
            this.orderDb = orderDb;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JdbcConfig {
        private String jdbcUrl;
        private String username;
        private String password;
        private int maxPoolSize = 5;

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedisConfig {
        private String host = "localhost";
        private int port = 6379;
        private String password;
        private int database = 0;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthConfig {
        private String tokenUrl;
        private String clientId;
        private String clientSecret;
        private String scope;

        public String getTokenUrl() {
            return tokenUrl;
        }

        public void setTokenUrl(String tokenUrl) {
            this.tokenUrl = tokenUrl;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }
}
