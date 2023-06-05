--
-- Insert Shopping Cart
--
--
INSERT INTO shopping_carts (user_email, total) VALUES (
  :shopper_email,
  0
) RETURNING cart_id;
