/*==============================================================*/
/* Table: ACLTTRM_GROUP                                         */
/*==============================================================*/
create table ACLTTRM_GROUP 
(
   TRM_ID               VARCHAR2(36)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   constraint PK_ACLTTRM_GROUP primary key (TRM_ID, GROUP_ID)
);

comment on table ACLTTRM_GROUP is
'Terminal-Group access contol list';

comment on column ACLTTRM_GROUP.TRM_ID is
'refer to a terminal';

comment on column ACLTTRM_GROUP.GROUP_ID is
'refer to a group';

/*==============================================================*/
/* Table: ACLTUSER_GROUP                                        */
/*==============================================================*/
create table ACLTUSER_GROUP 
(
   USER_ID              INTEGER              not null,
   GROUP_ID             INTEGER              not null,
   constraint PK_ACLTUSER_GROUP primary key (USER_ID, GROUP_ID)
);

comment on table ACLTUSER_GROUP is
'access control list of group, specifies which users are granted access to groups.';

comment on column ACLTUSER_GROUP.USER_ID is
'user id';

comment on column ACLTUSER_GROUP.GROUP_ID is
'group id';

/*==============================================================*/
/* Table: PUBTAUDITLOG                                          */
/*==============================================================*/
create table PUBTAUDITLOG 
(
   LOG_ID               NUMBER(38,0)         not null,
   USERNAME             VARCHAR2(36)         not null,
   ROLE                 VARCHAR2(256),
   USER_ID              NUMBER(38,0),
   CILENT_IP            VARCHAR2(38),
   ACTION_NAME          VARCHAR2(2000),
   ACTION_DATE          DATE,
   constraint PK_PUBTAUDITLOG primary key (LOG_ID)
);

comment on table PUBTAUDITLOG is
'audit logs';

comment on column PUBTAUDITLOG.LOG_ID is
'audit log id';

comment on column PUBTAUDITLOG.USERNAME is
'username';

comment on column PUBTAUDITLOG.ROLE is
'role';

comment on column PUBTAUDITLOG.USER_ID is
'user id';

comment on column PUBTAUDITLOG.CILENT_IP is
'client ip';

comment on column PUBTAUDITLOG.ACTION_NAME is
'operation name';

comment on column PUBTAUDITLOG.ACTION_DATE is
'action date';

/*==============================================================*/
/* Index: IX_PUBTAUDITLOG_1                                     */
/*==============================================================*/
create index IX_PUBTAUDITLOG_1 on PUBTAUDITLOG (
   ACTION_DATE ASC
);

/*==============================================================*/
/* Table: PUBTAUTHORITY                                         */
/*==============================================================*/
create table PUBTAUTHORITY 
(
   AUTH_ID              INTEGER              not null,
   AUTH_CODE            VARCHAR2(60)         not null,
   AUTH_NAME            VARCHAR2(120)        not null,
   AUTH_DESC            VARCHAR2(255),
   APP_NAME             VARCHAR2(60),
   MODULE_NAME          VARCHAR2(60),
   SORT_ORDER           INTEGER              default 0 not null,
   constraint PK_PUBTAUTHORITY primary key (AUTH_ID)
);

comment on table PUBTAUTHORITY is
'All user permissions defined in the system';

comment on column PUBTAUTHORITY.AUTH_ID is
'authority id';

comment on column PUBTAUTHORITY.AUTH_CODE is
'authority code';

comment on column PUBTAUTHORITY.AUTH_NAME is
'authority name';

comment on column PUBTAUTHORITY.AUTH_DESC is
'description';

comment on column PUBTAUTHORITY.APP_NAME is
'the application for which this authority is to be defined';

comment on column PUBTAUTHORITY.MODULE_NAME is
'the module in the application for which this authority is to be defined';

comment on column PUBTAUTHORITY.SORT_ORDER is
'the sort field that used in a query';

/*==============================================================*/
/* Index: UNI_PUBTAUTHORITY_ON_CODE                             */
/*==============================================================*/
create unique index UNI_PUBTAUTHORITY_ON_CODE on PUBTAUTHORITY (
   AUTH_CODE ASC
);

/*==============================================================*/
/* Table: PUBTCITY                                              */
/*==============================================================*/
create table PUBTCITY 
(
   CITY_ID              NUMBER(38,0)         not null,
   PROVINCE_ID          NUMBER(38,0)         not null,
   CITY_NAME            VARCHAR2(128)        not null,
   ABBR_NAME            VARCHAR2(36),
   CITY_DESC            VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_PUBTCITY primary key (CITY_ID)
);

comment on table PUBTCITY is
'city';

comment on column PUBTCITY.PROVINCE_ID is
'state/province id';

comment on column PUBTCITY.CITY_NAME is
'city name';

comment on column PUBTCITY.ABBR_NAME is
'city abbr name';

comment on column PUBTCITY.CITY_DESC is
'description';

comment on column PUBTCITY.CREATOR is
'creator';

comment on column PUBTCITY.CREATE_DATE is
'created date';

comment on column PUBTCITY.MODIFIER is
'modifier';

comment on column PUBTCITY.MODIFY_DATE is
'modified date';

/*==============================================================*/
/* Index: IX_PUBTCITY                                           */
/*==============================================================*/
create unique index IX_PUBTCITY on PUBTCITY (
   PROVINCE_ID ASC,
   CITY_NAME ASC
);

/*==============================================================*/
/* Table: PUBTCOUNTRY                                           */
/*==============================================================*/
create table PUBTCOUNTRY 
(
   COUNTRY_ID           NUMBER(38,0)         not null,
   COUNTRY_NAME         VARCHAR2(128)        not null,
   COUNTRY_CODE         VARCHAR2(10),
   ABBR_NAME            VARCHAR2(36),
   COUNTRY_DESC         VARCHAR2(256),
   TRANS_CCY_CODE       CHAR(4),
   TRANS_CCY_EXP        INTEGER,
   TRANS_REFER_CCY_CODE CHAR(4),
   TRANS_REFER_CCY_EXP  INTEGER,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_PUBTCOUNTRY primary key (COUNTRY_ID)
);

comment on table PUBTCOUNTRY is
'country or region';

comment on column PUBTCOUNTRY.COUNTRY_ID is
'country id';

comment on column PUBTCOUNTRY.COUNTRY_NAME is
'country name';

comment on column PUBTCOUNTRY.COUNTRY_CODE is
'country code';

comment on column PUBTCOUNTRY.ABBR_NAME is
'country abbr name';

comment on column PUBTCOUNTRY.COUNTRY_DESC is
'description';

comment on column PUBTCOUNTRY.TRANS_CCY_CODE is
'Trans Currency code';

comment on column PUBTCOUNTRY.TRANS_CCY_EXP is
'Trans Currency Exp(numerical precision)';

comment on column PUBTCOUNTRY.TRANS_REFER_CCY_CODE is
'Trans Refer Currency code';

comment on column PUBTCOUNTRY.TRANS_REFER_CCY_EXP is
'Trans Refer Currency Exp(numerical precision)';

comment on column PUBTCOUNTRY.CREATOR is
'creator';

comment on column PUBTCOUNTRY.CREATE_DATE is
'created date';

comment on column PUBTCOUNTRY.MODIFIER is
'modifier';

comment on column PUBTCOUNTRY.MODIFY_DATE is
'modified date';

/*==============================================================*/
/* Index: IX_PUBTCOUNTRY                                        */
/*==============================================================*/
create unique index IX_PUBTCOUNTRY on PUBTCOUNTRY (
   COUNTRY_NAME ASC
);

/*==============================================================*/
/* Table: PUBTDICT                                              */
/*==============================================================*/
create table PUBTDICT 
(
   ITEM_ID              NUMBER(38,0)         not null,
   ITEM_TYPE            VARCHAR2(64)         not null,
   ITEM_CODE            VARCHAR2(128)        not null,
   ITEM_NAME            VARCHAR2(128)        not null,
   ITEM_ATTR            VARCHAR2(256),
   ITEM_DESC            VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_PUBTDICT primary key (ITEM_ID)
);

comment on table PUBTDICT is
'data dictionary';

comment on column PUBTDICT.ITEM_ID is
'item id';

comment on column PUBTDICT.ITEM_TYPE is
'item type';

comment on column PUBTDICT.ITEM_CODE is
'item code';

comment on column PUBTDICT.ITEM_NAME is
'item name';

comment on column PUBTDICT.ITEM_ATTR is
'the attribute of data item';

comment on column PUBTDICT.ITEM_DESC is
'item description';

comment on column PUBTDICT.CREATOR is
'creator';

comment on column PUBTDICT.CREATE_DATE is
'creator date';

comment on column PUBTDICT.MODIFIER is
'modifier';

comment on column PUBTDICT.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: PUBTDICT_IX1                                          */
/*==============================================================*/
create unique index PUBTDICT_IX1 on PUBTDICT (
   ITEM_TYPE ASC,
   ITEM_CODE ASC
);

/*==============================================================*/
/* Index: PUBTDICT_IX2                                          */
/*==============================================================*/
create index PUBTDICT_IX2 on PUBTDICT (
   ITEM_TYPE ASC,
   MODIFY_DATE DESC
);

/*==============================================================*/
/* Table: PUBTGROUP                                             */
/*==============================================================*/
create table PUBTGROUP 
(
   GROUP_ID             NUMBER(38,0)         not null,
   GROUP_CODE           VARCHAR2(38)         not null,
   GROUP_NAME           VARCHAR2(60)         not null,
   PARENT_ID            NUMBER(38,0),
   COUNTRY              VARCHAR2(128),
   PROVINCE             VARCHAR2(128),
   CITY                 VARCHAR2(128),
   ZIP_CODE             VARCHAR2(10),
   TIME_ZONE            VARCHAR2(36),
   DAYLIGHT_SAVING      SMALLINT,
   ADDRESS              VARCHAR2(256),
   DESCRIPTION          VARCHAR2(4000),
   ID_PATH              VARCHAR2(800),
   NAME_PATH            VARCHAR2(2000),
   TREE_DEPTH           INTEGER              not null,
   SUB_COUNT            INTEGER              default 0 not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_PUBTGROUP primary key (GROUP_ID)
);

comment on table PUBTGROUP is
'groups';

comment on column PUBTGROUP.GROUP_ID is
'group id';

comment on column PUBTGROUP.GROUP_CODE is
'group code';

comment on column PUBTGROUP.GROUP_NAME is
'group name';

comment on column PUBTGROUP.PARENT_ID is
'refer to parent group';

comment on column PUBTGROUP.COUNTRY is
'refer to a country';

comment on column PUBTGROUP.PROVINCE is
'refer to a state/province';

comment on column PUBTGROUP.CITY is
'refer to a city';

comment on column PUBTGROUP.ZIP_CODE is
'ZIP/POST code';

comment on column PUBTGROUP.ADDRESS is
'address/location';

comment on column PUBTGROUP.DESCRIPTION is
'descrption';

comment on column PUBTGROUP.ID_PATH is
'full path of group id, e.g. "1/2/5"';

comment on column PUBTGROUP.NAME_PATH is
'full path of group name, e.g. "pax/group1/group2"';

comment on column PUBTGROUP.TREE_DEPTH is
'the depth of a group tree';

comment on column PUBTGROUP.CREATOR is
'creator';

comment on column PUBTGROUP.CREATE_DATE is
'create date';

comment on column PUBTGROUP.MODIFIER is
'modifier';

comment on column PUBTGROUP.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_PUBTGROUP_NAME                                     */
/*==============================================================*/
create unique index IX_PUBTGROUP_NAME on PUBTGROUP (
   PARENT_ID ASC,
   GROUP_NAME ASC
);

/*==============================================================*/
/* Table: PUBTGROUP_PARENTS                                     */
/*==============================================================*/
create table PUBTGROUP_PARENTS 
(
   GROUP_ID             NUMBER(38,0)         not null,
   PARENT_ID            NUMBER(38,0)         not null,
   constraint PK_PUBTGROUP_PARENTS primary key (GROUP_ID, PARENT_ID)
);

comment on table PUBTGROUP_PARENTS is
'group''s parents';

comment on column PUBTGROUP_PARENTS.GROUP_ID is
'refer to a group';

comment on column PUBTGROUP_PARENTS.PARENT_ID is
'refer to a parent group';

/*==============================================================*/
/* Index: IX_PUBTGROUP_PARENTS                                  */
/*==============================================================*/
create unique index IX_PUBTGROUP_PARENTS on PUBTGROUP_PARENTS (
   PARENT_ID ASC,
   GROUP_ID ASC
);

/*==============================================================*/
/* Table: PUBTPROVINCE                                          */
/*==============================================================*/
create table PUBTPROVINCE 
(
   PROVINCE_ID          NUMBER(38,0)         not null,
   COUNTRY_ID           NUMBER(38,0)         not null,
   PROVINCE_NAME        VARCHAR2(128)        not null,
   PROVINCE_CODE        VARCHAR2(10),
   ABBR_NAME            VARCHAR2(36),
   PROVINCE_DESC        VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_PUBTPROVINCE primary key (PROVINCE_ID)
);

