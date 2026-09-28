package com.library.management.service;

import com.library.management.dto.IssueRequest;
import com.library.management.dto.IssueResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Issue;
import com.library.management.entity.Student;
import com.library.management.exception.BadRequestException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.IssueRepository;
import com.library.management.repository.ReturnRepository;
import com.library.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IssueService {

    private final IssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final ReturnRepository returnRepository;

    public IssueService(IssueRepository issueRepository,
                        BookRepository bookRepository,
                        StudentRepository studentRepository,
                        ReturnRepository returnRepository) {
        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
        this.returnRepository = returnRepository;
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IssueResponse getIssueById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue transaction not found with ID: " + id));
        return mapToResponse(issue);
    }

    public IssueResponse createIssue(IssueRequest request) {
        // Rule 1: Book must exist
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));

        // Rule 2: Student must exist
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        // Rule 3: Book must be available
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Book is currently unavailable.");
        }

        // Rule 5: Issue date
        LocalDate issueDate = request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now();

        // Rule 6: Due date must be after issue date
        if (request.getDueDate() == null || !request.getDueDate().isAfter(issueDate)) {
            throw new BadRequestException("Due date must be after the issue date.");
        }

        // Rule 4 & 7: Decrease available copies atomically within transaction
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Save issue transaction
        Issue issue = new Issue();
        issue.setBook(book);
        issue.setStudent(student);
        issue.setIssueDate(issueDate);
        issue.setDueDate(request.getDueDate());

        Issue saved = issueRepository.save(issue);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> searchIssues(String keyword) {
        List<Issue> issues;
        if (keyword == null || keyword.trim().isEmpty()) {
            issues = issueRepository.findAll();
        } else {
            issues = issueRepository.searchIssues(keyword.trim());
        }
        return issues.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private IssueResponse mapToResponse(Issue issue) {
        boolean isReturned = returnRepository.existsByIssueIssueId(issue.getIssueId());
        String status;
        if (isReturned) {
            status = "Returned";
        } else if (LocalDate.now().isAfter(issue.getDueDate())) {
            status = "Overdue";
        } else {
            status = "Active";
        }

        return new IssueResponse(
                issue.getIssueId(),
                issue.getBook().getBookId(),
                issue.getBook().getTitle(),
                issue.getBook().getIsbn(),
                issue.getStudent().getStudentId(),
                issue.getStudent().getName(),
                issue.getStudent().getDepartment(),
                issue.getIssueDate(),
                issue.getDueDate(),
                status,
                isReturned
        );
    }
}
