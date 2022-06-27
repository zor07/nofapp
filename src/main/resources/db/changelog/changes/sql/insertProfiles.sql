BEGIN;
insert into profile (user_id)
select *
from (select u.id
      from "user" u) as userIds;
COMMIT;