--
-- findItemsByDescription
--

SELECT id, description, quantity
  FROM items 
  WHERE description = :description
  AND quantity > 0;