--
-- updateItemQuantity
--

UPDATE items
   SET quantity = quantity - :amount
 WHERE item_id = :item_id
  AND quantity >= :amount
;
