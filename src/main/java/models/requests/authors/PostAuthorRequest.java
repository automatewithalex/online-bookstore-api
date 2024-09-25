package models.requests.authors;

import models.AuthorModel;

import java.util.Objects;


/**
 * Represents a request to create or update an author entity.
 * Contains the author's ID, associated book ID, first name, and last name.
 */
public class PostAuthorRequest extends AuthorModel {

    public PostAuthorRequest(Long id, Long idBook, String firstName, String lastName) {
        super(id, idBook, firstName, lastName);
    }

    public PostAuthorRequest() {
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
        PostAuthorRequest that = (PostAuthorRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(idBook, that.idBook) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    /**
     * Returns a string representation of the AuthorRequest object.
     *
     * @return a string representing the author's request details.
     */
    @Override
    public String toString() {
        return String.format("PostAuthorRequest{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}