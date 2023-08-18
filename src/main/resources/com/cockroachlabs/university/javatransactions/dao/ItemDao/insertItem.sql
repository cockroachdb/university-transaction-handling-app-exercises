--
-- Insert
--
INSERT INTO items(
    name,
    description,
    quantity,
    price
) values (
 :name,
 :description,
 :quantity,
 :price
 )
 RETURNING item_id;