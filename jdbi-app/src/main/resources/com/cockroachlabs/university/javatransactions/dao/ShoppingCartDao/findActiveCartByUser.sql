--
-- findActiveCartByUser
--
SELECT (id, cart_id, user_email, purchased_at)
  FROM shopping_carts
 WHERE user_email = :user_email AND purchased_at IS NULL;