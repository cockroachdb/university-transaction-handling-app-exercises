-- an initially wide breadth transaction, spans 3 tables
BEGIN;
INSERT INTO customers (name, email) VALUES ('Jane Doe', 'janedoe@example.com');
INSERT INTO orders (order_date, customer_id) VALUES (NOW(), (SELECT customer_id FROM customers WHERE email = 'janedoe@example.com'));
INSERT INTO audit_logs (action, table_name, timestamp) VALUES ('Insert', 'orders', NOW());
COMMIT;