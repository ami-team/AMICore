------------------------------------------------------------------------------

DROP TABLE `router_country_blocks_ipv6`;
DROP TABLE `router_country_blocks_ipv4`;
DROP TABLE `router_country_locations`;
DROP TABLE `router_search_criteria`;
DROP TABLE `router_search_interface`;
DROP TABLE `router_user_role`;
DROP TABLE `router_user`;
DROP TABLE `router_command_role`;
DROP TABLE `router_command`;
DROP TABLE `router_role`;
DROP TABLE `router_converter`;
DROP TABLE `router_catalog`;
DROP TABLE `router_config`;

------------------------------------------------------------------------------

DROP SEQUENCE `seq_router_country_blocks_ipv6`;
DROP SEQUENCE `seq_router_country_blocks_ipv4`;
DROP SEQUENCE `seq_router_country_locations`;
DROP SEQUENCE `seq_router_search_criteria`;
DROP SEQUENCE `seq_router_search_interface`;
DROP SEQUENCE `seq_router_user_role`;
DROP SEQUENCE `seq_router_user`;
DROP SEQUENCE `seq_router_command_role`;
DROP SEQUENCE `seq_router_command`;
DROP SEQUENCE `seq_router_role`;
DROP SEQUENCE `seq_router_converter`;
DROP SEQUENCE `seq_router_catalog`;
DROP SEQUENCE `seq_router_config`;

------------------------------------------------------------------------------

CREATE TABLE `router_config` (
  `id` NUMBER(*, 0),
  `paramName` VARCHAR2(128),
  `paramValue` VARCHAR2(512)
);

