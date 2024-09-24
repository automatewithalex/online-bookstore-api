package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import data_providers.DataProviders;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.requests.books.BookRequest;
import models.responses.books.GetBooksResponse;
import org.testng.annotations.Test;
import tests.base.BaseTest;

import java.io.IOException;
import java.util.List;

import static utils.common.CommonUtils.*;
import static utils.common.JsonUtils.*;
import static utils.assertions.AssertionsUtils.*;
import static utils.assertions.BookAssertionUtils.assertBookCreated;
import static utils.assertions.BookAssertionUtils.assertBookErrors;

public class BooksTests extends BaseTest {

    @Epic("Books Management")
    @Feature("Get Books")
    @Story("US-006")
    @Issue("DE-006")
    @Test(testName = "GET All Books", description = "Validates that the Books API response contains all expected books and does not have any duplicates.", priority = 0)
    public void testBooksApiGetAll() throws IOException {
        Response response = RestAssured.given()
                .get(booksEndpoint);

        List<GetBooksResponse> responseBooks = parseJsonResponseList(response, GetBooksResponse.class);
        List<GetBooksResponse> expectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);
        List<GetBooksResponse> missingBooks = findMissingObjects(expectedBooks, responseBooks);
        List<GetBooksResponse> duplicateBooks = findDuplicateObjects(responseBooks);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertMissingItems(responseBooks, expectedBooks, missingBooks, printPrettyJson(response.body().asString()));
        assertNoDuplicateItems(duplicateBooks, printPrettyJson(response.body().asString()));
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Issue("DE-007")
    @Test(testName = "GET Book by existing ID", description = "Validates that the books API response contains all expected book details.", priority = 0)
    public void testBooksApiGetByID() throws IOException {
        List<GetBooksResponse> expectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);

        Response response = RestAssured.given()
                .get(booksEndpoint + "/" + expectedBooks.getFirst().getId());

        GetBooksResponse responseBook = parseJsonResponseObject(response, GetBooksResponse.class);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertItemMatches(expectedBooks.getFirst(), responseBook);
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Test(testName = "GET Book with non-existing ID", description = "Validates that the books API response code is 404 and response details are as expected", priority = 0)
    public void testBooksApiGetByNonExistentID() throws JsonProcessingException {
        Response response = RestAssured.given()
                .get(booksEndpoint + "/0000");

        assertStatusCode(response, 404);
        assertResponseTime(response, maxResponseTime);

        assertBadRequest(response, "Not Found", 404);
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Test(testName = "GET Book with invalid ID", description = "Validates that the books API response code is 400 for an invalid ID and response details are as expected", priority = 0)
    public void testBooksApiGetByInvalidID() throws JsonProcessingException {
        String invalidID = "invalidID";

        Response response = RestAssured.given()
                .get(booksEndpoint + "/" + invalidID);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid."
        );
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Test(testName = "GET Book with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the Book ID parameter correctly.", priority = 0)
    public void testBooksApiSqlInjectionInId() throws JsonProcessingException {
        String sqlInjectionId = "' OR 1=1; --";

        Response response = RestAssured.given()
                .get(booksEndpoint + "/" + sqlInjectionId);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + sqlInjectionId + "' is not valid."
        );
    }

