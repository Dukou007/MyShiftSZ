ALTER TABLE TMSTPACKAGE ADD COLUMN TRM_SN varchar(36) comment 'tminal serial number' AFTER MODEL_ID;

ALTER TABLE TMSTDEPLOY MODIFY COLUMN PKG_ID numeric(38,0) comment 'refer to a package';
ALTER TABLE TMSTDEPLOY ADD COLUMN LATEST_TYPE int comment 'group deploy offlinekey latest type 0:Lastest Upload Version 1:Highest Version';
ALTER TABLE TMSTDEPLOY ADD COLUMN DEPLOY_TYPE int comment 'DEPLOY_TYPE 0:OfflineKey 1:Package';

ALTER TABLE TMSTTERMINAL ADD COLUMN SYSMETRIC_KEYS text comment 'sysmetricKeys' AFTER REPORT_TIME;
ALTER TABLE TMSTTERMINAL ADD COLUMN KEY_REPORT_TIME datetime comment 'terminal send sysmetricKeys report time' AFTER SYSMETRIC_KEYS;

ALTER TABLE TMSTTRMSTATUS ADD COLUMN RKI int comment 'Indicate whether the terminal supports RKI,1: Yes (support), 0: No (not support)' AFTER SRED;
ALTER TABLE TMSTTRMSTATUS_TEMP ADD COLUMN RKI int comment 'Indicate whether the terminal supports RKI,1: Yes (support), 0: No (not support)' AFTER SRED;

ALTER TABLE TMSTIMPORTFILE ADD COLUMN TRM_SN varchar(36) comment 'tminal serial number' AFTER FILE_TYPE;

insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(67,'tms:offlinekey:view','View Offline key','desc','TMS','Offline key', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(68,'tms:offlinekey:import','Import Offline key','desc','TMS','Offline key', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(69,'tms:offlinekey:edit','Edit Offline key','desc','TMS','Offline key', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(70,'tms:offlinekey:remove','Remove Offline key','desc','TMS','Offline key', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(71,'tms:offlinekey:assign','Assign Offline key','desc','TMS','Offline key', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(72,'tms:offlinekey:delete','Delete Offline key','desc','TMS','Offline key', 1);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (177,1,67);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (178,1,68);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (179,1,69);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (180,1,70);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (181,1,71);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (182,1,72);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (183,2,67);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (184,2,68);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (185,2,69);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (186,2,70);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (187,2,71);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (188,2,72);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (189,3,67);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (190,3,68);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (191,3,71);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (192,4,67);


update PUBTGROUP set NAME_PATH = GROUP_NAME where GROUP_ID=1;

/*更新tmstalert_condition表中COND_ID为自增字段*/
ALTER TABLE tmstalert_condition CHANGE COND_ID COND_ID INT NOT NULL AUTO_INCREMENT;
/*tmstalert_condition表中新增RKI数据*/
INSERT INTO tmstalert_condition(`ALERT_ITEM`, `SETTING_ID`, `ALERT_SEVERITY`, `ALERT_THRESHOLD`, `ALERT_MESSAGE`, `TOPIC_ARN`, `CREATOR`, `CREATE_DATE`, `MODIFIER`, `MODIFY_DATE`) 
SELECT 'RKI' AS ALERT_ITEM,SETTING_ID AS SETTING_ID, '0' AS ALERT_SEVERITY, '10' AS ALERT_THRESHOLD, 'Group ?0 RKI not support exceeds the ?1%' AS ALERT_MESSAGE, NULL AS TOPIC_ARN, 'admin' AS CREATOR, NOW() AS CREATE_DATE, 'admin' AS MODIFIER, NOW() AS MODIFY_DATE FROM tmstalert_setting;
/*还原tmstalert_condition表中COND_ID字段的更新*/
ALTER TABLE tmstalert_condition CHANGE COND_ID COND_ID DECIMAL(38,0) NOT NULL ;

/*real-time加上RKICapable*/
update pubtuser set PREF_DASHBOARD_REAL = concat(PREF_DASHBOARD_REAL, ',RKI:0') where PREF_DASHBOARD_REAL not LIKE '%,RKI:0%';