comment on table PUBTPROVINCE is
'State/Province';

comment on column PUBTPROVINCE.PROVINCE_ID is
'state/province id';

comment on column PUBTPROVINCE.COUNTRY_ID is
'country id';

comment on column PUBTPROVINCE.PROVINCE_NAME is
'state/province name';

comment on column PUBTPROVINCE.PROVINCE_CODE is
'state/province code';

comment on column PUBTPROVINCE.ABBR_NAME is
'state/province abbr name';

comment on column PUBTPROVINCE.PROVINCE_DESC is
'description';

comment on column PUBTPROVINCE.CREATOR is
'creator';

comment on column PUBTPROVINCE.CREATE_DATE is
'created date';

comment on column PUBTPROVINCE.MODIFIER is
'modifier';

comment on column PUBTPROVINCE.MODIFY_DATE is
'modified date';

/*==============================================================*/
/* Index: IX_PUBTPROVINCE                                       */
/*==============================================================*/
create unique index IX_PUBTPROVINCE on PUBTPROVINCE (
   COUNTRY_ID ASC,
   PROVINCE_NAME ASC
);

/*==============================================================*/
/* Table: PUBTPWDCHGHST                                         */
/*==============================================================*/
create table PUBTPWDCHGHST 
(
   HST_ID               INTEGER              not null,
   USER_ID              INTEGER              not null,
   CHANGED_DATE         DATE                 not null,
   USER_PWD             VARCHAR2(128),
   ENCRYPT_SALT         VARCHAR2(128),
   ENCRYPT_ALG          VARCHAR2(16),
   ENCRYPT_ITERATION_COUNT SMALLINT             default 0,
   constraint PK_PUBTPWDCHGHST primary key (HST_ID)
);

comment on table PUBTPWDCHGHST is
'password change history of users. 24 passwords should be remembered for each user.';

comment on column PUBTPWDCHGHST.HST_ID is
'password history id';

comment on column PUBTPWDCHGHST.USER_ID is
'user id';

comment on column PUBTPWDCHGHST.CHANGED_DATE is
'the password changed date';

comment on column PUBTPWDCHGHST.USER_PWD is
'the encoded user password';

comment on column PUBTPWDCHGHST.ENCRYPT_SALT is
'cryptographic salt. A new salt is randomly generated for each password.';

comment on column PUBTPWDCHGHST.ENCRYPT_ALG is
'the digest algorithm which has been used to encrypt the user password';

comment on column PUBTPWDCHGHST.ENCRYPT_ITERATION_COUNT is
'The iteration count refers to the number of times that the hash function is applied to its own results.';

/*==============================================================*/
/* Index: UNI_PUBTPWDCHGHST_ON_DATE                             */
/*==============================================================*/
create unique index UNI_PUBTPWDCHGHST_ON_DATE on PUBTPWDCHGHST (
   USER_ID ASC,
   CHANGED_DATE DESC
);

/*==============================================================*/
/* Table: PUBTPWDRESETTOKEN                                     */
/*==============================================================*/
create table PUBTPWDRESETTOKEN 
(
   USER_ID              INTEGER              not null,
   RESET_PWD_TOKEN      VARCHAR2(128),
   TOKEN_ISSUE_DATE     DATE,
   MAX_TOKEN_AGE        INTEGER,
   IS_TOKEN_USED        SMALLINT,
   constraint PK_PUBTPWDRESETTOKEN primary key (USER_ID)
);

comment on table PUBTPWDRESETTOKEN is
'password reset token, issued to an individual to allow them to reset their password.';

comment on column PUBTPWDRESETTOKEN.USER_ID is
'user id';

comment on column PUBTPWDRESETTOKEN.RESET_PWD_TOKEN is
'password reset token, the token itfelf is the 16-character alpha-numeric code.';

comment on column PUBTPWDRESETTOKEN.TOKEN_ISSUE_DATE is
'the token was assigned to at the time of issue';

comment on column PUBTPWDRESETTOKEN.MAX_TOKEN_AGE is
'For security, Password Reset Tokens are time-limited, and will expire after that time, whether or not they have been used';

comment on column PUBTPWDRESETTOKEN.IS_TOKEN_USED is
'tokens can only be used once';

/*==============================================================*/
/* Table: PUBTROLE                                              */
/*==============================================================*/
create table PUBTROLE 
(
   ROLE_ID              INTEGER              not null,
   ROLE_NAME            VARCHAR2(60)         not null,
   ROLE_DESC            VARCHAR2(255),
   APP_NAME             VARCHAR2(60),
   IS_SYS               SMALLINT             default 0 not null,
   CREATOR              VARCHAR2(36)         not null,
   CREATE_DATE          DATE                 not null,
   MODIFIER             VARCHAR2(36)         not null,
   MODIFY_DATE          DATE                 not null,
   constraint PK_PUBTROLE primary key (ROLE_ID)
);

comment on table PUBTROLE is
'All roles have been defined in the system';

comment on column PUBTROLE.ROLE_ID is
'role id';

comment on column PUBTROLE.ROLE_NAME is
'role name';

comment on column PUBTROLE.ROLE_DESC is
'description';

comment on column PUBTROLE.APP_NAME is
'the application in which the role is to be defined';

comment on column PUBTROLE.IS_SYS is
'a boolean flag indicating whether the role is a system role';

comment on column PUBTROLE.CREATOR is
'the creator';

comment on column PUBTROLE.CREATE_DATE is
'the creation date';

comment on column PUBTROLE.MODIFIER is
'the modifier';

comment on column PUBTROLE.MODIFY_DATE is
'the last modification time';

/*==============================================================*/
/* Table: PUBTROLE_AUTHORITY                                    */
/*==============================================================*/
create table PUBTROLE_AUTHORITY 
(
   REL_ID               INTEGER              not null,
   ROLE_ID              INTEGER              not null,
   AUTH_ID              INTEGER              not null,
   constraint PK_PUBTROLE_AUTHORITY primary key (REL_ID)
);

comment on table PUBTROLE_AUTHORITY is
'The association of a role to each authority';

comment on column PUBTROLE_AUTHORITY.REL_ID is
'the relation id';

comment on column PUBTROLE_AUTHORITY.ROLE_ID is
'roel id';

comment on column PUBTROLE_AUTHORITY.AUTH_ID is
'authority id';

/*==============================================================*/
/* Index: UNI_PUBRROLE_AUTHORITY                                */
/*==============================================================*/
create unique index UNI_PUBRROLE_AUTHORITY on PUBTROLE_AUTHORITY (
   ROLE_ID ASC,
   AUTH_ID ASC
);

/*==============================================================*/
/* Table: PUBTSEQUENCE                                          */
/*==============================================================*/
create table PUBTSEQUENCE 
(
   SEQ_NAME             VARCHAR2(60)         not null,
   NEXT_VALUE           NUMBER(38,0)         default 1 not null,
   constraint PK_PUBTSEQUENCE primary key (SEQ_NAME)
);

comment on table PUBTSEQUENCE is
'sequnences table';

comment on column PUBTSEQUENCE.SEQ_NAME is
'sequence name';

comment on column PUBTSEQUENCE.NEXT_VALUE is
'next value, default 1';

/*==============================================================*/
/* Table: PUBTSYSCONF                                           */
/*==============================================================*/
create table PUBTSYSCONF 
(
   CATEGORY             VARCHAR2(36)         not null,
   PARA_KEY             VARCHAR2(128)        not null,
   PARA_VALUE           VARCHAR2(4000),
   constraint PK_PUBTSYSCONF primary key (CATEGORY, PARA_KEY)
);

comment on table PUBTSYSCONF is
'system config table';

comment on column PUBTSYSCONF.CATEGORY is
'parameter category';

comment on column PUBTSYSCONF.PARA_KEY is
'parameter key';

comment on column PUBTSYSCONF.PARA_VALUE is
'parameter value';

/*==============================================================*/
/* Table: PUBTUSER                                              */
/*==============================================================*/
create table PUBTUSER 
(
   USER_ID              INTEGER              not null,
   USERNAME             VARCHAR2(36)         not null,
   FULLNAME             VARCHAR2(128),
   EMAIL                VARCHAR2(128),
   PHONE                VARCHAR2(60),
   COUNTRY              VARCHAR2(128),
   PROVINCE             VARCHAR2(128),
   CITY                 VARCHAR2(128),
   ZIP_CODE             VARCHAR2(10),
   ADDRESS              VARCHAR2(255),
   USER_DESC            VARCHAR2(255),
   DIRECTORY            VARCHAR2(64),
   IS_SYS               SMALLINT             default 0 not null,
   IS_LDAP              SMALLINT             default 0 not null,
   IS_LOCKED            SMALLINT             default 0 not null,
   LOCKED_DATE          DATE,
   IS_ENABLED           SMALLINT             default 1 not null,
   ENABLED_DATE         DATE,
   DISABLED_AFTER_DAYS  INTEGER              default 0 not null,
   REMOVED_AFTER_DAYS   INTEGER              default 0 not null,
   LAST_LOGIN_DATE      DATE,
   ACC_EXPIRY_DATE      DATE,
   USER_PWD             VARCHAR2(128),
   ENCRYPT_SALT         VARCHAR2(128),
   ENCRYPT_ALG          VARCHAR2(16),
   ENCRYPT_ITERATION_COUNT SMALLINT             default 0,
   LAST_PWD_CHG_DATE    DATE,
   MAX_PWD_AGE          INTEGER              default 0,
   FORCE_CHG_PWD        SMALLINT             default 0,
   FAILED_LOGIN_COUNT   INTEGER              default 0,
   PREF_DASHBOARD_REAL  VARCHAR2(128)        not null,
   PREF_DASHBOARD_USAGE VARCHAR2(255)        not null,
   CREATOR              VARCHAR2(36)         not null,
   CREATE_DATE          DATE                 not null,
   MODIFIER             VARCHAR2(36)         not null,
   MODIFY_DATE          DATE                 not null,
   constraint PK_PUBTUSER primary key (USER_ID)
);

comment on table PUBTUSER is
'All users defined in the system';

comment on column PUBTUSER.USER_ID is
'user id';

comment on column PUBTUSER.USERNAME is
'user name ';

comment on column PUBTUSER.FULLNAME is
'user full name';

comment on column PUBTUSER.EMAIL is
'user''s email address';

comment on column PUBTUSER.PHONE is
'user''s phone number';

comment on column PUBTUSER.COUNTRY is
'user''s country';

comment on column PUBTUSER.PROVINCE is
'user''s state/province';

comment on column PUBTUSER.CITY is
'user''s city';

comment on column PUBTUSER.ZIP_CODE is
'user''s zip/post code';

comment on column PUBTUSER.ADDRESS is
'user''s address/location';

comment on column PUBTUSER.USER_DESC is
'user description';

comment on column PUBTUSER.DIRECTORY is
'the directory to find the authenticated user. the valid values: ''LDAP'', ''Local''';

comment on column PUBTUSER.IS_SYS is
'a boolean flag indicating whether the user is a system user. A system user is created while the system initialization and cannot be removed.';

comment on column PUBTUSER.IS_LDAP is
'a boolean flag indicating whether the user is authenticated by LDAP directory';

comment on column PUBTUSER.IS_LOCKED is
'a boolean flag indicating whether the user acount is locked. An account can be locked automatically based on the Account Lockout Policy.';

comment on column PUBTUSER.LOCKED_DATE is
'date and time when the user account has been locked';

comment on column PUBTUSER.IS_ENABLED is
'a boolean flag indicating whether the user acount is disabled. An account can be disabled provisionally for security and can be re-enabled upon need.';

comment on column PUBTUSER.ENABLED_DATE is
'date and time when the user has been enabled';

comment on column PUBTUSER.DISABLED_AFTER_DAYS is
'the inactive user is disabled after this days.';

comment on column PUBTUSER.REMOVED_AFTER_DAYS is
'the inactinve user is removed after this days.';

comment on column PUBTUSER.LAST_LOGIN_DATE is
'last login date';

comment on column PUBTUSER.ACC_EXPIRY_DATE is
'account expired date.when the time is reached, the account automatically expires.';

comment on column PUBTUSER.USER_PWD is
'the encoded user password';

comment on column PUBTUSER.ENCRYPT_SALT is
'cryptographic salt. A new salt is randomly generated for each password.';

comment on column PUBTUSER.ENCRYPT_ALG is
'the digest algorithm which has been used to encrypt the user password';

comment on column PUBTUSER.ENCRYPT_ITERATION_COUNT is
'the iteration count refers to the number of times that the hash function is applied to its own results.';

comment on column PUBTUSER.LAST_PWD_CHG_DATE is
'date of the last password change';

comment on column PUBTUSER.MAX_PWD_AGE is
'maximum password age. users are required to change their password when reaching the maxmum age.';

comment on column PUBTUSER.FORCE_CHG_PWD is
'An option to force a user to change password on next login';

