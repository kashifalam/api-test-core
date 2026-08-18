package com.org.apitest.config;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SecretResolver {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

    private SecretResolver() {
    }

    @SuppressWarnings("unchecked")
    public static void resolvePlaceholders(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String stringValue) {
                entry.setValue(resolveString(stringValue));
            } else if (value instanceof Map<?, ?> nested) {
                resolvePlaceholders((Map<String, Object>) nested);
            }
        }
    }

    public static String resolveString(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String envKey = matcher.group(1);
            String envValue = System.getenv(envKey);
            if (envValue == null) {
                envValue = System.getProperty(envKey, "");
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(envValue));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
