package com.org.apitest.http;

import java.util.regex.Pattern;

enum MaskRule {
    PASSWORD(Pattern.compile("(\"password\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE), "$1****$3"),
    CLIENT_SECRET(Pattern.compile("(\"client_secret\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE),
            "$1****$3"),
    SSN(Pattern.compile("(\"ssn\"\\s*:\\s*\")([^\"]*)(\")", Pattern.CASE_INSENSITIVE), "$1****$3"),
    AUTHORIZATION(Pattern.compile("(Authorization:\\s*Bearer\\s+)(\\S+)", Pattern.CASE_INSENSITIVE), "$1****"),
    EMAIL(Pattern.compile("(\"email\"\\s*:\\s*\")([^\"@]+)(@[^\"]+)(\")", Pattern.CASE_INSENSITIVE),
            "$1****$3$4");

    private final Pattern pattern;
    private final String replacement;

    MaskRule(Pattern pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    String apply(String input) {
        return pattern.matcher(input).replaceAll(replacement);
    }
}
