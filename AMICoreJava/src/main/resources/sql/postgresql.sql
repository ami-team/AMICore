------------------------------------------------------------------------------

DROP TABLE IF EXISTS `router_ipv6_blocks`;
DROP TABLE IF EXISTS `router_ipv4_blocks`;
DROP TABLE IF EXISTS `router_locations`;
DROP TABLE IF EXISTS `router_search_criteria`;
DROP TABLE IF EXISTS `router_search_interface`;
DROP TABLE IF EXISTS `router_authority`;
DROP TABLE IF EXISTS `router_user_role`;
DROP TABLE IF EXISTS `router_user`;
DROP TABLE IF EXISTS `router_command_role`;
DROP TABLE IF EXISTS `router_command`;
DROP TABLE IF EXISTS `router_role`;
DROP TABLE IF EXISTS `router_converter`;
DROP TABLE IF EXISTS `router_catalog`;
DROP TABLE IF EXISTS `router_config`;

------------------------------------------------------------------------------

CREATE TABLE `router_config` (
  `id` SERIAL NOT NULL,
  `paramName` VARCHAR(128) NOT NULL,
  `paramValue` VARCHAR(512) NOT NULL
);

ALTER TABLE `router_config` 
  ADD CONSTRAINT `pk1_router_config` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_config` UNIQUE (`paramName`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_catalog` (
  `id` SERIAL NOT NULL,
  `externalCatalog` VARCHAR(128) NOT NULL,
  `internalCatalog` VARCHAR(128) NOT NULL,
  `jdbcUrl` VARCHAR(512) NOT NULL,
  `user` VARCHAR(128) NOT NULL,
  `pass` VARCHAR(128) NOT NULL,
  `custom` TEXT,
  `archived` SMALLINT NOT NULL DEFAULT '0',
);

ALTER TABLE `router_catalog`
  ADD CONSTRAINT `pk1_router_catalog` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_catalog` UNIQUE (`externalCatalog`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_converter` (
  `id` SERIAL NOT NULL,
  `xslt` VARCHAR(128) NOT NULL,
  `mime` VARCHAR(128) NOT NULL
);

ALTER TABLE `router_converter`
  ADD CONSTRAINT `pk1_router_converter` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_converter` UNIQUE (`xslt`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_role` (
  `id` SERIAL NOT NULL,
  `lft` INT NOT NULL,
  `rgt` INT NOT NULL,
  `role` VARCHAR(128) NOT NULL,
  `roleValidatorClass` VARCHAR(256)
);

