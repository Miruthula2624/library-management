package com.library.management.service;

import com.library.management.dto.ReturnRequest;
import com.library.management.dto.ReturnResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Issue;
import com.library.management.entity.Return;
import com.library.management.exception.BadRequestException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
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
@Transactional
public class ReturnService {

    /** Fine rate: ₹5 per late day */
    private static final BigDecimal DAILY_FINE = new BigDecimal("5.00");

    private final ReturnRepository returnRepository;
    private final IssueRepository issueRepository;
    private final BookRepository bookRepository;

    public ReturnService(ReturnRepository returnRepository,
                         IssueRepository issueRepository,
                         BookRepository bookRepository) {
        this.returnRepository = returnRepository;
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<ReturnResponse> getAllReturns() {
        return returnRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReturnResponse getReturnById(Long id) {
        Return ret = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return record not found with ID: " + id));
        return mapToResponse(ret);
    }

    public ReturnResponse createReturn(ReturnRequest request) {
        // Rule 1: Issue must exist
        Issue issue = issueRepository.findById(request.getIssueId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue transaction not found with ID: " + request.getIssueId()));

        // Rule 2: Issue must not already be returned
        if (returnRepository.existsByIssueIssueId(issue.getIssueId())) {
            throw new BadRequestException(
                    "This book has already been returned for issue ID: " + issue.getIssueId());
        }

        // Rule 4: Determine return date
        LocalDate returnDate = (request.getReturnDate() != null) ? request.getReturnDate() : LocalDate.now();

        // Fine calculation
        BigDecimal fine = calculateFine(issue.getDueDate(), returnDate);

        // Rule 3: Increase available copies (must not exceed totalCopies)
        Book book = issue.getBook();
        int newAvailable = book.getAvailableCopies() + 1;
        if (newAvailable > book.getTotalCopies()) {
            newAvailable = book.getTotalCopies();
        }
        book.setAvailableCopies(newAvailable);
        bookRepository.save(book);

        // Save the return record
        Return ret = new Return(issue, returnDate, fine);
        Return saved = returnRepository.save(ret);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReturnResponse> searchReturns(String keyword) {
        List<Return> returns;
        if (keyword == null || keyword.trim().isEmpty()) {
            returns = returnRepository.findAll();
        } else {
            returns = returnRepository.searchReturns(keyword.trim());
        }
        return returns.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- Helpers ---

    private BigDecimal calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) {
            return BigDecimal.ZERO;
        }
        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return DAILY_FINE.multiply(BigDecimal.valueOf(lateDays));
    }

    private ReturnResponse mapToResponse(Return ret) {
        Issue issue = ret.getIssue();
        Book book = issue.getBook();
        boolean lateReturn = ret.getReturnDate().isAfter(issue.getDueDate());

        return new ReturnResponse(
                ret.getReturnId(),
                issue.getIssueId(),
                book.getBookId(),
                book.getTitle(),
                book.getIsbn(),
                issue.getStudent().getStudentId(),
                issue.getStudent().getName(),
                issue.getStudent().getDepartment(),
                issue.getIssueDate(),
                issue.getDueDate(),
                ret.getReturnDate(),
                ret.getFine(),
                lateReturn
        );
    }
}
