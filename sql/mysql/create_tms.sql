/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2018/10/16 15:49:13                          */
/*==============================================================*/


/*==============================================================*/
/* Table: ACLTTRM_GROUP                                         */
/*==============================================================*/
create table ACLTTRM_GROUP
(
   TRM_ID               varchar(36) not null comment 'refer to a terminal',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   primary key (TRM_ID, GROUP_ID)
);

alter table ACLTTRM_GROUP comment 'Terminal-Group access contol list';

/*==============================================================*/
/* Table: ACLTUSER_GROUP                                        */
/*==============================================================*/
create table ACLTUSER_GROUP
(
   USER_ID              int not null comment 'user id',
   GROUP_ID             int not null comment 'group id',
   primary key (USER_ID, GROUP_ID)
);

alter table ACLTUSER_GROUP comment 'access control list of group, specifies which users are gran';

/*==============================================================*/
/* Table: PUBTAUDITLOG                                          */
/*==============================================================*/
create table PUBTAUDITLOG
(
   LOG_ID               numeric(38,0) not null comment 'audit log id',
   USERNAME             varchar(36) not null comment 'username',
   ROLE                 varchar(256) comment 'role',
   USER_ID              numeric(38,0) comment 'user id',
   CILENT_IP            varchar(38) comment 'client ip',
   ACTION_NAME          varchar(2000) comment 'operation name',
   ACTION_DATE          datetime comment 'action date',
   primary key (LOG_ID)
);

alter table PUBTAUDITLOG comment 'audit logs';

/*==============================================================*/
/* Index: IX_PUBTAUDITLOG_1                                     */
/*==============================================================*/
create index IX_PUBTAUDITLOG_1 on PUBTAUDITLOG
(
   ACTION_DATE
);

/*==============================================================*/
/* Table: PUBTAUTHORITY                                         */
/*==============================================================*/
create table PUBTAUTHORITY
(
   AUTH_ID              int not null comment 'authority id',
   AUTH_CODE            varchar(60) not null comment 'authority code',
   AUTH_NAME            varchar(120) not null comment 'authority name',
   AUTH_DESC            varchar(255) comment 'description',
   APP_NAME             varchar(60) comment 'the application for which this authority is to be defined',
   MODULE_NAME          varchar(60) comment 'the module in the application for which this authority is to be defined',
   SORT_ORDER           int not null default 0 comment 'the sort field that used in a query',
   primary key (AUTH_ID)
);

alter table PUBTAUTHORITY comment 'All user permissions defined in the system';

/*==============================================================*/
/* Index: UNI_PUBTAUTHORITY_ON_CODE                             */
/*==============================================================*/
create unique index UNI_PUBTAUTHORITY_ON_CODE on PUBTAUTHORITY
(
   AUTH_CODE
);

/*==============================================================*/
/* Table: PUBTCITY                                              */
/*==============================================================*/
create table PUBTCITY
(
   CITY_ID              numeric(38,0) not null,
   PROVINCE_ID          numeric(38,0) not null comment 'state/province id',
   CITY_NAME            varchar(128) not null comment 'city name',
   ABBR_NAME            varchar(36) comment 'city abbr name',
   CITY_DESC            varchar(256) comment 'description',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'created date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modified date',
   primary key (CITY_ID)
);

alter table PUBTCITY comment 'city';

/*==============================================================*/
/* Index: IX_PUBTCITY                                           */
/*==============================================================*/
create unique index IX_PUBTCITY on PUBTCITY
(
   PROVINCE_ID,
   CITY_NAME
);

/*==============================================================*/
/* Table: PUBTCOUNTRY                                           */
/*==============================================================*/
create table PUBTCOUNTRY
(
   COUNTRY_ID           numeric(38,0) not null comment 'country id',
   COUNTRY_NAME         varchar(128) not null comment 'country name',
   COUNTRY_CODE         varchar(10) comment 'country code',
   ABBR_NAME            varchar(36) comment 'country abbr name',
   COUNTRY_DESC         varchar(256) comment 'description',
   TRANS_CCY_CODE       char(4) comment 'Trans Currency code',
   TRANS_CCY_EXP        int comment 'Trans Currency Exp(numerical precision)',
   TRANS_REFER_CCY_CODE char(4) comment 'Trans Refer Currency code',
   TRANS_REFER_CCY_EXP  int comment 'Trans Refer Currency Exp(numerical precision)',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'created date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modified date',
   primary key (COUNTRY_ID)
);

alter table PUBTCOUNTRY comment 'country or region';

/*==============================================================*/
/* Index: IX_PUBTCOUNTRY                                        */
/*==============================================================*/
create unique index IX_PUBTCOUNTRY on PUBTCOUNTRY
(
   COUNTRY_NAME
);

/*==============================================================*/
/* Table: PUBTDICT                                              */
/*==============================================================*/
create table PUBTDICT
(
   ITEM_ID              numeric(38,0) not null comment 'item id',
   ITEM_TYPE            varchar(64) not null comment 'item type',
   ITEM_CODE            varchar(128) not null comment 'item code',
   ITEM_NAME            varchar(128) not null comment 'item name',
   ITEM_ATTR            varchar(256) comment 'the attribute of data item',
   ITEM_DESC            varchar(256) comment 'item description',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'creator date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (ITEM_ID)
);

alter table PUBTDICT comment 'data dictionary';

/*==============================================================*/
/* Index: PUBTDICT_IX1                                          */
/*==============================================================*/
create unique index PUBTDICT_IX1 on PUBTDICT
(
   ITEM_TYPE,
   ITEM_CODE
);

/*==============================================================*/
/* Index: PUBTDICT_IX2                                          */
/*==============================================================*/
create index PUBTDICT_IX2 on PUBTDICT
(
   ITEM_TYPE,
   MODIFY_DATE
);

/*==============================================================*/
/* Table: PUBTGROUP                                             */
/*==============================================================*/
create table PUBTGROUP
(
   GROUP_ID             numeric(38,0) not null comment 'group id',
   GROUP_CODE           varchar(38) not null comment 'group code',
   GROUP_NAME           varchar(60) not null comment 'group name',
   PARENT_ID            numeric(38,0) comment 'refer to parent group',
   COUNTRY              varchar(128) comment 'refer to a country',
   PROVINCE             varchar(128) comment 'refer to a state/province',
   CITY                 varchar(128) comment 'refer to a city',
   ZIP_CODE             varchar(10) comment 'ZIP/POST code',
   TIME_ZONE            varchar(36),
   DAYLIGHT_SAVING      smallint,
   ADDRESS              varchar(256) comment 'address/location',
   DESCRIPTION          varchar(4000) comment 'descrption',
   ID_PATH              varchar(800) comment 'full path of group id, e.g. "1/2/5"',
   NAME_PATH            varchar(2000) comment 'full path of group name, e.g. "pax/group1/group2"',
   TREE_DEPTH           int not null comment 'the depth of a group tree',
   SUB_COUNT            int not null default 0,
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (GROUP_ID)
);

alter table PUBTGROUP comment 'groups';

/*==============================================================*/
/* Index: IX_PUBTGROUP_NAME                                     */
/*==============================================================*/
create unique index IX_PUBTGROUP_NAME on PUBTGROUP
(
   PARENT_ID,
   GROUP_NAME
);

/*==============================================================*/
/* Table: PUBTGROUP_PARENTS                                     */
/*==============================================================*/
create table PUBTGROUP_PARENTS
(
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   PARENT_ID            numeric(38,0) not null comment 'refer to a parent group',
   primary key (GROUP_ID, PARENT_ID)
);

alter table PUBTGROUP_PARENTS comment 'group''s parents';

/*==============================================================*/
/* Index: IX_PUBTGROUP_PARENTS                                  */
/*==============================================================*/
create unique index IX_PUBTGROUP_PARENTS on PUBTGROUP_PARENTS
(
   PARENT_ID,
   GROUP_ID
);

/*==============================================================*/
/* Table: PUBTPROVINCE                                          */
/*==============================================================*/
create table PUBTPROVINCE
(
   PROVINCE_ID          numeric(38,0) not null comment 'state/province id',
   COUNTRY_ID           numeric(38,0) not null comment 'country id',
   PROVINCE_NAME        varchar(128) not null comment 'state/province name',
   PROVINCE_CODE        varchar(10) comment 'state/province code',
   ABBR_NAME            varchar(36) comment 'state/province abbr name',
   PROVINCE_DESC        varchar(256) comment 'description',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'created date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modified date',
   primary key (PROVINCE_ID)
);

