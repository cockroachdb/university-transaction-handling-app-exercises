package com.cockroachlabs.university;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BookController {

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

	@PostMapping("/api/books/cancelSaleInChunks")
	void cancelSaleInChunks() {
		service.cancelSaleInChunks(1000);
	}

	@GetMapping("/api/books/onSale")
	List<Book> booksOnSale() {
		return service.booksOnSale();
	}
}
