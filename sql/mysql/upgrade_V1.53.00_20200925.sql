insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values (73,'tms:config:view','view ppmConfiguration','desc','TMS','Configuration', 1);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (200,1,73);

UPDATE pubtgroup SET NAME_PATH=CONCAT('Entities/',NAME_PATH) WHERE NAME_PATH NOT LIKE 'Entities%';