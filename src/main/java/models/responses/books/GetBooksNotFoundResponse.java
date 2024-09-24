package models.responses.books;

/**
 * Represents a response object for when a requested author is not found (HTTP 404).
 * This object contains information about the error type, title, status code, and trace ID.
 */
public class GetBooksNotFoundResponse {
    private String type;
    private String title;
    private int status;
    private String traceId;

    /**
     * Gets the type of the error, indicating the nature of the "Not Found" error.
     *
     * @return the error type as a String.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the error.
     *
     * @param type the error type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the title of the error, providing a short description of the error.
     *
     * @return the error title as a String.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the error.
     *
     * @param title the error title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the HTTP status code associated with the "Not Found" error.
     *
     * @return the HTTP status code as an integer (e.g., 404 for not found).
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param status the HTTP status code to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the trace ID, which is a unique identifier for tracking the request.
     *
     * @return the trace ID as a String.
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Sets the trace ID.
     *
     * @param traceId the trace ID to set.
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * Returns a string representation of the GetAuthorsNotFoundResponse object.
     * Provides a formatted string with the type, title, status, and traceId.
     *
     * @return a string representing the GetAuthorsNotFoundResponse object.
     */
    @Override
    public String toString() {
        return String.format("AuthorNotFoundResponse{type='%s', title='%s', status=%d, traceId='%s'}",
                type, title, status, traceId);
    }

}
