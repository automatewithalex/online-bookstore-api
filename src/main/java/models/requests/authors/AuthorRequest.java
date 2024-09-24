package models.requests.authors;

import java.util.Objects;

/**
 * Represents a request to create or update an author entity.
 * Contains the author's ID, associated book ID, first name, and last name.
 */
public class AuthorRequest {
    private Long id;
    private Long idBook;
    private String firstName;
    private String lastName;

    /**
     * Constructs a new AuthorRequest with the specified details.
     *
     * @param id        the unique identifier of the author.
     * @param idBook    the unique identifier of the book associated with the author.
     * @param firstName the author's first name.
     * @param lastName  the author's last name.
     */
    public AuthorRequest(Long id, Long idBook, String firstName, String lastName) {
        this.id = id;
        this.idBook = idBook;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Gets the author's ID.
     *
     * @return the author's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the author's ID.
     *
     * @param id the new ID for the author.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the associated book's ID.
     *
     * @return the book's ID.
     */
    public Long getIdBook() {
        return idBook;
    }

    /**
     * Sets the associated book's ID.
     *
     * @param idBook the new book ID for the author.
     */
    public void setIdBook(Long idBook) {
        this.idBook = idBook;
    }

    /**
     * Gets the author's first name.
     *
     * @return the author's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the author's first name.
     *
     * @param firstName the new first name for the author.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the author's last name.
     *
     * @return the author's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the author's last name.
     *
     * @param lastName the new last name for the author.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        AuthorRequest that = (AuthorRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(idBook, that.idBook) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
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
     * Returns a string representation of the AuthorRequest object.
     *
     * @return a string representing the author's request details.
     */
    @Override
    public String toString() {
        return String.format("AuthorsRequest{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}