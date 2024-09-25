package models.requests.books;

import models.BookModel;

import java.util.Objects;


/**
 * Represents the request object for creating or updating a book.
 * This class contains details about a book, including its ID, title, description, page count, excerpt, and publish date.
 */
public class PutBookRequest extends BookModel {

    public PutBookRequest(Long id, String title, String description, int pageCount, String excerpt, String publishDate) {
        super(id, title, description, pageCount, excerpt, publishDate);
    }

    public PutBookRequest() {
    }

    /**
     * Compares this BookRequest object with another object for equality.
     * Two BookRequest objects are considered equal if they have the same ID, title, description, page count, excerpt, and publish date.
     *
     * @param o the object to compare with.
     * @return true if both objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PutBookRequest that = (PutBookRequest) o;
        return pageCount == that.pageCount &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(excerpt, that.excerpt) &&
                Objects.equals(publishDate, that.publishDate);
    }

    /**
     * Returns a string representation of the BookRequest object.
     * Provides a formatted string with the book's ID, title, description, page count, excerpt, and publish date.
     *
     * @return a string representing the BookRequest object.
     */
    @Override
    public String toString() {
        return String.format("PutBookRequest{id=%d, title='%s', description='%s', pageCount=%d, excerpt='%s', publishDate='%s'}",
                id, title, description, pageCount, excerpt, publishDate);
    }

}