alter table PUBTPROVINCE comment 'State/Province';

/*==============================================================*/
/* Index: IX_PUBTPROVINCE                                       */
/*==============================================================*/
create unique index IX_PUBTPROVINCE on PUBTPROVINCE
(
   COUNTRY_ID,
   PROVINCE_NAME
);

/*==============================================================*/
/* Table: PUBTPWDCHGHST                                         */
/*==============================================================*/
create table PUBTPWDCHGHST
(
   HST_ID               int not null comment 'password history id',
   USER_ID              int not null comment 'user id',
   CHANGED_DATE         datetime not null comment 'the password changed date',
   USER_PWD             varchar(128) comment 'the encoded user password',
   ENCRYPT_SALT         varchar(128) comment 'cryptographic salt. A new salt is randomly generated for each password.',
   ENCRYPT_ALG          varchar(16) comment 'the digest algorithm which has been used to encrypt the user password',
   ENCRYPT_ITERATION_COUNT smallint default 0 comment 'The iteration count refers to the number of times that the hash function is applied to its own results.',
   primary key (HST_ID)
);

alter table PUBTPWDCHGHST comment 'password change history of users. 24 passwords should be rem';

/*==============================================================*/
/* Index: UNI_PUBTPWDCHGHST_ON_DATE                             */
/*==============================================================*/
create unique index UNI_PUBTPWDCHGHST_ON_DATE on PUBTPWDCHGHST
(
   USER_ID,
   CHANGED_DATE
);

/*==============================================================*/
/* Table: PUBTPWDRESETTOKEN                                     */
/*==============================================================*/
create table PUBTPWDRESETTOKEN
(
   USER_ID              int not null comment 'user id',
   RESET_PWD_TOKEN      varchar(128) comment 'password reset token, the token itfelf is the 16-character alpha-numeric code.',
   TOKEN_ISSUE_DATE     datetime comment 'the token was assigned to at the time of issue',
   MAX_TOKEN_AGE        int comment 'For security, Password Reset Tokens are time-limited, and will expire after that time, whether or not they have been used',
   IS_TOKEN_USED        smallint comment 'tokens can only be used once',
   primary key (USER_ID)
);

alter table PUBTPWDRESETTOKEN comment 'password reset token, issued to an individual to allow them ';

/*==============================================================*/
/* Table: PUBTROLE                                              */
/*==============================================================*/
create table PUBTROLE
(
   ROLE_ID              int not null comment 'role id',
   ROLE_NAME            varchar(60) not null comment 'role name',
   ROLE_DESC            varchar(255) comment 'description',
   APP_NAME             varchar(60) comment 'the application in which the role is to be defined',
   IS_SYS               smallint not null default 0 comment 'a boolean flag indicating whether the role is a system role',
   CREATOR              varchar(36) not null comment 'the creator',
   CREATE_DATE          datetime not null comment 'the creation date',
   MODIFIER             varchar(36) not null comment 'the modifier',
   MODIFY_DATE          datetime not null comment 'the last modification time',
   primary key (ROLE_ID)
);

alter table PUBTROLE comment 'All roles have been defined in the system';

/*==============================================================*/
/* Table: PUBTROLE_AUTHORITY                                    */
/*==============================================================*/
create table PUBTROLE_AUTHORITY
(
   REL_ID               int not null comment 'the relation id',
   ROLE_ID              int not null comment 'roel id',
   AUTH_ID              int not null comment 'authority id',
   primary key (REL_ID)
);

alter table PUBTROLE_AUTHORITY comment 'The association of a role to each authority';

/*==============================================================*/
/* Index: UNI_PUBRROLE_AUTHORITY                                */
/*==============================================================*/
create unique index UNI_PUBRROLE_AUTHORITY on PUBTROLE_AUTHORITY
(
   ROLE_ID,
   AUTH_ID
);

/*==============================================================*/
/* Table: PUBTSEQUENCE                                          */
/*==============================================================*/
create table PUBTSEQUENCE
(
   SEQ_NAME             varchar(60) not null comment 'sequence name',
   NEXT_VALUE           numeric(38,0) not null default 1 comment 'next value, default 1',
   primary key (SEQ_NAME)
);

alter table PUBTSEQUENCE comment 'sequnences table';

/*==============================================================*/
/* Table: PUBTSYSCONF                                           */
/*==============================================================*/
create table PUBTSYSCONF
(
   CATEGORY             varchar(36) not null comment 'parameter category',
   PARA_KEY             varchar(128) not null comment 'parameter key',
   PARA_VALUE           varchar(4000) comment 'parameter value',
   primary key (CATEGORY, PARA_KEY)
);

alter table PUBTSYSCONF comment 'system config table';

/*==============================================================*/
/* Table: PUBTUSER                                              */
/*==============================================================*/
create table PUBTUSER
(
   USER_ID              int not null comment 'user id',
   USERNAME             varchar(36) not null comment 'user name ',
   FULLNAME             varchar(128) comment 'user full name',
   EMAIL                varchar(128) comment 'user''s email address',
   PHONE                varchar(60) comment 'user''s phone number',
   COUNTRY              varchar(128) comment 'user''s country',
   PROVINCE             varchar(128) comment 'user''s state/province',
   CITY                 varchar(128) comment 'user''s city',
   ZIP_CODE             varchar(10) comment 'user''s zip/post code',
   ADDRESS              varchar(255) comment 'user''s address/location',
   USER_DESC            varchar(255) comment 'user description',
   DIRECTORY            varchar(64) comment 'the directory to find the authenticated user. the valid values: ''LDAP'', ''Local''',
   IS_SYS               smallint not null default 0 comment 'a boolean flag indicating whether the user is a system user. A system user is created while the system initialization and cannot be removed.',
   IS_LDAP              smallint not null default 0 comment 'a boolean flag indicating whether the user is authenticated by LDAP directory',
   IS_LOCKED            smallint not null default 0 comment 'a boolean flag indicating whether the user acount is locked. An account can be locked automatically based on the Account Lockout Policy.1:Locked,0:Unlocked.',
   LOCKED_DATE          datetime comment 'date and time when the user account has been locked',
   IS_ENABLED           smallint not null default 1 comment 'a boolean flag indicating whether the user acount is disabled. An account can be disabled provisionally for security and can be re-enabled upon need.IS_LOCKED means that after the user has logged in the password for six times, the user will automatically lock and cannot log in again. Site administrators can reactivate users through user management.IS_ENABLED means that the site administrator can manually activate and deactivate users through user management;1:enable,0:disable.',
   ENABLED_DATE         datetime comment 'date and time when the user has been enabled',
   DISABLED_AFTER_DAYS  int not null default 0 comment 'the inactive user is disabled after this days.',
   REMOVED_AFTER_DAYS   int not null default 0 comment 'the inactinve user is removed after this days.',
   LAST_LOGIN_DATE      datetime comment 'last login date',
   ACC_EXPIRY_DATE      datetime comment 'account expired date.when the time is reached, the account automatically expires.',
   USER_PWD             varchar(128) comment 'the encoded user password',
   ENCRYPT_SALT         varchar(128) comment 'cryptographic salt. A new salt is randomly generated for each password.',
   ENCRYPT_ALG          varchar(16) comment 'the digest algorithm which has been used to encrypt the user password',
   ENCRYPT_ITERATION_COUNT smallint default 0 comment 'the iteration count refers to the number of times that the hash function is applied to its own results.',
   LAST_PWD_CHG_DATE    datetime comment 'date of the last password change',
   MAX_PWD_AGE          int default 0 comment 'maximum password age. users are required to change their password when reaching the maxmum age.',
   FORCE_CHG_PWD        smallint default 0 comment 'An option to force a user to change password on next login',
   FAILED_LOGIN_COUNT   int default 0 comment 'failed login count',
   PREF_DASHBOARD_REAL  varchar(128) not null comment 'dashboard preferred setting',
   PREF_DASHBOARD_USAGE varchar(255) not null comment 'dashboard preferred setting',
   CREATOR              varchar(36) not null comment 'the creator',
   CREATE_DATE          datetime not null comment 'the creation date',
   MODIFIER             varchar(36) not null comment 'the modifier',
   MODIFY_DATE          datetime not null comment 'the last modification time',
   primary key (USER_ID)
);

