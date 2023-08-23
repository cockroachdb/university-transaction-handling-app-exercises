-- simulate processing batch of orders
INSERT INTO orders (customer_name, total_price) VALUES 
    ('John Doe', 2500.00),
    ('Jane Smith', 1700.00);

INSERT INTO order_items (order_id, product_id, quantity, subtotal) VALUES 
    ((SELECT order_id FROM orders WHERE customer_name = 'John Doe'), (SELECT product_id FROM products WHERE product_name = 'Laptop'), 2, 2401.00),
    ((SELECT order_id FROM orders WHERE customer_name = 'Jane Smith'), (SELECT product_id FROM products WHERE product_name = 'Phone'), 2, 1601.50);
