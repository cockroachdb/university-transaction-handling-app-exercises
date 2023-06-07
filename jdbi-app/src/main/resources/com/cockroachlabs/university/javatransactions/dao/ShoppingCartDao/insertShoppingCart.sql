--
-- insertShoppingCart
--
--
INSERT INTO shopping_carts (user_email) VALUES (
  :user_email
) RETURNING cart_id;