comment on column PUBTUSER.FAILED_LOGIN_COUNT is
'failed login count';

comment on column PUBTUSER.PREF_DASHBOARD_REAL is
'dashboard preferred setting';

comment on column PUBTUSER.PREF_DASHBOARD_USAGE is
'dashboard preferred setting';

comment on column PUBTUSER.CREATOR is
'the creator';

comment on column PUBTUSER.CREATE_DATE is
'the creation date';

comment on column PUBTUSER.MODIFIER is
'the modifier';

comment on column PUBTUSER.MODIFY_DATE is
'the last modification time';

/*==============================================================*/
/* Index: UNI_PUBTUSER_ON_USERNAME                              */
/*==============================================================*/
create unique index UNI_PUBTUSER_ON_USERNAME on PUBTUSER (
   USERNAME ASC
);

/*==============================================================*/
/* Index: IDX_PUBTUSER_ON_DATE                                  */
/*==============================================================*/
create index IDX_PUBTUSER_ON_DATE on PUBTUSER (
   MODIFY_DATE ASC
);

/*==============================================================*/
/* Table: PUBTUSERLOG                                           */
/*==============================================================*/
create table PUBTUSERLOG 
(
   LOG_ID               NUMBER(38,0)         not null,
   USERNAME             VARCHAR2(36)         not null,
   ROLE                 VARCHAR2(600),
   CILENT_IP            VARCHAR2(38),
   EVENT_TIME           DATE                 not null,
   EVENT_ACTION         VARCHAR2(4000)       not null,
   constraint PK_PUBTUSERLOG primary key (LOG_ID)
);

comment on table PUBTUSERLOG is
'log for user action';

comment on column PUBTUSERLOG.LOG_ID is
'audit log id';

comment on column PUBTUSERLOG.USERNAME is
'user name';

comment on column PUBTUSERLOG.ROLE is
'roles of the user';

comment on column PUBTUSERLOG.CILENT_IP is
'user''s client ip address';

comment on column PUBTUSERLOG.EVENT_TIME is
'the date & time happen the action';

comment on column PUBTUSERLOG.EVENT_ACTION is
'event action';

/*==============================================================*/
/* Index: IX_PUBTUSERLOG_1                                      */
/*==============================================================*/
create index IX_PUBTUSERLOG_1 on PUBTUSERLOG (
   EVENT_TIME ASC
);

/*==============================================================*/
/* Table: PUBTUSER_GROUP                                        */
/*==============================================================*/
create table PUBTUSER_GROUP 
(
   REL_ID               INTEGER              not null,
   USER_ID              INTEGER              not null,
   GROUP_ID             INTEGER              not null,
   IS_DEFAULT           SMALLINT             default 0 not null,
   LAST_ACCESS_TIME     DATE,
   constraint PK_PUBTUSER_GROUP primary key (REL_ID)
);

comment on table PUBTUSER_GROUP is
'The association of a user to each group.';

comment on column PUBTUSER_GROUP.REL_ID is
'relation id';

comment on column PUBTUSER_GROUP.USER_ID is
'user id';

comment on column PUBTUSER_GROUP.GROUP_ID is
'group id';

comment on column PUBTUSER_GROUP.IS_DEFAULT is
'a boolean flag indicating whether the it is the default group. The default group is the directory that a user is first in after loggin into the system.';

comment on column PUBTUSER_GROUP.LAST_ACCESS_TIME is
'the group last access time';

/*==============================================================*/
/* Index: UNI_PUBTUSER_GROUP                                    */
/*==============================================================*/
create unique index UNI_PUBTUSER_GROUP on PUBTUSER_GROUP (
   USER_ID ASC,
   GROUP_ID ASC
);

/*==============================================================*/
/* Table: PUBTUSER_ROLE                                         */
/*==============================================================*/
create table PUBTUSER_ROLE 
(
   REL_ID               INTEGER              not null,
   USER_ID              INTEGER              not null,
   ROLE_ID              INTEGER              not null,
   constraint PK_PUBTUSER_ROLE primary key (REL_ID)
);

comment on table PUBTUSER_ROLE is
'The association of a user to each role';

comment on column PUBTUSER_ROLE.REL_ID is
'the relation id';

comment on column PUBTUSER_ROLE.USER_ID is
'user id';

comment on column PUBTUSER_ROLE.ROLE_ID is
'role id';

/*==============================================================*/
/* Index: UNI_PUBTUSER_ROLE                                     */
/*==============================================================*/
create unique index UNI_PUBTUSER_ROLE on PUBTUSER_ROLE (
   USER_ID ASC,
   ROLE_ID ASC
);

/*==============================================================*/
/* Table: TMSPTSNS                                              */
/*==============================================================*/
create table TMSPTSNS 
(
   BATCH_ID             NUMBER(38,0)         not null,
   TSN                  VARCHAR2(36)         not null,
   constraint PK_TMSPTSNS primary key (BATCH_ID, TSN)
);

comment on table TMSPTSNS is
'temporary table. terminal sn batch ';

comment on column TMSPTSNS.BATCH_ID is
'batch id';

comment on column TMSPTSNS.TSN is
'terminal sn';

/*==============================================================*/
/* Table: TMSTALERT_CONDITION                                   */
/*==============================================================*/
create table TMSTALERT_CONDITION 
(
   COND_ID              NUMBER(38,0)         not null,
   ALERT_ITEM           VARCHAR2(36)         not null,
   SETTING_ID           NUMBER(38,0),
   ALERT_SEVERITY       INTEGER,
   ALERT_THRESHOLD      VARCHAR2(38),
   ALERT_MESSAGE        VARCHAR2(255),
   TOPIC_ARN            VARCHAR2(255),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTALERT_CONDITION primary key (COND_ID)
);

comment on table TMSTALERT_CONDITION is
'alert condition';

comment on column TMSTALERT_CONDITION.COND_ID is
'alert condition id';

comment on column TMSTALERT_CONDITION.ALERT_ITEM is
'alert item name';

comment on column TMSTALERT_CONDITION.SETTING_ID is
'refer to a alert setting';

comment on column TMSTALERT_CONDITION.ALERT_SEVERITY is
'alert serverity, 1-info, 2-warn, 3-critical';

comment on column TMSTALERT_CONDITION.ALERT_THRESHOLD is
'info threshold';

comment on column TMSTALERT_CONDITION.ALERT_MESSAGE is
'alert message';

comment on column TMSTALERT_CONDITION.TOPIC_ARN is
'Message Topic';

comment on column TMSTALERT_CONDITION.CREATOR is
'creator';

comment on column TMSTALERT_CONDITION.CREATE_DATE is
'create date';

comment on column TMSTALERT_CONDITION.MODIFIER is
'modifier';

comment on column TMSTALERT_CONDITION.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTALERT_CONDITION                                */
/*==============================================================*/
create index IX_TMSTALERT_CONDITION on TMSTALERT_CONDITION (
   SETTING_ID ASC
);

/*==============================================================*/
/* Table: TMSTALERT_EVENT                                       */
/*==============================================================*/
create table TMSTALERT_EVENT 
(
   EVENT_ID             NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   COND_ID              NUMBER(38,0)         not null,
   ALERT_TM             DATE,
   ALERT_VALUE          VARCHAR2(128),
   ALERT_SEVERITY       INTEGER,
   ALERT_MESSAGE        VARCHAR2(4000),
   constraint PK_TMSTALERT_EVENT primary key (EVENT_ID)
);

comment on table TMSTALERT_EVENT is
'alert event';

comment on column TMSTALERT_EVENT.EVENT_ID is
'alert event id';

comment on column TMSTALERT_EVENT.GROUP_ID is
'refer to a group';

comment on column TMSTALERT_EVENT.COND_ID is
'refer to a alert condition';

comment on column TMSTALERT_EVENT.ALERT_TM is
'alert date & time';

comment on column TMSTALERT_EVENT.ALERT_VALUE is
'value trigger the alert';

comment on column TMSTALERT_EVENT.ALERT_SEVERITY is
'alert servierty';

comment on column TMSTALERT_EVENT.ALERT_MESSAGE is
'alert message';

/*==============================================================*/
/* Table: TMSTALERT_OFF                                         */
/*==============================================================*/
create table TMSTALERT_OFF 
(
   OFF_ID               NUMBER(38,0)         not null,
   SETTING_ID           NUMBER(38,0)         not null,
   REPEAT_TYPE          VARCHAR2(10)         not null,
   OFF_DATE             VARCHAR2(20),
   OFF_START_TIME       CHAR(5),
   OFF_END_TIME         CHAR(5),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTALERT_OFF primary key (OFF_ID)
);

comment on table TMSTALERT_OFF is
'alert off setting';

comment on column TMSTALERT_OFF.SETTING_ID is
'refer to a alert setting';

comment on column TMSTALERT_OFF.REPEAT_TYPE is
'repeat, 0-one time,  1-every day, 2-every week, 3 - every month, 4 - every year,';

comment on column TMSTALERT_OFF.OFF_DATE is
'alert off date';

comment on column TMSTALERT_OFF.OFF_START_TIME is
'alert off start time';

comment on column TMSTALERT_OFF.OFF_END_TIME is
'alert off end time';

comment on column TMSTALERT_OFF.CREATOR is
'creator';

comment on column TMSTALERT_OFF.CREATE_DATE is
'create date';

comment on column TMSTALERT_OFF.MODIFIER is
'modifier';

comment on column TMSTALERT_OFF.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTALERT_OFF                                      */
/*==============================================================*/
create index IX_TMSTALERT_OFF on TMSTALERT_OFF (
   SETTING_ID ASC
);

/*==============================================================*/
/* Table: TMSTALERT_PUBLISH_EMAIL                               */
/*==============================================================*/
create table TMSTALERT_PUBLISH_EMAIL 
(
   PUBLISH_ID           NUMBER(38,0)         not null,
   EVENT_ID             NUMBER(38,0)         not null,
   COND_ID              NUMBER(38,0)         not null,
   PULISH_TM            DATE,
   USER_ID              NUMBER(38,0)         not null,
   constraint PK_TMSTALERT_PUBLISH_EMAIL primary key (PUBLISH_ID)
);

comment on table TMSTALERT_PUBLISH_EMAIL is
'alert -  publish email history';

comment on column TMSTALERT_PUBLISH_EMAIL.EVENT_ID is
'alert event id';

comment on column TMSTALERT_PUBLISH_EMAIL.COND_ID is
'refer to a alert condition';

comment on column TMSTALERT_PUBLISH_EMAIL.PULISH_TM is
'alert date & time';

/*==============================================================*/
/* Table: TMSTALERT_PUBLISH_SMS                                 */
/*==============================================================*/
create table TMSTALERT_PUBLISH_SMS 
(
   PUBLISH_ID           NUMBER(38,0)         not null,
   EVENT_ID             NUMBER(38,0)         not null,
   COND_ID              NUMBER(38,0)         not null,
   PULISH_TM            DATE,
   USER_ID              NUMBER(38,0)         not null,
   constraint PK_TMSTALERT_PUBLISH_SMS primary key (PUBLISH_ID)
);

comment on table TMSTALERT_PUBLISH_SMS is
'alert -  publish SMS history';

comment on column TMSTALERT_PUBLISH_SMS.EVENT_ID is
'alert event id';

comment on column TMSTALERT_PUBLISH_SMS.COND_ID is
'refer to a alert condition';

comment on column TMSTALERT_PUBLISH_SMS.PULISH_TM is
'alert date & time';

/*==============================================================*/
/* Table: TMSTALERT_SBSCRB                                      */
/*==============================================================*/
create table TMSTALERT_SBSCRB 
(
   SBSCRB_ID            NUMBER(38,0)         not null,
   USER_ID              NUMBER(38,0)         not null,
   COND_ID              NUMBER(38,0)         not null,
   SMS                  CHAR(1),
   EMAIL                CHAR(1),
   SUBSCRIBE_ARN        VARCHAR2(255),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTALERT_SBSCRB primary key (SBSCRB_ID)
);

comment on table TMSTALERT_SBSCRB is
'subscribe alert';

comment on column TMSTALERT_SBSCRB.COND_ID is
'alert condition id';

comment on column TMSTALERT_SBSCRB.SMS is
'subscribe SMS';

comment on column TMSTALERT_SBSCRB.EMAIL is
'subscribe EMAIL';

comment on column TMSTALERT_SBSCRB.CREATOR is
'creator';

comment on column TMSTALERT_SBSCRB.CREATE_DATE is
'create date';

comment on column TMSTALERT_SBSCRB.MODIFIER is
'modifier';

comment on column TMSTALERT_SBSCRB.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Table: TMSTALERT_SETTING                                     */
/*==============================================================*/
create table TMSTALERT_SETTING 
(
   SETTING_ID           NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTALERT_SETTING primary key (SETTING_ID)
);

comment on table TMSTALERT_SETTING is
'alert setting';

comment on column TMSTALERT_SETTING.SETTING_ID is
'condition id';

comment on column TMSTALERT_SETTING.GROUP_ID is
'refer to a group';

