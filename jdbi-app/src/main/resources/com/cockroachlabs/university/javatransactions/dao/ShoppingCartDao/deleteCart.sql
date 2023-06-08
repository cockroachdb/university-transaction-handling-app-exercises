--
-- deleteCart
--

DELETE FROM carts
      WHERE cart_id = :cart_id
;
