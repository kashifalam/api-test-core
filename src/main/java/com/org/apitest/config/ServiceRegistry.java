package com.org.apitest.config;

/**
 * Resolves service base URLs from the active environment configuration.
 */
public final class ServiceRegistry {

    private ServiceRegistry() {
    }

    public static String baseUrl(ServiceKey key) {
        EnvironmentConfig.ServicesConfig services = ConfigManager.get().services();
        return switch (key) {
            case ORDER -> services.order().baseUrl();
            case PAYMENT -> services.payment().baseUrl();
        };
    }
}
