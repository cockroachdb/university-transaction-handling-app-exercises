--
-- findItemsByDescription
--

SELECT id, name, description, quantity, price
  FROM items 
  WHERE name = :name
  AND quantity > 0;