comment on column TMSTALERT_SETTING.CREATOR is
'creator';

comment on column TMSTALERT_SETTING.CREATE_DATE is
'create date';

comment on column TMSTALERT_SETTING.MODIFIER is
'modifier';

comment on column TMSTALERT_SETTING.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTALERT_SETTING                                  */
/*==============================================================*/
create unique index IX_TMSTALERT_SETTING on TMSTALERT_SETTING (
   GROUP_ID ASC
);

/*==============================================================*/
/* Table: TMSTDELFILELOG                                        */
/*==============================================================*/
create table TMSTDELFILELOG 
(
   OPTLOG_ID            NUMBER(38,0)         not null,
   FILE_PATH            VARCHAR2(4000)       not null,
   OPT_TIME             DATE                 not null,
   STATUS               VARCHAR2(10)         not null,
   constraint PK_TMSTDELFILELOG primary key (OPTLOG_ID)
);

comment on table TMSTDELFILELOG is
'record pkg delete log';

/*==============================================================*/
/* Table: TMSTDEPLOY                                            */
/*==============================================================*/
create table TMSTDEPLOY 
(
   DEPLOY_ID            NUMBER(38,0)         not null,
   MODEL_ID             VARCHAR2(36),
   PKG_ID               NUMBER(38,0)         not null,
   TIME_ZONE            VARCHAR2(36),
   SCHEMA_ID            NUMBER(38,0),
   PARAM_VERSION        VARCHAR2(64),
   PARAM_SET            VARCHAR2(60),
   PARAM_STATUS         INTEGER              default 0,
   DEPLOY_STATUS        INTEGER              not null,
   DEPLOY_SOURCE        VARCHAR2(60),
   DEPLOY_SOURCE_ID     NUMBER(38,0),
   DEPLOY_SOURCE_GROUP_ID NUMBER(38,0),
   DAYLIGHT_SAVING      SMALLINT,
   DWNL_START_TM        DATE,
   DWNL_END_TM          DATE,
   DWNL_RETRY_COUNT     INTEGER,
   DWNL_PERIOD          INTEGER,
   DWNL_MAX_NUM         INTEGER,
   ACTV_START_TM        DATE,
   ACTV_RETRY_COUNT     INTEGER,
   ACTV_END_TM          DATE,
   FORCE_UPDATE         INTEGER,
   ONLY_PARAM           INTEGER,
   DWNL_ORDER           INTEGER              default 0,
   DELETED_WHEN_DONE    INTEGER              default 1 not null,
   DESCRIPTION          VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTDEPLOY primary key (DEPLOY_ID)
);

comment on table TMSTDEPLOY is
'deploy';

comment on column TMSTDEPLOY.DEPLOY_ID is
'terminal ID/SN';

comment on column TMSTDEPLOY.MODEL_ID is
'refer to a terminal type';

comment on column TMSTDEPLOY.PKG_ID is
'refer to a package';

comment on column TMSTDEPLOY.TIME_ZONE is
'time zone';

comment on column TMSTDEPLOY.SCHEMA_ID is
'refer to a package parameter template/schema';

comment on column TMSTDEPLOY.PARAM_VERSION is
'param version';

comment on column TMSTDEPLOY.PARAM_SET is
'param set';

comment on column TMSTDEPLOY.PARAM_STATUS is
'if param file has been generated';

comment on column TMSTDEPLOY.DEPLOY_STATUS is
'deploy status';

comment on column TMSTDEPLOY.DEPLOY_SOURCE is
'the deploy source terminal self or group';

comment on column TMSTDEPLOY.DEPLOY_SOURCE_ID is
'the deploy source id';

comment on column TMSTDEPLOY.DEPLOY_SOURCE_GROUP_ID is
'refer to group id';

comment on column TMSTDEPLOY.DAYLIGHT_SAVING is
'daylight saving';

comment on column TMSTDEPLOY.DWNL_START_TM is
'Date and time to start the download.';

comment on column TMSTDEPLOY.DWNL_END_TM is
'Date and time after which the download cannot be processed.';

comment on column TMSTDEPLOY.DWNL_RETRY_COUNT is
'terminal try to download count';

comment on column TMSTDEPLOY.DWNL_PERIOD is
'Period delay between download cyclic';

comment on column TMSTDEPLOY.DWNL_MAX_NUM is
'Maximum number of download cyclic.';

comment on column TMSTDEPLOY.ACTV_START_TM is
'Date and time to start the activation.';

comment on column TMSTDEPLOY.ACTV_RETRY_COUNT is
'terminal try to activate count';

comment on column TMSTDEPLOY.ACTV_END_TM is
'Date and time after which the activation cannot be processed.';

comment on column TMSTDEPLOY.FORCE_UPDATE is
'some package type support force update flag';

comment on column TMSTDEPLOY.ONLY_PARAM is
'only download parameter/data file';

comment on column TMSTDEPLOY.DWNL_ORDER is
'deployment with the lowest order will be downloaded first';

comment on column TMSTDEPLOY.DELETED_WHEN_DONE is
'delete when donw';

comment on column TMSTDEPLOY.DESCRIPTION is
'descrption';

comment on column TMSTDEPLOY.CREATOR is
'creator';

comment on column TMSTDEPLOY.CREATE_DATE is
'create date';

comment on column TMSTDEPLOY.MODIFIER is
'modifier';

comment on column TMSTDEPLOY.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Table: TMSTDEPLOY_PARA                                       */
/*==============================================================*/
create table TMSTDEPLOY_PARA 
(
   DEPLOY_PARA_ID       NUMBER(38,0)         not null,
   DEPLOY_ID            NUMBER(38,0)         not null,
   PGM_ID               NUMBER(38,0)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   FILE_NAME            VARCHAR2(255)        not null,
   FILE_VERSION         VARCHAR2(64)         not null,
   FILE_SIZE            NUMBER(38,0)         not null,
   FILE_PATH            VARCHAR2(4000)       not null,
   FILE_MD5             VARCHAR2(32),
   FILE_SHA256          VARCHAR2(64),
   CREATOR              VARCHAR2(32),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(38),
   MODIFY_DATE          DATE,
   constraint PK_TMSTDEPLOY_PARA primary key (DEPLOY_PARA_ID)
);

comment on table TMSTDEPLOY_PARA is
'deploy parameter';

comment on column TMSTDEPLOY_PARA.DEPLOY_PARA_ID is
'deploy parameter id';

comment on column TMSTDEPLOY_PARA.DEPLOY_ID is
'refer to a deploy';

comment on column TMSTDEPLOY_PARA.PGM_ID is
'refer to a program';

comment on column TMSTDEPLOY_PARA.PKG_ID is
'refer to a package';

comment on column TMSTDEPLOY_PARA.FILE_NAME is
'file name';

comment on column TMSTDEPLOY_PARA.FILE_VERSION is
'file version';

comment on column TMSTDEPLOY_PARA.FILE_SIZE is
'file size';

comment on column TMSTDEPLOY_PARA.FILE_PATH is
'file path';

comment on column TMSTDEPLOY_PARA.FILE_MD5 is
'md5 file digest';

comment on column TMSTDEPLOY_PARA.FILE_SHA256 is
'sha256 file digest';

comment on column TMSTDEPLOY_PARA.CREATOR is
'creator';

comment on column TMSTDEPLOY_PARA.CREATE_DATE is
'create date';

comment on column TMSTDEPLOY_PARA.MODIFIER is
'modifier';

comment on column TMSTDEPLOY_PARA.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTDEPLOY_PATA                                    */
/*==============================================================*/
create unique index IX_TMSTDEPLOY_PATA on TMSTDEPLOY_PARA (
   DEPLOY_ID ASC,
   PGM_ID ASC
);

/*==============================================================*/
/* Table: TMSTEVENT_GRP                                         */
/*==============================================================*/
create table TMSTEVENT_GRP 
(
   EVENT_ID             NUMBER(38,0)         not null,
   EVENT_TIME           DATE                 not null,
   EVENT_SOURCE         VARCHAR2(60)         not null,
   EVENT_SEVERITY       CHAR(1)              not null,
   EVENT_MSG            VARCHAR2(4000),
   constraint PK_TMSTEVENT_GRP primary key (EVENT_ID)
);

comment on table TMSTEVENT_GRP is
'group event';

comment on column TMSTEVENT_GRP.EVENT_ID is
'event id';

comment on column TMSTEVENT_GRP.EVENT_TIME is
'event date & time';

comment on column TMSTEVENT_GRP.EVENT_SOURCE is
'event source. e.g. terminal id, group id etc.';

comment on column TMSTEVENT_GRP.EVENT_SEVERITY is
'event serverity, 1-information, 2-warning, 3-critical';

comment on column TMSTEVENT_GRP.EVENT_MSG is
'event message';

/*==============================================================*/
/* Index: IX_TMSTEVENT_GRP                                      */
/*==============================================================*/
create index IX_TMSTEVENT_GRP on TMSTEVENT_GRP (
   EVENT_TIME ASC,
   EVENT_SOURCE ASC
);

/*==============================================================*/
/* Table: TMSTEVENT_TRM                                         */
/*==============================================================*/
create table TMSTEVENT_TRM 
(
   EVENT_ID             NUMBER(38,0)         not null,
   EVENT_TIME           DATE                 not null,
   EVENT_SOURCE         VARCHAR2(60)         not null,
   EVENT_SEVERITY       CHAR(1)              not null,
   EVENT_MSG            VARCHAR2(4000),
   constraint PK_TMSTEVENT_TRM primary key (EVENT_ID)
);

comment on table TMSTEVENT_TRM is
'terminal event';

comment on column TMSTEVENT_TRM.EVENT_ID is
'event id';

comment on column TMSTEVENT_TRM.EVENT_TIME is
'event date & time';

comment on column TMSTEVENT_TRM.EVENT_SOURCE is
'event source. e.g. terminal id, group id etc.';

comment on column TMSTEVENT_TRM.EVENT_SEVERITY is
'event serverity, 1-information, 2-warning, 3-critical';

comment on column TMSTEVENT_TRM.EVENT_MSG is
'event message';

/*==============================================================*/
/* Index: IX_TMSTEVENT_TRM                                      */
/*==============================================================*/
create index IX_TMSTEVENT_TRM on TMSTEVENT_TRM (
   EVENT_TIME ASC,
   EVENT_SOURCE ASC
);

/*==============================================================*/
/* Table: TMSTGROUPDEPLOY_HISTORY                               */
/*==============================================================*/
create table TMSTGROUPDEPLOY_HISTORY 
(
   GROUP_DEPLOY_HIS_ID  NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   MODEL_ID             VARCHAR2(36),
   DEPLOY_STATUS        INTEGER              not null,
   DEPLOY_SOURCE        VARCHAR2(60)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   PKG_NAME             VARCHAR2(128)        not null,
   PKG_VERSION          VARCHAR2(64)         not null,
   SCHEMA_ID            NUMBER(38,0),
   PARAM_VERSION        VARCHAR2(64),
   PARAM_SET            VARCHAR2(60),
   DWNL_START_TM        DATE,
   DWNL_PERIOD          INTEGER,
   DWNL_MAX_NUM         INTEGER,
   DOWN_RETRY_COUNT     INTEGER,
   ACTV_RETRY_COUNT     INTEGER,
   ACTV_START_TM        DATE,
   FORCE_UPDATE         INTEGER,
   ONLY_PARAM           INTEGER,
   DWNL_ORDER           INTEGER              default 0 not null,
   DESCRIPTION          VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTGROUPDEPLOY_HISTORY primary key (GROUP_DEPLOY_HIS_ID)
);

comment on table TMSTGROUPDEPLOY_HISTORY is
'group deploy  history';

comment on column TMSTGROUPDEPLOY_HISTORY.GROUP_DEPLOY_HIS_ID is
'terminal ID/SN';

comment on column TMSTGROUPDEPLOY_HISTORY.GROUP_ID is
'group id refer to pubtgroup ';

comment on column TMSTGROUPDEPLOY_HISTORY.MODEL_ID is
'refer to a terminal type';

comment on column TMSTGROUPDEPLOY_HISTORY.DEPLOY_STATUS is
'deploy status';

comment on column TMSTGROUPDEPLOY_HISTORY.DEPLOY_SOURCE is
'group name';

comment on column TMSTGROUPDEPLOY_HISTORY.PKG_ID is
'refer to a package';

comment on column TMSTGROUPDEPLOY_HISTORY.PKG_NAME is
'package name';

comment on column TMSTGROUPDEPLOY_HISTORY.PKG_VERSION is
'package version';

comment on column TMSTGROUPDEPLOY_HISTORY.SCHEMA_ID is
'refer to a package parameter template/schema';

comment on column TMSTGROUPDEPLOY_HISTORY.DWNL_START_TM is
'Date and time to start the download.';

comment on column TMSTGROUPDEPLOY_HISTORY.DWNL_PERIOD is
'Period delay between download cyclic';

