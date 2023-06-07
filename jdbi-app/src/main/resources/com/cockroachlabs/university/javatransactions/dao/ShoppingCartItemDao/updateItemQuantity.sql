--
-- updateItemQuantity
--

UPDATE items
   SET quantity = quantity - :amount
 WHERE item_id = :id
   AND quantity >= :amount;
