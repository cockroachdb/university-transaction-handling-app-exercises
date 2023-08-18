-- drop tables
DROP TABLE IF EXISTS items;

-- re-create tables
CREATE TABLE items (item_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                    name STRING,
                    description STRING,
                    price DECIMAL NOT NULL,
                    quantity INT DEFAULT 0);