comment on column TMSTGROUPDEPLOY_HISTORY.DWNL_MAX_NUM is
'Maximum number of download cyclic.';

comment on column TMSTGROUPDEPLOY_HISTORY.DOWN_RETRY_COUNT is
'terminal try to download count';

comment on column TMSTGROUPDEPLOY_HISTORY.ACTV_RETRY_COUNT is
'terminal try to activate count';

comment on column TMSTGROUPDEPLOY_HISTORY.ACTV_START_TM is
'Date and time to start the activation.';

comment on column TMSTGROUPDEPLOY_HISTORY.FORCE_UPDATE is
'some package type support force update flag';

comment on column TMSTGROUPDEPLOY_HISTORY.ONLY_PARAM is
'only download parameter/data file';

comment on column TMSTGROUPDEPLOY_HISTORY.DWNL_ORDER is
'deployment with the lowest order will be downloaded first';

comment on column TMSTGROUPDEPLOY_HISTORY.DESCRIPTION is
'descrption';

comment on column TMSTGROUPDEPLOY_HISTORY.CREATOR is
'creator';

comment on column TMSTGROUPDEPLOY_HISTORY.CREATE_DATE is
'create date';

comment on column TMSTGROUPDEPLOY_HISTORY.MODIFIER is
'modifier';

comment on column TMSTGROUPDEPLOY_HISTORY.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Table: TMSTGROUP_DEPLOY                                      */
/*==============================================================*/
create table TMSTGROUP_DEPLOY 
(
   REL_ID               NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   DEPLOY_ID            NUMBER(38,0)         not null,
   DEPLOY_TIME          NUMBER(38,0)         not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTGROUP_DEPLOY primary key (REL_ID)
);

comment on table TMSTGROUP_DEPLOY is
'the association between group and deploy';

comment on column TMSTGROUP_DEPLOY.REL_ID is
'releation id';

comment on column TMSTGROUP_DEPLOY.GROUP_ID is
'refer to a group';

comment on column TMSTGROUP_DEPLOY.DEPLOY_ID is
'refer to a deploy';

comment on column TMSTGROUP_DEPLOY.DEPLOY_TIME is
'deploy timestamp';

comment on column TMSTGROUP_DEPLOY.CREATOR is
'creator';

comment on column TMSTGROUP_DEPLOY.CREATE_DATE is
'create date';

comment on column TMSTGROUP_DEPLOY.MODIFIER is
'modifier';

comment on column TMSTGROUP_DEPLOY.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTGROUP_DEPLOY                                   */
/*==============================================================*/
create unique index IX_TMSTGROUP_DEPLOY on TMSTGROUP_DEPLOY (
   GROUP_ID ASC,
   DEPLOY_ID ASC
);

/*==============================================================*/
/* Index: IX_TMSTGROUP_DEPLOY_TIME                              */
/*==============================================================*/
create unique index IX_TMSTGROUP_DEPLOY_TIME on TMSTGROUP_DEPLOY (
   GROUP_ID ASC,
   DEPLOY_TIME ASC
);

/*==============================================================*/
/* Table: TMSTGROUP_REAL_STS                                    */
/*==============================================================*/
create table TMSTGROUP_REAL_STS 
(
   ID                   NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   GROUP_NAME           VARCHAR2(60),
   ITEM_NAME            VARCHAR2(36),
   TOTAL_TRMS           NUMBER(8,0),
   ABNORMAL_TRMS        NUMBER(8,0),
   NORMAL_TRMS          NUMBER(8,0),
   UNKNOWN_TRMS         NUMBER(8,0),
   ALERT_SEVERITY       INTEGER,
   ALERT_THRESHOLD      VARCHAR2(38),
   ALERT_VALUE          VARCHAR2(128),
   CREATE_DATE          DATE,
   constraint PK_TMSTGROUP_REAL_STS primary key (ID)
);

comment on table TMSTGROUP_REAL_STS is
'group terminal statistic data';

comment on column TMSTGROUP_REAL_STS.ID is
' id';

comment on column TMSTGROUP_REAL_STS.GROUP_ID is
'group id';

comment on column TMSTGROUP_REAL_STS.GROUP_NAME is
'group name';

comment on column TMSTGROUP_REAL_STS.ALERT_SEVERITY is
'alert serverity, 1-info, 2-warn, 3-critical';

comment on column TMSTGROUP_REAL_STS.ALERT_THRESHOLD is
'info threshold';

comment on column TMSTGROUP_REAL_STS.ALERT_VALUE is
'value trigger the alert';

comment on column TMSTGROUP_REAL_STS.CREATE_DATE is
'create date';

/*==============================================================*/
/* Table: TMSTGROUP_USAGE_STS                                   */
/*==============================================================*/
create table TMSTGROUP_USAGE_STS 
(
   ID                   NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   GROUP_NAME           VARCHAR2(60),
   ITEM_NAME            VARCHAR2(36),
   TOTAL_TRMS           NUMBER(8,0),
   ABNORMAL_TRMS        NUMBER(8,0),
   NORMAL_TRMS          NUMBER(8,0),
   UNKNOWN_TRMS         NUMBER(8,0),
   ALERT_SEVERITY       INTEGER,
   ALERT_THRESHOLD      VARCHAR2(38),
   ALERT_VALUE          VARCHAR2(128),
   START_TIME           DATE,
   END_TIME             DATE,
   REPORT_CYCLE         VARCHAR2(10),
   CREATE_DATE          DATE,
   constraint PK_TMSTGROUP_USAGE_STS primary key (ID)
);

comment on table TMSTGROUP_USAGE_STS is
'group terminal statistic data';

comment on column TMSTGROUP_USAGE_STS.ID is
' id';

comment on column TMSTGROUP_USAGE_STS.ALERT_SEVERITY is
'alert serverity, 1-info, 2-warn, 3-critical';

comment on column TMSTGROUP_USAGE_STS.ALERT_THRESHOLD is
'info threshold';

comment on column TMSTGROUP_USAGE_STS.ALERT_VALUE is
'value trigger the alert';

comment on column TMSTGROUP_USAGE_STS.START_TIME is
'The terminal statistics period';

comment on column TMSTGROUP_USAGE_STS.REPORT_CYCLE is
'report cycle';

comment on column TMSTGROUP_USAGE_STS.CREATE_DATE is
'create date';

/*==============================================================*/
/* Table: TMSTIMPORTFILE                                        */
/*==============================================================*/
create table TMSTIMPORTFILE 
(
   FILE_ID              NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   USER_ID              NUMBER(38,0)         not null,
   FILE_NAME            VARCHAR2(38)         not null,
   FILE_PATH            VARCHAR2(4000)       not null,
   FILE_SIZE            NUMBER(38,0)         not null,
   STATUS               VARCHAR2(38)         not null,
   FILE_TYPE            VARCHAR2(38)         not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTIMPORTFILE primary key (FILE_ID)
);

comment on column TMSTIMPORTFILE.FILE_ID is
'file id';

comment on column TMSTIMPORTFILE.GROUP_ID is
'refer to group id';

comment on column TMSTIMPORTFILE.USER_ID is
'user id';

comment on column TMSTIMPORTFILE.FILE_NAME is
'filename';

comment on column TMSTIMPORTFILE.FILE_PATH is
'filepath';

comment on column TMSTIMPORTFILE.FILE_SIZE is
'filesize';

comment on column TMSTIMPORTFILE.STATUS is
'refer to a country';

comment on column TMSTIMPORTFILE.FILE_TYPE is
'filetype';

comment on column TMSTIMPORTFILE.CREATOR is
'creator';

comment on column TMSTIMPORTFILE.CREATE_DATE is
'create date';

comment on column TMSTIMPORTFILE.MODIFIER is
'modifier';

comment on column TMSTIMPORTFILE.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_IMPORTFILE_NAME                                    */
/*==============================================================*/
create unique index IX_IMPORTFILE_NAME on TMSTIMPORTFILE (
   GROUP_ID ASC,
   FILE_NAME ASC,
   FILE_TYPE ASC
);

/*==============================================================*/
/* Table: TMSTMFR                                               */
/*==============================================================*/
create table TMSTMFR 
(
   MFR_ID               VARCHAR2(36)         not null,
   MFR_NAME             VARCHAR2(140)        not null,
   MFR_DESC             VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTMFR primary key (MFR_ID)
);

comment on table TMSTMFR is
'manufacturer table';

comment on column TMSTMFR.MFR_ID is
'manufacturer identifier/abbr. name';

comment on column TMSTMFR.MFR_NAME is
'manufacturer name';

comment on column TMSTMFR.MFR_DESC is
'manufacturer description';

comment on column TMSTMFR.CREATOR is
'creator';

comment on column TMSTMFR.CREATE_DATE is
'create date';

comment on column TMSTMFR.MODIFIER is
'modifier';

comment on column TMSTMFR.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTMFR_NAME                                       */
/*==============================================================*/
create unique index IX_TMSTMFR_NAME on TMSTMFR (
   MFR_NAME ASC
);

/*==============================================================*/
/* Index: IX_TMSTMFR_DATE                                       */
/*==============================================================*/
create index IX_TMSTMFR_DATE on TMSTMFR (
   MODIFY_DATE DESC
);

/*==============================================================*/
/* Table: TMSTMODEL                                             */
/*==============================================================*/
create table TMSTMODEL 
(
   MODEL_ID             VARCHAR2(36)         not null,
   MODEL_NAME           VARCHAR2(128)        not null,
   MFR_ID               VARCHAR2(36)         not null,
   PLATFORM             VARCHAR2(20),
   MODEL_DESC           VARCHAR2(256),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTMODEL primary key (MODEL_ID)
);

comment on table TMSTMODEL is
'terminal model/type table';

comment on column TMSTMODEL.MODEL_ID is
'model identify/abbr. name';

comment on column TMSTMODEL.MODEL_NAME is
'model name';

comment on column TMSTMODEL.MFR_ID is
'refer to a manufacturer';

comment on column TMSTMODEL.PLATFORM is
'terminal OS platform';

comment on column TMSTMODEL.MODEL_DESC is
'description';

comment on column TMSTMODEL.CREATOR is
'creator';

comment on column TMSTMODEL.CREATE_DATE is
'created date';

comment on column TMSTMODEL.MODIFIER is
'modifier';

comment on column TMSTMODEL.MODIFY_DATE is
'modified date';

/*==============================================================*/
/* Index: IX_TMSTMODEL_NAME                                     */
/*==============================================================*/
create unique index IX_TMSTMODEL_NAME on TMSTMODEL (
   MODEL_NAME ASC
);

/*==============================================================*/
/* Index: IX_TMSTMODEL_DATE                                     */
/*==============================================================*/
create index IX_TMSTMODEL_DATE on TMSTMODEL (
   MODIFY_DATE DESC
);

/*==============================================================*/
/* Table: TMSTPACKAGE                                           */
/*==============================================================*/
create table TMSTPACKAGE 
(
   PKG_ID               NUMBER(38,0)         not null,
   PKG_UUID             VARCHAR2(36)         not null,
   PKG_NAME             VARCHAR2(128)        not null,
   PKG_VERSION          VARCHAR2(64)         not null,
   MODEL_ID             VARCHAR2(36),
   PKG_TYPE             VARCHAR2(64),
   PGM_TYPE             VARCHAR2(64),
   PKG_DESC             VARCHAR2(256),
   PKG_STATUS           INTEGER              default 1 not null,
   EXPIRATION_DATE      DATE,
   FILE_NAME            VARCHAR2(256)        not null,
   FILE_SIZE            NUMBER(38,0)         not null,
   FILE_PATH            VARCHAR2(4000)       not null,
   FILE_MD5             VARCHAR2(32),
   FILE_SHA256          VARCHAR2(64),
   SCHEMA_FILE_SIZE     NUMBER(38,0),
   SCHEMA_FILE_PATH     VARCHAR2(4000),
   PARAM_SET            VARCHAR2(60),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTPACKAGE primary key (PKG_ID)
);

comment on table TMSTPACKAGE is
'packages';

comment on column TMSTPACKAGE.PKG_ID is
'package id';

comment on column TMSTPACKAGE.PKG_UUID is
'package uuid';

comment on column TMSTPACKAGE.PKG_NAME is
'package name';

comment on column TMSTPACKAGE.PKG_VERSION is
'package version';

comment on column TMSTPACKAGE.MODEL_ID is
'refer to a terminal model/type';

comment on column TMSTPACKAGE.PKG_TYPE is
'package  type';

comment on column TMSTPACKAGE.PKG_DESC is
'package description';

comment on column TMSTPACKAGE.PKG_STATUS is
'package status';

comment on column TMSTPACKAGE.EXPIRATION_DATE is
'Date and time after which the package cannot be deployed';

comment on column TMSTPACKAGE.FILE_NAME is
'file name';

comment on column TMSTPACKAGE.FILE_SIZE is
'file size';

comment on column TMSTPACKAGE.FILE_PATH is
'file path';

comment on column TMSTPACKAGE.FILE_MD5 is
'md5 file digest';

