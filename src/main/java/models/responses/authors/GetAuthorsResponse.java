package models.responses.authors;

import models.AuthorModel;

import java.util.Objects;

/**
 * Represents the response object for retrieving author information.
 * This class contains details about an author, including their ID, associated book ID, first name, and last name.
 */
public class GetAuthorsResponse extends AuthorModel {

    public GetAuthorsResponse(Long id, Long idBook, String firstName, String lastName) {
        super(id, idBook, firstName, lastName);
    }

    public GetAuthorsResponse() {
    }

    /**
     * Checks if two AuthorRequest objects are equal based on their fields.
     *
     * @param o the object to compare with.
     * @return true if both objects have the same field values, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetAuthorsResponse that = (GetAuthorsResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(idBook, that.idBook) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    /**
     * Returns a string representation of the GetAuthorsResponse object.
     * Provides a formatted string with the author's ID, book ID, first name, and last name.
     *
     * @return a string representing the GetAuthorsResponse object.
     */
    @Override
    public String toString() {
        return String.format("GetAuthorsResponse{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}