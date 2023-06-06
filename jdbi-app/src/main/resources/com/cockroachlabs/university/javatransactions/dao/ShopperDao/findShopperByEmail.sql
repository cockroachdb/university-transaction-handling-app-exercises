--
-- findShopperByEmail
--
SELECT (
  email,
  name,
  address)
FROM shoppers
 WHERE email = :email;
