package com.library.management.repository;

import com.library.management.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);

    /** SUM of totalCopies across all books; returns 0 if no books exist. */
    @Query("SELECT COALESCE(SUM(b.totalCopies), 0) FROM Book b")
    long sumTotalCopies();

    /** SUM of availableCopies across all books; returns 0 if no books exist. */
    @Query("SELECT COALESCE(SUM(b.availableCopies), 0) FROM Book b")
    long sumAvailableCopies();
}
