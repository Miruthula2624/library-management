package com.library.management.repository;

import com.library.management.entity.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    Optional<Return> findByIssueIssueId(Long issueId);
    boolean existsByIssueIssueId(Long issueId);

    @Query("SELECT r FROM Return r " +
           "WHERE LOWER(r.issue.book.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.issue.book.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.issue.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR CAST(r.returnId AS string) LIKE CONCAT('%', :keyword, '%') " +
           "OR CAST(r.issue.issueId AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Return> searchReturns(@Param("keyword") String keyword);
}
