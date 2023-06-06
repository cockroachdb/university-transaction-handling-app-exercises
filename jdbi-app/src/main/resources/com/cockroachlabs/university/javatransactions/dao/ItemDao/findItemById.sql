--
-- findById
--
select
  id,
  name,
  description,
  quantity,
  price
from 
  items
where
  id = :id
;  
