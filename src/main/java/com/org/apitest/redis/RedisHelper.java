package com.org.apitest.redis;

import com.org.apitest.config.EnvironmentConfig;
import com.org.apitest.data.DataIsolationContext;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Redis helper with test-scoped key prefixing. Must be closed after use.
 */
public final class RedisHelper implements AutoCloseable {

    private final RedisClient client;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> commands;
    private final String keyPrefix;

    public RedisHelper(EnvironmentConfig.RedisConfig config) {
        this(config, DataIsolationContext.getKeyPrefix());
    }

    public RedisHelper(EnvironmentConfig.RedisConfig config, String keyPrefix) {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(config.host())
                .withPort(config.port())
                .withDatabase(config.database());
        if (config.password() != null && !config.password().isBlank()) {
            builder.withPassword(config.password().toCharArray());
        }
        this.client = RedisClient.create(builder.build());
        this.connection = client.connect();
        this.commands = connection.sync();
        this.keyPrefix = keyPrefix == null ? "" : keyPrefix;
    }

    public String get(String key) {
        return commands.get(prefixed(key));
    }

    public void set(String key, String value) {
        commands.set(prefixed(key), value);
    }

    public void del(String key) {
        commands.del(prefixed(key));
    }

    public String prefixed(String key) {
        return keyPrefix.isBlank() ? key : keyPrefix + ":" + key;
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.close();
        }
        if (client != null) {
            client.shutdown();
        }
    }
}
