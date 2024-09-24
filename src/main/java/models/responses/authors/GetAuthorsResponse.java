package models.responses.authors;

import java.util.Objects;

/**
 * Represents the response object for retrieving author information.
 * This class contains details about an author, including their ID, associated book ID, first name, and last name.
 */
public class GetAuthorsResponse {
    private Long id;
    private Long idBook;
    private String firstName;
    private String lastName;

    /**
     * Gets the author's unique identifier.
     *
     * @return the author's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the author's unique identifier.
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
     * Gets the author's first name.
     *
     * @return the first name of the author.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the author's first name.
     *
     * @param firstName the first name to set for the author.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the author's last name.
     *
     * @return the last name of the author.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the author's last name.
     *
     * @param lastName the last name to set for the author.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Compares this GetAuthorsResponse object with another object for equality.
     * Two GetAuthorsResponse objects are considered equal if they have the same ID, book ID, first name, and last name.
     *
     * @param o the object to compare with.
     * @return true if both objects are equal, false otherwise.
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
     * Generates a hash code for this GetAuthorsResponse object.
     *
     * @return the hash code based on the object's ID, book ID, first name, and last name.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, idBook, firstName, lastName);
    }

    /**
     * Returns a string representation of the GetAuthorsResponse object.
     * Provides a formatted string with the author's ID, book ID, first name, and last name.
     *
     * @return a string representing the GetAuthorsResponse object.
     */
    @Override
    public String toString() {
        return String.format("AuthorsResponse{id=%d, idBook=%d, firstName='%s', lastName='%s'}",
                id, idBook, firstName, lastName);
    }

}
