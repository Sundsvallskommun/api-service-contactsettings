
alter table if exists contact_setting
   add column municipality_id varchar(255) AFTER id;
   
create index contact_setting_municipality_id_index
   on contact_setting (municipality_id);