alter table PUBTUSER comment 'All users defined in the system';

/*==============================================================*/
/* Index: UNI_PUBTUSER_ON_USERNAME                              */
/*==============================================================*/
create unique index UNI_PUBTUSER_ON_USERNAME on PUBTUSER
(
   USERNAME
);

/*==============================================================*/
/* Index: IDX_PUBTUSER_ON_DATE                                  */
/*==============================================================*/
create index IDX_PUBTUSER_ON_DATE on PUBTUSER
(
   MODIFY_DATE
);

/*==============================================================*/
/* Table: PUBTUSERLOG                                           */
/*==============================================================*/
create table PUBTUSERLOG
(
   LOG_ID               numeric(38,0) not null comment 'audit log id',
   USERNAME             varchar(36) not null comment 'user name',
   ROLE                 varchar(600) comment 'roles of the user',
   CILENT_IP            varchar(38) comment 'user''s client ip address',
   EVENT_TIME           datetime not null comment 'the date & time happen the action',
   EVENT_ACTION         varchar(4000) not null comment 'event action',
   primary key (LOG_ID)
);

alter table PUBTUSERLOG comment 'log for user action';

/*==============================================================*/
/* Index: IX_PUBTUSERLOG_1                                      */
/*==============================================================*/
create index IX_PUBTUSERLOG_1 on PUBTUSERLOG
(
   EVENT_TIME
);

/*==============================================================*/
/* Table: PUBTUSER_GROUP                                        */
/*==============================================================*/
create table PUBTUSER_GROUP
(
   REL_ID               int not null comment 'relation id',
   USER_ID              int not null comment 'user id',
   GROUP_ID             int not null comment 'group id',
   IS_DEFAULT           smallint not null default 0 comment 'a boolean flag indicating whether the it is the default group. The default group is the directory that a user is first in after loggin into the system.',
   LAST_ACCESS_TIME     datetime comment 'the group last access time',
   primary key (REL_ID)
);

alter table PUBTUSER_GROUP comment 'The association of a user to each group.';

/*==============================================================*/
/* Index: UNI_PUBTUSER_GROUP                                    */
/*==============================================================*/
create unique index UNI_PUBTUSER_GROUP on PUBTUSER_GROUP
(
   USER_ID,
   GROUP_ID
);

/*==============================================================*/
/* Table: PUBTUSER_ROLE                                         */
/*==============================================================*/
create table PUBTUSER_ROLE
(
   REL_ID               int not null comment 'the relation id',
   USER_ID              int not null comment 'user id',
   ROLE_ID              int not null comment 'role id',
   primary key (REL_ID)
);

alter table PUBTUSER_ROLE comment 'The association of a user to each role';

/*==============================================================*/
/* Index: UNI_PUBTUSER_ROLE                                     */
/*==============================================================*/
create unique index UNI_PUBTUSER_ROLE on PUBTUSER_ROLE
(
   USER_ID,
   ROLE_ID
);

/*==============================================================*/
/* Table: TMSPTSNS                                              */
/*==============================================================*/
create table TMSPTSNS
(
   BATCH_ID             numeric(38,0) not null comment 'batch id',
   TSN                  varchar(36) not null comment 'terminal sn',
   primary key (BATCH_ID, TSN)
);

alter table TMSPTSNS comment 'temporary table. terminal sn batch ';

/*==============================================================*/
/* Table: TMSTALERT_CONDITION                                   */
/*==============================================================*/
create table TMSTALERT_CONDITION
(
   COND_ID              numeric(38,0) not null comment 'alert condition id',
   ALERT_ITEM           varchar(36) not null comment 'alert item name',
   SETTING_ID           numeric(38,0) comment 'refer to a alert setting',
   ALERT_SEVERITY       int comment 'alert serverity, 1-info, 2-warn, 3-critical',
   ALERT_THRESHOLD      varchar(38) comment 'info threshold',
   ALERT_MESSAGE        varchar(255) comment 'alert message',
   TOPIC_ARN            varchar(255) comment 'Message Topic',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (COND_ID)
);

alter table TMSTALERT_CONDITION comment 'alert condition';

/*==============================================================*/
/* Index: IX_TMSTALERT_CONDITION                                */
/*==============================================================*/
create index IX_TMSTALERT_CONDITION on TMSTALERT_CONDITION
(
   SETTING_ID
);

/*==============================================================*/
/* Table: TMSTALERT_EVENT                                       */
/*==============================================================*/
create table TMSTALERT_EVENT
(
   EVENT_ID             numeric(38,0) not null comment 'alert event id',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   COND_ID              numeric(38,0) not null comment 'refer to a alert condition',
   ALERT_TM             datetime comment 'alert date & time',
   ALERT_VALUE          varchar(128) comment 'value trigger the alert',
   ALERT_SEVERITY       int comment 'alert servierty',
   ALERT_MESSAGE        varchar(4000) comment 'alert message',
   primary key (EVENT_ID)
);

alter table TMSTALERT_EVENT comment 'alert event';

/*==============================================================*/
/* Table: TMSTALERT_OFF                                         */
/*==============================================================*/
create table TMSTALERT_OFF
(
   OFF_ID               numeric(38,0) not null,
   SETTING_ID           numeric(38,0) not null comment 'refer to a alert setting',
   REPEAT_TYPE          varchar(10) not null comment 'repeat, 0-one time,  1-every day, 2-every week, 3 - every month, 4 - every year,',
   OFF_DATE             varchar(20) comment 'alert off date',
   OFF_START_TIME       char(5) comment 'alert off start time',
   OFF_END_TIME         char(5) comment 'alert off end time',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (OFF_ID)
);

alter table TMSTALERT_OFF comment 'alert off setting';

/*==============================================================*/
/* Index: IX_TMSTALERT_OFF                                      */
/*==============================================================*/
create index IX_TMSTALERT_OFF on TMSTALERT_OFF
(
   SETTING_ID
);

/*==============================================================*/
/* Table: TMSTALERT_PUBLISH_EMAIL                               */
/*==============================================================*/
create table TMSTALERT_PUBLISH_EMAIL
(
   PUBLISH_ID           numeric(38,0) not null,
   EVENT_ID             numeric(38,0) not null comment 'alert event id',
   COND_ID              numeric(38,0) not null comment 'refer to a alert condition',
   PULISH_TM            datetime comment 'alert date & time',
   USER_ID              numeric(38,0) not null,
   primary key (PUBLISH_ID)
);

alter table TMSTALERT_PUBLISH_EMAIL comment 'alert -  publish email history';

/*==============================================================*/
/* Table: TMSTALERT_PUBLISH_SMS                                 */
/*==============================================================*/
create table TMSTALERT_PUBLISH_SMS
(
   PUBLISH_ID           numeric(38,0) not null,
   EVENT_ID             numeric(38,0) not null comment 'alert event id',
   COND_ID              numeric(38,0) not null comment 'refer to a alert condition',
   PULISH_TM            datetime comment 'alert date & time',
   USER_ID              numeric(38,0) not null,
   primary key (PUBLISH_ID)
);

alter table TMSTALERT_PUBLISH_SMS comment 'alert -  publish SMS history';

/*==============================================================*/
/* Table: TMSTALERT_SBSCRB                                      */
/*==============================================================*/
create table TMSTALERT_SBSCRB
(
   SBSCRB_ID            numeric(38,0) not null,
   USER_ID              numeric(38,0) not null,
   COND_ID              numeric(38,0) not null comment 'alert condition id',
   SMS                  char(1) comment 'subscribe SMS',
   EMAIL                char(1) comment 'subscribe EMAIL',
   SUBSCRIBE_ARN        varchar(255),
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (SBSCRB_ID)
);

alter table TMSTALERT_SBSCRB comment 'subscribe alert';

/*==============================================================*/
/* Table: TMSTALERT_SETTING                                     */
/*==============================================================*/
create table TMSTALERT_SETTING
(
   SETTING_ID           numeric(38,0) not null comment 'condition id',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (SETTING_ID)
);

alter table TMSTALERT_SETTING comment 'alert setting';

/*==============================================================*/
/* Index: IX_TMSTALERT_SETTING                                  */
/*==============================================================*/
create unique index IX_TMSTALERT_SETTING on TMSTALERT_SETTING
(
   GROUP_ID
);

