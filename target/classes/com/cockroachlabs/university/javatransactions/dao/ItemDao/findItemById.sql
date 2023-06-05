--
-- findById
--
select
  id,
  name,
  description
from 
  item
where
  id = :id
;  
