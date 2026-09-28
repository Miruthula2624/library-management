package com.library.management.dto;

import java.time.LocalDate;

public class IssueResponse {

    private Long issueId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long studentId;
    private String studentName;
    private String studentDepartment;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;
    private boolean returned;

    public IssueResponse() {
    }

    public IssueResponse(Long issueId, Long bookId, String bookTitle, String bookIsbn,
                         Long studentId, String studentName, String studentDepartment,
                         LocalDate issueDate, LocalDate dueDate, String status, boolean returned) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookIsbn = bookIsbn;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentDepartment = studentDepartment;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.returned = returned;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