ALTER TABLE `router_config`
  ADD CONSTRAINT `pk1_router_config` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_config` UNIQUE KEY (`paramName`),
  ADD CONSTRAINT `ck1_router_config` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_config` CHECK(`paramName` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_config` CHECK(`paramValue` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_config` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_config`
BEFORE INSERT ON `router_config`
FOR EACH ROW
BEGIN
  SELECT `seq_router_config`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_catalog` (
  `id` NUMBER(*, 0),
  `externalcatalog` VARCHAR2(128),
  `internalCatalog` VARCHAR2(128),
  `jdbcUrl` VARCHAR2(512),
  `user` VARCHAR2(128),
  `pass` VARCHAR2(128),
  `archived` NUMBER(1, 0) DEFAULT '0',
  `jsonSerialization` CLOB
);

ALTER TABLE `router_catalog`
  ADD CONSTRAINT `pk1_router_catalog` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_catalog` UNIQUE KEY (`externalcatalog`),
  ADD CONSTRAINT `ck1_router_catalog` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_catalog` CHECK(`externalcatalog` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_catalog` CHECK(`internalCatalog` IS NOT NULL),
  ADD CONSTRAINT `ck4_router_catalog` CHECK(`jdbcUrl` IS NOT NULL),
  ADD CONSTRAINT `ck5_router_catalog` CHECK(`user` IS NOT NULL),
  ADD CONSTRAINT `ck6_router_catalog` CHECK(`pass` IS NOT NULL),
  ADD CONSTRAINT `ck7_router_catalog` CHECK(`archived` IS NOT NULL)
;

CREATE SEQUENCE `seq_pk1_router_catalog` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_pk1_router_catalog`
BEFORE INSERT ON `pk1_router_catalog`
FOR EACH ROW
BEGIN
  SELECT `seq_pk1_router_catalog`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_converter` (
  `id` NUMBER(*, 0),
  `xslt` VARCHAR2(128),
  `mime` VARCHAR2(128)
);

ALTER TABLE `router_converter`
  ADD CONSTRAINT `pk1_router_converter` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_converter` UNIQUE KEY (`xslt`),
  ADD CONSTRAINT `ck1_router_converter` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_converter` CHECK(`xslt` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_converter` CHECK(`mime` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_converter` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_converter`
BEFORE INSERT ON `router_converter`
FOR EACH ROW
BEGIN
  SELECT `seq_router_converter`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_role` (
  `id` NUMBER(*, 0),
  `lft` NUMBER(*, 0),
  `rgt` NUMBER(*, 0),
  `role` VARCHAR2(128),
  `roleValidatorClass` VARCHAR2(256)
);

ALTER TABLE `router_role`
  ADD CONSTRAINT `pk1_router_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_role` UNIQUE KEY (`role`),
  ADD CONSTRAINT `ck1_router_role` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_role` CHECK(`lft` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_role` CHECK(`rgt` IS NOT NULL),
  ADD CONSTRAINT `ck4_router_role` CHECK(`role` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_role` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_role`
BEFORE INSERT ON `router_role`
FOR EACH ROW
BEGIN
  SELECT `seq_router_role`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_command` (
  `id` NUMBER(*, 0),
  `command` VARCHAR2(128),
  `class` VARCHAR2(256)
);

ALTER TABLE `router_command`
  ADD CONSTRAINT `pk1_router_command` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command` UNIQUE KEY (`command`),
  ADD CONSTRAINT `ck1_router_command` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_command` CHECK(`command` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_command` CHECK(`class` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_command` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_command`
BEFORE INSERT ON `router_command`
FOR EACH ROW
BEGIN
  SELECT `seq_router_command`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_command_role` (
  `id` NUMBER(*, 0),
  `commandFK` NUMBER(*, 0),
  `roleFK` NUMBER(*, 0)
);

ALTER TABLE `router_command_role`
  ADD CONSTRAINT `pk1_router_command_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command_role` UNIQUE KEY (`commandFK`, `roleFK`),
  ADD CONSTRAINT `ck1_router_command_role` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_command_role` CHECK(`commandFK` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_command_role` CHECK(`roleFK` IS NOT NULL),
  ADD CONSTRAINT `fk1_router_command_role` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_command_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

CREATE SEQUENCE `seq_router_command_role` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_command_role`
BEFORE INSERT ON `router_command_role`
FOR EACH ROW
BEGIN
  SELECT `seq_router_command_role`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_user` (
  `id` NUMBER(*, 0),
  `AMIUser` VARCHAR2(128),
  `AMIPass` VARCHAR2(128),
  `clientDN` VARCHAR2(512),
  `issuerDN` VARCHAR2(512),
  `firstName` VARCHAR2(128),
  `lastName` VARCHAR2(128),
  `email` VARCHAR2(128),
  `country` VARCHAR2(128),
  `valid` NUMBER(1, 0) DEFAULT '1'
);

ALTER TABLE `router_user`
  ADD CONSTRAINT `pk1_router_user` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user` UNIQUE KEY (`AMIUser`),
  ADD CONSTRAINT `ck1_router_user` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_user` CHECK(`AMIUser` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_user` CHECK(`AMIPass` IS NOT NULL),
  ADD CONSTRAINT `ck4_router_user` CHECK(`clientDN` IS NOT NULL),
  ADD CONSTRAINT `ck5_router_user` CHECK(`issuerDN` IS NOT NULL),
  ADD CONSTRAINT `ck6_router_user` CHECK(`firstName` IS NOT NULL),
  ADD CONSTRAINT `ck7_router_user` CHECK(`lastName` IS NOT NULL),
  ADD CONSTRAINT `ck8_router_user` CHECK(`valid` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_user` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_user`
BEFORE INSERT ON `router_user`
FOR EACH ROW
BEGIN
  SELECT `seq_router_user`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_user_role` (
  `id` NUMBER(*, 0),
  `userFK` NUMBER(*, 0),
  `roleFK` NUMBER(*, 0)
);

ALTER TABLE `router_user_role`
  ADD CONSTRAINT `pk1_router_user_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user_role` UNIQUE KEY (`userFK`, `roleFK`),
  ADD CONSTRAINT `ck1_router_user_role` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck1_router_user_role` CHECK(`userFK` IS NOT NULL),
  ADD CONSTRAINT `ck1_router_user_role` CHECK(`roleFK` IS NOT NULL),
  ADD CONSTRAINT `fk1_router_user_role` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_user_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

CREATE SEQUENCE `seq_router_user_role` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_user_role`
BEFORE INSERT ON `router_user_role`
FOR EACH ROW
BEGIN
  SELECT `seq_router_user_role`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_search_interface` (
  `id` NUMBER(*, 0),
  `interface` VARCHAR2(128),
  `catalog` VARCHAR2(128),
  `entity` VARCHAR2(128),
  `archived` NUMBER(1, 0) DEFAULT '0'
);

ALTER TABLE `router_search_interface`
  ADD CONSTRAINT `pk1_router_search_interface` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`interface`),
  ADD CONSTRAINT `ck1_router_search_interface` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_search_interface` CHECK(`interface` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_search_interface` CHECK(`catalog` IS NOT NULL),
  ADD CONSTRAINT `ck4_router_search_interface` CHECK(`entity` IS NOT NULL),
  ADD CONSTRAINT `ck5_router_search_interface` CHECK(`archived` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_search_interface` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_search_interface`
BEFORE INSERT ON `router_search_interface`
FOR EACH ROW
BEGIN
  SELECT `seq_router_search_interface`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_search_criteria` (
  `id` NUMBER(*, 0),
  `interfaceFK` NUMBER(*, 0),
  `entity` VARCHAR2(128),
  `field` VARCHAR2(128),
  `alias` VARCHAR2(128) DEFAULT '',
  `type` NUMBER(*, 0) DEFAULT '1',
  `rank` NUMBER(*, 0) DEFAULT '0',
  `mask` NUMBER(*, 0) DEFAULT '0'
);

ALTER TABLE `router_search_criteria`
  ADD CONSTRAINT `pk1_router_search_criteria` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_criteria` UNIQUE KEY (`interfaceFK`, `entity`, `field`),
  ADD CONSTRAINT `ck1_router_search_criteria` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_search_criteria` CHECK(`interfaceFK` IS NOT NULL),
  ADD CONSTRAINT `ck3_router_search_criteria` CHECK(`entity` IS NOT NULL),
  ADD CONSTRAINT `ck4_router_search_criteria` CHECK(`field` IS NOT NULL),
  ADD CONSTRAINT `ck5_router_search_criteria` CHECK(`type` IS NOT NULL),
  ADD CONSTRAINT `ck6_router_search_criteria` CHECK(`rank` IS NOT NULL),
  ADD CONSTRAINT `ck7_router_search_criteria` CHECK(`mask` IS NOT NULL),
  ADD CONSTRAINT `fk1_router_search_criteria` FOREIGN KEY (`interfaceFK`) REFERENCES `router_search_interface` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

CREATE SEQUENCE `seq_router_search_criteria` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_search_criteria`
BEFORE INSERT ON `router_search_criteria`
FOR EACH ROW
BEGIN
  SELECT `seq_router_search_criteria`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_country_locations` (
  `id` NUMBER(*, 0),
  `continentCode` VARCHAR2(2),
  `countryCode` VARCHAR2(2)
);

ALTER TABLE `router_country_locations`
  ADD CONSTRAINT `pk1_router_country_locations` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_country_locations` UNIQUE KEY (`continentCode`, `countryCode`),
  ADD CONSTRAINT `ck1_router_country_locations` CHECK(`id` IS NOT NULL)
;

CREATE SEQUENCE `seq_router_country_locations` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_country_locations`
BEFORE INSERT ON `router_country_locations`
FOR EACH ROW
BEGIN
  SELECT `seq_router_country_locations`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_country_blocks_ipv4` (
  `id` NUMBER(*, 0),
  `network` VARCHAR2(32),
  `rangeBegin` NUMBER(10, 0),
  `rangeEnd` NUMBER(10, 0),
  `geoFK` NUMBER(*, 0)
);

ALTER TABLE `router_country_blocks_ipv4`
  ADD CONSTRAINT `pk1_router_country_blocks_ipv4` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_country_blocks_ipv4` UNIQUE KEY (`network`),
  ADD CONSTRAINT `ck1_router_country_blocks_ipv4` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_country_blocks_ipv4` CHECK(`geoFK` IS NOT NULL),
  ADD CONSTRAINT `fk1_router_country_blocks_ipv4` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;

CREATE SEQUENCE `seq_router_country_blocks_ipv4` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_country_blocks_ipv4`
BEFORE INSERT ON `router_country_blocks_ipv4`
FOR EACH ROW
BEGIN
  SELECT `seq_router_country_blocks_ipv4`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------

CREATE TABLE `router_country_blocks_ipv6` (
  `id` NUMBER(*, 0),
  `network` VARCHAR2(64),
  `rangeBegin` NUMBER(38, 0),
  `rangeEnd` NUMBER(38, 0),
  `geoFK` NUMBER(*, 0)
);

ALTER TABLE `router_country_blocks_ipv6`
  ADD CONSTRAINT `pk1_router_country_blocks_ipv6` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_country_blocks_ipv6` UNIQUE KEY (`network`),
  ADD CONSTRAINT `ck1_router_country_blocks_ipv6` CHECK(`id` IS NOT NULL),
  ADD CONSTRAINT `ck2_router_country_blocks_ipv6` CHECK(`geoFK` IS NOT NULL),
  ADD CONSTRAINT `fk1_router_country_blocks_ipv6` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;

CREATE SEQUENCE `seq_router_country_blocks_ipv6` START WITH 1 INCREMENT BY 1 CACHE 10;

CREATE TRIGGER `trig_router_country_blocks_ipv6`
BEFORE INSERT ON `router_country_blocks_ipv6`
FOR EACH ROW
BEGIN
  SELECT `seq_router_country_blocks_ipv6`.NEXTVAL INTO :NEW.`id` FROM dual;
END;
/

------------------------------------------------------------------------------
