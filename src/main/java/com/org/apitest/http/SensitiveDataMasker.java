package com.org.apitest.http;

import java.util.regex.Pattern;

public final class SensitiveDataMasker {

    private static final Pattern[] SENSITIVE_PATTERNS = {
            Pattern.compile("(\"password\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\"clientSecret\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\"ssn\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\"email\"\\s*:\\s*\")([^\"@]+)(@[^\"]+)(\")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(Authorization:\\s*Bearer\\s+)(\\S+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(Authorization:\\s*Basic\\s+)(\\S+)", Pattern.CASE_INSENSITIVE)
    };

    private SensitiveDataMasker() {}

    public static String mask(String input) {
        if (input == null) {
            return null;
        }
        String masked = input;
        for (Pattern pattern : SENSITIVE_PATTERNS) {
            masked = pattern.matcher(masked).replaceAll("$1****$3");
        }
        return masked;
    }
}
