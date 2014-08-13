create table t_common_user(
  fuserloginname varchar(32),
  fuserpassword varchar(32),
  fuserpasswordsalt char(8),
  fusermale int default 0,
  fcellphonenumber varchar(32),
  flogincount int default 0,
  flastlogintime date,
  fweixinopenid varchar(32),
  fdefaultloginorgid varchar(32),
  fname varchar(32),
  fdescription varchar(32),
  fcreateuserid varchar(32),
  fcreatedate date,
  flastupdateuserid varchar(32),
  flastupdatedate date,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_common_company(
  fname varchar(32),
  fdescription varchar(3000),
  fcreateuserid varchar(32),
  fcreatedate date,
  flastupdateuserid varchar(32),
  flastupdatedate date,
  fid varchar(32) not null,
  fversion int default 1, primary key (fid)
);

create table t_common_role(
  fvalue varchar(32),
  fname varchar(32),
  fdescription varchar(32),
  fcreateuserid varchar(32),
  fcreatedate date,
  flastupdateuserid varchar(32),
  flastupdatedate date,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table T_COMMON_USER_ORG
(
  FID          varchar(32) not null,
  FUSERID      varchar(32),
  FORGID       varchar(32),
  FUPPERUSERID varchar(32),
  FUSERPATH    varchar(200),
  primary key (fid)
);

create table T_COMMON_USER_ROLE
(
  FID     varchar(36) not null,
  FUSERID varchar(32),
  FROLEID varchar(32),
  FORGID  varchar(32),
  primary key (fid)
);

create table T_COMMON_ROLE_PERMISSION
(
  FID           varchar(36) not null,
  FROLEID       varchar(32),
  FPERMISSIONID varchar(32),
  primary key (fid)
)