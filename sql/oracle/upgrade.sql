-- ----------------------------------------------------------------------- --
-- upgrade from version V1.00.00 to version V1.00.05, datatime: 02/08/2018 --
-- ----------------------------------------------------------------------- --
-- admin2 --
insert into PUBTUSER( USER_ID,USERNAME,USER_DESC ,IS_SYS, 
USER_PWD ,ENCRYPT_SALT,ENCRYPT_ALG ,ENCRYPT_ITERATION_COUNT ,LAST_PWD_CHG_DATE ,MAX_PWD_AGE,FORCE_CHG_PWD ,FAILED_LOGIN_COUNT ,
PREF_DASHBOARD_REAL ,PREF_DASHBOARD_USAGE ,CREATOR ,CREATE_DATE,MODIFIER ,MODIFY_DATE )
values(2,'admin2','system admin2','1',
'16e6630cfbdd9bac70e959f05400d26aa2dc4349635470f29afc75d0aeba1feb08ab0cdf0578f3ded737cfb35ed0f9b516a7ea8f2e569be8d18aef4c7425df91',
'e95b1c844a2c535122d99112b4bdc0f5','SHA-512','332',sysdate,'0','0','0',
'Tampers:1,Offline:1,Privacy Shield:1,Stylus:1,Downloads:1,Activations:1',
'Download History:1,Activation History:1,MSR Read:1,Contact IC Read:1,PIN Encryption Failure:1,Signature:1,Contactless IC Read:1,Transactions:1,Power-cycles:1',
'system',sysdate,'system',sysdate);
insert into PUBTPWDRESETTOKEN (USER_ID,MAX_TOKEN_AGE,IS_TOKEN_USED) values (2,0,0);
insert into PUBTUSER_GROUP (REL_ID,USER_ID,GROUP_ID,IS_DEFAULT) values (2, 2, 1, '1');
insert into PUBTUSER_ROLE (REL_ID,USER_ID,ROLE_ID) values (2, 2, 1);
-- Q30 --
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('Q30', 'Q30', '1', null, 'Q30', 'system', sysdate, 'system', sysdate);
commit;



-- ----------------------------------------------------------------------- --
-- upgrade from version VV1.00.05 to version V1.00.06, datatime:           --
-- ----------------------------------------------------------------------- --
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('Px7A', 'Px7A', '1', null, 'Px7A', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('A920', 'A920', '1', null, 'A920', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('Aries8', 'Aries8', '1', null, 'Aries8', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('A60', 'A60', '1', null, 'A60', 'system', sysdate, 'system', sysdate);

commit;

-- ----------------------------------------------------------------------- --
-- upgrade from version V1.01.00 to version V1.02.00, datatime: 10/30/2018 --
-- ----------------------------------------------------------------------- --
ALTER TABLE "TMSTTERMINAL"
ADD ("INSTALL_APPS" VARCHAR2(2000) );

COMMENT ON COLUMN "TMSTTERMINAL"."INSTALL_APPS" IS 'install apps';


-- ----------------------------------------------------------------------- --
-- upgrade from version V1.02.00 to version V1.03.00, datatime: 25/12/2018 --
-- ----------------------------------------------------------------------- --


create table APP_CLIENT 
(
   APP_CLIENT_ID        INTEGER              not null,
   USER_ID              INTEGER,
   USER_NAME            VARCHAR2(26),
   APP_KEY              VARCHAR2(128),
   UPDATED_ON           TIMESTAMP,
   constraint PK_APP_CLIENT primary key (APP_CLIENT_ID)
);

comment on table APP_CLIENT is
'app_client';

comment on column APP_CLIENT.APP_CLIENT_ID is
'app client id  primary key';

/*==============================================================*/
/* Index: UNI_APPCLINET_ON_USER_NAME                            */
/*==============================================================*/
create index UNI_APPCLINET_ON_USER_NAME on APP_CLIENT (
   USER_NAME ASC
);

/*==============================================================*/
/* Index: IDX_APPCLINET_ON_APP_KEY                              */
/*==============================================================*/
create index IDX_APPCLINET_ON_APP_KEY on APP_CLIENT (
   APP_KEY ASC
);
ALTER TABLE "TMSTTERMINAL"
ADD ("REPORT_TIME" DATE);

COMMENT ON COLUMN "TMSTTERMINAL"."REPORT_TIME" IS 'report time';
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(56,'tms:app client:get','Get App Client','desc','TMS','App Client', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(57,'tms:app client:refresh','Refresh App Client','desc','TMS','App Client', 1);
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(58,'tms:app client:remove','Remove App Client','desc','TMS','App Client', 1);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (155,1,56);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (156,1,57);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (157,1,58);