/*==============================================================*/
/* Table: TMSTDELFILELOG                                        */
/*==============================================================*/
create table TMSTDELFILELOG
(
   OPTLOG_ID            numeric(38,0) not null,
   FILE_PATH            varchar(4000) not null,
   OPT_TIME             datetime not null,
   STATUS               varchar(10) not null,
   primary key (OPTLOG_ID)
);

alter table TMSTDELFILELOG comment 'record pkg delete log';

/*==============================================================*/
/* Table: TMSTDEPLOY                                            */
/*==============================================================*/
create table TMSTDEPLOY
(
   DEPLOY_ID            numeric(38,0) not null comment 'terminal ID/SN',
   MODEL_ID             varchar(36) comment 'refer to a terminal type',
   PKG_ID               numeric(38,0) comment 'refer to a package',
   TIME_ZONE            varchar(36) comment 'time zone',
   SCHEMA_ID            numeric(38,0) comment 'refer to a package parameter template/schema',
   PARAM_VERSION        varchar(64) comment 'param version',
   PARAM_SET            varchar(60) comment 'param set',
   PARAM_STATUS         int default 0 comment 'if param file has been generated',
   DEPLOY_STATUS        int not null comment 'deploy status',
   DEPLOY_SOURCE        varchar(60) comment 'the deploy source terminal self or group',
   DEPLOY_SOURCE_ID     numeric(38,0) comment 'the deploy source id',
   DEPLOY_SOURCE_GROUP_ID numeric(38,0) comment 'refer to group id',
   DAYLIGHT_SAVING      smallint comment 'daylight saving',
   DWNL_START_TM        datetime comment 'Date and time to start the download.',
   DWNL_END_TM          datetime comment 'Date and time after which the download cannot be processed.',
   DWNL_RETRY_COUNT     int comment 'terminal try to download count',
   DWNL_PERIOD          int comment 'Period delay between download cyclic',
   DWNL_MAX_NUM         int comment 'Maximum number of download cyclic.',
   ACTV_START_TM        datetime comment 'Date and time to start the activation.',
   ACTV_RETRY_COUNT     int comment 'terminal try to activate count',
   ACTV_END_TM          datetime comment 'Date and time after which the activation cannot be processed.',
   FORCE_UPDATE         int comment 'some package type support force update flag',
   ONLY_PARAM           int comment 'only download parameter/data file',
   DWNL_ORDER           int default 0 comment 'deployment with the lowest order will be downloaded first',
   DELETED_WHEN_DONE    int not null default 1 comment 'deleted when done',
   LATEST_TYPE          int comment 'group deploy offlinekey latest type 0:Lastest Upload Version 1:Highest Version',
   DEPLOY_TYPE 			int comment 'DEPLOY_TYPE 0:OfflineKey 1:Package',
   DESCRIPTION          varchar(256) comment 'descrption',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (DEPLOY_ID)
);

alter table TMSTDEPLOY comment 'deploy';

/*==============================================================*/
/* Table: TMSTDEPLOY_PARA                                       */
/*==============================================================*/
create table TMSTDEPLOY_PARA
(
   DEPLOY_PARA_ID       numeric(38,0) not null comment 'deploy parameter id',
   DEPLOY_ID            numeric(38,0) not null comment 'refer to a deploy',
   PGM_ID               numeric(38,0) not null comment 'refer to a program',
   PKG_ID               numeric(38,0) not null comment 'refer to a package',
   FILE_NAME            varchar(255) not null comment 'file name',
   FILE_VERSION         varchar(64) not null comment 'file version',
   FILE_SIZE            numeric(38,0) not null comment 'file size',
   FILE_PATH            varchar(4000) not null comment 'file path',
   FILE_MD5             varchar(32) comment 'md5 file digest',
   FILE_SHA256          varchar(64) comment 'sha256 file digest',
   CREATOR              varchar(32) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(38) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (DEPLOY_PARA_ID)
);

alter table TMSTDEPLOY_PARA comment 'deploy parameter';

/*==============================================================*/
/* Index: IX_TMSTDEPLOY_PATA                                    */
/*==============================================================*/
create unique index IX_TMSTDEPLOY_PATA on TMSTDEPLOY_PARA
(
   DEPLOY_ID,
   PGM_ID
);

/*==============================================================*/
/* Table: TMSTEVENT_GRP                                         */
/*==============================================================*/
create table TMSTEVENT_GRP
(
   EVENT_ID             numeric(38,0) not null comment 'event id',
   EVENT_TIME           datetime not null comment 'event date & time',
   EVENT_SOURCE         varchar(60) not null comment 'event source. e.g. terminal id, group id etc.',
   EVENT_SEVERITY       char(1) not null comment 'event serverity, 1-information, 2-warning, 3-critical',
   EVENT_MSG            varchar(4000) comment 'event message',
   primary key (EVENT_ID)
);

alter table TMSTEVENT_GRP comment 'group event';

/*==============================================================*/
/* Index: IX_TMSTEVENT_GRP                                      */
/*==============================================================*/
create index IX_TMSTEVENT_GRP on TMSTEVENT_GRP
(
   EVENT_TIME,
   EVENT_SOURCE
);

/*==============================================================*/
/* Table: TMSTEVENT_TRM                                         */
/*==============================================================*/
create table TMSTEVENT_TRM
(
   EVENT_ID             numeric(38,0) not null comment 'event id',
   EVENT_TIME           datetime not null comment 'event date & time',
   EVENT_SOURCE         varchar(60) not null comment 'event source. e.g. terminal id, group id etc.',
   EVENT_SEVERITY       char(1) not null comment 'event serverity, 1-information, 2-warning, 3-critical',
   EVENT_MSG            varchar(4000) comment 'event message',
   primary key (EVENT_ID)
);

alter table TMSTEVENT_TRM comment 'terminal event';

/*==============================================================*/
/* Index: IX_TMSTEVENT_TRM                                      */
/*==============================================================*/
create index IX_TMSTEVENT_TRM on TMSTEVENT_TRM
(
   EVENT_TIME,
   EVENT_SOURCE
);

/*==============================================================*/
/* Table: TMSTGROUPDEPLOY_HISTORY                               */
/*==============================================================*/
create table TMSTGROUPDEPLOY_HISTORY
(
   GROUP_DEPLOY_HIS_ID  numeric(38,0) not null comment 'terminal ID/SN',
   GROUP_ID             numeric(38,0) not null comment 'group id refer to pubtgroup ',
   MODEL_ID             varchar(36) comment 'refer to a terminal type',
   DEPLOY_STATUS        int not null comment 'deploy status',
   DEPLOY_SOURCE        varchar(60) not null comment 'group name',
   PKG_ID               numeric(38,0) not null comment 'refer to a package',
   PKG_NAME             varchar(128) not null comment 'package name',
   PKG_VERSION          varchar(64) not null comment 'package version',
   SCHEMA_ID            numeric(38,0) comment 'refer to a package parameter template/schema',
   PARAM_VERSION        varchar(64),
   PARAM_SET            varchar(60),
   DWNL_START_TM        datetime comment 'Date and time to start the download.',
   DWNL_PERIOD          int comment 'Period delay between download cyclic',
   DWNL_MAX_NUM         int comment 'Maximum number of download cyclic.',
   DOWN_RETRY_COUNT     int comment 'terminal try to download count',
   ACTV_RETRY_COUNT     int comment 'terminal try to activate count',
   ACTV_START_TM        datetime comment 'Date and time to start the activation.',
   FORCE_UPDATE         int comment 'some package type support force update flag',
   ONLY_PARAM           int comment 'only download parameter/data file',
   DWNL_ORDER           int not null default 0 comment 'deployment with the lowest order will be downloaded first',
   DESCRIPTION          varchar(256) comment 'descrption',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (GROUP_DEPLOY_HIS_ID)
);

alter table TMSTGROUPDEPLOY_HISTORY comment 'group deploy  history';

/*==============================================================*/
/* Table: TMSTGROUP_DEPLOY                                      */
/*==============================================================*/
create table TMSTGROUP_DEPLOY
(
   REL_ID               numeric(38,0) not null comment 'releation id',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   DEPLOY_ID            numeric(38,0) not null comment 'refer to a deploy',
   DEPLOY_TIME          numeric(38,0) not null comment 'deploy timestamp',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (REL_ID)
);

