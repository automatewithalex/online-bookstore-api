package models;

import java.util.Objects;

/**
 * Represents the response object for creating a new author (POST request).
 * This object contains details about the newly created author, including their ID,
 * associated book ID, first name, and last name.
 */
public class AuthorModel {
    protected Long id;
    protected Long idBook;
    protected String firstName;
    protected String lastName;

    public AuthorModel(Long id, Long idBook, String firstName, String lastName) {
        this.id = id;
        this.idBook = idBook;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public AuthorModel() {
    }

    /**
     * Gets the unique identifier of the newly created author.
     *
     * @return the author's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the newly created author.
     *
     * @param id the ID to set for the author.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of the book associated with the author.
     *
     * @return the associated book's ID.
     */
    public Long getIdBook() {
        return idBook;
    }

    /**
     * Sets the unique identifier of the book associated with the author.
     *
     * @param idBook the ID of the associated book to set.
     */
    public void setIdBook(Long idBook) {
        this.idBook = idBook;
    }

    /**
     * Gets the first name of the newly created author.
     *
     * @return the first name of the author.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the newly created author.
     *
     * @param firstName the first name to set for the author.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the newly created author.
     *
     * @return the last name of the author.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the newly created author.
     *
     * @param lastName the last name to set for the author.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Generates a hash code for the AuthorRequest object.
     *
     * @return the hash code based on the object's fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, idBook, firstName, lastName);
    }

    /**
     * Returns a string representation of the PostAuthorResponse object.
     * Provides a formatted string with the author's ID, book ID, first name, and last name.
     *
     * @return a string representing the PostAuthorResponse object.
     */
    @Override
    public String toString() {
        return String.format("Author{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}
