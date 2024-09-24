package models.responses.books;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents the response object for retrieving book information.
 * This class contains details about a book, including its ID, title, description, page count, excerpt, and publish date.
 */
public class GetBooksResponse {
    private Long id;
    private String title;
    private String description;
    private int pageCount;
    private String excerpt;
    private String publishDate;

    /**
     * Gets the unique identifier of the book.
     *
     * @return the book's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the book.
     *
     * @param id the ID to set for the book.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the title of the book.
     *
     * @return the book's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the title to set for the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the book.
     *
     * @return the book's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the book.
     *
     * @param description the description to set for the book.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the page count of the book.
     *
     * @return the page count of the book.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Sets the page count of the book.
     *
     * @param pageCount the number of pages to set for the book.
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Gets the excerpt of the book.
     *
     * @return the book's excerpt.
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * Sets the excerpt of the book.
     *
     * @param excerpt the excerpt to set for the book.
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    /**
     * Gets the publish date of the book.
     *
     * @return the publish date of the book.
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the publish date of the book.
     *
     * @param publishDate the date to set for the book's publication.
     */
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Compares this GetBooksResponse object with another object for equality.
     * Two GetBooksResponse objects are considered equal if they have the same ID, title, description, page count, excerpt, and publish date.
     *
     * @param o the object to compare with.
     * @return true if both objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetBooksResponse that = (GetBooksResponse) o;

        return pageCount == that.pageCount &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(excerpt, that.excerpt) &&
                Objects.equals(publishDate, that.publishDate);
    }

    /**
     * Generates a hash code for this GetBooksResponse object.
     *
     * @return the hash code based on the object's fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, pageCount, excerpt, publishDate);
    }

    /**
     * Returns a string representation of the GetBooksResponse object.
     * Provides a formatted string with the book's ID, title, description, page count, excerpt, and publish date.
     *
     * @return a string representing the GetBooksResponse object.
     */
    @Override
    public String toString() {
        return String.format("BooksResponse{id=%d, title='%s', description='%s', pageCount=%d, excerpt='%s', publishDate='%s'}",
                id, title, description, pageCount, excerpt, publishDate);
    }

}
