package utils.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import models.responses.common.BadRequestResponse;

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
     *
     * @param response           the Response object to check.
     * @param expectedStatusCode the expected HTTP status code.
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected HTTP status code " + expectedStatusCode + " but got " + response.getStatusCode());
    }

    /**
     * Asserts that the response time is within the specified maximum time.
     *
     * @param response        the Response object to check.
     * @param maxTimeInMillis the maximum allowed response time in milliseconds.
     */
    public static void assertResponseTime(Response response, long maxTimeInMillis) {
        long responseTime = response.time();
        assertTrue(responseTime <= maxTimeInMillis,
                "Response time exceeded the maximum allowed time. Expected <= " + maxTimeInMillis + "ms but got " + responseTime + "ms");
    }

    /**
     * Asserts that all expected items are present in the actual list, and reports missing items.
     *
     * @param actualList   the list returned from the API response.
     * @param expectedList the list of expected items.
     * @param missingItems the list of missing items to display in the failure message.
     * @param jsonResponse the raw API JSON response for debugging.
     * @param <T>          the type of items in the lists.
     */
    public static <T> void assertMissingItems(List<T> actualList, List<T> expectedList, List<T> missingItems, String jsonResponse) {
        assertTrue(actualList.containsAll(expectedList),
                String.format(
                        "The API response does not contain the following expected items:\n%s\nResponse:\n%s",
                        missingItems,
                        jsonResponse
                )
        );
    }

    /**
     * Asserts that there are no duplicate items in the list.
     *
     * @param duplicateItems the list of duplicate items found in the API response.
     * @param jsonResponse   the raw API JSON response for debugging.
     * @param <T>            the type of items in the list.
     */
    public static <T> void assertNoDuplicateItems(List<T> duplicateItems, String jsonResponse) {
        assertTrue(duplicateItems.isEmpty(),
                String.format(
                        "The API response contains the following duplicate items:\n%s\nResponse:\n%s",
                        duplicateItems,
                        jsonResponse
                )
        );
    }

    /**
     * Asserts that two objects match, comparing the expected and actual values.
     *
     * @param expected the expected object.
     * @param actual   the actual object returned from the API.
     * @param <T>      the type of the objects.
     */
    public static <T> void assertItemMatches(T expected, T actual) {
        assertEquals(actual, expected, String.format("Expected object:\n%s\nbut got:\n%s", expected, actual));
    }

    /**
     * Asserts the details of a bad request response, including the title, status, type, and trace ID.
     *
     * @param response       the Response object to check.
     * @param expectedTitle  the expected title in the bad request response.
     * @param expectedStatus the expected HTTP status code for the bad request.
     * @throws JsonProcessingException if the response cannot be parsed into a BadRequestResponse object.
     */
    public static void assertBadRequest(Response response, String expectedTitle, int expectedStatus) throws JsonProcessingException {
        BadRequestResponse badRequestResponse = parseJsonResponseObject(response, BadRequestResponse.class);

        assertEquals(badRequestResponse.getTitle(), expectedTitle, "The error title does not match the expected value.");
        assertEquals(badRequestResponse.getStatus(), expectedStatus, "The error status does not match the expected value.");
        assertTrue(isNonEmptyString(badRequestResponse.getType()), "The 'type' field should be a non-empty string.");
        assertTrue(badRequestResponse.getType().startsWith("https://"), "The 'type' field should start with 'https://'.");
        assertTrue(isNonEmptyString(badRequestResponse.getTraceId()), "The 'traceId' field should be a non-empty string.");
    }

    /**
     * Asserts the details of a bad request response, including errors related to a specific field.
     *
     * @param response             the Response object to check.
     * @param expectedTitle        the expected title in the bad request response.
     * @param expectedStatus       the expected HTTP status code for the bad request.
     * @param field                the field for which the error is expected.
     * @param expectedErrorMessage the expected error message for the field.
     * @throws JsonProcessingException if the response cannot be parsed into a BadRequestResponse object.
     */
    public static void assertBadRequestWithErrors(Response response, String expectedTitle, int expectedStatus, String field, String expectedErrorMessage) throws JsonProcessingException {
        BadRequestResponse badRequestResponse = parseJsonResponseObject(response, BadRequestResponse.class);

        assertBadRequest(response, expectedTitle, expectedStatus);
        assertErrors(badRequestResponse, field, expectedErrorMessage);
    }

    /**
     * Asserts that a specific field contains the expected error message in a bad request response.
     *
     * @param badRequestResponse   the BadRequestResponse object containing the errors.
     * @param field                the field name where the error occurred.
     * @param expectedErrorMessage the expected error message for the field.
     */
    public static void assertErrors(BadRequestResponse badRequestResponse, String field, String expectedErrorMessage) {
        List<String> fieldErrors = badRequestResponse.getErrors().get(field);

        assertTrue(badRequestResponse.getErrors().containsKey(field), "The 'errors' field should contain '" + field + "' as a key.");
        assertNotNull(fieldErrors, "The 'errors' field '" + field + "' should not be null.");
        assertTrue(fieldErrors.contains(expectedErrorMessage), "The error message for '" + field + "' is incorrect. Error message: \n" + fieldErrors);
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
