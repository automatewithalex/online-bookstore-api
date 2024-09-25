package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import data_providers.DataProviders;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.requests.authors.PostAuthorRequest;
import models.requests.authors.PutAuthorRequest;
import models.responses.authors.GetAuthorsResponse;
import org.testng.annotations.*;
import tests.base.BaseTest;

import java.io.IOException;
import java.util.List;

import static utils.common.CommonUtils.*;
import static utils.common.JsonUtils.*;
import static utils.assertions.AuthorAssertionUtils.*;
import static utils.common.LogUtils.*;

public class AuthorsTests extends BaseTest {

    List<GetAuthorsResponse> expectedAuthors;

    @BeforeClass
    @Parameters({"apiVersion", "env"})
    public void setupAuthorsTestConfig(String apiVersion, String env) throws IOException {
        logInfo(logger, "Initializing Authors test setup");
        expectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);
        logInfo(logger, "Authors test setup completed");
    }

    @Epic("Authors Management")
    @Feature("Get Authors")
    @Story("US-001")
    @Test(testName = "GET All Authors", description = "Validates that the authors API response contains all expected authors and does not have any duplicates.")
    public void testAuthorsApiGetConsistencyCheck() throws IOException {
        logTestStart(logger, "GET All Authors");

        Response response = RestAssured.given().get(authorsEndpoint);

        logResponseInfo(logger, "GET" + authorsEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + authorsEndpoint, printPrettyJson(response.body().asString()));

        List<GetAuthorsResponse> responseAuthors = parseJsonResponseList(response, GetAuthorsResponse.class);
        List<GetAuthorsResponse> missingAuthors = findMissingObjects(expectedAuthors, responseAuthors);
        List<GetAuthorsResponse> duplicateAuthors = findDuplicateObjects(responseAuthors);

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertMissingItems(responseAuthors, expectedAuthors, missingAuthors, printPrettyJson(response.body().asString()), logger);
        assertNoDuplicateItems(duplicateAuthors, printPrettyJson(response.body().asString()), logger);

        logTestEnd(logger, "GET All Authors");
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author by existing ID", description = "Validates that the authors API response contains all expected author details.")
    public void testAuthorsApiGetByID() throws IOException {
        logTestStart(logger, "GET Author by existing ID");

        String path = authorsEndpoint + "/" + expectedAuthors.getFirst().getId();
        Response response = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(response.body().asString()));

        GetAuthorsResponse responseAuthor = parseJsonResponseObject(response, GetAuthorsResponse.class);

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertItemMatches(expectedAuthors.getFirst(), responseAuthor, logger);

        logTestEnd(logger, "GET Author by existing ID");
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author with non-existing ID", description = "Validates that the authors API response code is 404 and response details are as expected")
    public void testAuthorsApiGetByNonExistentID() throws JsonProcessingException {
        logTestStart(logger, "GET Author with non-existing ID");

        String path = authorsEndpoint + "/0000";
        Response response = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 404, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequest(response, "Not Found", 404, logger);

        logTestEnd(logger, "GET Author with non-existing ID");
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author with invalid ID", description = "Validates that the authors API response code is 400 for an invalid ID and response details are as expected")
    public void testAuthorsApiGetByInvalidID() throws JsonProcessingException {
        logTestStart(logger, "GET Author with invalid ID");

        String invalidID = "invalidID";
        String path = authorsEndpoint + "/" + invalidID;
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

        logTestEnd(logger, "GET Author with invalid ID");
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the author ID parameter correctly.")
    public void testAuthorsApiSqlInjectionInId() throws JsonProcessingException {
        logTestStart(logger, "GET Author with SQL Injection in ID");

        String sqlInjectionId = "' OR 1=1; --";
        String path = authorsEndpoint + "/" + sqlInjectionId;
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

        logTestEnd(logger, "GET Author with SQL Injection in ID");
    }

    @Epic("Authors Management")
    @Feature("Create Author")
    @Story("US-003")
    @Test(dataProvider = "createAuthorDataProvider", dataProviderClass = DataProviders.class, description = "Creates a new author and validates the response and status code.", testName = "POST", priority = 1)
    public void testCreateNewAuthor(String testName, int expectedStatusCode, Long id, Long idBook, String firstName, String lastName) throws JsonProcessingException {
        logTestStart(logger, "POST " + testName);

        PostAuthorRequest newAuthor = new PostAuthorRequest(id, idBook, firstName, lastName);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newAuthor)
                .post(authorsEndpoint);

        logResponseInfo(logger, "POST" + authorsEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "POST" + authorsEndpoint, printPrettyJson(response.body().asString()));

        assertResponseTime(response, maxResponseTime, logger);

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200, logger);
            assertAuthorCreated(response, newAuthor, logger);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400, logger);
            assertBadRequest(response, "One or more validation errors occurred.", 400, logger);
            assertAuthorErrors(response, id, idBook, logger);
        }

        logTestEnd(logger, "POST " + testName);
    }

    @Epic("Authors Management")
    @Feature("Create Author")
    @Story("US-003")
    @Issue("DE-001")
    @Test(testName = "POST Create Author with valid data persistence check", description = "Validates that the authors API is persisting authors", priority = 1)
    public void testCreateNewAuthorPersistenceCheck() throws IOException {
        logTestStart(logger, "POST Create Author with valid data persistence check");

        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        logResponseInfo(logger, "GET" + authorsEndpoint, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + authorsEndpoint, printPrettyJson(responseAllAuthors.body().asString()));

        assertStatusCode(responseAllAuthors, 200, logger);
        assertResponseTime(responseAllAuthors, maxResponseTime, logger);

        List<GetAuthorsResponse> getResponseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long newAuthorId = getResponseAuthors.getLast().getId() + 1;

        PostAuthorRequest newAuthor = new PostAuthorRequest(
                newAuthorId,
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newAuthor)
                .post(authorsEndpoint);

        logResponseInfo(logger, "POST" + authorsEndpoint, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "POST" + authorsEndpoint, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertAuthorCreated(response, newAuthor, logger);

        String path = authorsEndpoint + "/" + newAuthorId;
        Response responseAuthor = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, responseAuthor.getStatusCode(), responseAuthor.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(responseAuthor.body().asString()));

        assertStatusCode(responseAuthor, 200, logger);
        assertResponseTime(responseAuthor, maxResponseTime, logger);

        GetAuthorsResponse getResponseAuthorsLatest = parseJsonResponseObject(responseAuthor, GetAuthorsResponse.class);
        assertItemMatches(newAuthor, getResponseAuthorsLatest, logger);

        logTestEnd(logger, "POST Create Author with valid data persistence check");
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Test(dataProvider = "updateAuthorDataProvider", dataProviderClass = DataProviders.class, description = "Updates author and validates the response and status code.", testName = "PUT", priority = 2)
    public void testUpdateAuthor(String testName, int expectedStatusCode, Long id, Long idBook, String firstName, String lastName) throws JsonProcessingException {
        logTestStart(logger, "PUT " + testName);

        PutAuthorRequest authorRequest = new PutAuthorRequest(id, idBook, firstName, lastName);

        String path = authorsEndpoint + "/" + id;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(path);

        logResponseInfo(logger, "PUT" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + path, printPrettyJson(response.body().asString()));

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200, logger);
            assertAuthorUpdated(response, authorRequest, logger);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400, logger);
            assertBadRequest(response, "One or more validation errors occurred.", 400, logger);
            assertAuthorErrors(response, id, idBook, logger);
        }

        assertResponseTime(response, maxResponseTime, logger);

        logTestEnd(logger, "PUT " + testName);
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Issue("DE-002")
    @Test(testName = "PUT Authors with non existing ID", description = "Validates that the authors API response is handling non existing ID values", priority = 2)
    public void testUpdateAuthorWithNonExistingID() throws IOException {
        logTestStart(logger, "PUT Authors with non existing ID");

        PutAuthorRequest authorRequest = new PutAuthorRequest(
                expectedAuthors.getFirst().getId(),
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        logResponseInfo(logger, "GET" + authorsEndpoint, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + authorsEndpoint, printPrettyJson(responseAllAuthors.body().asString()));

        List<GetAuthorsResponse> responseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);

        String path = authorsEndpoint + "/" + (responseAuthors.getLast().getId() + 1);
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(path);

        logResponseInfo(logger, "PUT" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 404, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequest(response, "Not Found", 404, logger);

        logTestEnd(logger, "PUT Authors with non existing ID");
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Test(testName = "PUT Authors with invalid ID", description = "Validates that the authors API response is handling non existing ID values", priority = 2)
    public void testUpdateAuthorWithInvalidID() throws IOException {
        logTestStart(logger, "PUT Authors with invalid ID");

        String invalidID = "invalidID";

        PutAuthorRequest authorRequest = new PutAuthorRequest(
                expectedAuthors.getFirst().getId(),
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        String path = authorsEndpoint + "/" + invalidID;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(path);

        logResponseInfo(logger, "PUT" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + path, printPrettyJson(response.body().asString()));

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

        logTestEnd(logger, "PUT Authors with invalid ID");
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Test(testName = "PUT Authors with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the author ID parameter correctly.", priority = 2)
    public void testUpdateAuthorsApiSqlInjectionInId() throws IOException {
        logTestStart(logger, "PUT Authors with SQL Injection in ID");

        String sqlInjectionId = "' OR 1=1; --";

        PutAuthorRequest authorRequest = new PutAuthorRequest(
                expectedAuthors.getFirst().getId(),
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        String path = authorsEndpoint + "/" + sqlInjectionId;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(path);

        logResponseInfo(logger, "PUT" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + path, printPrettyJson(response.body().asString()));

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

        logTestEnd(logger, "PUT Authors with SQL Injection in ID");
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Issue("DE-003")
    @Test(testName = "PUT update Author with valid data persistence check", description = "Validates that the update authors API is persisting authors", priority = 2)
    public void testUpdateNewAuthorPersistenceCheck() throws IOException {
        logTestStart(logger, "PUT update Author with valid data persistence check");

        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        logResponseInfo(logger, "GET" + authorsEndpoint, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + authorsEndpoint, printPrettyJson(responseAllAuthors.body().asString()));

        assertStatusCode(responseAllAuthors, 200, logger);
        assertResponseTime(responseAllAuthors, maxResponseTime, logger);

        List<GetAuthorsResponse> getResponseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long existingAuthorID = getResponseAuthors.getFirst().getId();

        PutAuthorRequest updatedAuthor = new PutAuthorRequest(
                existingAuthorID,
                getResponseAuthors.getFirst().getIdBook(),
                "UpdatedName",
                getResponseAuthors.getFirst().getLastName()
        );

        String path = authorsEndpoint + "/" + existingAuthorID;
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(updatedAuthor)
                .put(path);

        logResponseInfo(logger, "PUT" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "PUT" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertAuthorUpdated(response, updatedAuthor, logger);

        Response responseAuthor = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(responseAllAuthors.body().asString()));


        assertStatusCode(responseAuthor, 200, logger);
        assertResponseTime(responseAuthor, maxResponseTime, logger);

        GetAuthorsResponse getResponseAuthor = parseJsonResponseObject(responseAuthor, GetAuthorsResponse.class);
        assertItemMatches(updatedAuthor, getResponseAuthor, logger);

        logTestEnd(logger, "PUT update Author with valid data persistence check");
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Issue("DE-004")
    @Test(testName = "DELETE Author by ID deletion check", description = "Validates that the delete author API response deletes an author", priority = 3)
    public void testDeleteAuthorByID() throws IOException {
        logTestStart(logger, "DELETE Author by ID deletion check");

        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        logResponseInfo(logger, "GET" + authorsEndpoint, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + authorsEndpoint, printPrettyJson(responseAllAuthors.body().asString()));

        assertStatusCode(responseAllAuthors, 200, logger);
        assertResponseTime(responseAllAuthors, maxResponseTime, logger);

        List<GetAuthorsResponse> getResponseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long existingAuthorID = getResponseAuthors.getFirst().getId();

        String path = authorsEndpoint + "/" + existingAuthorID;
        Response response = RestAssured.given().delete(path);

        logResponseInfo(logger, "DELETE" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "DELETE" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 200, logger);
        assertResponseTime(response, maxResponseTime, logger);

        Response responseAuthor = RestAssured.given().get(path);

        logResponseInfo(logger, "GET" + path, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + path, printPrettyJson(responseAllAuthors.body().asString()));

        assertStatusCode(responseAuthor, 404, logger);
        assertResponseTime(responseAuthor, maxResponseTime, logger);

        logTestEnd(logger, "DELETE Author by ID deletion check");
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Test(testName = "DELETE Author by invalid ID", description = "Validates that the delete authors API response code is 400 for an invalid ID and response details are as expected", priority = 3)
    public void testDeleteAuthorWithInvalidID() throws IOException {
        logTestStart(logger, "DELETE Author by invalid ID");

        String invalidID = "invalidID";
        String path = authorsEndpoint + "/" + invalidID;
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

        logTestEnd(logger, "DELETE Author by invalid ID");
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Issue("DE-005")
    @Test(testName = "DELETE Author with non-existing ID", description = "Validates that the authors API response is handling non existing ID values", priority = 3)
    public void testDeleteAuthorWithNonExistingID() throws IOException {
        logTestStart(logger, "DELETE Author with non-existing ID");

        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        logResponseInfo(logger, "GET" + authorsEndpoint, responseAllAuthors.getStatusCode(), responseAllAuthors.getTime());
        logResponseDebug(logger, "GET" + authorsEndpoint, printPrettyJson(responseAllAuthors.body().asString()));

        assertStatusCode(responseAllAuthors, 200, logger);
        assertResponseTime(responseAllAuthors, maxResponseTime, logger);

        List<GetAuthorsResponse> responseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long nonExistentID = responseAuthors.getLast().getId() + 1;

        String path = authorsEndpoint + "/" + nonExistentID;
        Response response = RestAssured.given().delete(path);

        logResponseInfo(logger, "DELETE" + path, response.getStatusCode(), response.getTime());
        logResponseDebug(logger, "DELETE" + path, printPrettyJson(response.body().asString()));

        assertStatusCode(response, 404, logger);
        assertResponseTime(response, maxResponseTime, logger);
        assertBadRequest(response, "Not Found", 404, logger);

        logTestEnd(logger, "DELETE Author with non-existing ID");
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Test(testName = "DELETE Author with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the author ID parameter correctly.", priority = 3)
    public void testDeleteAuthorsApiSqlInjectionInId() throws IOException {
        logTestStart(logger, "DELETE Author with SQL Injection in ID");

        String sqlInjectionId = "' OR 1=1; --";
        String path = authorsEndpoint + "/" + sqlInjectionId;
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

        logTestEnd(logger, "DELETE Author with SQL Injection in ID");
    }

}