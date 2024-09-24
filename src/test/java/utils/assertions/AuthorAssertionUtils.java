package utils.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import models.requests.authors.AuthorRequest;
import models.responses.common.BadRequestResponse;
import models.responses.authors.PostAuthorResponse;

import static org.testng.Assert.assertEquals;
import static utils.common.JsonUtils.parseJsonResponseObject;

/**
 * Utility class for performing assertions specific to author-related API requests.
 * This class extends {@link AssertionsUtils} to provide additional helper methods
 * for asserting successful author creation and validation of error responses.
 */
public class AuthorAssertionUtils extends AssertionsUtils {

    /**
     * Asserts that the author created by the API matches the expected details provided in the request.
     *
     * @param response  the Response object from the API containing the created author details.
     * @param newAuthor the AuthorRequest object with the expected author details.
     * @throws JsonProcessingException if the response cannot be parsed into a PostAuthorResponse object.
     */
    public static void assertAuthorCreated(Response response, AuthorRequest newAuthor) throws JsonProcessingException {
        PostAuthorResponse createdAuthor = parseJsonResponseObject(response, PostAuthorResponse.class);

        assertEquals(createdAuthor.getId(), newAuthor.getId(), "The ID does not match.");
        assertEquals(createdAuthor.getIdBook(), newAuthor.getIdBook(), "The book ID does not match.");
        assertEquals(createdAuthor.getFirstName(), newAuthor.getFirstName(), "The first name does not match.");
        assertEquals(createdAuthor.getLastName(), newAuthor.getLastName(), "The last name does not match.");
    }

    /**
     * Asserts that the API response contains the expected error messages when creating an author with invalid data.
     * The method validates that the appropriate error message is returned for missing or invalid 'id' and 'idBook' fields.
     *
     * @param response the Response object containing the bad request details.
     * @param id       the ID that was sent in the request, or null if it was missing.
     * @param idBook   the book ID that was sent in the request, or null if it was missing.
     * @throws JsonProcessingException if the response cannot be parsed into a BadRequestResponse object.
     */
    public static void assertAuthorErrors(Response response, Long id, Long idBook) throws JsonProcessingException {
        BadRequestResponse badRequestResponse = parseJsonResponseObject(response, BadRequestResponse.class);

        if (id == null && idBook == null) {
            assertErrors(badRequestResponse, "$.id", "The JSON value could not be converted to System.Int32. Path: $.id | LineNumber: 0 | BytePositionInLine: 10.");
        } else if (id == null) {
            assertErrors(badRequestResponse, "$.id", "The JSON value could not be converted to System.Int32. Path: $.id | LineNumber: 0 | BytePositionInLine: 10.");
        } else if (idBook == null) {
            assertErrors(badRequestResponse, "$.idBook", "The JSON value could not be converted to System.Int32. Path: $.idBook | LineNumber: 0 | BytePositionInLine: 21.");
        }
    }

}