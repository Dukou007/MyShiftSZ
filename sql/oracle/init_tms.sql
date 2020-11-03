-- group --
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTGROUP_ID', 1000);

insert into PUBTGROUP (GROUP_ID,GROUP_CODE,GROUP_NAME,DAYLIGHT_SAVING,DESCRIPTION,ID_PATH,NAME_PATH,TREE_DEPTH,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(1, '1', 'Entities','0', null, '1', null, 0, 'system', sysdate, 'system',sysdate);
insert into PUBTGROUP_PARENTS (GROUP_ID,PARENT_ID) values (1, 1);

-- authority --
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTAUTHORITY_ID', 10000);

-- role --
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTROLE_ID', 1000);
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTROLE_AUTHORITY_ID', 10000);

insert into  PUBTROLE (ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(1, 'Site Administrator', 'Site Administrator', 'TMS', '1', 'system', sysdate, 'system', sysdate);
insert into  PUBTROLE (ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(2, 'Administrator', 'Administrator', 'TMS', '1', 'system', sysdate, 'system', sysdate);
insert into  PUBTROLE (ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(3, 'Supervisor', 'Supervisor', 'TMS','1', 'system', sysdate, 'system', sysdate);
insert into  PUBTROLE (ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(4, 'Operator', 'Operator', 'TMS', '1', 'system', sysdate, 'system', sysdate);
insert into  PUBTROLE (ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(5, 'Installer', 'Installer', 'TMS', '1','system', sysdate, 'system', sysdate);

insert into  PUBTROLE(ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values 
(6, 'Administrator','role_Administrator','PxDesigner', '1','system',sysdate,'system',sysdate);
insert into  PUBTROLE(ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values
(7, 'Developer','role_Developer','PxDesigner', '1','system',sysdate,'system',sysdate);
insert into  PUBTROLE(ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values
(8, 'Deployer', 'role_Deployer','PxDesigner', '1','system',sysdate,'system',sysdate);
insert into  PUBTROLE(ROLE_ID,ROLE_NAME,ROLE_DESC,APP_NAME,IS_SYS,CREATOR,CREATE_DATE,MODIFIER,MODIFY_DATE) values
(9, 'Signer','role_Signer','PxDesigner','1','system',sysdate,'system',sysdate);




-- user --
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTUSER_ID', 1000);
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTUSER_GROUP_ID', 1000);
insert into PUBTSEQUENCE (SEQ_NAME,NEXT_VALUE) values ('PUBTUSER_ROLE_ID', 1000);

-- admin1 --
insert into PUBTUSER( USER_ID,USERNAME,USER_DESC ,IS_SYS, 
USER_PWD ,ENCRYPT_SALT,ENCRYPT_ALG ,ENCRYPT_ITERATION_COUNT ,LAST_PWD_CHG_DATE ,MAX_PWD_AGE,FORCE_CHG_PWD ,FAILED_LOGIN_COUNT ,
PREF_DASHBOARD_REAL ,PREF_DASHBOARD_USAGE ,CREATOR ,CREATE_DATE,MODIFIER ,MODIFY_DATE )
values(1,'admin','system admin1','1',
'0c1126bdfa28148cd433cf220f884da23372f43cf3fb2a408e876999fec3ba320ea0443bcb4c45e9621f67a9f3fca8ad6eab0a7849caeae3d2ae52e69dfd3689',
'25389240a6e1605d095a87b7e7e3bd7c','SHA-512','691',sysdate,'0','0','0',
'Tampers:1,Offline:1,Privacy Shield:1,Stylus:1,Downloads:1,Activations:1',
'Download History:1,Activation History:1,MSR Read:1,Contact IC Read:1,PIN Encryption Failure:1,Signature:1,Contactless IC Read:1,Transactions:1,Power-cycles:1',
'system',sysdate,'system',sysdate);
insert into PUBTPWDRESETTOKEN (USER_ID,MAX_TOKEN_AGE,IS_TOKEN_USED) values(1,0,0);
insert into PUBTUSER_GROUP (REL_ID,USER_ID,GROUP_ID,IS_DEFAULT) values (1, 1, 1, '1');
insert into PUBTUSER_ROLE (REL_ID,USER_ID,ROLE_ID) values (1, 1, 1);
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

-- mfr --
INSERT INTO TMSTMFR VALUES ('1', 'pax', 'pax', 'system', sysdate, 'system', sysdate);

-- model  --
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('D210', 'D210', '1', null, 'D210', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('D220', 'D220', '1', null, 'D220', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('D210s', 'D210s', '1', null, 'D210s', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('MT30', 'MT30', '1', null, 'MT30', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('MT30s', 'MT30s', '1', null, 'MT30s', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('Px5', 'Px5', '1', null, 'Px5', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('Px7', 'Px7', '1', null, 'Px7', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('PX7L', 'PX7L', '1', null, 'PX7L', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('S300', 'S300', '1', null, 'S300', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('S58', 'S58', '1', null, 'S58', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('S80', 'S80', '1', null, 'S80', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('S90', 'S90', '1', null, 'S90', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('S920', 'S920', '1', null, 'S920', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('SP20', 'SP20', '1', null, 'SP20', 'system', sysdate, 'system', sysdate);
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('SP30', 'SP30', '1', null, 'SP30', 'system', sysdate, 'system', sysdate);
-- Q30 --
INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE) 
VALUES ('Q30', 'Q30', '1', null, 'Q30', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('Px7A', 'Px7A', '1', null, 'Px7A', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('A920', 'A920', '1', null, 'A920', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('Aries8', 'Aries8', '1', null, 'Aries8', 'system', sysdate, 'system', sysdate);

INSERT INTO TMSTMODEL (MODEL_ID, MODEL_NAME, MFR_ID, PLATFORM, MODEL_DESC, CREATOR, CREATE_DATE, MODIFIER, MODIFY_DATE)
VALUES ('A60', 'A60', '1', null, 'A60', 'system', sysdate, 'system', sysdate);
commit;



