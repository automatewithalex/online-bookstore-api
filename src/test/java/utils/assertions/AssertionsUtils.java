package utils.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import models.responses.common.BadRequestResponse;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static org.testng.Assert.*;
import static utils.common.JsonUtils.parseJsonResponseObject;

/**
 * Utility class for custom assertion methods to be used in API testing.
 * This class provides helper methods for asserting HTTP response details,
 * including status codes, response times, and specific error messages.
 */
public class AssertionsUtils {

    /**
     * Asserts that the response status code matches the expected status code.
     * Logs the result and error message if the assertion fails.
     *
     * @param response           the Response object to check.
     * @param expectedStatusCode the expected HTTP status code.
     * @param logger             the Logger instance to log the messages.
     */
    public static void assertStatusCode(Response response, int expectedStatusCode, Logger logger) {
        int actualStatusCode = response.getStatusCode();

        try {
            assertEquals(actualStatusCode, expectedStatusCode);
            logger.info("Status code assertion PASSED: Expected [{}], Actual [{}]", expectedStatusCode, actualStatusCode);
        } catch (AssertionError e) {
            logger.error("Status code assertion FAILED: Expected [{}], Actual [{}]. Error: {}", expectedStatusCode, actualStatusCode, e.getMessage());
            throw e;
        }
    }

    /**
     * Asserts that the response time is within the specified maximum time.
     * Logs the result and error message if the assertion fails.
     *
     * @param response        the Response object to check.
     * @param maxTimeInMillis the maximum allowed response time in milliseconds.
     * @param logger          the Logger instance to log the messages.
     */
    public static void assertResponseTime(Response response, long maxTimeInMillis, Logger logger) {
        long responseTime = response.time();

        try {
            assertTrue(responseTime <= maxTimeInMillis,
                    "Response time exceeded the maximum allowed time. Expected <= " + maxTimeInMillis + "ms but got " + responseTime + "ms");
            logger.info("Response time assertion PASSED: Expected <= {} ms, Actual [{}] ms", maxTimeInMillis, responseTime);
        } catch (AssertionError e) {
            logger.error("Response time assertion FAILED: Expected <= {} ms, Actual [{}] ms", maxTimeInMillis, responseTime);
            throw e;
        }
    }

    /**
     * Asserts that all expected items are present in the actual list, and reports missing items.
     * Logs the result and error message if the assertion fails.
     *
     * @param actualList   the list returned from the API response.
     * @param expectedList the list of expected items.
     * @param missingItems the list of missing items to display in the failure message.
     * @param jsonResponse the raw API JSON response for debugging.
     * @param logger       the Logger instance to log the messages.
     * @param <T>          the type of items in the lists.
     */
    public static <T> void assertMissingItems(List<T> actualList, List<T> expectedList, List<T> missingItems, String jsonResponse, Logger logger) {
        try {
            assertTrue(actualList.containsAll(expectedList),
                    String.format(
                            "The API response does not contain the following expected items:\n%s\nResponse:\n%s",
                            missingItems,
                            jsonResponse
                    )
            );
            logger.info("Missing items assertion PASSED. All expected items are present.");
        } catch (AssertionError e) {
            logger.error("Missing items assertion FAILED. Missing items: {}\nResponse: {}", missingItems, jsonResponse);
            throw e;
        }
    }

    /**
     * Asserts that there are no duplicate items in the list.
     * Logs the result and error message if the assertion fails.
     *
     * @param duplicateItems the list of duplicate items found in the API response.
     * @param jsonResponse   the raw API JSON response for debugging.
     * @param logger         the Logger instance to log the messages.
     * @param <T>            the type of items in the list.
     */
    public static <T> void assertNoDuplicateItems(List<T> duplicateItems, String jsonResponse, Logger logger) {
        try {
            assertTrue(duplicateItems.isEmpty(),
                    String.format(
                            "The API response contains the following duplicate items:\n%s\nResponse:\n%s",
                            duplicateItems,
                            jsonResponse
                    )
            );
            logger.info("Duplicate items assertion PASSED. No duplicates found.");
        } catch (AssertionError e) {
            logger.error("Duplicate items assertion FAILED. Duplicates found: {}\nResponse: {}", duplicateItems, jsonResponse);
            throw e;
        }
    }

