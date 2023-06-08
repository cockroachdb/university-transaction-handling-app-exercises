--
-- deleteCart
--

DELETE FROM shopping_carts
      WHERE cart_id = :cart_id
;
