--
-- Insert
--
insert into cart(user_email) values (
 :user_email
 )
 RETURNING
 id;