-- Set the ID initial value of the province table --
UPDATE pubtsequence SET NEXT_VALUE=200 WHERE SEQ_NAME='PUBTPROVINCE_ID';

-- Add PKG_NOTES column --
ALTER TABLE tmstpackage ADD COLUMN PKG_NOTES VARCHAR(200) NULL COMMENT 'package notes' DEFAULT NULL AFTER PARAM_SET;

-- Add permission about assign terminals --
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(61,'tms:unrterminal:assign','Assign Unregistered Terminal','desc','TMS','Terminal', 1);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (164,1,61);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (165,2,61);
-- Update the default order of all users' Real-Time --
UPDATE pubtuser SET PREF_DASHBOARD_REAL='Offline:1,Tampers:1,SRED:1,Downloads:1,Activations:1,Privacy Shield:1,Stylus:0';