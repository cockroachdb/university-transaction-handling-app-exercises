package com.cockroachlabs.university;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootApplication
public class BooklyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooklyApplication.class);
    }

    @Bean
    CommandLineRunner loadDatabase(JdbcClient jdbcClient) {
        return args -> {
            jdbcClient //
                    .sql("""
                            IMPORT INTO book (book_id, title, author, price, format, publish_date)
                            CSV DATA ('nodelocal://self/100000_books.csv')
                            WITH skip = '1';
                            """) //
                    .query() //
                    .rowSet();
            jdbcClient //
                    .sql("""
                            CREATE TABLE books_msrp AS
                            SELECT * FROM book;
                            """) //
                    .update();
        };
    }
}
