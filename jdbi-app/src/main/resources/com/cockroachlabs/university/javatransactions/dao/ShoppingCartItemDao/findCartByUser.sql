--
-- findCartByUser.sql
--
select
  cart_id,
  user_email,
  purchased_at
from 
  cart
where
  user_email = :user_email
;  