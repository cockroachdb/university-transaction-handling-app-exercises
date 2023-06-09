-- drop tables
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS shopping_carts CASCADE;
DROP TABLE IF EXISTS shoppers;
DROP TABLE IF EXISTS items;

-- re-create tables
CREATE TABLE shoppers (email STRING PRIMARY KEY, name STRING, address STRING);
CREATE TABLE items (item_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                    name STRING,
                    description STRING,
                    price DECIMAL NOT NULL,
                    quantity INT DEFAULT 0);
CREATE TABLE shopping_carts (cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                             user_email STRING NOT NULL REFERENCES shoppers(email) NOT NULL,
                             purchased_at TIMESTAMPTZ NULL);
CREATE TABLE cart_items (id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                         cart_id UUID NOT NULL REFERENCES shopping_carts(cart_id),
                         item_id UUID NOT NULL REFERENCES items(item_id),
                         quantity INT DEFAULT 1 NOT NULL);

