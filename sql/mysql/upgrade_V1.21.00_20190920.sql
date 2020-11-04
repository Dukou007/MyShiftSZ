-- Sync Profile To Terminals --
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(59,'tms:group:sync','Sync Profile To Terminals','desc','TMS','Group', 1);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (158,1,59);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (159,2,59);
