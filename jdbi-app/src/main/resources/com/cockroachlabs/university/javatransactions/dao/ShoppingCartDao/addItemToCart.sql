--
-- insert
--

INSERT INTO cart_items (
  cart_id,
  item_id,
  quantity
) VALUES (
  :cart_id,
  :item_id,
  :quantity
) RETURNING id;
