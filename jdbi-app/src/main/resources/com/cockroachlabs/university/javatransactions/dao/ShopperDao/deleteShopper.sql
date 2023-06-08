--
-- deleteCart
--

DELETE FROM shoppers
      WHERE email = :email
;
