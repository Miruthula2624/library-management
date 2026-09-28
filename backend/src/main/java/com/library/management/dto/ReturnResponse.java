package com.library.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnResponse {

    private Long returnId;
    private Long issueId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long studentId;
    private String studentName;
    private String studentDepartment;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fine;
    private boolean lateReturn;

    public ReturnResponse() {
    }

    public ReturnResponse(Long returnId, Long issueId, Long bookId, String bookTitle, String bookIsbn,
                          Long studentId, String studentName, String studentDepartment,
                          LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                          BigDecimal fine, boolean lateReturn) {
        this.returnId = returnId;
        this.issueId = issueId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookIsbn = bookIsbn;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentDepartment = studentDepartment;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.lateReturn = lateReturn;
    }

    public Long getReturnId() { return returnId; }
    public void setReturnId(Long returnId) { this.returnId = returnId; }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }

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

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public BigDecimal getFine() { return fine; }
    public void setFine(BigDecimal fine) { this.fine = fine; }

    public boolean isLateReturn() { return lateReturn; }
    public void setLateReturn(boolean lateReturn) { this.lateReturn = lateReturn; }
}
