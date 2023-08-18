--
-- findById
--
select
  item_id,
  name,
  description,
  quantity,
  price
from 
  items
where
  item_id = :item_id
;  
