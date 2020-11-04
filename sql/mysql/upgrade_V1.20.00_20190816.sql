alter table TMSTTRM_REPORT_MSG add GROUP_ID numeric(38,0) not null comment 'group id' AFTER `TRM_ID`;
alter table TMSTTRM_REPORT_MSG add DOWN_PENDING numeric(8,0) comment 'Pending Downloads' AFTER `DOWN_FAILS`;
alter table TMSTTRM_REPORT_MSG add ACTV_PENDING numeric(8,0) comment 'Pending Activations' AFTER `ACTV_FAILS`;
alter table TMSTTRM_REPORT_MSG modify column REPORT_TM  varchar(32) not null comment 'health report time(group time)'; 

alter table TMSTTRM_USAGE_MSG add GROUP_ID numeric(38,0) not null comment 'group id' AFTER `TRM_ID`;
alter table TMSTTRM_USAGE_MSG add ITEM_PENDING numeric(8,0) comment 'Pending'  AFTER `ITEM_ERRS`;
alter table TMSTTRM_USAGE_MSG modify column START_TIME  varchar(32); 
alter table TMSTTRM_USAGE_MSG modify column END_TIME  varchar(32); 

alter table TMSTGROUP_USAGE_STS add CYCLE_DATE varchar(10) AFTER `END_TIME`;
alter table TMSTGROUP_USAGE_STS modify column START_TIME  varchar(32); 
alter table TMSTGROUP_USAGE_STS modify column END_TIME  varchar(32); 
