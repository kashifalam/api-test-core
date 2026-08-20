package com.org.apitest.config;

/**
 * Known microservice identifiers used for HTTP client routing.
 */
public enum ServiceKey {
    ORDER("order"),
    PAYMENT("payment");

    private final String id;

    ServiceKey(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
