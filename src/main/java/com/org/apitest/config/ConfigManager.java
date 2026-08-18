package com.org.apitest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public final class ConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private static EnvironmentConfig config;

    private ConfigManager() {
    }

    public static synchronized void load(String env) {
        String resourcePath = "/environments/" + env + ".yaml";
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath.substring(1));
        if (input == null) {
            input = ConfigManager.class.getResourceAsStream(resourcePath);
        }
        try (InputStream configStream = input) {
            if (configStream == null) {
                throw new IllegalStateException("Environment config not found: " + resourcePath);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> raw = YAML_MAPPER.readValue(configStream, Map.class);
            SecretResolver.resolvePlaceholders(raw);
            config = YAML_MAPPER.convertValue(raw, EnvironmentConfig.class);
            LOG.info("Loaded environment config: {}", config.getEnvironment());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load environment config: " + env, e);
        }
    }

    public static EnvironmentConfig get() {
        if (config == null) {
            throw new IllegalStateException("ConfigManager.load(env) must be called before get()");
        }
        return config;
    }

    public static void reset() {
        config = null;
    }
}
