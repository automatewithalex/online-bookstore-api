package models.responses.common;

import java.util.Map;
import java.util.List;

/**
 * Represents a response object for a bad request (HTTP 400).
 * This object contains information about the error type, title, status code, trace ID,
 * and specific validation or request errors.
 */
public class BadRequestResponse {
    protected String type;
    protected String title;
    protected int status;
    protected String traceId;
    private Map<String, List<String>> errors;

    /**
     * Gets the type of the error, which indicates the kind of error that occurred.
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
     * Gets the title of the error, which provides a short summary of the error.
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
     * Gets the HTTP status code associated with the bad request.
     *
     * @return the HTTP status code as an integer (e.g., 400 for bad request).
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
     * Gets the specific errors related to the bad request, usually in the form of validation errors.
     *
     * @return a Map where the key is the field or parameter name, and the value is a list of error messages.
     */
    public Map<String, List<String>> getErrors() {
        return errors;
    }

    /**
     * Sets the specific errors for the bad request.
     *
     * @param errors a Map of field or parameter names to a list of error messages.
     */
    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }

    /**
     * Returns a string representation of the BadRequestResponse object.
     * Provides a formatted string with the type, title, status, and traceId.
     *
     * @return a string representing the BadRequestResponse object.
     */
    @Override
    public String toString() {
        return String.format("BadRequestResponse{type='%s', title='%s', status=%d, traceId='%s'}",
                type, title, status, traceId);
    }

}