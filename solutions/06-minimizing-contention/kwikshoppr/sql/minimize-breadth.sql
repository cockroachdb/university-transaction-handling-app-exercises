-- One transaction for each table action. In this case they can happen independently.
-- Another option is to have the customers and orders statement in the same transaction
--    to ensure the customer is created before the order. 
BEGIN;
INSERT INTO customers (name, email) VALUES ('Jane Doe', 'janedoe@example.com');
COMMIT;

BEGIN;
INSERT INTO orders (order_date, customer_id) VALUES (NOW(), (SELECT customer_id FROM customers WHERE email = 'janedoe@example.com'));
COMMIT;

BEGIN;
INSERT INTO audit_logs (action, table_name, timestamp) VALUES ('Insert', 'orders', NOW());
COMMIT;