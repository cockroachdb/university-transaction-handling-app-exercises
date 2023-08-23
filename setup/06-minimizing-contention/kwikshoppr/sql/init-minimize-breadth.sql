-- Drop tables if they exist
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS audit_logs;

-- Creating the tables
CREATE TABLE customers (
	customer_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	name STRING,
	email STRING UNIQUE NOT NULL
);

CREATE TABLE orders (
	order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	order_date TIMESTAMP,
	customer_id UUID REFERENCES customers(customer_id)
);

CREATE TABLE audit_logs (
	log_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	action STRING,
	table_name STRING,
	timestamp TIMESTAMP
);