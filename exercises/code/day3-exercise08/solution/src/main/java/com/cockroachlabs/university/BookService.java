package com.cockroachlabs.university;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
class BookService {

	private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

	private final BookRepository repository;

	private final TransactionTemplate txTemplate;

	private final JdbcClient jdbcClient;

	BookService(BookRepository repository, TransactionTemplate txTemplate, JdbcClient jdbcClient) {
		this.repository = repository;
		this.txTemplate = txTemplate;
		this.jdbcClient = jdbcClient;
	}

	@Transactional
	void putBooksOnSale() {
		repository.putBooksOnSale("ebook", 0.1f);
		repository.putBooksOnSale("audio", 0.25f);
	}

	void cancelBookSale() {
		txTemplate.executeWithoutResult(transactionStatus -> {
			repository.cancelSale("ebook");
		});
		publishMessageAbout("ebook");

		txTemplate.executeWithoutResult(transactionStatus -> {
			repository.cancelSale("audio");
		});
		publishMessageAbout("audio");
	}

	void publishMessageAbout(String format) {
		LOG.info("*** This simulates publishing a message about '" + format + "' to some broker");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	void cancelSaleInChunks(int chunkSize) {

		List<UUID> booksOnSale;

		do {
			LOG.debug(">>> Looks there are a total of " + repository.numberOfBooksOnSale() + " books currently on sale.");

			booksOnSale = repository.chunkOfBooksOnSale(chunkSize);

			if (!booksOnSale.isEmpty()) {

				jdbcClient //
						.sql("""
								WITH original_prices AS (
									SELECT book_id, price
									FROM books_msrp
								)
								UPDATE book
								SET price = original_prices.price
								FROM original_prices
								WHERE book.book_id = original_prices.book_id
								AND book.book_id IN (:books_on_sale)
								""") //
						.param("books_on_sale", booksOnSale) //
						.update();

				LOG.debug(">>> Canceled the sale on " + booksOnSale.size() + " books!");
			} else {
				LOG.debug(">>> No books in this chunk of book_id's");
			}

			LOG.debug(">>> There are now " + repository.numberOfBooksOnSale() + " books currently on sale.");

			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} while (!booksOnSale.isEmpty());

		LOG.debug(">>> Done canceling sale using chunks");
	}

	List<Book> booksOnSale() {
		return repository.booksOnSale();
	}
}
