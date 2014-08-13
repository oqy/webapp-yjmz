create table t_common_user(fuserloginname varchar(32),fuserpassword varchar(32),fuserpasswordsalt char(8),fcellphonenumber varchar(32),fweixinopenid varchar(32),fname varchar(32),fdescription varchar(32),fcreateuserid varchar(32),fcreatedate date,flastupdateuserid varchar(32),flastupdatedate date,fid varchar(32) not null,fversion int default 1, primary key (fid));
create table t_common_company(fname varchar(32),fdescription varchar(3000),fcreateuserid varchar(32),fcreatedate date,flastupdateuserid varchar(32),flastupdatedate date,fid varchar(32) not null,fversion int default 1, primary key (fid));
create table t_common_role(fvalue varchar(32),fname varchar(32),fdescription varchar(32),fcreateuserid varchar(32),fcreatedate date,flastupdateuserid varchar(32),flastupdatedate date,fid varchar(32) not null,fversion int default 1, primary key (fid));

create table T_COMMON_USER_ORG
(
  FID          VARCHAR2(32) not null,
  FUSERID      VARCHAR2(32),
  FORGID       VARCHAR2(32),
  FUSERTYPE    INTEGER,
  FUPPERUSERID VARCHAR2(32),
  FUSERPATH    VARCHAR2(200),
  primary key (fid)
)

create table T_COMMON_USER_ROLE
(
  FID     VARCHAR2(36) not null,
  FUSERID VARCHAR2(32),
  FROLEID VARCHAR2(32),
  FORGID  VARCHAR2(32),
  primary key (fid)
)

create table T_COMMON_ROLE_PERMISSION
(
  FID           VARCHAR2(36) not null,
  FROLEID       VARCHAR2(32),
  FPERMISSIONID VARCHAR2(32),
  primary key (fid)
)