    @Epic("Books Management")
    @Feature("Create Book")
    @Story("US-008")
    @Issues({
            @Issue("DE-008"),
            @Issue("DE-009"),
            @Issue("DE-010"),
            @Issue("DE-011"),
            @Issue("DE-012"),
            @Issue("DE-013")
    })
    @Test(dataProvider = "createBookDataProvider", dataProviderClass = DataProviders.class, description = "Creates a new Book and validates the response and status code.", testName = "POST", priority = 1)
    public void testCreateNewBook(String testName, int expectedStatusCode, Long id, String title, String description, int pageCount, String excerpt, String publishDate) throws JsonProcessingException {
        BookRequest newBook = new BookRequest(id, title, description, pageCount, excerpt, publishDate);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .post(booksEndpoint);

        assertResponseTime(response, maxResponseTime);

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200);
            assertBookCreated(response, newBook);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400);
            assertBadRequest(response, "One or more validation errors occurred.", 400);
            assertBookErrors(response, id, title, pageCount);
        }
    }

    @Epic("Books Management")
    @Feature("Create Book")
    @Story("US-008")
    @Test(testName = "POST Create Book with valid data persistence check", description = "Validates that the Books API is persisting Books", priority = 1)
    public void testCreateNewBookPersistenceCheck() throws IOException {
        Response responseAllBooks = RestAssured.given()
                .get(booksEndpoint);

        assertStatusCode(responseAllBooks, 200);
        assertResponseTime(responseAllBooks, maxResponseTime);

        List<GetBooksResponse> getResponseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        List<GetBooksResponse> getExpectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);
        long newBookId = getResponseBooks.getLast().getId() + 1;

        BookRequest newBook = new BookRequest(
                newBookId,
                getExpectedBooks.getFirst().getTitle(),
                getExpectedBooks.getFirst().getDescription(),
                getExpectedBooks.getFirst().getPageCount(),
                getExpectedBooks.getFirst().getExcerpt(),
                getExpectedBooks.getFirst().getPublishDate()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .post(booksEndpoint);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertBookCreated(response, newBook);

        Response responseBook = RestAssured.given()
                .get(booksEndpoint + "/" + newBookId);

        assertStatusCode(responseBook, 200);
        assertResponseTime(responseBook, maxResponseTime);

        GetBooksResponse getResponseBooksLatest = parseJsonResponseObject(responseBook, GetBooksResponse.class);

        assertItemMatches(newBook, getResponseBooksLatest);
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Issues({
            @Issue("DE-014"),
            @Issue("DE-015"),
            @Issue("DE-016")
    })
    @Test(dataProvider = "updateBookDataProvider", dataProviderClass = DataProviders.class, description = "Updates Book and validates the response and status code.", testName = "PUT", priority = 2)
    public void testUpdateBook(String testName, int expectedStatusCode, Long id, String title, String description, int pageCount, String excerpt, String publishDate) throws JsonProcessingException {
        BookRequest BookRequest = new BookRequest(id, title, description, pageCount, excerpt, publishDate);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(BookRequest)
                .put(booksEndpoint + "/" + id);

        assertResponseTime(response, maxResponseTime);

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200);
            assertBookCreated(response, BookRequest);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400);
            assertBadRequest(response, "One or more validation errors occurred.", 400);
            assertBookErrors(response, id, title, pageCount);
        }
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Issue("DE-017")
    @Test(testName = "PUT Books with non existing ID", description = "Validates that the Books API response is handling non existing ID values", priority = 2)
    public void testUpdateBookWithNonExistingID() throws IOException {
        List<GetBooksResponse> expectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);

        BookRequest newBook = new BookRequest(
                expectedBooks.getFirst().getId(),
                expectedBooks.getFirst().getTitle(),
                expectedBooks.getFirst().getDescription(),
                expectedBooks.getFirst().getPageCount(),
                expectedBooks.getFirst().getExcerpt(),
                expectedBooks.getFirst().getPublishDate()
        );

        Response responseAllBooks = RestAssured.given().get(booksEndpoint);
        List<GetBooksResponse> responseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .put(booksEndpoint + "/" + (responseBooks.getLast().getId() + 1));

        assertStatusCode(response, 404);
        assertResponseTime(response, maxResponseTime);

        assertBadRequest(response, "Not Found", 404);
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Test(testName = "PUT Books with invalid ID", description = "Validates that the Books API response is handling non existing ID values", priority = 2)
    public void testUpdateBookWithInvalidID() throws IOException {
        String invalidID = "invalidID";
        List<GetBooksResponse> expectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);

        BookRequest newBook = new BookRequest(
                expectedBooks.getFirst().getId(),
                expectedBooks.getFirst().getTitle(),
                expectedBooks.getFirst().getDescription(),
                expectedBooks.getFirst().getPageCount(),
                expectedBooks.getFirst().getExcerpt(),
                expectedBooks.getFirst().getPublishDate()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .put(booksEndpoint + "/" + invalidID);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid."
        );
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Test(testName = "PUT Books with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the Book ID parameter correctly.", priority = 2)
    public void testUpdateBooksApiSqlInjectionInId() throws IOException {
        String sqlInjectionId = "' OR 1=1; --";
        List<GetBooksResponse> expectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);

        BookRequest newBook = new BookRequest(
                expectedBooks.getFirst().getId(),
                expectedBooks.getFirst().getTitle(),
                expectedBooks.getFirst().getDescription(),
                expectedBooks.getFirst().getPageCount(),
                expectedBooks.getFirst().getExcerpt(),
                expectedBooks.getFirst().getPublishDate()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .put(booksEndpoint + "/" + sqlInjectionId);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + sqlInjectionId + "' is not valid."
        );
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Issue("DE-018")
    @Test(testName = "PUT update Book with valid data persistence check", description = "Validates that the update Books API is persisting Books", priority = 2)
    public void testUpdateNewBookPersistenceCheck() throws IOException {
        Response responseAllBooks = RestAssured.given().get(booksEndpoint);

        assertStatusCode(responseAllBooks, 200);
        assertResponseTime(responseAllBooks, maxResponseTime);

        List<GetBooksResponse> getResponseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long existingBookID = getResponseBooks.getFirst().getId();

        BookRequest updatedBook = new BookRequest(
                existingBookID,
                getResponseBooks.getFirst().getTitle(),
                getResponseBooks.getFirst().getDescription(),
                getResponseBooks.getFirst().getPageCount(),
                getResponseBooks.getFirst().getExcerpt(),
                getResponseBooks.getFirst().getPublishDate()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(updatedBook)
                .put(booksEndpoint + "/" + existingBookID);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertBookCreated(response, updatedBook);

        Response responseBook = RestAssured.given()
                .get(booksEndpoint + "/" + existingBookID);

        assertStatusCode(responseBook, 200);
        assertResponseTime(responseBook, maxResponseTime);

        GetBooksResponse getResponseBook = parseJsonResponseObject(responseBook, GetBooksResponse.class);

        assertItemMatches(updatedBook, getResponseBook);
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Issue("DE-019")
    @Test(testName = "DELETE Book by ID deletion check", description = "Validates that the delete Book API response deletes an Book", priority = 3)
    public void testDeleteBookByID() throws IOException {
        Response responseAllBooks = RestAssured.given().get(booksEndpoint);

        assertStatusCode(responseAllBooks, 200);
        assertResponseTime(responseAllBooks, maxResponseTime);

        List<GetBooksResponse> getResponseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long existingBookID = getResponseBooks.getFirst().getId();

        Response response = RestAssured.given()
                .delete(booksEndpoint + "/" + existingBookID);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        Response responseBook = RestAssured.given()
                .get(booksEndpoint + "/" + existingBookID);

        assertStatusCode(responseBook, 404);
        assertResponseTime(responseBook, maxResponseTime);
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Issue("DE-020")
    @Test(testName = "DELETE Book by invalid ID", description = "Validates that the delete Books API response code is 400 for an invalid ID and response details are as expected", priority = 3)
    public void testDeleteBookWithInvalidID() throws IOException {
        String invalidID = "invalidID";

        Response response = RestAssured.given()
                .delete(booksEndpoint + "/" + invalidID);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid."
        );
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Test(testName = "DELETE Book with non-existing ID", description = "Validates that the Books API response is handling non existing ID values", priority = 3)
    public void testDeleteBookWithNonExistingID() throws IOException {
        Response responseAllBooks = RestAssured.given().get(booksEndpoint);
        List<GetBooksResponse> responseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long nonExistentID = responseBooks.getLast().getId() + 1;

        Response response = RestAssured.given()
                .delete(booksEndpoint + "/" + nonExistentID);

        assertStatusCode(response, 404);
        assertResponseTime(response, maxResponseTime);

        assertBadRequest(response, "Not Found", 404);
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Test(testName = "DELETE Book with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the Book ID parameter correctly.", priority = 3)
    public void testDeleteBooksApiSqlInjectionInId() throws IOException {
        String sqlInjectionId = "' OR 1=1; --";

        Response response = RestAssured.given()
                .delete(booksEndpoint + "/" + sqlInjectionId);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + sqlInjectionId + "' is not valid."
        );
    }

}