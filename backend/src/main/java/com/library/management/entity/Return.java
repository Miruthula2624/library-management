package com.library.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "returns")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;

    @NotNull(message = "Issue is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    private Issue issue;

    @NotNull(message = "Return date is required")
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @NotNull(message = "Fine is required")
    @Column(name = "fine", nullable = false, precision = 10, scale = 2)
    private BigDecimal fine = BigDecimal.ZERO;

    public Return() {
    }

    public Return(Long returnId, Issue issue, LocalDate returnDate, BigDecimal fine) {
        this.returnId = returnId;
        this.issue = issue;
        this.returnDate = returnDate;
        this.fine = fine != null ? fine : BigDecimal.ZERO;
    }

    public Return(Issue issue, LocalDate returnDate, BigDecimal fine) {
        this.issue = issue;
        this.returnDate = returnDate;
        this.fine = fine != null ? fine : BigDecimal.ZERO;
    }

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine != null ? fine : BigDecimal.ZERO;
    }
}
