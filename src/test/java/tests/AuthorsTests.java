package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import config.ConfigManager;
import data_providers.DataProviders;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.requests.authors.AuthorRequest;
import models.responses.authors.GetAuthorsResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tests.base.BaseTest;

import java.io.IOException;
import java.util.List;

import static utils.common.CommonUtils.*;
import static utils.common.JsonUtils.*;
import static utils.assertions.AuthorAssertionUtils.*;

public class AuthorsTests extends BaseTest {

    @Epic("Authors Management")
    @Feature("Get Authors")
    @Story("US-001")
    @Test(testName = "GET All Authors", description = "Validates that the authors API response contains all expected authors and does not have any duplicates.", priority = 0)
    public void testAuthorsApiGetConsistencyCheck() throws IOException {
        Response response = RestAssured.given()
                .get(authorsEndpoint);

        List<GetAuthorsResponse> responseAuthors = parseJsonResponseList(response, GetAuthorsResponse.class);
        List<GetAuthorsResponse> expectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);
        List<GetAuthorsResponse> missingAuthors = findMissingObjects(expectedAuthors, responseAuthors);
        List<GetAuthorsResponse> duplicateAuthors = findDuplicateObjects(responseAuthors);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertMissingItems(responseAuthors, expectedAuthors, missingAuthors, printPrettyJson(response.body().asString()));
        assertNoDuplicateItems(duplicateAuthors, printPrettyJson(response.body().asString()));
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author by existing ID", description = "Validates that the authors API response contains all expected author details.", priority = 0)
    public void testAuthorsApiGetByID() throws IOException {
        List<GetAuthorsResponse> expectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);

        Response response = RestAssured.given()
                .get(authorsEndpoint + "/" + expectedAuthors.getFirst().getId());

