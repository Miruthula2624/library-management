package com.library.management.service;

import com.library.management.dto.BookRequest;
import com.library.management.entity.Book;
import com.library.management.exception.BadRequestException;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
    }

    public Book createBook(BookRequest request) {
        // Validate ISBN uniqueness
        if (bookRepository.findByIsbn(request.getIsbn().trim()).isPresent()) {
            throw new DuplicateResourceException("ISBN already exists: " + request.getIsbn());
        }

        // Validate copy counts
        int total = request.getTotalCopies();
        if (total < 0) {
            throw new BadRequestException("Total copies cannot be negative.");
        }

        int available = request.getAvailableCopies() != null ? request.getAvailableCopies() : total;
        if (available < 0) {
            throw new BadRequestException("Available copies cannot be negative.");
        }
        if (available > total) {
            throw new BadRequestException("Available copies cannot exceed total copies.");
        }

        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setPublisher(request.getPublisher() != null ? request.getPublisher().trim() : null);
        book.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        book.setIsbn(request.getIsbn().trim());
        book.setTotalCopies(total);
        book.setAvailableCopies(available);

        return bookRepository.save(book);
    }

    public Book updateBook(Long id, BookRequest request) {
        Book book = getBookById(id);

        // Check ISBN uniqueness if changed
        String newIsbn = request.getIsbn().trim();
        if (!book.getIsbn().equalsIgnoreCase(newIsbn)) {
            bookRepository.findByIsbn(newIsbn).ifPresent(existing -> {
                if (!existing.getBookId().equals(id)) {
                    throw new DuplicateResourceException("ISBN already exists: " + newIsbn);
                }
            });
        }

        int total = request.getTotalCopies();
        if (total < 0) {
            throw new BadRequestException("Total copies cannot be negative.");
        }

        int available = request.getAvailableCopies() != null ? request.getAvailableCopies() : book.getAvailableCopies();
        if (available < 0) {
            throw new BadRequestException("Available copies cannot be negative.");
        }
        if (available > total) {
            throw new BadRequestException("Available copies cannot exceed total copies.");
        }

        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setPublisher(request.getPublisher() != null ? request.getPublisher().trim() : null);
        book.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);
        book.setIsbn(newIsbn);
        book.setTotalCopies(total);
        book.setAvailableCopies(available);

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.searchBooks(keyword.trim());
    }
}
