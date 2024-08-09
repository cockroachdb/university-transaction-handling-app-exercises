package com.cockroachlabs.university;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookServiceIntegrationTests {

	@Autowired BookService bookService;

	@BeforeEach
	void setUp() {
		bookService.putBooksOnSale();
	}

	@Test
	void cancelSale() {
		bookService.cancelSaleInChunks(10000);
	}

}
