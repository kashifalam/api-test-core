package com.org.apitest.config;

public record DatabaseConfig(
        String jdbcUrl,
        String username,
        String password,
        String driverClassName
) {}
