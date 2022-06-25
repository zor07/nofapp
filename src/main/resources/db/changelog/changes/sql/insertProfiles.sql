BEGIN;
insert into profile (id)
select *
from (select u.id
      from "user" u) as userIds;
COMMIT;