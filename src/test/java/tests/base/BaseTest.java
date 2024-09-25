package tests.base;

import config.ConfigManager;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Parameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static utils.common.LogUtils.*;

/**
 * The base class for API tests. This class is responsible for setting up the test environment
 * and loading configuration properties before the tests are run. It provides common setup
 * and teardown methods for all tests that extend this class.
 */
public class BaseTest {

    protected String env;
    protected long maxResponseTime;
    protected String authorsEndpoint;
    protected String booksEndpoint;

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    /**
     * Sets up the test environment before any test methods in the class are executed.
     * Loads the necessary API and path properties and configures RestAssured with the base URI.
     *
     * @param apiVersion the version of the API to be tested (e.g., "v1", "v2").
     * @param env        the environment in which the tests are running (e.g., "dev", "prod").
     */
    @BeforeClass
    @Parameters({"apiVersion", "env"})
    public void setupConfig(String apiVersion, String env) {
        logInfo(logger, "Initializing setup");
        logTestParams(logger, env, apiVersion);
        setEnv(env);
        setVersion(apiVersion);
        loadConfig();
        configureRestAssured();
        setupEndpoints();
        setMaxResponseTime();
        logInfo(logger, "Setup completed");
    }

    /**
     * Loads configuration for the given environment.
     * This method fetches and loads the necessary properties for the provided environment.
     *
     */
    private void loadConfig() {
        ConfigManager.loadApiProperties();
        ConfigManager.loadPathProperties();
    }

    /**
     * Configures the environment
     */
    private void setEnv(String env) {
        this.env = env;
        ConfigManager.setEnv(env);
    }

    /**
     * Configures the version
     */
    private void setVersion(String apiVersion) {
        ConfigManager.setVersion(apiVersion);
    }

    /**
     * Configures the max response time
     */
    private void setMaxResponseTime() {
        maxResponseTime = ConfigManager.getMaxResponseTime();
    }

    /**
     * Sets up the API endpoints for authors and books.
     */
    private void setupEndpoints() {
        String versionedApiPath = ConfigManager.getApiVersion(ConfigManager.getVersion());
        this.authorsEndpoint = versionedApiPath + ConfigManager.getEndpoint("authors");
        this.booksEndpoint = versionedApiPath + ConfigManager.getEndpoint("books");
    }

    /**
     * Configures the RestAssured base URI.
     */
    private void configureRestAssured() {
        RestAssured.baseURI = ConfigManager.getBaseUrl(ConfigManager.getEnv());
        logger.debug("API Base URL: {}", RestAssured.baseURI);
    }

    /**
     * Cleans up resources after all test methods in the class have been executed.
     * This method can be used to close connections or reset configurations.
     * Currently, this method is empty but can be overridden or extended as needed.
     */
    @AfterClass
    public void tearDown() {
        logInfo(logger, "Initializing tear down");
        // Clean up after all tests (optional)
        // For example, closing database connections or resetting configurations
        logInfo(logger, "Tear down completed");
    }

}