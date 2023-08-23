-- Possible CTE solution
WITH UpdatedStock AS (
	SELECT
    	p.product_id,
    	p.product_name,
    	(p.stock_quantity - COALESCE(SUM(oi.quantity), 0)) as new_stock_quantity
	FROM products p
	LEFT JOIN order_items oi ON p.product_id = oi.product_id
	GROUP BY p.product_id, p.stock_quantity
)

UPDATE products
SET stock_quantity = us.new_stock_quantity
FROM UpdatedStock us
WHERE products.product_id = us.product_id;
