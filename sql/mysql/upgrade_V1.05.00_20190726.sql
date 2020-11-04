alter table TMSTTRMSTATUS add SRED int comment 'SRED, 1-Encrypted、0-Not Encrypted、other-Unavailable' AFTER `STYLUS`;


update pubtuser set PREF_DASHBOARD_REAL = concat(PREF_DASHBOARD_REAL, ',SRED:1') where PREF_DASHBOARD_REAL not LIKE '%,SRED:1%';

/*更新tmstalert_condition表中SRED*/
ALTER TABLE tmstalert_condition CHANGE COND_ID COND_ID INT NOT NULL AUTO_INCREMENT;

INSERT INTO tmstalert_condition(`ALERT_ITEM`, `SETTING_ID`, `ALERT_SEVERITY`, `ALERT_THRESHOLD`, `ALERT_MESSAGE`, `TOPIC_ARN`, `CREATOR`, `CREATE_DATE`, `MODIFIER`, `MODIFY_DATE`) 
SELECT 'SRED' AS ALERT_ITEM,SETTING_ID AS SETTING_ID, '0' AS ALERT_SEVERITY, '10' AS ALERT_THRESHOLD, 'Group ?0 SRED not encrypted exceeds the ?1%' AS ALERT_MESSAGE, NULL AS TOPIC_ARN, 'admin' AS CREATOR, NOW() AS CREATE_DATE, 'admin' AS MODIFIER, NOW() AS MODIFY_DATE FROM tmstalert_setting;

ALTER TABLE tmstalert_condition CHANGE COND_ID COND_ID DECIMAL(38,0) NOT NULL ;

UPDATE pubtsequence SET NEXT_VALUE=(SELECT MAX(COND_ID)+1 FROM tmstalert_condition)
WHERE SEQ_NAME='TMSTALERT_CONDITION_SEQ';

/*更新user表字段注释*/
ALTER TABLE pubtuser MODIFY COLUMN IS_LOCKED SMALLINT NOT NULL DEFAULT 0 COMMENT 'a boolean flag indicating whether the user acount is locked. An account can be locked automatically based on the Account Lockout Policy.1:Locked,0:Unlocked.';

ALTER TABLE pubtuser MODIFY COLUMN IS_ENABLED SMALLINT NOT NULL DEFAULT 0 COMMENT 'a boolean flag indicating whether the user acount is disabled. An account can be disabled provisionally for security and can be re-enabled upon need.IS_LOCKED means that after the user has logged in the password for six times, the user will automatically lock and cannot log in again. Site administrators can reactivate users through user management.IS_ENABLED means that the site administrator can manually activate and deactivate users through user management;1:enable,0:disable.';