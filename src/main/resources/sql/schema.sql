create table t_common_user(
  fuserloginname varchar(32),
  fuserpassword varchar(32),
  fuserpasswordsalt char(8),
  fusermale int default 0,
  fcellphonenumber varchar(32),
  flogincount int default 0,
  flastlogintime datetime,
  fweixinopenid varchar(32),
  fdefaultloginorgid varchar(32),
  fname varchar(32),
  fdescription varchar(32),
  fcreateuserid varchar(32),
  fcreatedate datetime,
  flastupdateuserid varchar(32),
  flastupdatedate datetime,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_common_company(
  fname varchar(32),
  fdescription varchar(3000),
  fcreateuserid varchar(32),
  fcreatedate datetime,
  flastupdateuserid varchar(32),
  flastupdatedate datetime,
  fid varchar(32) not null,
  fversion int default 1, primary key (fid)
);

create table t_common_role(
  fvalue varchar(32),
  fname varchar(32),
  fdescription varchar(32),
  fcreateuserid varchar(32),
  fcreatedate datetime,
  flastupdateuserid varchar(32),
  flastupdatedate datetime,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table T_COMMON_PERMISSION
(
  FID               varchar(32) not null,
  FCREATEUSERID     varchar(32),
  FDESCRIPTION      varchar(32),
  FNAME             varchar(100),
  FVALUE            varchar(45),
  FLASTUPDATEUSERID varchar(32),
  FCREATEDATE       datetime,
  FLASTUPDATEDATE   datetime,
  FGROUPLABEL       varchar(12),
  FMODULECODE       varchar(32),
  FVERSION          int default 1,
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
);

insert into t_common_user(fid,fcreatedate,fname,fuserloginname,fuserpassword,fuserpasswordsalt) values('AAABR8Qev3KEgi7aew1G8JUc2qtHfXML',now(),'系统管理员','admin','65e26de12aeac2eed0729d977d4ddd44','bb81f26d');