alter table TMSTGROUP_DEPLOY comment 'the association between group and deploy';

/*==============================================================*/
/* Index: IX_TMSTGROUP_DEPLOY                                   */
/*==============================================================*/
create unique index IX_TMSTGROUP_DEPLOY on TMSTGROUP_DEPLOY
(
   GROUP_ID,
   DEPLOY_ID
);

/*==============================================================*/
/* Index: IX_TMSTGROUP_DEPLOY_TIME                              */
/*==============================================================*/
create unique index IX_TMSTGROUP_DEPLOY_TIME on TMSTGROUP_DEPLOY
(
   GROUP_ID,
   DEPLOY_TIME
);

/*==============================================================*/
/* Table: TMSTGROUP_REAL_STS                                    */
/*==============================================================*/
create table TMSTGROUP_REAL_STS
(
   ID                   numeric(38,0) not null comment ' id',
   GROUP_ID             numeric(38,0) not null comment 'group id',
   GROUP_NAME           varchar(60) comment 'group name',
   ITEM_NAME            varchar(36),
   TOTAL_TRMS           numeric(8,0),
   ABNORMAL_TRMS        numeric(8,0),
   NORMAL_TRMS          numeric(8,0),
   UNKNOWN_TRMS         numeric(8,0),
   ALERT_SEVERITY       int comment 'alert serverity, 1-info, 2-warn, 3-critical',
   ALERT_THRESHOLD      varchar(38) comment 'info threshold',
   ALERT_VALUE          varchar(128) comment 'value trigger the alert',
   CREATE_DATE          datetime comment 'create date',
   primary key (ID)
);

alter table TMSTGROUP_REAL_STS comment 'group terminal statistic data';

/*==============================================================*/
/* Table: TMSTGROUP_USAGE_STS                                   */
/*==============================================================*/
create table TMSTGROUP_USAGE_STS
(
   ID                   numeric(38,0) not null comment ' id',
   GROUP_ID             numeric(38,0) not null,
   GROUP_NAME           varchar(60),
   ITEM_NAME            varchar(36),
   TOTAL_TRMS           numeric(8,0),
   ABNORMAL_TRMS        numeric(8,0),
   NORMAL_TRMS          numeric(8,0),
   UNKNOWN_TRMS         numeric(8,0),
   ALERT_SEVERITY       int comment 'alert serverity, 1-info, 2-warn, 3-critical',
   ALERT_THRESHOLD      varchar(38) comment 'info threshold',
   ALERT_VALUE          varchar(128) comment 'value trigger the alert',
   START_TIME           varchar(32) comment 'The terminal statistics period',
   END_TIME             varchar(32),
   CYCLE_DATE			varchar(10),
   REPORT_CYCLE         varchar(10) comment 'report cycle',
   CREATE_DATE          datetime comment 'create date',
   primary key (ID)
);

alter table TMSTGROUP_USAGE_STS comment 'group terminal statistic data';

/*==============================================================*/
/* Table: TMSTIMPORTFILE                                        */
/*==============================================================*/
create table TMSTIMPORTFILE
(
   FILE_ID              numeric(38,0) not null comment 'file id',
   GROUP_ID             numeric(38,0) not null comment 'refer to group id',
   USER_ID              numeric(38,0) not null comment 'user id',
   FILE_NAME            varchar(38) not null comment 'filename',
   FILE_PATH            varchar(4000) not null comment 'filepath',
   FILE_SIZE            numeric(38,0) not null comment 'filesize',
   STATUS               varchar(38) not null comment 'refer to a country',
   FILE_TYPE            varchar(38) not null comment 'filetype',
   TRM_SN 				varchar(36) comment 'tminal serial number',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (FILE_ID)
);

/*==============================================================*/
/* Index: IX_IMPORTFILE_NAME                                    */
/*==============================================================*/
create unique index IX_IMPORTFILE_NAME on TMSTIMPORTFILE
(
   GROUP_ID,
   FILE_NAME,
   FILE_TYPE
);

/*==============================================================*/
/* Table: TMSTMFR                                               */
/*==============================================================*/
create table TMSTMFR
(
   MFR_ID               varchar(36) not null comment 'manufacturer identifier/abbr. name',
   MFR_NAME             varchar(140) not null comment 'manufacturer name',
   MFR_DESC             varchar(256) comment 'manufacturer description',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (MFR_ID)
);

alter table TMSTMFR comment 'manufacturer table';

/*==============================================================*/
/* Index: IX_TMSTMFR_NAME                                       */
/*==============================================================*/
create unique index IX_TMSTMFR_NAME on TMSTMFR
(
   MFR_NAME
);

/*==============================================================*/
/* Index: IX_TMSTMFR_DATE                                       */
/*==============================================================*/
create index IX_TMSTMFR_DATE on TMSTMFR
(
   MODIFY_DATE
);

/*==============================================================*/
/* Table: TMSTMODEL                                             */
/*==============================================================*/
create table TMSTMODEL
(
   MODEL_ID             varchar(36) not null comment 'model identify/abbr. name',
   MODEL_NAME           varchar(128) not null comment 'model name',
   MFR_ID               varchar(36) not null comment 'refer to a manufacturer',
   PLATFORM             varchar(20) comment 'terminal OS platform',
   MODEL_DESC           varchar(256) comment 'description',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'created date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modified date',
   primary key (MODEL_ID)
);

alter table TMSTMODEL comment 'terminal model/type table';

/*==============================================================*/
/* Index: IX_TMSTMODEL_NAME                                     */
/*==============================================================*/
create unique index IX_TMSTMODEL_NAME on TMSTMODEL
(
   MODEL_NAME
);

/*==============================================================*/
/* Index: IX_TMSTMODEL_DATE                                     */
/*==============================================================*/
create index IX_TMSTMODEL_DATE on TMSTMODEL
(
   MODIFY_DATE
);

/*==============================================================*/
/* Table: TMSTPACKAGE                                           */
/*==============================================================*/
create table TMSTPACKAGE
(
   PKG_ID               numeric(38,0) not null comment 'package id',
   PKG_UUID             varchar(36) not null comment 'package uuid',
   PKG_NAME             varchar(128) not null comment 'package name',
   PKG_VERSION          varchar(64) not null comment 'package version',
   MODEL_ID             varchar(36) comment 'refer to a terminal model/type',
   TRM_SN               varchar(36) comment 'tminal serial number',
   PKG_TYPE             varchar(64) comment 'package  type',
   PGM_TYPE             varchar(64),
   PKG_DESC             varchar(256) comment 'package description',
   PKG_STATUS           int not null default 1 comment 'package status',
   EXPIRATION_DATE      datetime comment 'Date and time after which the package cannot be deployed',
   FILE_NAME            varchar(256) not null comment 'file name',
   FILE_SIZE            numeric(38,0) not null comment 'file size',
   FILE_PATH            varchar(4000) not null comment 'file path',
   FILE_MD5             varchar(32) comment 'md5 file digest',
   FILE_SHA256          varchar(64) comment 'sha256 file digest',
   SCHEMA_FILE_SIZE     numeric(38,0) comment 'schema file size',
   SCHEMA_FILE_PATH     varchar(4000) comment 'schema file path',
   PARAM_SET            varchar(60) comment 'parameter set',
   PKG_NOTES			varchar(200) comment 'package notes',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'creat date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   PKG_SIGNED           int comment 'package signed',
   primary key (PKG_ID)
);

alter table TMSTPACKAGE comment 'packages';

/*==============================================================*/
/* Index: IX_TMSTPACKAGE_NAME                                   */
/*==============================================================*/
create unique index IX_TMSTPACKAGE_NAME on TMSTPACKAGE
(
   PKG_NAME,
   PKG_VERSION
);

/*==============================================================*/
/* Index: IX_TMSTPACKAGE_DATE                                   */
/*==============================================================*/
create index IX_TMSTPACKAGE_DATE on TMSTPACKAGE
(
   MODIFY_DATE
);

/*==============================================================*/
/* Table: TMSTPARAMETER_HISTORY                                 */
/*==============================================================*/
create table TMSTPARAMETER_HISTORY
(
   HIS_ID               numeric(38,0) not null comment 'history id',
   TRM_ID               varchar(36) not null comment 'terminal id',
   REL_ID               varchar(36) not null comment 'deploy version',
   PARAMETER            varchar(60) comment 'parameter',
   OLD_VALUE            varchar(120) comment 'old value',
   NEW_VALUE            varchar(120) comment 'new value',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   primary key (HIS_ID)
);

