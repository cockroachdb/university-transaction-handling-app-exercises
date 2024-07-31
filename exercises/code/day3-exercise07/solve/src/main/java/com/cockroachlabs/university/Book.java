package com.cockroachlabs.university;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookId;

    private String title;

    private String author;

    private Float price;

    private String format;

    private LocalDate publishDate;

    protected Book() {
        this(null, null, null, null, null, null);
    }

    Book(UUID bookId, String title, String author, Float price, String format, LocalDate publishDate) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.format = format;
        this.publishDate = publishDate;
    }

    Book(String title, String author) {
        this(null, title, author, null, null, null);
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(price, book.price) && Objects.equals(format, book.format) && Objects.equals(publishDate, book.publishDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, title, author, price, format, publishDate);
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", format='" + format + '\'' +
                ", publishDate=" + publishDate +
                '}';
    }
}
