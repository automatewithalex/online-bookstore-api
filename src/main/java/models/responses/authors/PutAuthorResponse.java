package models.responses.authors;

import models.AuthorModel;

import java.util.Objects;

/**
 * Represents the response object for creating a new author (PUT request).
 * This object contains details about the newly created author, including their ID,
 * associated book ID, first name, and last name.
 */
public class PutAuthorResponse extends AuthorModel {

    public PutAuthorResponse(Long id, Long idBook, String firstName, String lastName) {
        super(id, idBook, firstName, lastName);
    }

    public PutAuthorResponse() {
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
        PutAuthorResponse that = (PutAuthorResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(idBook, that.idBook) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    /**
     * Returns a string representation of the PutAuthorResponse object.
     * Provides a formatted string with the author's ID, book ID, first name, and last name.
     *
     * @return a string representing the PutAuthorResponse object.
     */
    @Override
    public String toString() {
        return String.format("PutAuthorResponse{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}