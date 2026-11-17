package com.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    // Eager singleton: the one object is created when the class loads
    private static final ConfigManager INSTANCE = new ConfigManager();
    private final Properties prop = new Properties();

    private ConfigManager() {
        String env = System.getProperty("env", "qa");
        String Filename = "config/" + env + ".properties";
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(Filename)) {
            ;
            if (input == null) {
                throw new RuntimeException(Filename + "not found ");
            }
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException("could not read" + Filename, e);
        }
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    public String getSecret(String envName) {
        String env = System.getenv(envName);
        if (env == null || env.isBlank()) {
            throw new RuntimeException(envName + " Environment variable is not set");
        }
        return env;

    }
}
