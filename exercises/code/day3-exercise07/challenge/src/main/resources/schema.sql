DROP TABLE IF EXISTS book;

CREATE TABLE book (
    book_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title STRING NOT NULL,
    author STRING NOT NULL,
    price FLOAT NOT NULL,
    format STRING NOT NULL,
    publish_date DATE NOT NULL
);

DROP TABLE IF EXISTS books_msrp;