comment on column TMSTPACKAGE.FILE_SHA256 is
'sha256 file digest';

comment on column TMSTPACKAGE.SCHEMA_FILE_SIZE is
'schema file size';

comment on column TMSTPACKAGE.SCHEMA_FILE_PATH is
'schema file path';

comment on column TMSTPACKAGE.PARAM_SET is
'parameter set';

comment on column TMSTPACKAGE.CREATOR is
'creator';

comment on column TMSTPACKAGE.CREATE_DATE is
'creat date';

comment on column TMSTPACKAGE.MODIFIER is
'modifier';

comment on column TMSTPACKAGE.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTPACKAGE_NAME                                   */
/*==============================================================*/
create unique index IX_TMSTPACKAGE_NAME on TMSTPACKAGE (
   PKG_NAME ASC,
   PKG_VERSION ASC
);

/*==============================================================*/
/* Index: IX_TMSTPACKAGE_DATE                                   */
/*==============================================================*/
create index IX_TMSTPACKAGE_DATE on TMSTPACKAGE (
   MODIFY_DATE DESC
);

/*==============================================================*/
/* Table: TMSTPARAMETER_HISTORY                                 */
/*==============================================================*/
create table TMSTPARAMETER_HISTORY 
(
   HIS_ID               NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   REL_ID               VARCHAR2(36)         not null,
   PARAMETER            VARCHAR2(60),
   OLD_VALUE            VARCHAR2(120),
   NEW_VALUE            VARCHAR2(120),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   constraint PK_TMSTPARAMETER_HISTORY primary key (HIS_ID)
);

comment on table TMSTPARAMETER_HISTORY is
'parameter change histroy';

comment on column TMSTPARAMETER_HISTORY.HIS_ID is
'history id';

comment on column TMSTPARAMETER_HISTORY.TRM_ID is
'terminal id';

comment on column TMSTPARAMETER_HISTORY.REL_ID is
'deploy version';

comment on column TMSTPARAMETER_HISTORY.PARAMETER is
'parameter';

comment on column TMSTPARAMETER_HISTORY.OLD_VALUE is
'old value';

comment on column TMSTPARAMETER_HISTORY.NEW_VALUE is
'new value';

comment on column TMSTPARAMETER_HISTORY.CREATOR is
'creator';

comment on column TMSTPARAMETER_HISTORY.CREATE_DATE is
'create date';

/*==============================================================*/
/* Table: TMSTPKG_GROUP                                         */
/*==============================================================*/
create table TMSTPKG_GROUP 
(
   REL_ID               NUMBER(38,0)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTPKG_GROUP primary key (REL_ID)
);

comment on table TMSTPKG_GROUP is
'the association between package and group';

comment on column TMSTPKG_GROUP.REL_ID is
'relation id';

comment on column TMSTPKG_GROUP.PKG_ID is
'refer to a package';

comment on column TMSTPKG_GROUP.GROUP_ID is
'refer to a group';

comment on column TMSTPKG_GROUP.CREATOR is
'creator';

comment on column TMSTPKG_GROUP.CREATE_DATE is
'creat date';

comment on column TMSTPKG_GROUP.MODIFIER is
'modifier';

comment on column TMSTPKG_GROUP.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTPKG_GROUP                                      */
/*==============================================================*/
create unique index IX_TMSTPKG_GROUP on TMSTPKG_GROUP (
   PKG_ID ASC,
   GROUP_ID ASC
);

/*==============================================================*/
/* Table: TMSTPKG_PROGRAM                                       */
/*==============================================================*/
create table TMSTPKG_PROGRAM 
(
   PGM_ID               NUMBER(38,0)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   PGM_ABBR_NAME        VARCHAR2(128),
   PGM_VERSION          VARCHAR2(64),
   PGM_NAME             VARCHAR2(128),
   PGM_DISPLAY_NAME     VARCHAR2(128),
   PGM_TYPE             VARCHAR2(36),
   PGM_DESC             VARCHAR2(256),
   PGM_FILE_ID          NUMBER(38,0),
   CONF_FILE_ID         NUMBER(38,0),
   DIGEST_FILE_ID       NUMBER(38,0),
   SIGN_FILE_ID         NUMBER(38,0),
   SIGN_VERSION         VARCHAR2(64),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTPKG_PROGRAM primary key (PGM_ID)
);

comment on table TMSTPKG_PROGRAM is
'package programs';

comment on column TMSTPKG_PROGRAM.PGM_ID is
'file id';

comment on column TMSTPKG_PROGRAM.PKG_ID is
'refer to a package';

comment on column TMSTPKG_PROGRAM.PGM_ABBR_NAME is
'PGM identifier on terminal side';

comment on column TMSTPKG_PROGRAM.PGM_VERSION is
'program version';

comment on column TMSTPKG_PROGRAM.PGM_NAME is
'progam name on TMS side';

comment on column TMSTPKG_PROGRAM.PGM_DISPLAY_NAME is
'progam display name on terminal side';

comment on column TMSTPKG_PROGRAM.PGM_TYPE is
'program type';

comment on column TMSTPKG_PROGRAM.PGM_DESC is
'program description';

comment on column TMSTPKG_PROGRAM.PGM_FILE_ID is
'refer to program file';

comment on column TMSTPKG_PROGRAM.CONF_FILE_ID is
'program config file';

comment on column TMSTPKG_PROGRAM.SIGN_FILE_ID is
'refer to signature file';

comment on column TMSTPKG_PROGRAM.SIGN_VERSION is
'signature version';

comment on column TMSTPKG_PROGRAM.CREATOR is
'creator';

comment on column TMSTPKG_PROGRAM.CREATE_DATE is
'creat date';

comment on column TMSTPKG_PROGRAM.MODIFIER is
'modifier';

comment on column TMSTPKG_PROGRAM.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSPKG_PROGRAM                                     */
/*==============================================================*/
create index IX_TMSPKG_PROGRAM on TMSTPKG_PROGRAM (
   PKG_ID ASC
);

/*==============================================================*/
/* Table: TMSTPKG_SCHEMA                                        */
/*==============================================================*/
create table TMSTPKG_SCHEMA 
(
   SCHEMA_ID            NUMBER(38,0)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   SCHEMA_NAME          VARCHAR2(128)        not null,
   SCHEMA_VERSION       VARCHAR2(64)         not null,
   SCHEMA_STATUS        INTEGER              default 1 not null,
   SCHEMA_FILE_SIZE     NUMBER(38,0),
   SCHEMA_FILE_PATH     VARCHAR2(4000),
   PARAM_SET            VARCHAR2(60),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   IS_SYS               INTEGER              not null,
   constraint PK_TMSTPKG_SCHEMA primary key (SCHEMA_ID)
);

comment on table TMSTPKG_SCHEMA is
'package parameter template/schema';

comment on column TMSTPKG_SCHEMA.SCHEMA_ID is
'schema id';

comment on column TMSTPKG_SCHEMA.PKG_ID is
'refer to a package';

comment on column TMSTPKG_SCHEMA.SCHEMA_NAME is
'schame name';

comment on column TMSTPKG_SCHEMA.SCHEMA_VERSION is
'schema version';

comment on column TMSTPKG_SCHEMA.SCHEMA_STATUS is
'schema status';

comment on column TMSTPKG_SCHEMA.SCHEMA_FILE_SIZE is
'schema file size';

comment on column TMSTPKG_SCHEMA.SCHEMA_FILE_PATH is
'schema file path';

comment on column TMSTPKG_SCHEMA.PARAM_SET is
'parameter set';

comment on column TMSTPKG_SCHEMA.CREATOR is
'creator';

comment on column TMSTPKG_SCHEMA.CREATE_DATE is
'creat date';

comment on column TMSTPKG_SCHEMA.MODIFIER is
'modifier';

comment on column TMSTPKG_SCHEMA.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTPKG_SCHEMA                                     */
/*==============================================================*/
create index IX_TMSTPKG_SCHEMA on TMSTPKG_SCHEMA (
   PKG_ID ASC
);

/*==============================================================*/
/* Table: TMSTPKG_USAGE_INFO                                    */
/*==============================================================*/
create table TMSTPKG_USAGE_INFO 
(
   PKG_ID               NUMBER(38,0)         not null,
   LAST_OPT_TIME        DATE                 not null,
   constraint PK_TMSTPKG_USAGE_INFO primary key (PKG_ID)
);

comment on table TMSTPKG_USAGE_INFO is
'record pkg usage info';

/*==============================================================*/
/* Table: TMSTPROGRAM_FILE                                      */
/*==============================================================*/
create table TMSTPROGRAM_FILE 
(
   FILE_ID              NUMBER(38,0)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   PGM_ID               NUMBER(38,0)         not null,
   FILE_NAME            VARCHAR2(256),
   FILE_VERSION         VARCHAR2(64),
   FILE_TYPE            VARCHAR2(36),
   FILE_DESC            VARCHAR2(256),
   FILE_SIZE            NUMBER(38,0),
   FILE_PATH            VARCHAR2(4000),
   FILE_MD5             VARCHAR2(32),
   FILE_SHA256          VARCHAR2(64),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTPROGRAM_FILE primary key (FILE_ID)
);

comment on table TMSTPROGRAM_FILE is
'program files';

comment on column TMSTPROGRAM_FILE.FILE_ID is
'file id';

comment on column TMSTPROGRAM_FILE.PGM_ID is
'refer to a program';

comment on column TMSTPROGRAM_FILE.FILE_NAME is
'file name';

comment on column TMSTPROGRAM_FILE.FILE_VERSION is
'file version';

comment on column TMSTPROGRAM_FILE.FILE_TYPE is
'file  type';

comment on column TMSTPROGRAM_FILE.FILE_DESC is
'file description';

comment on column TMSTPROGRAM_FILE.FILE_SIZE is
'file size';

comment on column TMSTPROGRAM_FILE.FILE_PATH is
'file path';

comment on column TMSTPROGRAM_FILE.FILE_MD5 is
'md5 file digest';

comment on column TMSTPROGRAM_FILE.FILE_SHA256 is
'sha256 file digest';

comment on column TMSTPROGRAM_FILE.CREATOR is
'creator';

comment on column TMSTPROGRAM_FILE.CREATE_DATE is
'creat date';

comment on column TMSTPROGRAM_FILE.MODIFIER is
'modifier';

comment on column TMSTPROGRAM_FILE.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTPROGRAM_FILE_PGM                               */
/*==============================================================*/
create index IX_TMSTPROGRAM_FILE_PGM on TMSTPROGRAM_FILE (
   PGM_ID ASC
);

/*==============================================================*/
/* Index: IX_TMSTPROGRAM_FILE_PKG                               */
/*==============================================================*/
create index IX_TMSTPROGRAM_FILE_PKG on TMSTPROGRAM_FILE (
   PKG_ID ASC
);

/*==============================================================*/
/* Table: TMSTTERMINAL                                          */
/*==============================================================*/
create table TMSTTERMINAL 
(
   TRM_ID               VARCHAR2(36)         not null,
   TRM_SN               VARCHAR2(36),
   MODEL_ID             VARCHAR2(36)         not null,
   TRM_STATUS           INTEGER              not null,
   LIC_CODE             VARCHAR2(16),
   LIC_SDATE            DATE,
   LIC_EDATE            DATE,
   LIC_STATUS           INTEGER,
   COUNTRY              VARCHAR2(128),
   PROVINCE             VARCHAR2(128),
   CITY                 VARCHAR2(128),
   ZIP_CODE             VARCHAR2(10),
   TIME_ZONE            VARCHAR2(36),
   SYNC_TO_SERVER_TIME  SMALLINT,
   DAYLIGHT_SAVING      SMALLINT,
   ADDRESS              VARCHAR2(256),
   DESCRIPTION          VARCHAR2(256),
   INSTALL_APPS         VARCHAR(2000),
   REPORT_TIME          DATE,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTTERMINAL primary key (TRM_ID)
);

comment on table TMSTTERMINAL is
'terminal';

comment on column TMSTTERMINAL.TRM_ID is
'terminal ID/SN';

comment on column TMSTTERMINAL.TRM_SN is
'tminal serial number';

comment on column TMSTTERMINAL.MODEL_ID is
'refer to a terminal type';

comment on column TMSTTERMINAL.TRM_STATUS is
'terminal status';

comment on column TMSTTERMINAL.LIC_CODE is
'license code';

comment on column TMSTTERMINAL.LIC_SDATE is
'license start date';

comment on column TMSTTERMINAL.LIC_EDATE is
'license end date';

comment on column TMSTTERMINAL.LIC_STATUS is
'license status';

comment on column TMSTTERMINAL.COUNTRY is
'refer to a country';

comment on column TMSTTERMINAL.PROVINCE is
'refer to a state/province';

comment on column TMSTTERMINAL.CITY is
'refer to a city';

comment on column TMSTTERMINAL.ZIP_CODE is
'ZIP/POST code';

comment on column TMSTTERMINAL.TIME_ZONE is
'time zone';

comment on column TMSTTERMINAL.DAYLIGHT_SAVING is
'daylight saving time';

