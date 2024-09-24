package models.responses.books;

/**
 * Represents the response object for creating a new book (POST request).
 * This object contains details about the newly created book, including its ID,
 * title, description, page count, excerpt, and publish date.
 */
public class PostBookResponse {
    private Long id;
    private String title;
    private String description;
    private int pageCount;
    private String excerpt;
    private String publishDate;  // Keeping publishDate as String per your preference

    /**
     * Gets the unique identifier of the newly created book.
     *
     * @return the book's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the newly created book.
     *
     * @param id the ID to set for the book.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the title of the newly created book.
     *
     * @return the book's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the newly created book.
     *
     * @param title the title to set for the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the newly created book.
     *
     * @return the book's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the newly created book.
     *
     * @param description the description to set for the book.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the page count of the newly created book.
     *
     * @return the page count of the book.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Sets the page count of the newly created book.
     *
     * @param pageCount the page count to set for the book.
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Gets the excerpt of the newly created book.
     *
     * @return the book's excerpt.
     */
    public String getExcerpt() {
        return excerpt;
    }

    /**
     * Sets the excerpt of the newly created book.
     *
     * @param excerpt the excerpt to set for the book.
     */
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    /**
     * Gets the publish date of the newly created book as a string.
     *
     * @return the publish date of the book.
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the publish date of the newly created book as a string.
     *
     * @param publishDate the date to set for the book's publication.
     */
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Returns a string representation of the PostBookResponse object.
     * Provides a formatted string with the book's ID, title, description, page count, excerpt, and publish date.
     *
     * @return a string representing the PostBookResponse object.
     */
    @Override
    public String toString() {
        return String.format("PostBookResponse{id=%d, title='%s', description='%s', pageCount=%d, excerpt='%s', publishDate='%s'}",
                id, title, description, pageCount, excerpt, publishDate);
    }
}
