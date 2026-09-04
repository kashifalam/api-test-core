package com.org.apitest.base.listeners;

import com.org.apitest.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Loads environment configuration once per TestNG suite.
 */
public class ConfigListener implements ISuiteListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigListener.class);

    @Override
    public void onStart(ISuite suite) {
        String env = System.getProperty("env", "qa");
        LOG.info("Loading config for environment: {}", env);
        ConfigManager.load(env);
    }

    @Override
    public void onFinish(ISuite suite) {
        ConfigManager.reset();
    }
}
