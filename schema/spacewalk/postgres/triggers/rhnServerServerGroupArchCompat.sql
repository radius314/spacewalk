-- oracle equivalent source sha1 7d2f626814388899c877451ac47f4a2f1b242f63

create or replace function rhn_ssg_ac_mod_trig_fun() returns trigger as
$$
begin
	new.modified := current_timestamp;
 	return new;
end;
$$ language plpgsql;

create trigger
rhn_ssg_ac_mod_trig
before insert or update on rhnServerServerGroupArchCompat
for each row
execute procedure rhn_ssg_ac_mod_trig_fun();