alter table TMSTPARAMETER_HISTORY comment 'parameter change histroy';

/*==============================================================*/
/* Table: TMSTPKG_GROUP                                         */
/*==============================================================*/
create table TMSTPKG_GROUP
(
   REL_ID               numeric(38,0) not null comment 'relation id',
   PKG_ID               numeric(38,0) not null comment 'refer to a package',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'creat date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (REL_ID)
);

alter table TMSTPKG_GROUP comment 'the association between package and group';

/*==============================================================*/
/* Index: IX_TMSTPKG_GROUP                                      */
/*==============================================================*/
create unique index IX_TMSTPKG_GROUP on TMSTPKG_GROUP
(
   PKG_ID,
   GROUP_ID
);

/*==============================================================*/
/* Table: TMSTPKG_PROGRAM                                       */
/*==============================================================*/
create table TMSTPKG_PROGRAM
(
   PGM_ID               numeric(38,0) not null comment 'file id',
   PKG_ID               numeric(38,0) not null comment 'refer to a package',
   PGM_ABBR_NAME        varchar(128) comment 'PGM identifier on terminal side',
   PGM_VERSION          varchar(64) comment 'program version',
   PGM_NAME             varchar(128) comment 'progam name on TMS side',
   PGM_DISPLAY_NAME     varchar(128) comment 'progam display name on terminal side',
   PGM_TYPE             varchar(36) comment 'program type',
   PGM_DESC             varchar(256) comment 'program description',
   PGM_FILE_ID          numeric(38,0) comment 'refer to program file',
   CONF_FILE_ID         numeric(38,0) comment 'program config file',
   DIGEST_FILE_ID       numeric(38,0),
   SIGN_FILE_ID         numeric(38,0) comment 'refer to signature file',
   SIGN_VERSION         varchar(64) comment 'signature version',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'creat date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (PGM_ID)
);

alter table TMSTPKG_PROGRAM comment 'package programs';

/*==============================================================*/
/* Index: IX_TMSPKG_PROGRAM                                     */
/*==============================================================*/
create index IX_TMSPKG_PROGRAM on TMSTPKG_PROGRAM
(
   PKG_ID
);

/*==============================================================*/
/* Table: TMSTPKG_SCHEMA                                        */
/*==============================================================*/
create table TMSTPKG_SCHEMA
(
   SCHEMA_ID            numeric(38,0) not null comment 'schema id',
   PKG_ID               numeric(38,0) not null comment 'refer to a package',
   SCHEMA_NAME          varchar(128) not null comment 'schame name',
   SCHEMA_VERSION       varchar(64) not null comment 'schema version',
   SCHEMA_STATUS        int not null default 1 comment 'schema status',
   SCHEMA_FILE_SIZE     numeric(38,0) comment 'schema file size',
   SCHEMA_FILE_PATH     varchar(4000) comment 'schema file path',
   PARAM_SET            varchar(60) comment 'parameter set',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'creat date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   IS_SYS               int not null,
   primary key (SCHEMA_ID)
);

alter table TMSTPKG_SCHEMA comment 'package parameter template/schema';

/*==============================================================*/
/* Index: IX_TMSTPKG_SCHEMA                                     */
/*==============================================================*/
create index IX_TMSTPKG_SCHEMA on TMSTPKG_SCHEMA
(
   PKG_ID
);

/*==============================================================*/
/* Table: TMSTPKG_USAGE_INFO                                    */
/*==============================================================*/
create table TMSTPKG_USAGE_INFO
(
   PKG_ID               numeric(38,0) not null,
   LAST_OPT_TIME        datetime not null,
   primary key (PKG_ID)
);

alter table TMSTPKG_USAGE_INFO comment 'record pkg usage info';

/*==============================================================*/
/* Table: TMSTPROGRAM_FILE                                      */
/*==============================================================*/
create table TMSTPROGRAM_FILE
(
   FILE_ID              numeric(38,0) not null comment 'file id',
   PKG_ID               numeric(38,0) not null,
   PGM_ID               numeric(38,0) not null comment 'refer to a program',
   FILE_NAME            varchar(256) comment 'file name',
   FILE_VERSION         varchar(64) comment 'file version',
   FILE_TYPE            varchar(36) comment 'file  type',
   FILE_DESC            varchar(256) comment 'file description',
   FILE_SIZE            numeric(38,0) comment 'file size',
   FILE_PATH            varchar(4000) comment 'file path',
   FILE_MD5             varchar(32) comment 'md5 file digest',
   FILE_SHA256          varchar(64) comment 'sha256 file digest',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'creat date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (FILE_ID)
);

alter table TMSTPROGRAM_FILE comment 'program files';

/*==============================================================*/
/* Index: IX_TMSTPROGRAM_FILE_PGM                               */
/*==============================================================*/
create index IX_TMSTPROGRAM_FILE_PGM on TMSTPROGRAM_FILE
(
   PGM_ID
);

/*==============================================================*/
/* Index: IX_TMSTPROGRAM_FILE_PKG                               */
/*==============================================================*/
create index IX_TMSTPROGRAM_FILE_PKG on TMSTPROGRAM_FILE
(
   PKG_ID
);

/*==============================================================*/
/* Table: TMSTTERMINAL                                          */
/*==============================================================*/
create table TMSTTERMINAL
(
   TRM_ID               varchar(36) not null comment 'terminal ID/SN',
   TRM_SN               varchar(36) comment 'tminal serial number',
   MODEL_ID             varchar(36) not null comment 'refer to a terminal type',
   TRM_STATUS           int not null comment 'terminal status',
   LIC_CODE             varchar(16) comment 'license code',
   LIC_SDATE            datetime comment 'license start date',
   LIC_EDATE            datetime comment 'license end date',
   LIC_STATUS           int comment 'license status',
   COUNTRY              varchar(128) comment 'refer to a country',
   PROVINCE             varchar(128) comment 'refer to a state/province',
   CITY                 varchar(128) comment 'refer to a city',
   ZIP_CODE             varchar(10) comment 'ZIP/POST code',
   TIME_ZONE            varchar(36) comment 'time zone',
   SYNC_TO_SERVER_TIME  smallint,
   DAYLIGHT_SAVING      smallint comment 'daylight saving time',
   ADDRESS              varchar(256) comment 'address/location',
   DESCRIPTION          varchar(256) comment 'descrption',
   INSTALL_APPS         text comment 'install apps',
   REPORT_TIME          datetime comment 'terminal send installed app report time',
   SYSMETRIC_KEYS       text comment 'sysmetricKeys',
   KEY_REPORT_TIME      datetime comment 'terminal send sysmetricKeys report time',
   LOCAL_TIME           VARCHAR(36) comment 'terminal local time utc',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (TRM_ID)
);

alter table TMSTTERMINAL comment 'terminal';

/*==============================================================*/
/* Index: IX_TMSTERMINAL_SN                                     */
/*==============================================================*/
create index IX_TMSTERMINAL_SN on TMSTTERMINAL
(
   TRM_SN
);

