package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static String env;
    private static String apiVersion;
    private static final Properties apiProperties = new Properties();
    private static final Properties pathProperties = new Properties();

    private static final String DEFAULT_API_PROPERTIES_PATH = "src/test/resources/api.properties";
    private static final String DEFAULT_PATH_PROPERTIES_PATH = "src/test/resources/path.properties";

    /**
     * Loads properties from a specified file path into the provided Properties object.
     *
     * @param filePath   the path to the properties file to be loaded.
     * @param properties the Properties object where the properties will be stored.
     * @throws RuntimeException if the properties file cannot be loaded.
     */
    private static void loadProperties(String filePath, Properties properties) {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load properties from file: " + filePath);
        }
    }

    /**
     * Loads API-related properties from the default API properties file.
     */
    public static void loadApiProperties() {
        loadProperties(DEFAULT_API_PROPERTIES_PATH, apiProperties);
    }

    /**
     * Loads path-related properties from the default path properties file.
     */
    public static void loadPathProperties() {
        loadProperties(DEFAULT_PATH_PROPERTIES_PATH, pathProperties);
    }

    /**
     * Retrieves a property value from the API properties file.
     *
     * @param key the property key to retrieve the value for.
     * @return the property value associated with the provided key, or null if the key does not exist.
     */
    public static String getApiProperty(String key) {
        return apiProperties.getProperty(key);
    }

    /**
     * Retrieves a property value from the path properties file.
     *
     * @param key the property key to retrieve the value for.
     * @return the property value associated with the provided key, or null if the key does not exist.
     */
    public static String getPathProperty(String key) {
        return pathProperties.getProperty(key);
    }

    /**
     * Retrieves the base URL for the specified environment from the API properties.
     *
     * @param env the environment (e.g., "dev", "prod") for which to retrieve the base URL.
     * @return the base URL for the specified environment, or null if the environment key does not exist.
     */
    public static String getBaseUrl(String env) {
        return getApiProperty("base.url." + env);
    }

    /**
     * Retrieves the API version based on the specified version identifier.
     *
     * @param version the version identifier (e.g., "v1", "v2").
     * @return the API version for the specified version identifier, or null if the version key does not exist.
     */
    public static String getApiVersion(String version) {
        return getApiProperty("api." + version);
    }

    /**
     * Retrieves the API endpoint for the specified endpoint identifier from the API properties.
     *
     * @param endpoint the identifier for the endpoint (e.g., "Authors", "Users").
     * @return the API endpoint associated with the specified identifier, or null if the endpoint key does not exist.
     */
    public static String getEndpoint(String endpoint) {
        return getApiProperty("api.endpoint." + endpoint);
    }

    /**
     * Retrieves the maximum allowable API response time from the API properties.
     *
     * @return the maximum response time in milliseconds, or null if the key does not exist.
     * @throws NumberFormatException if the value cannot be parsed as a Long.
     */
    public static Long getMaxResponseTime() {
        return Long.valueOf(getApiProperty("max.api.response.time"));
    }

    /**
     * Sets the environment value to be used in the tests.
     * This method stores the provided environment string (e.g., "dev", "qa", "prod")
     * and makes it accessible throughout the test suite.
     *
     * @param environment the environment value (e.g., "dev", "qa", "prod") to set for the tests.
     */
    public static void setEnv(String environment) {
        env = environment;
    }

    /**
     * Retrieves the current environment value set for the tests.
     * This method returns the environment value that was previously set using {@link #setEnv(String)}.
     *
     * @return the current environment value (e.g., "dev", "qa", "prod") used in the tests.
     */
    public static String getEnv() {
        return env;
    }

    /**
     * Sets the API version to be used in the tests.
     * This method stores the provided API version string (e.g., "v1", "v2")
     * and makes it accessible throughout the test suite.
     *
     * @param version the API version (e.g., "v1", "v2") to set for the tests.
     */
    public static void setVersion(String version) {
        apiVersion = version;
    }

    /**
     * Retrieves the current API version value set for the tests.
     * This method returns the API version value that was previously set using {@link #setVersion(String)}.
     *
     * @return the current API version (e.g., "v1", "v2") used in the tests.
     */
    public static String getVersion() {
        return apiVersion;
    }

}