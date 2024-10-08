package models.responses.authors;

import models.AuthorModel;

import java.util.Objects;

/**
 * Represents the response object for creating a new author (POST request).
 * This object contains details about the newly created author, including their ID,
 * associated book ID, first name, and last name.
 */
public class PostAuthorResponse extends AuthorModel {

    public PostAuthorResponse(Long id, Long idBook, String firstName, String lastName) {
        super(id, idBook, firstName, lastName);
    }

    public PostAuthorResponse() {
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
        PostAuthorResponse that = (PostAuthorResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(idBook, that.idBook) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    /**
     * Returns a string representation of the PostAuthorResponse object.
     * Provides a formatted string with the author's ID, book ID, first name, and last name.
     *
     * @return a string representing the PostAuthorResponse object.
     */
    @Override
    public String toString() {
        return String.format("PostAuthorResponse{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}