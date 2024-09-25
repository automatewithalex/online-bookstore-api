package models.responses.books;

import models.responses.common.BadRequestResponse;

/**
 * Represents a response object for when a requested author is not found (HTTP 404).
 * This object contains information about the error type, title, status code, and trace ID.
 */
public class GetBooksNotFoundResponse extends BadRequestResponse {

    /**
     * Returns a string representation of the GetAuthorsNotFoundResponse object.
     * Provides a formatted string with the type, title, status, and traceId.
     *
     * @return a string representing the GetAuthorsNotFoundResponse object.
     */
    @Override
    public String toString() {
        return String.format("BookNotFoundResponse{type='%s', title='%s', status=%d, traceId='%s'}",
                type, title, status, traceId);
    }

}