package com.library.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long issueId;

    @NotNull(message = "Book is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    public Issue() {
    }

    public Issue(Long issueId, Book book, Student student, LocalDate issueDate, LocalDate dueDate) {
        this.issueId = issueId;
        this.book = book;
        this.student = student;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Issue(Book book, Student student, LocalDate issueDate, LocalDate dueDate) {
        this.book = book;
        this.student = student;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
