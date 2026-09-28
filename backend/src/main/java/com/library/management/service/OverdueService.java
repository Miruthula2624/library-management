package com.library.management.service;

import com.library.management.dto.OverdueResponse;
import com.library.management.entity.Issue;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.IssueRepository;
import com.library.management.repository.ReturnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OverdueService {

    /** Fine rate: ₹5 per overdue day — centralised, not scattered */
    private static final BigDecimal DAILY_FINE = new BigDecimal("5.00");

    private final IssueRepository issueRepository;
    private final ReturnRepository returnRepository;

    public OverdueService(IssueRepository issueRepository,
                          ReturnRepository returnRepository) {
        this.issueRepository = issueRepository;
        this.returnRepository = returnRepository;
    }

    /** Returns all currently overdue, unreturned issues. */
    public List<OverdueResponse> getAllOverdue() {
        LocalDate today = LocalDate.now();
        return issueRepository.findOverdueIssues(today).stream()
                .map(i -> mapToResponse(i, today))
                .collect(Collectors.toList());
    }

    /**
     * Returns the overdue record for a specific issue.
     * Throws 404 if the issue does not exist or is not currently overdue.
     */
    public OverdueResponse getOverdueById(Long issueId) {
        LocalDate today = LocalDate.now();

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue not found with ID: " + issueId));

        // Must be overdue (dueDate < today) and not returned
        if (!issue.getDueDate().isBefore(today)) {
            throw new ResourceNotFoundException(
                    "Issue " + issueId + " is not currently overdue.");
        }
        if (returnRepository.existsByIssueIssueId(issueId)) {
            throw new ResourceNotFoundException(
                    "Issue " + issueId + " has already been returned — not in overdue list.");
        }

        return mapToResponse(issue, today);
    }

    /** Search overdue issues by keyword (book title, ISBN, student name, IDs). */
    public List<OverdueResponse> searchOverdue(String keyword) {
        LocalDate today = LocalDate.now();
        List<Issue> issues;
        if (keyword == null || keyword.trim().isEmpty()) {
            issues = issueRepository.findOverdueIssues(today);
        } else {
            issues = issueRepository.searchOverdueIssues(today, keyword.trim());
        }
        return issues.stream()
                .map(i -> mapToResponse(i, today))
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private OverdueResponse mapToResponse(Issue issue, LocalDate today) {
        long overdueDays = ChronoUnit.DAYS.between(issue.getDueDate(), today);
        BigDecimal fine  = DAILY_FINE.multiply(BigDecimal.valueOf(overdueDays));

        return new OverdueResponse(
                issue.getIssueId(),
                issue.getBook().getBookId(),
                issue.getBook().getTitle(),
                issue.getBook().getIsbn(),
                issue.getStudent().getStudentId(),
                issue.getStudent().getName(),
                issue.getStudent().getDepartment(),
                issue.getIssueDate(),
                issue.getDueDate(),
                overdueDays,
                fine
        );
    }
}
