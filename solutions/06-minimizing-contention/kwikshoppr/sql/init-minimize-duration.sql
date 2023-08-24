-- Drop tables if they exist
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS orders;

-- Create tables
CREATE TABLE products (
  product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  product_name STRING,
  stock_quantity INT,
  price DECIMAL(10,2)
);

CREATE TABLE orders (
	order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    customer_name STRING,
    order_date TIMESTAMP DEFAULT current_timestamp(),
    total_price DECIMAL(10,2)
);

CREATE TABLE order_items (
	order_item_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID REFERENCES orders(order_id),
    product_id UUID REFERENCES products(product_id),
    quantity INT,
    subtotal DECIMAL(10, 2)
);

-- Insert data into table
INSERT INTO products (product_name, stock_quantity, price) VALUES 
    ('Laptop', 50, 1200.50),
    ('Phone', 30, 800.75),
    ('Charger', 100, 20.00);