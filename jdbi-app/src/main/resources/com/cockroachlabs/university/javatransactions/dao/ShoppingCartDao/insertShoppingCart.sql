--
-- Insert Shopping Cart
--
--
INSERT INTO shopping_carts (user_email) VALUES (
  :shopper_email
) RETURNING cart_id;
