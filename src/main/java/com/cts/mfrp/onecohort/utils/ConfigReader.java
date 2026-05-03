package com.cts.mfrp.onecohort.utils;

import com.cts.mfrp.onecohort.constants.AppConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        loadConfig("src/test/resources/config.properties");
    }

    private static void loadConfig(String path) {
        try (FileInputStream fis = new FileInputStream(path)) {
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + path + " — " + e.getMessage());
        }
    }

    /** Switch to staging config at runtime: ConfigReader.loadEnv("staging") */
    public static void loadEnv(String env) {
        loadConfig("src/test/resources/config-" + env + ".properties");
    }

    public static String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) throw new RuntimeException("Property not found: " + key);
        return value.trim();
    }

    public static String getBrowser()      { return get(AppConstants.PROP_BROWSER); }
    public static String getBaseUrl()      { return get(AppConstants.PROP_BASE_URL); }
    public static String getUsername()     { return get(AppConstants.PROP_USERNAME); }
    public static String getPassword()     { return get(AppConstants.PROP_PASSWORD); }
    public static boolean isHeadless()     { return Boolean.parseBoolean(get(AppConstants.PROP_HEADLESS)); }
    public static int getImplicitWait()    { return Integer.parseInt(get(AppConstants.PROP_IMPLICIT_WAIT)); }
    public static int getExplicitWait()    { return Integer.parseInt(get(AppConstants.PROP_EXPLICIT_WAIT)); }
}
