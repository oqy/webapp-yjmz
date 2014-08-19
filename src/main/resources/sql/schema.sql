create table t_common_user(
  fstatus int default 0,
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
  fstatus int default 0,
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
insert into t_common_role(fvalue,fname,fcreatedate,fid) values('ADMINISTRATOR','系统管理员',now(),'AAABR8QfMr6LYoVnC7RHeI9VMox37vue');
insert into t_common_user_role(fid,fuserid,froleid) values(UUID(),'AAABR8Qev3KEgi7aew1G8JUc2qtHfXML','AAABR8QfMr6LYoVnC7RHeI9VMox37vue');

create table t_common_department(
  fname varchar(32),
  forgid varchar(32),
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_common_userorgrelation(
  fuserid varchar(32),
  forgid varchar(32),
  fdepartmentid varchar(32),
  fjobnumber varchar(32),
  ftitle varchar(32),
  fupperuserid varchar(32),
  fseq int default 1,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_oa_report(
  fdepartmentid varchar(32),
  ffilenumber varchar(32),
  freporttitle varchar(200),
  fattachmenturl varchar(200),
  fbillnumber varchar(32),
  fdescription text,
  fsourcebillid varchar(32),
  fcreateuserid varchar(32),
  fcreatedate datetime,
  flastupdateuserid varchar(32),
  flastupdatedate datetime,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_oa_maintainreqbill(
  fapplydepartmentid varchar(32),
  fapplyuserid varchar(32),
  flocation varchar(200),
  fmaintaintypesid varchar(32),
  fmaintenancemanid varchar(32),
  ffinishdate datetime,
  freceiverid varchar(32),
  fexaminerid varchar(32),
  fbillnumber varchar(32),
  fdescription varchar(5000),
  fsourcebillid varchar(32),
  fcreateuserid varchar(32),
  fcreatedate datetime,
  flastupdateuserid varchar(32),
  flastupdatedate datetime,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_oa_maintainreqentry(
  freqbillid varchar(32),
  fname varchar(200),
  fquantity decimal(13,2) default 0,
  fprice decimal(13,2) default 0,
  fseq int default 1,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_oa_purchasereqbill(
  fdepartmentid varchar(32),
  fbillnumber varchar(32),
  fdescription varchar(10000),
  fsourcebillid varchar(32),
  fcreateuserid varchar(32),
  fcreatedate datetime,
  flastupdateuserid varchar(32),
  flastupdatedate datetime,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

create table t_oa_purchasereqentry(
  freqbillid varchar(32),
  fname varchar(200),
  fstandard varchar(200),
  fquantity decimal(13,2) default 0,
  funitprice decimal(13,2) default 0,
  fremark varchar(1000),
  fseq int default 1,
  fid varchar(32) not null,
  fversion int default 1, 
  primary key (fid)
);

ALTER TABLE t_oa_maintainreqbill ADD fprocessinstanceid VARCHAR(32) NULL AFTER fexaminerid;
ALTER TABLE t_oa_purchasereqbill ADD fprocessinstanceid VARCHAR(32) NULL AFTER fdepartmentid;
ALTER TABLE t_oa_report ADD fprocessinstanceid VARCHAR(32) NULL AFTER fattachmenturl;
ALTER TABLE t_common_userorgrelation ADD fuserpath VARCHAR(200) NULL;
ALTER TABLE t_common_department ADD fseq INT NULL DEFAULT 1 AFTER forgid;