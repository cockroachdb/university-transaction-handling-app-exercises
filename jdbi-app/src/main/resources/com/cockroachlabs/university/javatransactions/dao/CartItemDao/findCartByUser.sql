--
-- findCartByUser.sql
--
select
  id,
  username,
  item, 
  quantity
from 
  cart
where
  username = :username
;  