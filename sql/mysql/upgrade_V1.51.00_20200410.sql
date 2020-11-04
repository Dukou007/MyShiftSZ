-- Add terminalLog column --
ALTER TABLE TMSTTRM_LOG ADD COLUMN DEVICE_NAME varchar(32) comment 'device name' AFTER TRM_ID;

insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(65,'tms:terminal log:add','Add Terminal log','desc','TMS','Terminal log', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(66,'tms:terminal log:level','set Terminal log level','desc','TMS','Terminal log', 1);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (172,1,65);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (173,2,65);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (174,3,65);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (175,4,65);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (176,1,66);

INSERT INTO pubtsysconf(CATEGORY, PARA_KEY, PARA_VALUE) VALUES ('logSetting', 'TerminalLogLevel', 'ALL');
