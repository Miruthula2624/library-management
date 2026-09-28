package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.BookRequest;
import com.library.management.entity.Book;
import com.library.management.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequest request) {
        Book created = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        Book updated = bookService.updateBook(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}
