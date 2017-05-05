------------------------------------------------------------------------------

DROP TABLE IF EXISTS `router_country_blocks_ipv6`;
DROP TABLE IF EXISTS `router_country_blocks_ipv4`;
DROP TABLE IF EXISTS `router_country_locations`;
DROP TABLE IF EXISTS `router_search_criteria`;
DROP TABLE IF EXISTS `router_search_interface`;
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
  `id` INT(11) NOT NULL,
  `paramName` VARCHAR(128) NOT NULL,
  `paramValue` VARCHAR(512) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_config`
  ADD CONSTRAINT `pk_router_config` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_config` UNIQUE KEY (`paramName`)
;

ALTER TABLE `router_config` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_catalog` (
  `id` INT(11) NOT NULL,
  `catalog` VARCHAR(128) NOT NULL,
  `jdbcUrl` VARCHAR(512) NOT NULL,
  `user` VARCHAR(128) NOT NULL,
  `pass` VARCHAR(128) NOT NULL,
  `archived` INT(1) NOT NULL DEFAULT '0',
  `jsonSerialization` TEXT

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_catalog`
  ADD CONSTRAINT `pk_router_catalog` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_catalog` UNIQUE KEY (`catalog`)
;

ALTER TABLE `router_catalog` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_converter` (
  `id` INT(11) NOT NULL,
  `xslt` VARCHAR(128) NOT NULL,
  `mime` VARCHAR(128) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_converter`
  ADD CONSTRAINT `pk_router_converter` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_converter` UNIQUE KEY (`xslt`)
;

ALTER TABLE `router_converter` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_role` (
  `id` INT(11) NOT NULL,
  `lft` INT(11) NOT NULL,
  `rgt` INT(11) NOT NULL,
  `role` VARCHAR(128) NOT NULL,
  `roleValidatorClass` VARCHAR(256)

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_role`
  ADD CONSTRAINT `pk_router_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_role` UNIQUE KEY (`role`)
;

ALTER TABLE `router_role` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_command` (
  `id` INT(11) NOT NULL,
  `command` VARCHAR(128) NOT NULL,
  `class` VARCHAR(256) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_command`
  ADD CONSTRAINT `pk_router_command` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command` UNIQUE KEY (`command`)
;

ALTER TABLE `router_command` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_command_role` (
  `id` INT(11) NOT NULL,
  `commandFK` INT(11) NOT NULL,
  `roleFK` INT(11) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_command_role`
  ADD CONSTRAINT `pk_router_command_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_command_role` UNIQUE KEY (`commandFK`, `roleFK`),
  ADD CONSTRAINT `fk1_router_command_role` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_command_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

ALTER TABLE `router_command_role` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_user` (
  `id` INT(11) NOT NULL,
  `AMIUser` VARCHAR(128) NOT NULL,
  `AMIPass` VARCHAR(128) NOT NULL,
  `clientDN` VARCHAR(512) NOT NULL,
  `issuerDN` VARCHAR(512) NOT NULL,
  `firstName` VARCHAR(128) NOT NULL,
  `lastName` VARCHAR(128) NOT NULL,
  `email` VARCHAR(128),
  `country` VARCHAR(128),
  `valid` INT(1) NOT NULL DEFAULT '1'

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_user`
  ADD CONSTRAINT `pk_router_user` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user` UNIQUE KEY (`AMIUser`)
;

ALTER TABLE `router_user` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_user_role` (
  `id` INT(11) NOT NULL,
  `userFK` INT(11) NOT NULL,
  `roleFK` INT(11) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_user_role`
  ADD CONSTRAINT `pk_router_user_role` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_user_role` UNIQUE KEY (`userFK`, `roleFK`),
  ADD CONSTRAINT `fk1_router_user_role` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk2_router_user_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

ALTER TABLE `router_user_role` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_search_interface` (
  `id` INT(11) NOT NULL,
  `interface` VARCHAR(128) NOT NULL,
  `catalog` VARCHAR(128) NOT NULL,
  `entity` VARCHAR(128) NOT NULL,
  `archived` INT(1) NOT NULL DEFAULT '0'

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_search_interface`
  ADD CONSTRAINT `pk_router_search_interface` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`interface`)
;

ALTER TABLE `router_search_interface` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_search_criteria` (
  `id` INT(11) NOT NULL,
  `interfaceFK` INT(11) NOT NULL,
  `entity` VARCHAR(128) NOT NULL,
  `field` VARCHAR(128) NOT NULL,
  `alias` VARCHAR(128) DEFAULT '',
  `type` INT(11) NOT NULL DEFAULT '1',
  `rank` INT(11) NOT NULL DEFAULT '0',
  `mask` INT(11) NOT NULL DEFAULT '0'

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_search_criteria`
  ADD CONSTRAINT `pk_router_search_criteria` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_search_criteria` UNIQUE KEY (`interfaceFK`, `entity`, `field`),
  ADD CONSTRAINT `fk1_router_search_criteria` FOREIGN KEY (`interfaceFK`) REFERENCES `router_search_interface` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
;

ALTER TABLE `router_search_criteria` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_country_locations` (
  `id` INT(11) NOT NULL,
  `continentCode` VARCHAR(2),
  `countryCode` VARCHAR(2)

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_country_locations`
  ADD CONSTRAINT `pk_router_country_locations` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_country_locations` UNIQUE KEY (`continentCode`, `countryCode`)
;

ALTER TABLE `router_country_locations` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_country_blocks_ipv4` (
  `id` INT(11) NOT NULL,
  `network` VARCHAR(32),
  `rangeBegin` DECIMAL(10, 0),
  `rangeEnd` DECIMAL(10, 0),
  `geoFK` INT(11) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_country_blocks_ipv4`
  ADD CONSTRAINT `pk_router_country_blocks_ipv4` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_country_blocks_ipv4` UNIQUE KEY (`network`),
  ADD CONSTRAINT `fk1_router_country_blocks_ipv4` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE `router_country_blocks_ipv4` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------

CREATE TABLE `router_country_blocks_ipv6` (
  `id` INT(11) NOT NULL,
  `network` VARCHAR(64),
  `rangeBegin` DECIMAL(38, 0),
  `rangeEnd` DECIMAL(38, 0),
  `geoFK` INT(11) NOT NULL

) CHARSET=`utf8` COLLATE=`utf8_unicode_ci`;

ALTER TABLE `router_country_blocks_ipv6`
  ADD CONSTRAINT `pk_router_country_blocks_ipv6` PRIMARY KEY (`id`),
  ADD CONSTRAINT `uk1_router_country_blocks_ipv6` UNIQUE KEY (`network`),
  ADD CONSTRAINT `fk1_router_country_blocks_ipv6` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE `router_country_blocks_ipv6` MODIFY COLUMN `id` INT(11) NOT NULL AUTO_INCREMENT;

------------------------------------------------------------------------------
