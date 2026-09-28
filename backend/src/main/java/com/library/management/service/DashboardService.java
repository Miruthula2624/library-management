package com.library.management.service;

import com.library.management.dto.DashboardResponse;
import com.library.management.dto.OverdueResponse;
import com.library.management.repository.BookRepository;
import com.library.management.repository.IssueRepository;
import com.library.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final BookRepository    bookRepository;
    private final StudentRepository studentRepository;
    private final IssueRepository   issueRepository;
    private final OverdueService    overdueService;   // reuse Phase 7 overdue logic

    public DashboardService(BookRepository bookRepository,
                            StudentRepository studentRepository,
                            IssueRepository issueRepository,
                            OverdueService overdueService) {
        this.bookRepository    = bookRepository;
        this.studentRepository = studentRepository;
        this.issueRepository   = issueRepository;
        this.overdueService    = overdueService;
    }

    /**
     * Gathers all dashboard statistics in one call.
     * Reuses the existing OverdueService so the overdue definition is never duplicated.
     */
    public DashboardResponse getDashboardStats() {
        // 1. Total distinct book records
        long totalBooks = bookRepository.count();

        // 2. Total student records
        long totalStudents = studentRepository.count();

        // 3. Issues without a Return record (currently active)
        long totalIssuedBooks = issueRepository.countActiveIssues();

        // 4. Overdue books — delegate entirely to OverdueService (same definition as /api/overdue)
        List<OverdueResponse> overdueList = overdueService.getAllOverdue();
        long totalOverdueBooks = overdueList.size();

        // 5. Sum total and available copies across all books (0 when table is empty)
        long totalCopies     = bookRepository.sumTotalCopies();
        long availableCopies = bookRepository.sumAvailableCopies();

        // 6. Total current overdue fine — sum from the same overdue list (BigDecimal, no float)
        BigDecimal totalOverdueFine = overdueList.stream()
                .map(OverdueResponse::getFine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(
                totalBooks,
                totalStudents,
                totalIssuedBooks,
                totalOverdueBooks,
                totalCopies,
                availableCopies,
                totalOverdueFine
        );
    }
}