        GetAuthorsResponse responseAuthor = parseJsonResponseObject(response, GetAuthorsResponse.class);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertItemMatches(expectedAuthors.getFirst(), responseAuthor);
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author with non-existing ID", description = "Validates that the authors API response code is 404 and response details are as expected", priority = 0)
    public void testAuthorsApiGetByNonExistentID() throws JsonProcessingException {
        Response response = RestAssured.given()
                .get(authorsEndpoint + "/0000");

        assertStatusCode(response, 404);
        assertResponseTime(response, maxResponseTime);

        assertBadRequest(response, "Not Found", 404);
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author with invalid ID", description = "Validates that the authors API response code is 400 for an invalid ID and response details are as expected", priority = 0)
    public void testAuthorsApiGetByInvalidID() throws JsonProcessingException {
        String invalidID = "invalidID";

        Response response = RestAssured.given()
                .get(authorsEndpoint + "/" + invalidID);

        assertStatusCode(response, 400);
        assertResponseTime(response, maxResponseTime);

        assertBadRequestWithErrors(response,
                "One or more validation errors occurred.",
                400,
                "id",
                "The value '" + invalidID + "' is not valid."
        );
    }

    @Epic("Authors Management")
    @Feature("Get Author")
    @Story("US-002")
    @Test(testName = "GET Author with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the author ID parameter correctly.", priority = 0)
    public void testAuthorsApiSqlInjectionInId() throws JsonProcessingException {
        String sqlInjectionId = "' OR 1=1; --";

        Response response = RestAssured.given()
                .get(authorsEndpoint + "/" + sqlInjectionId);

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

    @Epic("Authors Management")
    @Feature("Create Author")
    @Story("US-003")
    @Test(dataProvider = "createAuthorDataProvider", dataProviderClass = DataProviders.class, description = "Creates a new author and validates the response and status code.", testName = "POST", priority = 1)
    public void testCreateNewAuthor(String testName, int expectedStatusCode, Long id, Long idBook, String firstName, String lastName) throws JsonProcessingException {
        AuthorRequest newAuthor = new AuthorRequest(id, idBook, firstName, lastName);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newAuthor)
                .post(authorsEndpoint);

        assertResponseTime(response, maxResponseTime);

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200);
            assertAuthorCreated(response, newAuthor);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400);
            assertBadRequest(response, "One or more validation errors occurred.", 400);
            assertAuthorErrors(response, id, idBook);
        }
    }

    @Epic("Authors Management")
    @Feature("Create Author")
    @Story("US-003")
    @Issue("DE-001")
    @Test(testName = "POST Create Author with valid data persistence check", description = "Validates that the authors API is persisting authors", priority = 1)
    public void testCreateNewAuthorPersistenceCheck() throws IOException {
        Response responseAllAuthors = RestAssured.given()
                .get(authorsEndpoint);

        assertStatusCode(responseAllAuthors, 200);
        assertResponseTime(responseAllAuthors, maxResponseTime);

        List<GetAuthorsResponse> getResponseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        List<GetAuthorsResponse> getExpectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);
        long newAuthorId = getResponseAuthors.getLast().getId() + 1;

        AuthorRequest newAuthor = new AuthorRequest(
                newAuthorId,
                getExpectedAuthors.getFirst().getIdBook(),
                getExpectedAuthors.getFirst().getFirstName(),
                getExpectedAuthors.getFirst().getLastName()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(newAuthor)
                .post(authorsEndpoint);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertAuthorCreated(response, newAuthor);

        Response responseAuthor = RestAssured.given()
                .get(authorsEndpoint + "/" + newAuthorId);

        assertStatusCode(responseAuthor, 200);
        assertResponseTime(responseAuthor, maxResponseTime);

        GetAuthorsResponse getResponseAuthorsLatest = parseJsonResponseObject(responseAuthor, GetAuthorsResponse.class);

        assertItemMatches(newAuthor, getResponseAuthorsLatest);
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Test(dataProvider = "updateAuthorDataProvider", dataProviderClass = DataProviders.class, description = "Updates author and validates the response and status code.", testName = "PUT", priority = 2)
    public void testUpdateAuthor(String testName, int expectedStatusCode, Long id, Long idBook, String firstName, String lastName) throws JsonProcessingException {
        AuthorRequest authorRequest = new AuthorRequest(id, idBook, firstName, lastName);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(authorsEndpoint + "/" + id);

        assertResponseTime(response, maxResponseTime);

        if (expectedStatusCode == 200) {
            assertStatusCode(response, 200);
            assertAuthorCreated(response, authorRequest);
        } else if (expectedStatusCode == 400) {
            assertStatusCode(response, 400);
            assertBadRequest(response, "One or more validation errors occurred.", 400);
            assertAuthorErrors(response, id, idBook);
        }
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Issue("DE-002")
    @Test(testName = "PUT Authors with non existing ID", description = "Validates that the authors API response is handling non existing ID values", priority = 2)
    public void testUpdateAuthorWithNonExistingID() throws IOException {
        List<GetAuthorsResponse> expectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);

        AuthorRequest authorRequest = new AuthorRequest(
                expectedAuthors.getFirst().getId(),
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);
        List<GetAuthorsResponse> responseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(authorsEndpoint + "/" + (responseAuthors.getLast().getId() + 1));

        assertStatusCode(response, 404);
        assertResponseTime(response, maxResponseTime);

        assertBadRequest(response, "Not Found", 404);
    }

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Test(testName = "PUT Authors with invalid ID", description = "Validates that the authors API response is handling non existing ID values", priority = 2)
    public void testUpdateAuthorWithInvalidID() throws IOException {
        String invalidID = "invalidID";
        List<GetAuthorsResponse> expectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);

        AuthorRequest authorRequest = new AuthorRequest(
                expectedAuthors.getFirst().getId(),
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(authorsEndpoint + "/" + invalidID);

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

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Test(testName = "PUT Authors with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the author ID parameter correctly.", priority = 2)
    public void testUpdateAuthorsApiSqlInjectionInId() throws IOException {
        String sqlInjectionId = "' OR 1=1; --";
        List<GetAuthorsResponse> expectedAuthors = loadDataFromJsonFile(env, "authors/authors", GetAuthorsResponse.class);

        AuthorRequest authorRequest = new AuthorRequest(
                expectedAuthors.getFirst().getId(),
                expectedAuthors.getFirst().getIdBook(),
                expectedAuthors.getFirst().getFirstName(),
                expectedAuthors.getFirst().getLastName()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(authorRequest)
                .put(authorsEndpoint + "/" + sqlInjectionId);

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

    @Epic("Authors Management")
    @Feature("Update Author")
    @Story("US-004")
    @Issue("DE-003")
    @Test(testName = "PUT update Author with valid data persistence check", description = "Validates that the update authors API is persisting authors", priority = 2)
    public void testUpdateNewAuthorPersistenceCheck() throws IOException {
        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        assertStatusCode(responseAllAuthors, 200);
        assertResponseTime(responseAllAuthors, maxResponseTime);

        List<GetAuthorsResponse> getResponseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long existingAuthorID = getResponseAuthors.getFirst().getId();

        AuthorRequest updatedAuthor = new AuthorRequest(
                existingAuthorID,
                getResponseAuthors.getFirst().getIdBook(),
                "UpdatedName",
                getResponseAuthors.getFirst().getLastName()
        );

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(updatedAuthor)
                .put(authorsEndpoint + "/" + existingAuthorID);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        assertAuthorCreated(response, updatedAuthor);

        Response responseAuthor = RestAssured.given()
                .get(authorsEndpoint + "/" + existingAuthorID);

        assertStatusCode(responseAuthor, 200);
        assertResponseTime(responseAuthor, maxResponseTime);

        GetAuthorsResponse getResponseAuthor = parseJsonResponseObject(responseAuthor, GetAuthorsResponse.class);

        assertItemMatches(updatedAuthor, getResponseAuthor);
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Issue("DE-004")
    @Test(testName = "DELETE Author by ID deletion check", description = "Validates that the delete author API response deletes an author", priority = 3)
    public void testDeleteAuthorByID() throws IOException {
        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);

        assertStatusCode(responseAllAuthors, 200);
        assertResponseTime(responseAllAuthors, maxResponseTime);

        List<GetAuthorsResponse> getResponseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long existingAuthorID = getResponseAuthors.getFirst().getId();

        Response response = RestAssured.given()
                .delete(authorsEndpoint + "/" + existingAuthorID);

        assertStatusCode(response, 200);
        assertResponseTime(response, maxResponseTime);

        Response responseAuthor = RestAssured.given()
                .get(authorsEndpoint + "/" + existingAuthorID);

        assertStatusCode(responseAuthor, 404);
        assertResponseTime(responseAuthor, maxResponseTime);
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Test(testName = "DELETE Author by invalid ID", description = "Validates that the delete authors API response code is 400 for an invalid ID and response details are as expected", priority = 3)
    public void testDeleteAuthorWithInvalidID() throws IOException {
        String invalidID = "invalidID";

        Response response = RestAssured.given()
                .delete(authorsEndpoint + "/" + invalidID);

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

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Issue("DE-005")
    @Test(testName = "DELETE Author with non-existing ID", description = "Validates that the authors API response is handling non existing ID values", priority = 3)
    public void testDeleteAuthorWithNonExistingID() throws IOException {
        Response responseAllAuthors = RestAssured.given().get(authorsEndpoint);
        List<GetAuthorsResponse> responseAuthors = parseJsonResponseList(responseAllAuthors, GetAuthorsResponse.class);
        long nonExistentID = responseAuthors.getLast().getId() + 1;

        Response response = RestAssured.given()
                .delete(authorsEndpoint + "/" + nonExistentID);

        assertStatusCode(response, 404);
        assertResponseTime(response, maxResponseTime);

        assertBadRequest(response, "Not Found", 404);
    }

    @Epic("Authors Management")
    @Feature("Delete Author")
    @Story("US-005")
    @Test(testName = "DELETE Author with SQL Injection in ID", description = "Validates that the API handles SQL injection attempts in the author ID parameter correctly.", priority = 3)
    public void testDeleteAuthorsApiSqlInjectionInId() throws IOException {
        String sqlInjectionId = "' OR 1=1; --";

        Response response = RestAssured.given()
                .delete(authorsEndpoint + "/" + sqlInjectionId);

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