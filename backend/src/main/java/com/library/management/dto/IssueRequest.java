package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class IssueRequest {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    public IssueRequest() {
    }

    public IssueRequest(Long bookId, Long studentId, LocalDate issueDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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
