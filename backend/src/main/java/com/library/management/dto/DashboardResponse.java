package com.library.management.dto;

import java.math.BigDecimal;

public class DashboardResponse {

    private long totalBooks;
    private long totalStudents;
    private long totalIssuedBooks;      // active (not yet returned)
    private long totalOverdueBooks;
    private long totalCopies;
    private long availableCopies;
    private BigDecimal totalOverdueFine;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalBooks, long totalStudents, long totalIssuedBooks,
                             long totalOverdueBooks, long totalCopies, long availableCopies,
                             BigDecimal totalOverdueFine) {
        this.totalBooks = totalBooks;
        this.totalStudents = totalStudents;
        this.totalIssuedBooks = totalIssuedBooks;
        this.totalOverdueBooks = totalOverdueBooks;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.totalOverdueFine = totalOverdueFine != null ? totalOverdueFine : BigDecimal.ZERO;
    }

    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalIssuedBooks() { return totalIssuedBooks; }
    public void setTotalIssuedBooks(long totalIssuedBooks) { this.totalIssuedBooks = totalIssuedBooks; }

    public long getTotalOverdueBooks() { return totalOverdueBooks; }
    public void setTotalOverdueBooks(long totalOverdueBooks) { this.totalOverdueBooks = totalOverdueBooks; }

    public long getTotalCopies() { return totalCopies; }
    public void setTotalCopies(long totalCopies) { this.totalCopies = totalCopies; }

    public long getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(long availableCopies) { this.availableCopies = availableCopies; }

    public BigDecimal getTotalOverdueFine() { return totalOverdueFine; }
    public void setTotalOverdueFine(BigDecimal totalOverdueFine) {
        this.totalOverdueFine = totalOverdueFine != null ? totalOverdueFine : BigDecimal.ZERO;
    }
}