comment on column TMSTTERMINAL.ADDRESS is
'address/location';

comment on column TMSTTERMINAL.DESCRIPTION is
'descrption';

comment on column TMSTTERMINAL.INSTALL_APPS is
'install apps';

comment on column TMSTTERMINAL.REPORT_TIME is
'terminal send installed app report time';

comment on column TMSTTERMINAL.CREATOR is
'creator';

comment on column TMSTTERMINAL.CREATE_DATE is
'create date';

comment on column TMSTTERMINAL.MODIFIER is
'modifier';

comment on column TMSTTERMINAL.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTERMINAL_SN                                     */
/*==============================================================*/
create index IX_TMSTERMINAL_SN on TMSTTERMINAL (
   TRM_SN ASC
);

/*==============================================================*/
/* Table: TMSTTRMDEPLOY_HISTORY                                 */
/*==============================================================*/
create table TMSTTRMDEPLOY_HISTORY 
(
   TRM_DEPLOY_HIS_ID    NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   MODEL_ID             VARCHAR2(36),
   DEPLOY_STATUS        INTEGER              not null,
   DEPLOY_SOURCE        VARCHAR2(60)         not null,
   PKG_ID               NUMBER(38,0)         not null,
   PKG_NAME             VARCHAR2(128)        not null,
   PKG_VERSION          VARCHAR2(64)         not null,
   SCHEMA_ID            NUMBER(38,0),
   PARAM_VERSION        VARCHAR2(64),
   PARAM_SET            VARCHAR2(60),
   DWNL_START_TM        DATE,
   DWNL_PERIOD          INTEGER,
   DWNL_MAX_NUM         INTEGER,
   DOWN_RETRY_COUNT     INTEGER,
   ACTV_RETRY_COUNT     INTEGER,
   ACTV_START_TM        DATE,
   FORCE_UPDATE         INTEGER,
   ONLY_PARAM           INTEGER,
   DWNL_ORDER           INTEGER              default 0,
   DESCRIPTION          VARCHAR2(256),
   DWNL_TIME            DATE,
   DWNL_STATUS          VARCHAR2(38),
   DWNL_SUCC_COUNT      INTEGER              default 0,
   DWNL_FAIL_COUNT      INTEGER              default 0,
   ACTV_STATUS          VARCHAR2(38),
   ACTV_TIME            DATE,
   ACTV_FAIL_COUNT      INTEGER              default 0,
   CREATOR              VARCHAR2(32),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(38),
   MODIFY_DATE          DATE,
   constraint PK_TMSTTRMDEPLOY_HISTORY primary key (TRM_DEPLOY_HIS_ID)
);

comment on table TMSTTRMDEPLOY_HISTORY is
'terminal deploy  history';

comment on column TMSTTRMDEPLOY_HISTORY.TRM_DEPLOY_HIS_ID is
'terminal ID/SN';

comment on column TMSTTRMDEPLOY_HISTORY.TRM_ID is
'group id refer to pubtgroup ';

comment on column TMSTTRMDEPLOY_HISTORY.MODEL_ID is
'refer to a terminal type';

comment on column TMSTTRMDEPLOY_HISTORY.DEPLOY_STATUS is
'deploy status';

comment on column TMSTTRMDEPLOY_HISTORY.DEPLOY_SOURCE is
'group name';

comment on column TMSTTRMDEPLOY_HISTORY.PKG_ID is
'refer to a package';

comment on column TMSTTRMDEPLOY_HISTORY.PKG_NAME is
'package name';

comment on column TMSTTRMDEPLOY_HISTORY.PKG_VERSION is
'package version';

comment on column TMSTTRMDEPLOY_HISTORY.SCHEMA_ID is
'refer to a package parameter template/schema';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_START_TM is
'Date and time to start the download.';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_PERIOD is
'Period delay between download cyclic';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_MAX_NUM is
'Maximum number of download cyclic.';

comment on column TMSTTRMDEPLOY_HISTORY.DOWN_RETRY_COUNT is
'terminal try to download count';

comment on column TMSTTRMDEPLOY_HISTORY.ACTV_RETRY_COUNT is
'terminal try to activate count';

comment on column TMSTTRMDEPLOY_HISTORY.ACTV_START_TM is
'Date and time to start the activation.';

comment on column TMSTTRMDEPLOY_HISTORY.FORCE_UPDATE is
'some package type support force update flag';

comment on column TMSTTRMDEPLOY_HISTORY.ONLY_PARAM is
'only download parameter/data file';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_ORDER is
'deployment with the lowest order will be downloaded first';

comment on column TMSTTRMDEPLOY_HISTORY.DESCRIPTION is
'descrption';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_TIME is
'date time of the download action performed';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_STATUS is
'download status';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_SUCC_COUNT is
'download successful times';

comment on column TMSTTRMDEPLOY_HISTORY.DWNL_FAIL_COUNT is
'download failed times';

comment on column TMSTTRMDEPLOY_HISTORY.ACTV_STATUS is
'activation status';

comment on column TMSTTRMDEPLOY_HISTORY.ACTV_TIME is
'date time of the activation action performed';

comment on column TMSTTRMDEPLOY_HISTORY.CREATOR is
'creator';

comment on column TMSTTRMDEPLOY_HISTORY.CREATE_DATE is
'create date';

comment on column TMSTTRMDEPLOY_HISTORY.MODIFIER is
'modifier';

comment on column TMSTTRMDEPLOY_HISTORY.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Table: TMSTTRMDWNL                                           */
/*==============================================================*/
create table TMSTTRMDWNL 
(
   LOG_ID               NUMBER(38,0)         not null,
   DEPLOY_ID            NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   TRM_SN               VARCHAR2(36),
   MODEL_ID             VARCHAR2(36),
   PKG_NAME             VARCHAR2(128)        not null,
   PKG_TYPE             VARCHAR2(60)         not null,
   PKG_VERSION          VARCHAR2(64)         not null,
   DWNL_START_TIME      DATE,
   DWNL_END_TIME        DATE,
   EXPIRE_DATE          DATE,
   DWNL_STATUS          VARCHAR2(36)         not null,
   ACTV_SCHEDULE        DATE,
   DWNL_SCHEDULE        DATE,
   ACTV_TIME            DATE,
   ACTV_STATUS          VARCHAR2(36)         not null,
   CREATE_DATE          DATE,
   MODIFY_DATE          DATE,
   constraint PK_TMSTTRMDWNL primary key (LOG_ID)
);

comment on table TMSTTRMDWNL is
'terminal download';

comment on column TMSTTRMDWNL.LOG_ID is
'releation id';

comment on column TMSTTRMDWNL.TRM_ID is
'refer to a terminal';

comment on column TMSTTRMDWNL.MODEL_ID is
'model id';

comment on column TMSTTRMDWNL.PKG_NAME is
'package name';

comment on column TMSTTRMDWNL.PKG_VERSION is
'package version';

comment on column TMSTTRMDWNL.DWNL_START_TIME is
'date time of the download action performed';

comment on column TMSTTRMDWNL.DWNL_END_TIME is
'date time of the download action performed';

comment on column TMSTTRMDWNL.DWNL_STATUS is
'download status';

comment on column TMSTTRMDWNL.ACTV_TIME is
'date time of the activation action performed';

comment on column TMSTTRMDWNL.ACTV_STATUS is
'activation status';

comment on column TMSTTRMDWNL.CREATE_DATE is
'create date';

comment on column TMSTTRMDWNL.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IDX_TMSTTRMDWNL_DT                                    */
/*==============================================================*/
create index IDX_TMSTTRMDWNL_DT on TMSTTRMDWNL (
   TRM_ID ASC,
   DWNL_END_TIME DESC
);

/*==============================================================*/
/* Index: IDX_TMSTTRMDWNL_AT                                    */
/*==============================================================*/
create index IDX_TMSTTRMDWNL_AT on TMSTTRMDWNL (
   TRM_ID ASC,
   ACTV_TIME DESC
);

/*==============================================================*/
/* Table: TMSTTRMSTATUS                                         */
/*==============================================================*/
create table TMSTTRMSTATUS 
(
   TRM_ID               VARCHAR2(36)         not null,
   TRM_SN               VARCHAR2(36)         not null,
   MODEL_ID             VARCHAR2(36),
   LAST_CONN_TIME       DATE,
   LAST_DWNL_TIME       DATE,
   LAST_DWNL_STATUS     VARCHAR2(20),
   LAST_DWNL_TASK       NUMBER(38,0),
   LAST_ACTV_TIME       DATE,
   LAST_ACTV_STATUS     VARCHAR2(20),
   LAST_SOURCE_IP       VARCHAR2(38),
   IS_ONLINE            INTEGER,
   TAMPER               VARCHAR2(36),
   PRIVACY_SHIELD       INTEGER,
   STYLUS               INTEGER,
   ONLINE_SINCE         DATE,
   OFFLINE_SINCE        DATE,
   constraint PK_TMSTTRMSTATUS primary key (TRM_ID)
);

comment on table TMSTTRMSTATUS is
'terminal status';

comment on column TMSTTRMSTATUS.TRM_ID is
'terminal id';

comment on column TMSTTRMSTATUS.TRM_SN is
'terminal sn';

comment on column TMSTTRMSTATUS.MODEL_ID is
'terminal model identifier';

comment on column TMSTTRMSTATUS.LAST_CONN_TIME is
'last connection time';

comment on column TMSTTRMSTATUS.LAST_DWNL_TIME is
'last downloaded (finished) time';

comment on column TMSTTRMSTATUS.LAST_DWNL_STATUS is
'last download status';

comment on column TMSTTRMSTATUS.LAST_DWNL_TASK is
'last downloaded deployment id';

comment on column TMSTTRMSTATUS.LAST_ACTV_TIME is
'last activation/update time';

comment on column TMSTTRMSTATUS.LAST_ACTV_STATUS is
'last activation/update status';

comment on column TMSTTRMSTATUS.LAST_SOURCE_IP is
'last source ip';

comment on column TMSTTRMSTATUS.IS_ONLINE is
'online/offline status';

comment on column TMSTTRMSTATUS.TAMPER is
'tamper reason';

comment on column TMSTTRMSTATUS.PRIVACY_SHIELD is
'privcy shield status, 1-nomal 2-removed, 3 - unknow';

comment on column TMSTTRMSTATUS.STYLUS is
'stylus status, 1-nomal 2-removed, 3 - unknow';

/*==============================================================*/
/* Index: IX_TMSTTRMSTATUS                                      */
/*==============================================================*/
create index IX_TMSTTRMSTATUS on TMSTTRMSTATUS (
   LAST_CONN_TIME ASC
);

/*==============================================================*/
/* Table: TMSTTRM_DEPLOY                                        */
/*==============================================================*/
create table TMSTTRM_DEPLOY 
(
   REL_ID               NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   DEPLOY_ID            NUMBER(38,0)         not null,
   DEPLOY_TIME          NUMBER(38,0)         not null,
   DWNL_TIME            DATE,
   DWNL_STATUS          VARCHAR2(38)         not null,
   DWNL_SUCC_COUNT      INTEGER              default 0 not null,
   DWNL_FAIL_COUNT      INTEGER              default 0 not null,
   ACTV_STATUS          VARCHAR2(38)         not null,
   ACTV_TIME            DATE,
   ACTV_FAIL_COUNT      INTEGER              default 0 not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTTRM_DEPLOY primary key (REL_ID)
);

comment on table TMSTTRM_DEPLOY is
'the association between terminal and deploy';

comment on column TMSTTRM_DEPLOY.REL_ID is
'releation id';

comment on column TMSTTRM_DEPLOY.TRM_ID is
'refer to a terminal';

comment on column TMSTTRM_DEPLOY.DEPLOY_ID is
'refer to a deploy';

comment on column TMSTTRM_DEPLOY.DEPLOY_TIME is
'deploy timestamp';

comment on column TMSTTRM_DEPLOY.DWNL_TIME is
'date time of the download action performed';

comment on column TMSTTRM_DEPLOY.DWNL_STATUS is
'download status';

comment on column TMSTTRM_DEPLOY.DWNL_SUCC_COUNT is
'download successful times';

comment on column TMSTTRM_DEPLOY.DWNL_FAIL_COUNT is
'download failed times';

comment on column TMSTTRM_DEPLOY.ACTV_STATUS is
'activation status';

comment on column TMSTTRM_DEPLOY.ACTV_TIME is
'date time of the activation action performed';

comment on column TMSTTRM_DEPLOY.CREATOR is
'creator';

comment on column TMSTTRM_DEPLOY.CREATE_DATE is
'create date';

comment on column TMSTTRM_DEPLOY.MODIFIER is
'modifier';

comment on column TMSTTRM_DEPLOY.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTTRM_DEPLOY                                     */
/*==============================================================*/
create unique index IX_TMSTTRM_DEPLOY on TMSTTRM_DEPLOY (
   TRM_ID ASC,
   DEPLOY_ID ASC
);