/*==============================================================*/
/* Table: TMSTTRMDEPLOY_HISTORY                                 */
/*==============================================================*/
create table TMSTTRMDEPLOY_HISTORY
(
   TRM_DEPLOY_HIS_ID    numeric(38,0) not null comment 'terminal ID/SN',
   TRM_ID               varchar(36) not null comment 'group id refer to pubtgroup ',
   MODEL_ID             varchar(36) comment 'refer to a terminal type',
   DEPLOY_STATUS        int not null comment 'deploy status',
   DEPLOY_SOURCE        varchar(60) not null comment 'group name',
   PKG_ID               numeric(38,0) not null comment 'refer to a package',
   PKG_NAME             varchar(128) not null comment 'package name',
   PKG_VERSION          varchar(64) not null comment 'package version',
   SCHEMA_ID            numeric(38,0) comment 'refer to a package parameter template/schema',
   PARAM_VERSION        varchar(64),
   PARAM_SET            varchar(60),
   DWNL_START_TM        datetime comment 'Date and time to start the download.',
   DWNL_PERIOD          int comment 'Period delay between download cyclic',
   DWNL_MAX_NUM         int comment 'Maximum number of download cyclic.',
   DOWN_RETRY_COUNT     int comment 'terminal try to download count',
   ACTV_RETRY_COUNT     int comment 'terminal try to activate count',
   ACTV_START_TM        datetime comment 'Date and time to start the activation.',
   FORCE_UPDATE         int comment 'some package type support force update flag',
   ONLY_PARAM           int comment 'only download parameter/data file',
   DWNL_ORDER           int default 0 comment 'deployment with the lowest order will be downloaded first',
   DESCRIPTION          varchar(256) comment 'descrption',
   DWNL_TIME            datetime comment 'date time of the download action performed',
   DWNL_STATUS          varchar(38) comment 'download status',
   DWNL_SUCC_COUNT      int default 0 comment 'download successful times',
   DWNL_FAIL_COUNT      int default 0 comment 'download failed times',
   ACTV_STATUS          varchar(38) comment 'activation status',
   ACTV_TIME            datetime comment 'date time of the activation action performed',
   ACTV_FAIL_COUNT      int default 0,
   CREATOR              varchar(32) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(38) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (TRM_DEPLOY_HIS_ID)
);

alter table TMSTTRMDEPLOY_HISTORY comment 'terminal deploy  history';

/*==============================================================*/
/* Table: TMSTTRMDWNL                                           */
/*==============================================================*/
create table TMSTTRMDWNL
(
   LOG_ID               numeric(38,0) not null comment 'releation id',
   DEPLOY_ID            numeric(38,0) not null,
   TRM_ID               varchar(36) not null comment 'refer to a terminal',
   TRM_SN               varchar(36),
   MODEL_ID             varchar(36) comment 'model id',
   PKG_NAME             varchar(128) not null comment 'package name',
   PKG_TYPE             varchar(60) not null,
   PKG_VERSION          varchar(64) not null comment 'package version',
   DWNL_START_TIME      datetime comment 'date time of the download action performed',
   DWNL_END_TIME        datetime comment 'date time of the download action performed',
   EXPIRE_DATE          datetime,
   DWNL_STATUS          varchar(36) not null comment 'download status',
   ACTV_SCHEDULE        datetime,
   DWNL_SCHEDULE        datetime,
   ACTV_TIME            datetime comment 'date time of the activation action performed',
   ACTV_STATUS          varchar(36) not null comment 'activation status',
   CREATE_DATE          datetime comment 'create date',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (LOG_ID)
);

alter table TMSTTRMDWNL comment 'terminal download';

/*==============================================================*/
/* Index: IDX_TMSTTRMDWNL_DT                                    */
/*==============================================================*/
create index IDX_TMSTTRMDWNL_DT on TMSTTRMDWNL
(
   TRM_ID,
   DWNL_END_TIME
);

/*==============================================================*/
/* Index: IDX_TMSTTRMDWNL_AT                                    */
/*==============================================================*/
create index IDX_TMSTTRMDWNL_AT on TMSTTRMDWNL
(
   TRM_ID,
   ACTV_TIME
);

/*==============================================================*/
/* Table: TMSTTRMSTATUS                                         */
/*==============================================================*/
create table TMSTTRMSTATUS
(
   TRM_ID               varchar(36) not null comment 'terminal id',
   TRM_SN               varchar(36) not null comment 'terminal sn',
   MODEL_ID             varchar(36) comment 'terminal model identifier',
   LAST_CONN_TIME       datetime comment 'last connection time',
   LAST_DWNL_TIME       datetime comment 'last downloaded (finished) time',
   LAST_DWNL_STATUS     varchar(20) comment 'last download status',
   LAST_DWNL_TASK       numeric(38,0) comment 'last downloaded deployment id',
   LAST_ACTV_TIME       datetime comment 'last activation/update time',
   LAST_ACTV_STATUS     varchar(20) comment 'last activation/update status',
   LAST_SOURCE_IP       varchar(38) comment 'last source ip',
   IS_ONLINE            int comment 'online/offline status',
   TAMPER               varchar(36) comment 'tamper reason',
   PRIVACY_SHIELD       int comment 'privcy shield status, 1-nomal 2-removed, 3 - unknow',
   STYLUS               int comment 'stylus status, 1-nomal 2-removed, 3 - unknow',
   SRED               	int comment 'SRED, 1-Encrypted、0-Not Encrypted、other-Unavailable',
   RKI               	int comment 'Indicate whether the terminal supports RKI,1: Yes (support), 0: No (not support)',
   ONLINE_SINCE         datetime,
   OFFLINE_SINCE        datetime,
   primary key (TRM_ID)
);

alter table TMSTTRMSTATUS comment 'terminal status';

/*==============================================================*/
/* Index: IX_TMSTTRMSTATUS                                      */
/*==============================================================*/
create index IX_TMSTTRMSTATUS on TMSTTRMSTATUS
(
   LAST_CONN_TIME
);

/*==============================================================*/
/* Table: TMSTTRM_DEPLOY                                        */
/*==============================================================*/
create table TMSTTRM_DEPLOY
(
   REL_ID               numeric(38,0) not null comment 'releation id',
   TRM_ID               varchar(36) not null comment 'refer to a terminal',
   DEPLOY_ID            numeric(38,0) not null comment 'refer to a deploy',
   DEPLOY_TIME          numeric(38,0) not null comment 'deploy timestamp',
   DWNL_TIME            datetime comment 'date time of the download action performed',
   DWNL_STATUS          varchar(38) not null comment 'download status',
   DWNL_SUCC_COUNT      int not null default 0 comment 'download successful times',
   DWNL_FAIL_COUNT      int not null default 0 comment 'download failed times',
   ACTV_STATUS          varchar(38) not null comment 'activation status',
   ACTV_TIME            datetime comment 'date time of the activation action performed',
   ACTV_FAIL_COUNT      int not null default 0,
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (REL_ID)
);

alter table TMSTTRM_DEPLOY comment 'the association between terminal and deploy';

/*==============================================================*/
/* Index: IX_TMSTTRM_DEPLOY                                     */
/*==============================================================*/
create unique index IX_TMSTTRM_DEPLOY on TMSTTRM_DEPLOY
(
   TRM_ID,
   DEPLOY_ID
);

/*==============================================================*/
/* Index: IX_TMSTTRM_DEPLOY_TIME                                */
/*==============================================================*/
create unique index IX_TMSTTRM_DEPLOY_TIME on TMSTTRM_DEPLOY
(
   TRM_ID,
   DEPLOY_TIME
);

/*==============================================================*/
/* Table: TMSTTRM_GROUP                                         */
/*==============================================================*/
create table TMSTTRM_GROUP
(
   REL_ID               numeric(38,0) not null comment 'releation id',
   TRM_ID               varchar(36) not null comment 'refer to a terminal',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (REL_ID)
);

alter table TMSTTRM_GROUP comment 'the association between terminal and group';

/*==============================================================*/
/* Index: IX_TMSTTRM_GROUP1                                     */
/*==============================================================*/
create unique index IX_TMSTTRM_GROUP1 on TMSTTRM_GROUP
(
   GROUP_ID,
   TRM_ID
);

/*==============================================================*/
/* Index: IX_TMSTTRM_GROUP2                                     */
/*==============================================================*/
create unique index IX_TMSTTRM_GROUP2 on TMSTTRM_GROUP
(
   TRM_ID,
   GROUP_ID
);

/*==============================================================*/
/* Table: TMSTTRM_REAL_STS                                      */
/*==============================================================*/
create table TMSTTRM_REAL_STS
(
   ID                   numeric(38,0) not null comment ' id',
   TRM_ID               varchar(36) not null comment 'terminal sn',
   REPORT_TM            datetime comment 'The terminal report time',
   SERVER_TM            datetime comment 'server time',
   ITEM_NAME            varchar(36),
   ITEM_STS             char(1),
   primary key (ID)
);

alter table TMSTTRM_REAL_STS comment 'terminal real-time item status';

