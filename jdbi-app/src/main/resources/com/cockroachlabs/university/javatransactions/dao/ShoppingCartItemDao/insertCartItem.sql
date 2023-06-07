--
-- Insert
--
INSERT INTO cart_items (
    cart_id,
    item_id,
    quantity
) values (
 :cart_id,
 :item_id,
 :quantity
 )
 RETURNING id;