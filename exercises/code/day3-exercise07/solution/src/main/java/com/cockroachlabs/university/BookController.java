package com.cockroachlabs.university;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class BookController {

    private static final Logger LOG = LoggerFactory.getLogger(BookController.class);

    private final BookService service;

    BookController(BookService service) {
        this.service = service;
    }

    @PostMapping("/api/books/sale")
    void putBooksOnSale() {
        service.putBooksOnSale();
    }

    @PostMapping("/api/books/cancelSale")
    void cancelBookSaleLong() {
        service.cancelBookSale();
    }

    @GetMapping("/api/books/onSale")
    List<Book> booksOnSale() {
        return service.booksOnSale();
    }
}
