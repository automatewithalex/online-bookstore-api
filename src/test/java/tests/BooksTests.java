package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import data_providers.DataProviders;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.requests.books.PostBookRequest;
import models.requests.books.PutBookRequest;
import models.responses.books.GetBooksResponse;
import org.testng.annotations.*;
import tests.base.BaseTest;

import java.io.IOException;
import java.util.List;

import static utils.common.CommonUtils.*;
import static utils.common.JsonUtils.*;
import static utils.assertions.AssertionsUtils.*;
import static utils.assertions.BookAssertionUtils.*;
import static utils.common.LogUtils.*;

public class BooksTests extends BaseTest {

    List<GetBooksResponse> expectedBooks;

    @BeforeClass
    @Parameters({"apiVersion", "env"})
    public void setupBooksTestConfig(String apiVersion, String env) throws IOException {
        logInfo(logger, "Initializing Books test setup");
        expectedBooks = loadDataFromJsonFile(env, "books/books", GetBooksResponse.class);
        logInfo(logger, "Books test setup completed");
    }

    @Epic("Books Management")
    @Feature("Get Books")
    @Story("US-006")
    @Issue("DE-006")
    @Test(testName = "GET All Books", description = "Validates that the Books API response contains all expected books and does not have any duplicates.")
    public void testBooksApiGetAll() throws IOException {
        logTestStart(logger, "GET All Books");

        Response response = RestAssured.given().get(booksEndpoint);

        logResponseInfo(logger, "GET" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);

        List<GetBooksResponse> responseBooks = parseJsonResponseList(response, GetBooksResponse.class);
        List<GetBooksResponse> missingBooks = findMissingObjects(expectedBooks, responseBooks);
        assertMissingItems(responseBooks, expectedBooks, missingBooks, printPrettyJson(response.body().asString()), logger);

        List<GetBooksResponse> duplicateBooks = findDuplicateObjects(responseBooks);
        assertNoDuplicateItems(duplicateBooks, printPrettyJson(response.body().asString()), logger);

        logTestEnd(logger, "GET All Books");
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Issue("DE-007")
    @Test(testName = "GET Book by existing ID", description = "Validates that the books API response contains all expected book details.")
    public void testBooksApiGetByID() throws IOException {
        logTestStart(logger, "GET Book by existing ID");

        String path = booksEndpoint + "/" + expectedBooks.getFirst().getId();
        Response response = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);

        GetBooksResponse responseBook = parseJsonResponseObject(response, GetBooksResponse.class);
        assertItemMatches(expectedBooks.getFirst(), responseBook, logger);

        logTestEnd(logger, "GET Book by existing ID");
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Test(testName = "GET Book with non-existing ID", description = "Validates that the books API response code is 404 and response details are as expected")
    public void testBooksApiGetByNonExistentID() throws JsonProcessingException {
        logTestStart(logger, "GET Book with non-existing ID");

        String path = booksEndpoint + "/0000";
        Response response = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 404, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequest(response, "Not Found", 404, logger);

        logTestEnd(logger, "GET Book with non-existing ID");
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Test(testName = "GET Book with invalid ID", description = "Validates that the books API response code is 400 for an invalid ID and response details are as expected")
    public void testBooksApiGetByInvalidID() throws JsonProcessingException {
        logTestStart(logger, "GET Book with invalid ID");

        String invalidID = "invalidID";
        String path = booksEndpoint + "/" + invalidID;
        Response response = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 400, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequestWithErrors(response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid.",
                logger
        );

        logTestEnd(logger, "GET Book with invalid ID");
    }

    @Epic("Books Management")
    @Feature("Get Book")
    @Story("US-007")
    @Test(testName = "GET Book with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the Book ID parameter correctly.")
    public void testBooksApiSqlInjectionInId() throws JsonProcessingException {
        logTestStart(logger, "GET Book with SQL Injection in ID");

        String sqlInjectionId = "' OR 1=1; --";
        String path = booksEndpoint + "/" + sqlInjectionId;
        Response response = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 400, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + sqlInjectionId + "' is not valid.",
                logger
        );

        logTestEnd(logger, "GET Book with SQL Injection in ID");
    }

    @Epic("Books Management")
    @Feature("Create Book")
    @Story("US-008")
    @Issues({
            @Issue("DE-008"),
            @Issue("DE-009"),
            @Issue("DE-010"),
            @Issue("DE-011")
    })
    @Test(dataProvider = "createBookDataProvider", dataProviderClass = DataProviders.class, description = "Creates a new Book and validates the response and status code.", testName = "POST", priority = 1)
    public void testCreateNewBook(String testName, int expectedStatusCode, Long id, String title, String description, int pageCount, String excerpt, String publishDate) throws JsonProcessingException {
        logTestStart(logger, "POST " + testName);

        PostBookRequest newBook = new PostBookRequest(id, title, description, pageCount, excerpt, publishDate);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .post(booksEndpoint);

        logResponseInfo(logger, "POST" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "POST" + booksEndpoint, printPrettyJson(response.body().asString()));

        assertResponseTime(response, maxResponseTime, logger);

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200, logger);
            assertBookCreated(response, newBook, logger);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400, logger);
            assertBadRequest(response, "One or more validation errors occurred.", 400, logger);
            assertBookErrors(response, id, title, pageCount, logger);
        }

        logTestEnd(logger, "POST " + testName);
    }

    @Epic("Books Management")
    @Feature("Create Book")
    @Story("US-008")
    @Test(testName = "POST Create Book with valid data persistence check", description = "Validates that the Books API is persisting Books", priority = 1)
    public void testCreateNewBookPersistenceCheck() throws IOException {
        logTestStart(logger, "POST Create Book with valid data persistence check");

        Response responseAllBooks = RestAssured.given().get(booksEndpoint);

        logResponseInfo(logger, "GET" + booksEndpoint, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseAllBooks, 200, logger);
        assertResponseTime(responseAllBooks, maxResponseTime, logger);

        List<GetBooksResponse> getResponseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long newBookId = getResponseBooks.getLast().getId() + 1;

        PostBookRequest newBook = new PostBookRequest(
                newBookId,
                expectedBooks.getFirst().getTitle(),
                expectedBooks.getFirst().getDescription(),
                expectedBooks.getFirst().getPageCount(),
                expectedBooks.getFirst().getExcerpt(),
                expectedBooks.getFirst().getPublishDate()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .post(booksEndpoint);

        logResponseInfo(logger, "POST" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "POST" + booksEndpoint, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBookCreated(response, newBook, logger);

        String path = booksEndpoint + "/" + newBookId;
        Response responseBook = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + booksEndpoint, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseBook, 200, logger);
        assertResponseTime(responseBook, maxResponseTime, logger);

        GetBooksResponse getResponseBooksLatest = parseJsonResponseObject(responseBook, GetBooksResponse.class);
        assertItemMatches(newBook, getResponseBooksLatest, logger);

        logTestEnd(logger, "POST Create Book with valid data persistence check");
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
        logTestStart(logger, "PUT " + testName);

        PutBookRequest bookRequest = new PutBookRequest(id, title, description, pageCount, excerpt, publishDate);

        String path = booksEndpoint + "/" + id;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(bookRequest)
                .put(path);

        logResponseInfo(logger, "PUT" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + booksEndpoint, printPrettyJson(response.body().asString()));

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200, logger);
            assertBookUpdated(response, bookRequest, logger);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400, logger);
            assertBadRequest(response, "One or more validation errors occurred.", 400, logger);
            assertBookErrors(response, id, title, pageCount, logger);
        }

        assertResponseTime(response, maxResponseTime, logger);

        logTestEnd(logger, "PUT " + testName);
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Issue("DE-017")
    @Test(testName = "PUT Books with non existing ID", description = "Validates that the Books API response is handling non existing ID values", priority = 2)
    public void testUpdateBookWithNonExistingID() throws IOException {
        logTestStart(logger, "PUT Books with non existing ID");

        PutBookRequest newBook = new PutBookRequest(
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

        logResponseInfo(logger, "PUT" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + booksEndpoint, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 404, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequest(response, "Not Found", 404, logger);

        logTestEnd(logger, "PUT Books with non existing ID");
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Test(testName = "PUT Books with invalid ID", description = "Validates that the Books API response is handling non existing ID values", priority = 2)
    public void testUpdateBookWithInvalidID() throws IOException {
        logTestStart(logger, "PUT Books with invalid ID");

        PutBookRequest newBook = new PutBookRequest(
                expectedBooks.getFirst().getId(),
                expectedBooks.getFirst().getTitle(),
                expectedBooks.getFirst().getDescription(),
                expectedBooks.getFirst().getPageCount(),
                expectedBooks.getFirst().getExcerpt(),
                expectedBooks.getFirst().getPublishDate()
        );

        String invalidID = "invalidID";
        String path = booksEndpoint + "/" + invalidID;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .put(path);

        logResponseInfo(logger, "PUT" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + booksEndpoint, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 400, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid.",
                logger
        );

        logTestEnd(logger, "PUT Books with invalid ID");
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Test(testName = "PUT Books with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the Book ID parameter correctly.", priority = 2)
    public void testUpdateBooksApiSqlInjectionInId() throws IOException {
        logTestStart(logger, "PUT Books with SQL Injection in ID");

        PutBookRequest newBook = new PutBookRequest(
                expectedBooks.getFirst().getId(),
                expectedBooks.getFirst().getTitle(),
                expectedBooks.getFirst().getDescription(),
                expectedBooks.getFirst().getPageCount(),
                expectedBooks.getFirst().getExcerpt(),
                expectedBooks.getFirst().getPublishDate()
        );

        String sqlInjectionId = "' OR 1=1; --";
        String path = booksEndpoint + "/" + sqlInjectionId;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newBook)
                .put(path);

        logResponseInfo(logger, "PUT" + booksEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + booksEndpoint, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 400, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + sqlInjectionId + "' is not valid.",
                logger
        );

        logTestEnd(logger, "PUT Books with SQL Injection in ID");
    }

    @Epic("Books Management")
    @Feature("Update Book")
    @Story("US-009")
    @Issue("DE-018")
    @Test(testName = "PUT update Book with valid data persistence check", description = "Validates that the update Books API is persisting Books", priority = 2)
    public void testUpdateNewBookPersistenceCheck() throws IOException {
        logTestStart(logger, "PUT update Book with valid data persistence check");

        Response responseAllBooks = RestAssured.given().get(booksEndpoint);

        logResponseInfo(logger, "GET" + booksEndpoint, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseAllBooks, 200, logger);
        assertResponseTime(responseAllBooks, maxResponseTime, logger);

        List<GetBooksResponse> getResponseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long existingBookID = getResponseBooks.getFirst().getId();

        PutBookRequest updatedBook = new PutBookRequest(
                existingBookID,
                getResponseBooks.getFirst().getTitle(),
                getResponseBooks.getFirst().getDescription(),
                getResponseBooks.getFirst().getPageCount(),
                getResponseBooks.getFirst().getExcerpt(),
                getResponseBooks.getFirst().getPublishDate()
        );

        String path = booksEndpoint + "/" + existingBookID;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(updatedBook)
                .put(path);

        logResponseInfo(logger, "PUT" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBookUpdated(response, updatedBook, logger);

        Response responseBook = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + booksEndpoint, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseBook, 200, logger);
        assertResponseTime(responseBook, maxResponseTime, logger);

        GetBooksResponse getResponseBook = parseJsonResponseObject(responseBook, GetBooksResponse.class);
        assertItemMatches(updatedBook, getResponseBook, logger);

        logTestEnd(logger, "PUT update Book with valid data persistence check");
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Issue("DE-019")
    @Test(testName = "DELETE Book by ID deletion check", description = "Validates that the delete Book API response deletes an Book", priority = 3)
    public void testDeleteBookByID() throws IOException {
        logTestStart(logger, "DELETE Book by ID deletion check");

        Response responseAllBooks = RestAssured.given().get(booksEndpoint);

        logResponseInfo(logger, "GET" + booksEndpoint, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseAllBooks, 200, logger);
        assertResponseTime(responseAllBooks, maxResponseTime, logger);

        List<GetBooksResponse> getResponseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long existingBookID = getResponseBooks.getFirst().getId();

        String path = booksEndpoint + "/" + existingBookID;
        Response response = RestAssured.given().delete(path);

        logResponseInfo(logger, "DELETE" + path, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "DELETE" + path, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);

        Response responseBook = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseBook, 404, logger);
        assertResponseTime(responseBook, maxResponseTime, logger);

        logTestStart(logger, "DELETE Book by ID deletion check");
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Issue("DE-020")
    @Test(testName = "DELETE Book by invalid ID", description = "Validates that the delete Books API response code is 400 for an invalid ID and response details are as expected", priority = 3)
    public void testDeleteBookWithInvalidID() throws IOException {
        logTestStart(logger, "DELETE Book by ID deletion check");

        String invalidID = "invalidID";
        String path = booksEndpoint + "/" + invalidID;
        Response response = RestAssured.given().delete(path);

        logResponseInfo(logger, "DELETE" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "DELETE" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 400, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid.",
                logger
        );

        logTestEnd(logger, "DELETE Book by ID deletion check");
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Test(testName = "DELETE Book with non-existing ID", description = "Validates that the Books API response is handling non existing ID values", priority = 3)
    public void testDeleteBookWithNonExistingID() throws IOException {
        logTestStart(logger, "DELETE Book by ID deletion check");

        Response responseAllBooks = RestAssured.given().get(booksEndpoint);

        logResponseInfo(logger, "GET" + booksEndpoint, responseAllBooks.getStatusCode(), responseAllBooks.getTime());
        logResponseDebug(logger, "GET" + booksEndpoint, printPrettyJson(responseAllBooks.body().asString()));

        assertStatusCode(responseAllBooks, 200, logger);
        assertResponseTime(responseAllBooks, maxResponseTime, logger);

        List<GetBooksResponse> responseBooks = parseJsonResponseList(responseAllBooks, GetBooksResponse.class);
        long nonExistentID = responseBooks.getLast().getId() + 1;

        String path = booksEndpoint + "/" + nonExistentID;
        Response response = RestAssured.given().delete(path);

        logResponseInfo(logger, "DELETE" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "DELETE" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 404, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequest(response, "Not Found", 404, logger);

        logTestEnd(logger, "DELETE Book by ID deletion check");
    }

    @Epic("Books Management")
    @Feature("Delete Book")
    @Story("US-010")
    @Test(testName = "DELETE Book with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the Book ID parameter correctly.", priority = 3)
    public void testDeleteBooksApiSqlInjectionInId() throws IOException {
        logTestStart(logger, "DELETE Book by ID deletion check");

        String sqlInjectionId = "' OR 1=1; --";
        String path = booksEndpoint + "/" + sqlInjectionId;
        Response response = RestAssured.given().delete(path);

        logResponseInfo(logger, "DELETE" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "DELETE" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 400, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequestWithErrors(
                response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + sqlInjectionId + "' is not valid.",
                logger
        );

        logTestEnd(logger, "DELETE Book by ID deletion check");
    }

}