package com.library.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @NotBlank(message = "Title cannot be empty")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Author cannot be empty")
    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "category")
    private String category;

    @NotBlank(message = "ISBN cannot be empty")
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Total copies cannot be null")
    @Min(value = 0, message = "Total copies cannot be negative")
    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @NotNull(message = "Available copies cannot be null")
    @Min(value = 0, message = "Available copies cannot be negative")
    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    public Book() {
    }

    public Book(Long bookId, String title, String author, String publisher, String category, String isbn, Integer totalCopies, Integer availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public Book(String title, String author, String publisher, String category, String isbn, Integer totalCopies, Integer availableCopies) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
}
