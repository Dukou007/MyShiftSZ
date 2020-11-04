-- terminal log --
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(63,'tms:terminal log:delete','Delete Terminal log','desc','TMS','Terminal log', 1);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (168,1,63);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (169,2,63);

-- Add PKG_NOTES column --
ALTER TABLE tmstpackage ADD COLUMN PKG_SIGNED int comment 'package signed' AFTER MODIFY_DATE;

-- Add new column 'LOCALTIME' --
ALTER TABLE tmstterminal ADD COLUMN `LOCAL_TIME` VARCHAR(36) NULL COMMENT 'terminal local time utc' AFTER `REPORT_TIME`;

-- Add new authority --
INSERT INTO PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) VALUES 
(64,'tms:report:billing','View Terminal Not Billing','desc','TMS','Report', 1);
INSERT INTO PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) VALUES (170,1,64);
INSERT INTO PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) VALUES (171,2,64);


create index IDX_TRMID_ON_TMSTTRM_LOG on TMSTTRM_LOG
(
   TRM_ID
);

-- Create TMSTTRMSTATUS_TEMP --
CREATE TABLE TMSTTRMSTATUS_TEMP
(
   TRM_ID               VARCHAR(36) NOT NULL COMMENT 'terminal id',
   TRM_SN               VARCHAR(36) NOT NULL COMMENT 'terminal sn',
   MODEL_ID             VARCHAR(36) COMMENT 'terminal model identifier',
   LAST_CONN_TIME       DATETIME COMMENT 'last connection time',
   LAST_DWNL_TIME       DATETIME COMMENT 'last downloaded (finished) time',
   LAST_DWNL_STATUS     VARCHAR(20) COMMENT 'last download status',
   LAST_DWNL_TASK       NUMERIC(38,0) COMMENT 'last downloaded deployment id',
   LAST_ACTV_TIME       DATETIME COMMENT 'last activation/update time',
   LAST_ACTV_STATUS     VARCHAR(20) COMMENT 'last activation/update status',
   LAST_SOURCE_IP       VARCHAR(38) COMMENT 'last source ip',
   IS_ONLINE            INT COMMENT 'online/offline status',
   TAMPER               VARCHAR(36) COMMENT 'tamper reason',
   PRIVACY_SHIELD       INT COMMENT 'privcy shield status, 1-nomal 2-removed, 3 - unknow',
   STYLUS               INT COMMENT 'stylus status, 1-nomal 2-removed, 3 - unknow',
   SRED               	INT COMMENT 'SRED, 1-Encrypted、0-Not Encrypted、other-Unavailable',
   ONLINE_SINCE         DATETIME,
   OFFLINE_SINCE        DATETIME,
   PRIMARY KEY (TRM_ID)
);

-- Create tmsttrm_billing --
CREATE TABLE tmsttrm_billing
(
   BILLING_ID NUMERIC(38,0) NOT NULL COMMENT 'billing id',
   BILLING_GROUP_ID NUMERIC(38,0) NOT NULL COMMENT 'group id',
   BILLING_MONTH VARCHAR(36) NULL COMMENT 'billing month',
   BILLING_CONNECTED_DEVICES NUMERIC(38,0) NULL DEFAULT 0 COMMENT 'billing connected devices',
   BILLING_STATEMENT VARCHAR(36) NULL DEFAULT 'Download' COMMENT 'billing statement default Download',
   BILLING_CREATE_TIME DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
   PRIMARY KEY (BILLING_ID)
);
INSERT INTO PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) VALUES ('`tmsttrm_billing`', 1);

-- Create tmsttrm_billing_detail --
CREATE TABLE tmsttrm_billing_detail
(
   BILLING_DETAIL_ID NUMERIC(38,0) NOT NULL COMMENT 'billing detail id',
   BILLING_DETAIL_GROUP_ID NUMERIC(38,0) NOT NULL COMMENT 'group id',
   BILLING_DETAIL_GROUP_NAME VARCHAR(60) NOT NULL COMMENT 'group name',
   BILLING_DETAIL_MONTH VARCHAR(36) NULL COMMENT 'billing month',
   BILLING_DETAIL_TRM_SN VARCHAR(36) NULL COMMENT 'terminal sn',
   BILLING_DETAIL_TRM_TYPE VARCHAR(36) NULL COMMENT 'terminal type',
   BILLING_DETAIL_LAST_ACCESS_TIME DATETIME NULL COMMENT 'last accessed time',
   BILLING_DETAIL_CREATE_TIME DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
   PRIMARY KEY (BILLING_DETAIL_ID)
);
INSERT INTO PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) VALUES ('TMSTTRM_BILLING_DETAIL_ID', 1);
