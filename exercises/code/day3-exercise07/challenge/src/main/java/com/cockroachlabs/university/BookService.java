package com.cockroachlabs.university;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
class BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

    private final BookRepository repository;

    private final TransactionTemplate txTemplate;

    BookService(BookRepository repository, TransactionTemplate txTemplate) {
        this.repository = repository;
        this.txTemplate = txTemplate;
    }

    @Transactional
    void putBooksOnSale() {
        repository.putBooksOnSale("ebook", 0.1f);
        repository.putBooksOnSale("audio", 0.25f);
    }

    void cancelBookSale() {

        // TODO: Break up this one, long transaction into two
        // separate transactions, with the publishMessageAbout()
        // happening outside the transactions

        txTemplate.executeWithoutResult(transactionStatus -> {
            repository.cancelSale("ebook");
            publishMessageAbout("ebook");

            repository.cancelSale("audio");
            publishMessageAbout("audio");
        });
    }

    void publishMessageAbout(String format) {
        LOG.info("*** This simulates publishing a message about '" + format + "' to some broker");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    List<Book> booksOnSale() {
        return repository.booksOnSale();
    }
}
