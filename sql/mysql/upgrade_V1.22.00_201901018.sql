-- Event list report --
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(60,'tms:event:export','Event List Report Export','desc','TMS','Report', 1);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (160,1,60);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (161,2,60);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (162,3,60);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (163,4,60);