package com.org.apitest.observability;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public final class AllureSteps {

    private AllureSteps() {
    }

    @Step("{stepName}")
    public static void step(String stepName) {
        // Annotation drives Allure step; body intentionally empty.
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", json);
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }
}
