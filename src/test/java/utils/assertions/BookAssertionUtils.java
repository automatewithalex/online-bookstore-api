package utils.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import models.requests.books.BookRequest;
import models.responses.books.PostBookResponse;
import models.responses.common.BadRequestResponse;

import static org.testng.Assert.assertEquals;
import static utils.common.JsonUtils.parseJsonResponseObject;

/**
 * Utility class for performing assertions specific to author-related API requests.
 * This class extends {@link AssertionsUtils} to provide additional helper methods
 * for asserting successful author creation and validation of error responses.
 */
public class BookAssertionUtils extends AssertionsUtils {

    /**
     * Asserts that the book created by the API matches the expected details provided in the request.
     *
     * @param response the Response object from the API containing the created book details.
     * @param newBook  the BookRequest object with the expected book details.
     * @throws JsonProcessingException if the response cannot be parsed into a PostBookResponse object.
     */
    public static void assertBookCreated(Response response, BookRequest newBook) throws JsonProcessingException {
        PostBookResponse createdBook = parseJsonResponseObject(response, PostBookResponse.class);

        assertEquals(createdBook.getId(), newBook.getId(), "The ID does not match.");
        assertEquals(createdBook.getTitle(), newBook.getTitle(), "The title does not match.");
        assertEquals(createdBook.getDescription(), newBook.getDescription(), "The description does not match.");
        assertEquals(createdBook.getPageCount(), newBook.getPageCount(), "The page count does not match.");
        assertEquals(createdBook.getExcerpt(), newBook.getExcerpt(), "The excerpt does not match.");
        assertEquals(createdBook.getPublishDate(), newBook.getPublishDate(), "The publish date does not match.");
    }

    /**
     * Asserts that the API response contains the expected error messages when creating a book with invalid data.
     * The method validates that the appropriate error message is returned for missing or invalid fields such as 'id', 'title', and 'pageCount'.
     *
     * @param response  the Response object containing the bad request details.
     * @param id        the ID that was sent in the request, or null if it was missing.
     * @param title     the title that was sent in the request, or null if it was missing.
     * @param pageCount the page count that was sent in the request, or null if it was missing.
     * @throws JsonProcessingException if the response cannot be parsed into a BadRequestResponse object.
     */
    public static void assertBookErrors(Response response, Long id, String title, Integer pageCount) throws JsonProcessingException {
        BadRequestResponse badRequestResponse = parseJsonResponseObject(response, BadRequestResponse.class);

        if (id == null) {
            assertErrors(badRequestResponse, "$.id", "The JSON value could not be converted to System.Int32. Path: $.id | LineNumber: 0.");
        }
        if (title == null) {
            assertErrors(badRequestResponse, "$.title", "The JSON value could not be converted to System.String. Path: $.title | LineNumber: 1.");
        }
        if (pageCount == null) {
            assertErrors(badRequestResponse, "$.pageCount", "The JSON value could not be converted to System.Int32. Path: $.pageCount | LineNumber: 2.");
        }
    }

}