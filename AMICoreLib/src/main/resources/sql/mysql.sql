------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `router_ipv6_blocks`;;
DROP TABLE IF EXISTS `router_ipv4_blocks`;;
DROP TABLE IF EXISTS `router_locations`;;
DROP TABLE IF EXISTS `router_authority`;;
DROP TABLE IF EXISTS `router_markdown`;;
DROP TABLE IF EXISTS `router_short_url`;;
DROP TABLE IF EXISTS `router_search_interface`;;
DROP TABLE IF EXISTS `router_dashboard`;;
DROP TABLE IF EXISTS `router_user_role`;;
DROP TABLE IF EXISTS `router_user`;;
DROP TABLE IF EXISTS `router_command_role`;;
DROP TABLE IF EXISTS `router_command`;;
DROP TABLE IF EXISTS `router_role`;;
DROP TABLE IF EXISTS `router_foreign_key`;;
DROP TABLE IF EXISTS `router_field`;;
DROP TABLE IF EXISTS `router_entity`;;
DROP TABLE IF EXISTS `router_catalog`;;
DROP TABLE IF EXISTS `router_monitoring`;;
DROP TABLE IF EXISTS `router_converter`;;
DROP TABLE IF EXISTS `router_config`;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_config` (
  `id` INT NOT NULL,
  `paramName` VARCHAR(128) NOT NULL,
  `paramValue` VARCHAR(512),
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_config`
  ADD CONSTRAINT `pk1_router_config` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_config` UNIQUE KEY (`paramName`)
;;

ALTER TABLE `router_config`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_converter` (
  `id` INT NOT NULL,
  `xslt` VARCHAR(128) NOT NULL,
  `mime` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_converter`
  ADD CONSTRAINT `pk1_router_converter` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_converter` UNIQUE KEY (`xslt`)
;;

ALTER TABLE `router_converter`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_monitoring` (
  `id` INT NOT NULL,
  `node` VARCHAR(128) NOT NULL,
  `endpoint` VARCHAR(512) NOT NULL,
  `service` VARCHAR(128) NOT NULL,
  `frequency` INT DEFAULT 10,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_monitoring`
  ADD CONSTRAINT `pk1_router_monitoring` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_monitoring` UNIQUE KEY (`node`)
;;

ALTER TABLE `router_monitoring`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_catalog` (
  `id` INT NOT NULL,
  `externalCatalog` VARCHAR(128) NOT NULL,
  `internalCatalog` VARCHAR(128) NOT NULL,
  `internalSchema` VARCHAR(128),
  `jdbcUrl` VARCHAR(2048) NOT NULL,
  `user` VARCHAR(128) NOT NULL,
  `pass` VARCHAR(128) NOT NULL,
  `json` TEXT,
  `description` VARCHAR(512) DEFAULT 'N/A',
  `archived` TINYINT(1) NOT NULL DEFAULT 0,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_catalog`
  ADD CONSTRAINT `pk1_router_catalog` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_catalog` UNIQUE KEY (`externalCatalog`)
;;

ALTER TABLE `router_catalog`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_entity` (
  `id` INT NOT NULL,
  `catalog` VARCHAR(128) NOT NULL,
  `entity` VARCHAR(128) NOT NULL,
  `rank` INT,
  `json` TEXT,
  `description` VARCHAR(512) DEFAULT 'N/A',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_entity`
  ADD CONSTRAINT `pk1_router_entity` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_entity` UNIQUE KEY (`catalog`(64), `entity`(64))
;;

ALTER TABLE `router_entity`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_field` (
  `id` INT NOT NULL,
  `catalog` VARCHAR(128) NOT NULL,
  `entity` VARCHAR(128) NOT NULL,
  `field` VARCHAR(128) NOT NULL,
  `rank` INT,
  `json` TEXT,
  `description` VARCHAR(512) DEFAULT 'N/A',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_field`
  ADD CONSTRAINT `pk1_router_field` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_field` UNIQUE KEY (`catalog`(64), `entity`(64), `field`)
;;

ALTER TABLE `router_field`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_foreign_key` (
  `id` INT NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `fkCatalog` VARCHAR(128) NOT NULL,
  `fkTable` VARCHAR(128) NOT NULL,
  `fkColumn` VARCHAR(128) NOT NULL,
  `pkCatalog` VARCHAR(128) NOT NULL,
  `pkTable` VARCHAR(128) NOT NULL,
  `pkColumn` VARCHAR(128) NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_foreign_key`
  ADD CONSTRAINT `pk1_router_frgn_key` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_frgn_key` UNIQUE KEY (`name`),
  ADD CONSTRAINT `uk2_router_frgn_key` UNIQUE KEY (`fkCatalog`, `fkTable`, `fkColumn`, `pkCatalog`, `pkTable`, `pkColumn`)
;;

ALTER TABLE `router_foreign_key`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_role` (
  `id` INT NOT NULL,
  `role` VARCHAR(128) NOT NULL,
  `description` VARCHAR(512) DEFAULT 'N/A'

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_role`
  ADD CONSTRAINT `pk1_router_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_role` UNIQUE KEY (`role`)
;;

ALTER TABLE `router_role`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_command` (
  `id` INT NOT NULL,
  `command` VARCHAR(128) NOT NULL,
  `class` VARCHAR(256) NOT NULL,
  `visible` TINYINT(1) NOT NULL DEFAULT 1,
  `roleValidatorClass` VARCHAR(256)

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_command`
  ADD CONSTRAINT `pk1_router_command` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command` UNIQUE KEY (`command`)
;;

ALTER TABLE `router_command`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_command_role` (
  `id` INT NOT NULL,
  `commandFK` INT NOT NULL,
  `roleFK` INT NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_command_role`
  ADD CONSTRAINT `pk1_router_command_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command_role` UNIQUE KEY (`commandFK`, `roleFK`),
  ADD CONSTRAINT `fk1_router_command_role` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_command_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;;

ALTER TABLE `router_command_role`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_user` (
  `id` INT NOT NULL,
  `AMIUser` VARCHAR(128) NOT NULL,
  `ssoUser` VARCHAR(128),
  `AMIPass` VARCHAR(128) NOT NULL,
  `clientDN` VARCHAR(512),
  `issuerDN` VARCHAR(512),
  `firstName` VARCHAR(128) NOT NULL,
  `lastName` VARCHAR(128) NOT NULL,
  `email` VARCHAR(128) NOT NULL,
  `country` VARCHAR(128) DEFAULT 'N/A',
  `json` TEXT,
  `valid` TINYINT(1) NOT NULL DEFAULT 1,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_user`
  ADD CONSTRAINT `pk1_router_user` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user` UNIQUE KEY (`AMIUser`)
;;

ALTER TABLE `router_user`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_user_role` (
  `id` INT NOT NULL,
  `userFK` INT NOT NULL,
  `roleFK` INT NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_user_role`
  ADD CONSTRAINT `pk1_router_user_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user_role` UNIQUE KEY (`userFK`, `roleFK`),
  ADD CONSTRAINT `fk1_router_user_role` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_user_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;;

ALTER TABLE `router_user_role`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_dashboard` (
  `id` INT NOT NULL,
  `hash` VARCHAR(16) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `rank` INT NOT NULL DEFAULT 0,
  `json` TEXT NOT NULL,
  `shared` TINYINT(1) NOT NULL DEFAULT 0,
  `archived` TINYINT(1) NOT NULL DEFAULT 0,
  `owner` VARCHAR(128) NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_dashboard`
  ADD CONSTRAINT `pk1_router_dashboard` PRIMARY KEY (`id`)
;;

ALTER TABLE `router_dashboard`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_search_interface` (
  `id` INT NOT NULL,
  `group` VARCHAR(128) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `rank` INT NOT NULL DEFAULT 0,
  `json` TEXT NOT NULL,
  `archived` TINYINT(1) NOT NULL DEFAULT 0,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_search_interface`
  ADD CONSTRAINT `pk1_router_search_interface` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`group`, `name`)
;;

ALTER TABLE `router_search_interface`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_short_url` (
  `id` INT NOT NULL,
  `hash` VARCHAR(16) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `rank` INT NOT NULL DEFAULT 0,
  `json` TEXT NOT NULL,
  `shared` TINYINT(1) NOT NULL DEFAULT 0,
  `expire` TINYINT(1) NOT NULL DEFAULT 0,
  `owner` VARCHAR(128) NOT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_short_url`
  ADD CONSTRAINT `pk1_router_short_url` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_short_url` UNIQUE KEY (`hash`)
;;

ALTER TABLE `router_short_url`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_markdown` (
  `id` INT NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `title` VARCHAR(128) NOT NULL,
  `body` TEXT NOT NULL,
  `archived` TINYINT(1) NOT NULL DEFAULT 0,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_markdown`
  ADD CONSTRAINT `pk1_router_markdown` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_markdown` UNIQUE KEY (`name`)
;;

ALTER TABLE `router_markdown`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_authority` (
  `id` INT NOT NULL,
  `vo` VARCHAR(128) NOT NULL DEFAULT 'ami',
  `clientDN` VARCHAR(512) NOT NULL,
  `issuerDN` VARCHAR(512) NOT NULL,
  `notBefore` DATE NOT NULL,
  `notAfter` DATE NOT NULL,
  `serial` VARCHAR(128) NOT NULL,
  `email` VARCHAR(128) NOT NULL,
  `reason` SMALLINT,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `createdBy` VARCHAR(128) NOT NULL,
  `modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifiedBy` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_authority`
  ADD CONSTRAINT `pk1_router_authority` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_authority` UNIQUE KEY (`serial`)
;;

ALTER TABLE `router_authority`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_locations` (
  `id` INT NOT NULL,
  `continentCode` VARCHAR(3) NOT NULL DEFAULT 'N/A',
  `countryCode` VARCHAR(3) NOT NULL DEFAULT 'N/A'

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_locations`
  ADD CONSTRAINT `pk1_router_locations` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_locations` UNIQUE KEY (`continentCode`, `countryCode`)
;;

ALTER TABLE `router_locations`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_ipv4_blocks` (
  `id` INT NOT NULL,
  `network` VARCHAR(32) NOT NULL,
  `rangeBegin` DECIMAL(10, 0) NOT NULL,
  `rangeEnd` DECIMAL(10, 0) NOT NULL,
  `geoFK` INT NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_ipv4_blocks`
  ADD CONSTRAINT `pk1_router_ipv4_blocks` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_ipv4_blocks` UNIQUE KEY (`network`),
  ADD CONSTRAINT `fk1_router_ipv4_blocks` FOREIGN KEY (`geoFK`) REFERENCES `router_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;;

ALTER TABLE `router_ipv4_blocks`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_ipv6_blocks` (
  `id` INT NOT NULL,
  `network` VARCHAR(64) NOT NULL,
  `rangeBegin` DECIMAL(38, 0) NOT NULL,
  `rangeEnd` DECIMAL(38, 0) NOT NULL,
  `geoFK` INT NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_bin` ENGINE=`INNODB`;;

ALTER TABLE `router_ipv6_blocks`
  ADD CONSTRAINT `pk1_router_ipv6_blocks` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_ipv6_blocks` UNIQUE KEY (`network`),
  ADD CONSTRAINT `fk1_router_ipv6_blocks` FOREIGN KEY (`geoFK`) REFERENCES `router_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;;

ALTER TABLE `router_ipv6_blocks`
  MODIFY COLUMN `id` INT NOT NULL AUTO_INCREMENT
;;

------------------------------------------------------------------------------------------------------------------------