/*==============================================================*/
/* Table: TMSTTRM_REPORT_MSG                                    */
/*==============================================================*/
create table TMSTTRM_REPORT_MSG
(
   RPT_ID               numeric(38,0) not null comment 'report id',
   TRM_ID               varchar(36) not null comment 'terminal id',
   GROUP_ID				numeric(38,0) not null comment 'group id',
   REPORT_TM            varchar(32) not null comment 'health report time(group time)',
   TAMPER               char(1) comment 'tamper, 1-normal,2-tamper detected,',
   ON_OFF_LINE          char(1) comment 'online/offline, 1-online, 2-offline',
   SHIELD               char(1) comment 'privacy shield, 1-nomal, 2-removed',
   STYLUS               char(1) comment 'stylus pen, 1-normal, 2- removed',
   DOWN_STS             varchar(16) comment 'download status, values: SUCCESS,PENDING,DOWNLOADING,FAILED,CANCELED',
   ACTV_STS             varchar(16) comment 'activate status, values: SUCCESS,PENDING,DOWNLOADING,FAILED,CANCELED',
   MSR_ERRS             numeric(8,0) comment 'Magnetic Stripe Reader Errors',
   MSR_TOTS             numeric(8,0) comment 'Magnetic Stripe Reader Totals',
   ICR_ERRS             numeric(8,0) comment 'Contact IC Reader Errors',
   ICR_TOTS             numeric(8,0) comment 'Contact IC Reader Totals',
   PIN_FAILS            numeric(8,0) comment 'PIN Encryption Failures',
   PIN_TOTS             numeric(8,0) comment 'PIN Encryption Totals',
   SIGN_ERRS            numeric(8,0) comment 'Electronic Signature Capture Errors',
   SIGN_TOTS            numeric(8,0) comment 'Electronic Signature Capture Totals',
   DOWN_FAILS           numeric(8,0) comment 'Failure Downloads',
   DOWN_PENDING         numeric(8,0) comment 'Pending Downloads',
   DOWN_TOTS            numeric(8,0) comment 'Total Downloads',
   ACTV_FAILS           numeric(8,0) comment 'Failure Activations',
   ACTV_PENDING         numeric(8,0) comment 'Pending Activations',
   ACTV_TOTS            numeric(8,0) comment 'Total Activations',
   CL_ICR_ERRS          numeric(8,0) comment 'Contacless IC Reader Errors',
   CL_ICR_TOTS          numeric(8,0) comment 'Contacless IC Reader Totals',
   TXN_ERRS             numeric(8,0) comment 'Failed Financial Transactions',
   TXN_TOTS             numeric(8,0) comment 'Total Financial Transactions',
   POWER_NO             numeric(8,0) comment 'Power on/off Cycles',
   primary key (RPT_ID)
);

alter table TMSTTRM_REPORT_MSG comment 'terminal reportmessage';

/*==============================================================*/
/* Table: TMSTTRM_UNREG                                         */
/*==============================================================*/
create table TMSTTRM_UNREG
(
   ID                   numeric(38,0) not null comment 'id',
   TRM_SN               varchar(36) not null comment 'terminal serial number',
   MODEL_ID             varchar(36) not null comment 'terminal model/type id',
   TRM_ID               varchar(36) comment 'terminal id',
   SOURCE_IP            varchar(38),
   LAST_DATE            datetime not null comment 'last date the terminal connection to TMS',
   CREATE_DATE          datetime comment 'create date',
   primary key (ID)
);

alter table TMSTTRM_UNREG comment 'terminal not registered';

/*==============================================================*/
/* Index: UK_TMSTTRM_UNREG                                      */
/*==============================================================*/
create unique index UK_TMSTTRM_UNREG on TMSTTRM_UNREG
(
   TRM_SN,
   MODEL_ID
);

/*==============================================================*/
/* Table: TMSTTRM_USAGE_MSG                                     */
/*==============================================================*/
create table TMSTTRM_USAGE_MSG
(
   ID                   numeric(38,0) not null comment ' id',
   TRM_ID               varchar(36) not null comment 'terminal sn',
   GROUP_ID 			numeric(38,0) not null comment 'group id',
   START_TIME           varchar(32),
   END_TIME             varchar(32),
   ITEM_NAME            varchar(36),
   ITEM_ERRS            numeric(8,0) comment 'Errors',
   ITEM_PENDING         numeric(8,0) comment 'Pending',
   ITEM_TOTS            numeric(8,0) comment 'Totals',
   MSG_CYCLE            varchar(10) comment 'report message cycle',
   CREATE_DATE          datetime comment 'create date',
   primary key (ID)
);

alter table TMSTTRM_USAGE_MSG comment 'terminal usage message accord to the cycle of statistical';

/*==============================================================*/
/* Table: TMSTTRM_USAGE_STS                                     */
/*==============================================================*/
create table TMSTTRM_USAGE_STS
(
   ID                   numeric(38,0) not null comment ' id',
   TRM_ID               varchar(36) not null comment 'terminal sn',
   THD_ID               numeric(38,0),
   START_TIME           datetime comment 'The terminal statistics period',
   END_TIME             datetime,
   ITEM_NAME            varchar(36),
   ITEM_STS             int,
   REPORT_CYCLE         varchar(10) comment 'report cycle',
   CREATE_DATE          datetime comment 'create date',
   primary key (ID)
);

alter table TMSTTRM_USAGE_STS comment 'terminal usage status accord to usage threshold';

/*==============================================================*/
/* Table: TMSTTRM_USAGE_THRESHOLD                               */
/*==============================================================*/
create table TMSTTRM_USAGE_THRESHOLD
(
   THD_ID               numeric(38,0) not null comment 'threshold setting id',
   GROUP_ID             numeric(38,0) not null comment 'refer to a group',
   ITEM_NAME            varchar(36) not null comment 'usage item name',
   THD_VALUE            varchar(38) comment 'threshold value',
   REPORT_CYCLE         varchar(10) comment 'report cycle',
   CREATOR              varchar(36) comment 'creator',
   CREATE_DATE          datetime comment 'create date',
   MODIFIER             varchar(36) comment 'modifier',
   MODIFY_DATE          datetime comment 'modify date',
   primary key (THD_ID)
);

alter table TMSTTRM_USAGE_THRESHOLD comment 'terminal usage threshold setting';

/*==============================================================*/
/* Index: IX_TMSTTRM_USAGE_THRESHOLD                            */
/*==============================================================*/
create unique index IX_TMSTTRM_USAGE_THRESHOLD on TMSTTRM_USAGE_THRESHOLD
(
   GROUP_ID,
   ITEM_NAME
);

create table APP_CLIENT
(
   APP_CLIENT_ID        int not null comment 'app client id  primary key',
   USER_ID              int,
   USER_NAME            varchar(26),
   APP_KEY              varchar(128),
   UPDATED_ON           datetime,
   primary key (APP_CLIENT_ID)
);

alter table APP_CLIENT comment 'app_client';

/*==============================================================*/
/* Index: UNI_APPCLINET_ON_USER_NAME                            */
/*==============================================================*/
create index UNI_APPCLINET_ON_USER_NAME on APP_CLIENT
(
   USER_NAME
);

/*==============================================================*/
/* Index: IDX_APPCLINET_ON_APP_KEY                              */
/*==============================================================*/
create index IDX_APPCLINET_ON_APP_KEY on APP_CLIENT
(
   APP_KEY
);

CREATE TABLE TMSTTRM_LOG (
	ID                   numeric(38,0) not null comment ' id',
	TRM_ID               varchar(36) comment 'terminal sn',
	DEVICE_NAME          varchar(32) comment 'device name',
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
   RKI               	INT COMMENT 'Indicate whether the terminal supports RKI,1: Yes (support), 0: No (not support)',
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

-- Create tmsttrm_billing_detail --
CREATE TABLE tmsttrm_billing_detail
(
   BILLING_DETAIL_ID NUMERIC(38,0) NOT NULL COMMENT 'billing detail id',
   BILLING_DETAIL_GROUP_ID NUMERIC(38,0) NOT NULL COMMENT 'group id',
   BILLING_DETAIL_GROUP_NAME VARCHAR(60) NOT NULL COMMENT 'group name',
   BILLING_DETAIL_MONTH VARCHAR(36) NULL COMMENT 'billing month',
   BILLING_DETAIL_TRM_SN VARCHAR(36) NULL COMMENT 'terminal sn',
   BILLING_DETAIL_TRM_TYPE VARCHAR(36) NULL COMMENT 'terminal type',
   LAST_ACCESS_TIME DATETIME NULL COMMENT 'last accessed time',
   CREATE_TIME DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
   PRIMARY KEY (BILLING_DETAIL_ID)
);