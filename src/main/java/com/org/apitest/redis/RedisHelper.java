package com.org.apitest.redis;

import com.org.apitest.config.EnvironmentConfig;
import com.org.apitest.data.DataIsolationContext;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisHelper implements AutoCloseable {

    private final RedisClient client;
    private final RedisCommands<String, String> commands;
    private final String keyPrefix;

    public RedisHelper(EnvironmentConfig.RedisConfig config) {
        this(config, DataIsolationContext.getKeyPrefix());
    }

    public RedisHelper(EnvironmentConfig.RedisConfig config, String keyPrefix) {
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(config.getHost())
                .withPort(config.getPort())
                .withDatabase(config.getDatabase());
        if (config.getPassword() != null && !config.getPassword().isBlank()) {
            builder.withPassword(config.getPassword().toCharArray());
        }
        this.client = RedisClient.create(builder.build());
        this.commands = client.connect().sync();
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
        if (client != null) {
            client.shutdown();
        }
    }
}
