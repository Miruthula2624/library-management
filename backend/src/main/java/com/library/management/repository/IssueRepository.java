package com.library.management.repository;

import com.library.management.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByStudentStudentId(Long studentId);
    List<Issue> findByBookBookId(Long bookId);

    @Query("SELECT i FROM Issue i WHERE " +
           "LOWER(i.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "CAST(i.student.studentId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(i.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.book.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Issue> searchIssues(@Param("keyword") String keyword);

    /** All unreturned issues whose due date is before today (overdue). */
    @Query("SELECT i FROM Issue i " +
           "WHERE i.dueDate < :today " +
           "AND NOT EXISTS (SELECT r FROM Return r WHERE r.issue.issueId = i.issueId)")
    List<Issue> findOverdueIssues(@Param("today") LocalDate today);

    /** Overdue issues filtered by keyword (book title, isbn, student name, issue id). */
    @Query("SELECT i FROM Issue i " +
           "WHERE i.dueDate < :today " +
           "AND NOT EXISTS (SELECT r FROM Return r WHERE r.issue.issueId = i.issueId) " +
           "AND (" +
           "  LOWER(i.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(i.book.isbn)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(i.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  CAST(i.student.studentId AS string) LIKE CONCAT('%', :keyword, '%') OR " +
           "  CAST(i.issueId AS string) LIKE CONCAT('%', :keyword, '%')" +
           ")")
    List<Issue> searchOverdueIssues(@Param("today") LocalDate today,
                                    @Param("keyword") String keyword);

    /** Count issues that have NOT been returned (currently active). */
    @Query("SELECT COUNT(i) FROM Issue i " +
           "WHERE NOT EXISTS (SELECT r FROM Return r WHERE r.issue.issueId = i.issueId)")
    long countActiveIssues();
}