    /**
     * Asserts that two objects match, comparing the expected and actual values.
     * Logs the result and error message if the assertion fails.
     *
     * @param expected the expected object.
     * @param actual   the actual object returned from the API.
     * @param logger   the Logger instance to log the messages.
     * @param <T>      the type of the objects.
     */
    public static <T> void assertItemMatches(T expected, T actual, Logger logger) {
        try {
            assertEquals(actual, expected, String.format("Expected object:\n%s\nbut got:\n%s", expected, actual));
            logger.info("Item matches assertion PASSED. Expected and actual items match.");
        } catch (AssertionError e) {
            logger.error("Item matches assertion FAILED. Expected: {}\nActual: {}", expected, actual);
            throw e;
        }
    }

    /**
     * Asserts the details of a bad request response, including the title, status, type, and trace ID.
     * Logs the result and error message if the assertion fails.
     *
     * @param response       the Response object to check.
     * @param expectedTitle  the expected title in the bad request response.
     * @param expectedStatus the expected HTTP status code for the bad request.
     * @param logger         the Logger instance to log the messages.
     * @throws JsonProcessingException if the response cannot be parsed into a BadRequestResponse object.
     */
    public static void assertBadRequest(Response response, String expectedTitle, int expectedStatus, Logger logger) throws JsonProcessingException {
        BadRequestResponse badRequestResponse = parseJsonResponseObject(response, BadRequestResponse.class);

        try {
            assertEquals(badRequestResponse.getTitle(), expectedTitle, "The error title does not match the expected value.");
            assertEquals(badRequestResponse.getStatus(), expectedStatus, "The error status does not match the expected value.");
            assertTrue(isNonEmptyString(badRequestResponse.getType()), "The 'type' field should be a non-empty string.");
            assertTrue(badRequestResponse.getType().startsWith("https://"), "The 'type' field should start with 'https://'.");
            assertTrue(isNonEmptyString(badRequestResponse.getTraceId()), "The 'traceId' field should be a non-empty string.");

            logger.info("Bad request assertion PASSED.");
        } catch (AssertionError e) {
            logger.error("Bad request assertion FAILED. Expected title: {}, status: {}. Response: {}", expectedTitle, expectedStatus, response.body().asString());
            throw e;
        }
    }

    /**
     * Asserts the details of a bad request response, including errors related to a specific field.
     * Logs the result and error message if the assertion fails.
     *
     * @param response             the Response object to check.
     * @param expectedTitle        the expected title in the bad request response.
     * @param expectedStatus       the expected HTTP status code for the bad request.
     * @param field                the field for which the error is expected.
     * @param expectedErrorMessage the expected error message for the field.
     * @param logger               the Logger instance to log the messages.
     * @throws JsonProcessingException if the response cannot be parsed into a BadRequestResponse object.
     */
    public static void assertBadRequestWithErrors(Response response, String expectedTitle, int expectedStatus, String field, String expectedErrorMessage, Logger logger) throws JsonProcessingException {
        BadRequestResponse badRequestResponse = parseJsonResponseObject(response, BadRequestResponse.class);

        assertBadRequest(response, expectedTitle, expectedStatus, logger);

        try {
            assertErrors(badRequestResponse, field, expectedErrorMessage, logger);
            logger.info("Bad request with field errors assertion PASSED.");
        } catch (AssertionError e) {
            logger.error("Bad request with field errors assertion FAILED. Field: {}, Expected error: {}. Response: {}", field, expectedErrorMessage, response.body().asString());
            throw e;
        }
    }

    /**
     * Asserts that a specific field contains the expected error message in a bad request response.
     * Logs the result and error message if the assertion fails.
     *
     * @param badRequestResponse   the BadRequestResponse object containing the errors.
     * @param field                the field name where the error occurred.
     * @param expectedErrorMessage the expected error message for the field.
     * @param logger               the Logger instance to log the messages.
     */
    public static void assertErrors(BadRequestResponse badRequestResponse, String field, String expectedErrorMessage, Logger logger) {
        List<String> fieldErrors = badRequestResponse.getErrors().get(field);

        try {
            assertTrue(badRequestResponse.getErrors().containsKey(field), "The 'errors' field should contain '" + field + "' as a key.");
            assertNotNull(fieldErrors, "The 'errors' field '" + field + "' should not be null.");
            assertTrue(fieldErrors.contains(expectedErrorMessage), "The error message for '" + field + "' is incorrect.");
            logger.info("Field error assertion PASSED for field '{}'.", field);
        } catch (AssertionError e) {
            logger.error("Field error assertion FAILED for field '{}'. Expected: '{}', Actual: '{}'", field, expectedErrorMessage, fieldErrors);
            throw e;
        }
    }

    /**
     * Utility method to check if a string is non-empty (i.e., not null or empty after trimming).
     *
     * @param value the string to check.
     * @return true if the string is non-empty, false otherwise.
     */
    public static boolean isNonEmptyString(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
