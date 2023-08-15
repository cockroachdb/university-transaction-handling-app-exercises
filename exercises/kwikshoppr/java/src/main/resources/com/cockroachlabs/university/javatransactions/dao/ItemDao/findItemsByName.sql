--
-- findItemsByDescription
--

SELECT item_id, name, description, quantity, price
  FROM items 
  WHERE name = :name
  AND quantity > 0;