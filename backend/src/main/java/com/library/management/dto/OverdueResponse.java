package com.library.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OverdueResponse {

    private Long issueId;
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private Long studentId;
    private String studentName;
    private String studentDepartment;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private long overdueDays;
    private BigDecimal fine;

    public OverdueResponse() {
    }

    public OverdueResponse(Long issueId, Long bookId, String bookTitle, String isbn,
                           Long studentId, String studentName, String studentDepartment,
                           LocalDate issueDate, LocalDate dueDate,
                           long overdueDays, BigDecimal fine) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentDepartment = studentDepartment;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.overdueDays = overdueDays;
        this.fine = fine;
    }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(String studentDepartment) { this.studentDepartment = studentDepartment; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public long getOverdueDays() { return overdueDays; }
    public void setOverdueDays(long overdueDays) { this.overdueDays = overdueDays; }

    public BigDecimal getFine() { return fine; }
    public void setFine(BigDecimal fine) { this.fine = fine; }
}
