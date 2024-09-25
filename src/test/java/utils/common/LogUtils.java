package utils.common;

import org.apache.logging.log4j.Logger;

public class LogUtils {

    /**
     * Logs the start of a test case with its name and description.
     *
     * @param logger     the logger instance.
     * @param info   the name of the test.
     */
    public static void logInfo(Logger logger, String info) {
        logger.info("{}", info);
    }

    /**
     * Logs the response info.
     *
     * @param logger     the logger instance.
     * @param endpoint   the name of the test.
     * @param status   the name of the test.
     * @param time   the name of the test.
     */
    public static void logResponseInfo(Logger logger, String endpoint, int status, long time) {
        logger.info("Endpoint: {} Status code: {} Response time: {}", endpoint, status, time);
    }

    /**
     * Logs the response body info.
     *
     * @param logger     the logger instance.
     * @param endpoint   the name of the test.
     * @param body   the name of the test.
     */
    public static void logResponseDebug(Logger logger, String endpoint, String body) {
        logger.debug("Endpoint: {}\n Response body: {}", endpoint, body);
    }

    /**
     * Logs the start of a test case with its name and description.
     *
     * @param logger     the logger instance.
     * @param testName   the name of the test.
     */
    public static void logTestStart(Logger logger, String testName) {
        logger.info("\n ### Starting Test: {}", testName);
    }

    /**
     * Logs the test parameters being used in the test.
     *
     * @param logger the logger instance.
     * @param params the parameters being used in the test.
     */
    public static void logTestParams(Logger logger, Object... params) {
        logger.info("Test Parameters: {}", (Object) params);
    }

    /**
     * Logs the completion of a test case.
     *
     * @param logger   the logger instance.
     * @param testName the name of the test.
     */
    public static void logTestEnd(Logger logger, String testName) {
        logger.info("\n ### Test Completed: {}", testName);
    }

}
