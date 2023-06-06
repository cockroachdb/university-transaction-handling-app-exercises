--
-- insertShoppingCart
--
--
INSERT INTO shopping_carts (user_email) VALUES (
  :shopper_email
) RETURNING cart_id;
