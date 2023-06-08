--
-- deleteItem
--

DELETE FROM items
  WHERE item_id = :item_id
;