/*==============================================================*/
/* Index: IX_TMSTTRM_DEPLOY_TIME                                */
/*==============================================================*/
create unique index IX_TMSTTRM_DEPLOY_TIME on TMSTTRM_DEPLOY (
   TRM_ID ASC,
   DEPLOY_TIME ASC
);

/*==============================================================*/
/* Table: TMSTTRM_GROUP                                         */
/*==============================================================*/
create table TMSTTRM_GROUP 
(
   REL_ID               NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTTRM_GROUP primary key (REL_ID)
);

comment on table TMSTTRM_GROUP is
'the association between terminal and group';

comment on column TMSTTRM_GROUP.REL_ID is
'releation id';

comment on column TMSTTRM_GROUP.TRM_ID is
'refer to a terminal';

comment on column TMSTTRM_GROUP.GROUP_ID is
'refer to a group';

comment on column TMSTTRM_GROUP.CREATOR is
'creator';

comment on column TMSTTRM_GROUP.CREATE_DATE is
'create date';

comment on column TMSTTRM_GROUP.MODIFIER is
'modifier';

comment on column TMSTTRM_GROUP.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTTRM_GROUP1                                     */
/*==============================================================*/
create unique index IX_TMSTTRM_GROUP1 on TMSTTRM_GROUP (
   GROUP_ID ASC,
   TRM_ID ASC
);

/*==============================================================*/
/* Index: IX_TMSTTRM_GROUP2                                     */
/*==============================================================*/
create unique index IX_TMSTTRM_GROUP2 on TMSTTRM_GROUP (
   TRM_ID ASC,
   GROUP_ID ASC
);

/*==============================================================*/
/* Table: TMSTTRM_REAL_STS                                      */
/*==============================================================*/
create table TMSTTRM_REAL_STS 
(
   ID                   NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   REPORT_TM            DATE,
   SERVER_TM            DATE,
   ITEM_NAME            VARCHAR2(36),
   ITEM_STS             CHAR(1),
   constraint PK_TMSTTRM_REAL_STS primary key (ID)
);

comment on table TMSTTRM_REAL_STS is
'terminal real-time item status';

comment on column TMSTTRM_REAL_STS.ID is
' id';

comment on column TMSTTRM_REAL_STS.TRM_ID is
'terminal sn';

comment on column TMSTTRM_REAL_STS.REPORT_TM is
'The terminal report time';

comment on column TMSTTRM_REAL_STS.SERVER_TM is
'server time';

/*==============================================================*/
/* Table: TMSTTRM_REPORT_MSG                                    */
/*==============================================================*/
create table TMSTTRM_REPORT_MSG 
(
   RPT_ID               NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   REPORT_TM            DATE                 not null,
   TAMPER               CHAR(1),
   ON_OFF_LINE          CHAR(1),
   SHIELD               CHAR(1),
   STYLUS               CHAR(1),
   DOWN_STS             VARCHAR2(16),
   ACTV_STS             VARCHAR2(16),
   MSR_ERRS             NUMBER(8,0),
   MSR_TOTS             NUMBER(8,0),
   ICR_ERRS             NUMBER(8,0),
   ICR_TOTS             NUMBER(8,0),
   PIN_FAILS            NUMBER(8,0),
   PIN_TOTS             NUMBER(8,0),
   SIGN_ERRS            NUMBER(8,0),
   SIGN_TOTS            NUMBER(8,0),
   DOWN_FAILS           NUMBER(8,0),
   DOWN_TOTS            NUMBER(8,0),
   ACTV_FAILS           NUMBER(8,0),
   ACTV_TOTS            NUMBER(8,0),
   CL_ICR_ERRS          NUMBER(8,0),
   CL_ICR_TOTS          NUMBER(8,0),
   TXN_ERRS             NUMBER(8,0),
   TXN_TOTS             NUMBER(8,0),
   POWER_NO             NUMBER(8,0),
   constraint PK_TMSTTRM_REPORT_MSG primary key (RPT_ID)
);

comment on table TMSTTRM_REPORT_MSG is
'terminal reportmessage';

comment on column TMSTTRM_REPORT_MSG.RPT_ID is
'report id';

comment on column TMSTTRM_REPORT_MSG.TRM_ID is
'terminal id';

comment on column TMSTTRM_REPORT_MSG.REPORT_TM is
'health report time(server time)';

comment on column TMSTTRM_REPORT_MSG.TAMPER is
'tamper, 1-normal,2-tamper detected,';

comment on column TMSTTRM_REPORT_MSG.ON_OFF_LINE is
'online/offline, 1-online, 2-offline';

comment on column TMSTTRM_REPORT_MSG.SHIELD is
'privacy shield, 1-nomal, 2-removed';

comment on column TMSTTRM_REPORT_MSG.STYLUS is
'stylus pen, 1-normal, 2- removed';

comment on column TMSTTRM_REPORT_MSG.DOWN_STS is
'download status, values: SUCCESS,PENDING,DOWNLOADING,FAILED,CANCELED';

comment on column TMSTTRM_REPORT_MSG.ACTV_STS is
'activate status, values: SUCCESS,PENDING,DOWNLOADING,FAILED,CANCELED';

comment on column TMSTTRM_REPORT_MSG.MSR_ERRS is
'Magnetic Stripe Reader Errors';

comment on column TMSTTRM_REPORT_MSG.MSR_TOTS is
'Magnetic Stripe Reader Totals';

comment on column TMSTTRM_REPORT_MSG.ICR_ERRS is
'Contact IC Reader Errors';

comment on column TMSTTRM_REPORT_MSG.ICR_TOTS is
'Contact IC Reader Totals';

comment on column TMSTTRM_REPORT_MSG.PIN_FAILS is
'PIN Encryption Failures';

comment on column TMSTTRM_REPORT_MSG.PIN_TOTS is
'PIN Encryption Totals';

comment on column TMSTTRM_REPORT_MSG.SIGN_ERRS is
'Electronic Signature Capture Errors';

comment on column TMSTTRM_REPORT_MSG.SIGN_TOTS is
'Electronic Signature Capture Totals';

comment on column TMSTTRM_REPORT_MSG.DOWN_FAILS is
'Failure Downloads';

comment on column TMSTTRM_REPORT_MSG.DOWN_TOTS is
'Total Downloads';

comment on column TMSTTRM_REPORT_MSG.ACTV_FAILS is
'Failure Activations';

comment on column TMSTTRM_REPORT_MSG.ACTV_TOTS is
'Total Activations';

comment on column TMSTTRM_REPORT_MSG.CL_ICR_ERRS is
'Contacless IC Reader Errors';

comment on column TMSTTRM_REPORT_MSG.CL_ICR_TOTS is
'Contacless IC Reader Totals';

comment on column TMSTTRM_REPORT_MSG.TXN_ERRS is
'Failed Financial Transactions';

comment on column TMSTTRM_REPORT_MSG.TXN_TOTS is
'Total Financial Transactions';

comment on column TMSTTRM_REPORT_MSG.POWER_NO is
'Power on/off Cycles';

/*==============================================================*/
/* Table: TMSTTRM_UNREG                                         */
/*==============================================================*/
create table TMSTTRM_UNREG 
(
   ID                   NUMBER(38,0)         not null,
   TRM_SN               VARCHAR2(36)         not null,
   MODEL_ID             VARCHAR2(36)         not null,
   TRM_ID               VARCHAR2(36),
   SOURCE_IP            VARCHAR2(38),
   LAST_DATE            DATE                 not null,
   CREATE_DATE          DATE,
   constraint PK_TMSTTRM_UNREG primary key (ID)
);

comment on table TMSTTRM_UNREG is
'terminal not registered';

comment on column TMSTTRM_UNREG.ID is
'id';

comment on column TMSTTRM_UNREG.TRM_SN is
'terminal serial number';

comment on column TMSTTRM_UNREG.MODEL_ID is
'terminal model/type id';

comment on column TMSTTRM_UNREG.TRM_ID is
'terminal id';

comment on column TMSTTRM_UNREG.LAST_DATE is
'last date the terminal connection to TMS';

comment on column TMSTTRM_UNREG.CREATE_DATE is
'create date';

/*==============================================================*/
/* Index: UK_TMSTTRM_UNREG                                      */
/*==============================================================*/
create unique index UK_TMSTTRM_UNREG on TMSTTRM_UNREG (
   TRM_SN ASC,
   MODEL_ID ASC
);

/*==============================================================*/
/* Table: TMSTTRM_USAGE_MSG                                     */
/*==============================================================*/
create table TMSTTRM_USAGE_MSG 
(
   ID                   NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   START_TIME           DATE,
   END_TIME             DATE,
   ITEM_NAME            VARCHAR2(36),
   ITEM_ERRS            NUMBER(8,0),
   ITEM_TOTS            NUMBER(8,0),
   MSG_CYCLE            VARCHAR2(10),
   CREATE_DATE          DATE,
   constraint PK_TMSTTRM_USAGE_MSG primary key (ID)
);

comment on table TMSTTRM_USAGE_MSG is
'terminal usage message accord to the cycle of statistical';

comment on column TMSTTRM_USAGE_MSG.ID is
' id';

comment on column TMSTTRM_USAGE_MSG.TRM_ID is
'terminal sn';

comment on column TMSTTRM_USAGE_MSG.ITEM_ERRS is
'Errors';

comment on column TMSTTRM_USAGE_MSG.ITEM_TOTS is
'Totals';

comment on column TMSTTRM_USAGE_MSG.MSG_CYCLE is
'report message cycle';

comment on column TMSTTRM_USAGE_MSG.CREATE_DATE is
'create date';

/*==============================================================*/
/* Table: TMSTTRM_USAGE_STS                                     */
/*==============================================================*/
create table TMSTTRM_USAGE_STS 
(
   ID                   NUMBER(38,0)         not null,
   TRM_ID               VARCHAR2(36)         not null,
   THD_ID               NUMBER(38,0),
   START_TIME           DATE,
   END_TIME             DATE,
   ITEM_NAME            VARCHAR2(36),
   ITEM_STS             INTEGER,
   REPORT_CYCLE         VARCHAR2(10),
   CREATE_DATE          DATE,
   constraint PK_TMSTTRM_USAGE_STS primary key (ID)
);

comment on table TMSTTRM_USAGE_STS is
'terminal usage status accord to usage threshold';

comment on column TMSTTRM_USAGE_STS.ID is
' id';

comment on column TMSTTRM_USAGE_STS.TRM_ID is
'terminal sn';

comment on column TMSTTRM_USAGE_STS.START_TIME is
'The terminal statistics period';

comment on column TMSTTRM_USAGE_STS.REPORT_CYCLE is
'report cycle';

comment on column TMSTTRM_USAGE_STS.CREATE_DATE is
'create date';

/*==============================================================*/
/* Table: TMSTTRM_USAGE_THRESHOLD                               */
/*==============================================================*/
create table TMSTTRM_USAGE_THRESHOLD 
(
   THD_ID               NUMBER(38,0)         not null,
   GROUP_ID             NUMBER(38,0)         not null,
   ITEM_NAME            VARCHAR2(36)         not null,
   THD_VALUE            VARCHAR2(38),
   REPORT_CYCLE         VARCHAR2(10),
   CREATOR              VARCHAR2(36),
   CREATE_DATE          DATE,
   MODIFIER             VARCHAR2(36),
   MODIFY_DATE          DATE,
   constraint PK_TMSTTRM_USAGE_THRESHOLD primary key (THD_ID)
);

comment on table TMSTTRM_USAGE_THRESHOLD is
'terminal usage threshold setting';

comment on column TMSTTRM_USAGE_THRESHOLD.THD_ID is
'threshold setting id';

comment on column TMSTTRM_USAGE_THRESHOLD.GROUP_ID is
'refer to a group';

comment on column TMSTTRM_USAGE_THRESHOLD.ITEM_NAME is
'usage item name';

comment on column TMSTTRM_USAGE_THRESHOLD.THD_VALUE is
'threshold value';

comment on column TMSTTRM_USAGE_THRESHOLD.REPORT_CYCLE is
'report cycle';

comment on column TMSTTRM_USAGE_THRESHOLD.CREATOR is
'creator';

comment on column TMSTTRM_USAGE_THRESHOLD.CREATE_DATE is
'create date';

comment on column TMSTTRM_USAGE_THRESHOLD.MODIFIER is
'modifier';

comment on column TMSTTRM_USAGE_THRESHOLD.MODIFY_DATE is
'modify date';

/*==============================================================*/
/* Index: IX_TMSTTRM_USAGE_THRESHOLD                            */
/*==============================================================*/
create unique index IX_TMSTTRM_USAGE_THRESHOLD on TMSTTRM_USAGE_THRESHOLD (
   GROUP_ID ASC,
   ITEM_NAME ASC
);

create table APP_CLIENT 
(
   APP_CLIENT_ID        INTEGER              not null,
   USER_ID              INTEGER,
   USER_NAME            VARCHAR2(26),
   APP_KEY              VARCHAR2(128),
   UPDATED_ON           DATE,
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