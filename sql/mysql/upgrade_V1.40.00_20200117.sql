DROP TABLE IF EXISTS TMSTTRM_LOG;
CREATE TABLE TMSTTRM_LOG (
	ID                   numeric(38,0) not null comment ' id',
	TRM_ID               varchar(36) comment 'terminal sn',
	DEVICE_TYPE			 varchar(32) comment 'terminal sn',
	SOURCE               varchar(50) not null comment 'source',
	SOURCE_VERSION       varchar(36) not null comment 'source version',
	SOURCE_TYPE          varchar(36) comment 'Source type',
	SEVERITY             varchar(16) not null comment 'severity',
	MESSAGE              text not null comment 'log message',
	EVENT_LOCAL_TIME     varchar(24) not null comment 'event time',
	COMM_TYPE     		 varchar(16) comment 'comm type',
	LOCAL_IP     		 varchar(16) comment 'local ip',
	APP_INFO     		 text comment 'app info',
	CREATE_DATE          datetime comment 'created date',
	primary key (ID)
);
alter table TMSTTRM_LOG comment 'terminal event message';

-- terminal log --
insert into PUBTAUTHORITY (AUTH_ID,AUTH_CODE,AUTH_NAME,AUTH_DESC,APP_NAME,MODULE_NAME,SORT_ORDER) values 
(62,'tms:terminal log:view','Get Terminal log','desc','TMS','Terminal log', 1);

insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (166,1,62);
insert into PUBTROLE_AUTHORITY (REL_ID,ROLE_ID,AUTH_ID) values (167,2,62);

alter table tmstterminal modify column  INSTALL_APPS text;  