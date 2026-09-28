package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReturnRequest {

    @NotNull(message = "Issue ID is required")
    private Long issueId;

    private LocalDate returnDate; // optional; defaults to today if not provided

    public ReturnRequest() {
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