ALTER TABLE `router_role`
  ADD CONSTRAINT `pk1_router_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_role` UNIQUE (`role`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_command` (
  `id` SERIAL NOT NULL,
  `command` VARCHAR(128) NOT NULL,
  `class` VARCHAR(256) NOT NULL
);

ALTER TABLE `router_command`
  ADD CONSTRAINT `pk1_router_command` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command` UNIQUE (`command`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_command_role` (
  `id` SERIAL NOT NULL,
  `commandFK` INT NOT NULL,
  `roleFK` INT NOT NULL
);

ALTER TABLE `router_command_role`
  ADD CONSTRAINT `pk1_router_command_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command_role` UNIQUE  (`commandFK`, `roleFK`),
  ADD CONSTRAINT `fk1_router_command_role` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_command_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

------------------------------------------------------------------------------

CREATE TABLE `router_user` (
  `id` SERIAL NOT NULL,
  `AMIUser` VARCHAR(128) NOT NULL,
  `AMIPass` VARCHAR(128) NOT NULL,
  `clientDN` VARCHAR(512),
  `issuerDN` VARCHAR(512),
  `firstName` VARCHAR(128) NOT NULL,
  `lastName` VARCHAR(128) NOT NULL,
  `email` VARCHAR(128),
  `country` VARCHAR(128) DEFAULT 'N/A',
  `valid` SMALLINT NOT NULL DEFAULT '1'
);

ALTER TABLE `router_user`
  ADD CONSTRAINT `pk1_router_user` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user` UNIQUE (`AMIUser`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_user_role` (
  `id` SERIAL NOT NULL,
  `userFK` INT NOT NULL,
  `roleFK` INT NOT NULL
);

ALTER TABLE `router_user_role`
  ADD CONSTRAINT `pk1_router_user_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user_role` UNIQUE (`userFK`, `roleFK`),
  ADD CONSTRAINT `fk1_router_user_role` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_user_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

------------------------------------------------------------------------------

CREATE TABLE `router_authority` (
  `id` SERIAL NOT NULL,
  `clientDN` VARCHAR(512) NOT NULL,
  `issuerDN` VARCHAR(512) NOT NULL,
  `notBefore` DATE NOT NULL,
  `notAfter` DATE NOT NULL,
  `serial` VARCHAR(32) NOT NULL,
  `revocationReason` INT(11) DEFAULT NULL,
  `revocationDate` DATE DEFAULT NULL
);

ALTER TABLE `router_authority`
  ADD CONSTRAINT `pk1_router_authority` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_authority` UNIQUE KEY (`serial`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_search_interface` (
  `id` SERIAL NOT NULL,
  `interface` VARCHAR(128) NOT NULL,
  `catalog` VARCHAR(128) NOT NULL,
  `entity` VARCHAR(128) NOT NULL,
  `archived` SMALLINT NOT NULL DEFAULT '0'
);

ALTER TABLE `router_search_interface`
  ADD CONSTRAINT `pk1_router_search_interface` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE (`interface`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_search_criteria` (
  `id` SERIAL NOT NULL,
  `interfaceFK` INT NOT NULL,
  `entity` VARCHAR(128) NOT NULL,
  `field` VARCHAR(128) NOT NULL,
  `alias` VARCHAR(128) DEFAULT '',
  `type` INT NOT NULL DEFAULT '1',
  `rank` INT NOT NULL DEFAULT '0',
  `mask` INT NOT NULL DEFAULT '0'
);

ALTER TABLE `router_search_criteria`
  ADD CONSTRAINT `pk1_router_search_criteria` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_criteria` UNIQUE (`interfaceFK`, `entity`, `field`),
  ADD CONSTRAINT `fk1_router_search_criteria` FOREIGN KEY (`interfaceFK`) REFERENCES `router_search_interface` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

------------------------------------------------------------------------------

CREATE TABLE `router_locations` (
  `id` SERIAL NOT NULL,
  `continentCode` VARCHAR(3) NOT NULL DEFAULT 'N/A',
  `countryCode` VARCHAR(3) NOT NULL DEFAULT 'N/A'
);

ALTER TABLE `router_locations`
  ADD CONSTRAINT `pk1_router_locations` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_locations` UNIQUE (`continentCode`, `countryCode`)
;

------------------------------------------------------------------------------

CREATE TABLE `router_ipv4_blocks` (
  `id` SERIAL NOT NULL,
  `network` VARCHAR(32) NOT NULL,
  `rangeBegin` DECIMAL(10, 0) NOT NULL,
  `rangeEnd` DECIMAL(10, 0) NOT NULL,
  `geoFK` INT NOT NULL
);

ALTER TABLE `router_ipv4_blocks`
  ADD CONSTRAINT `pk1_router_ipv4_blocks` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_ipv4_blocks` UNIQUE (`network`),
  ADD CONSTRAINT `fk1_router_ipv4_blocks` FOREIGN KEY (`geoFK`) REFERENCES `router_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;
------------------------------------------------------------------------------

CREATE TABLE `router_ipv6_blocks` (
  `id` SERIAL NOT NULL,
  `network` VARCHAR(64) NOT NULL,
  `rangeBegin` DECIMAL(38, 0) NOT NULL,
  `rangeEnd` DECIMAL(38, 0) NOT NULL,
  `geoFK` INT NOT NULL
);

ALTER TABLE `router_ipv6_blocks`
  ADD CONSTRAINT `pk1_router_ipv6_blocks` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_ipv6_blocks` UNIQUE (`network`),
  ADD CONSTRAINT `fk1_router_ipv6_blocks` FOREIGN KEY (`geoFK`) REFERENCES `router_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;

------------------------------------------------------------------------------
