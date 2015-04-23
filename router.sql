-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `router` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE router;

-- --------------------------------------------------------
-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_config`;

CREATE TABLE IF NOT EXISTS `router_config` (
 `name` varchar(128) NOT NULL,
 `value` varchar(512) NOT NULL
);

ALTER TABLE `router_config`
 ADD PRIMARY KEY (`name`);

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_node`;

CREATE TABLE IF NOT EXISTS `router_node` (
 `id` int(11) NOT NULL,
 `node` varchar(128) NOT NULL,
 `url` varchar(512) NOT NULL,
 `service` varchar(128) NOT NULL
) AUTO_INCREMENT=1;

ALTER TABLE `router_node`
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_node`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_catalog`;

CREATE TABLE IF NOT EXISTS `router_catalog` (
 `id` int(11) NOT NULL,
 `catalog` varchar(128) NOT NULL,
 `jdbcUrl` varchar(512) NOT NULL,
 `user` varchar(128) NOT NULL,
 `pass` varchar(128) NOT NULL,
 `archived` int(1) NOT NULL DEFAULT '0',
 `jsonSerialization` text
) AUTO_INCREMENT=1;

ALTER TABLE `router_catalog`
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_catalog`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_role`;

CREATE TABLE IF NOT EXISTS `router_role` (
 `id` int(11) NOT NULL,
 `parentFK` int(11) NOT NULL,
 `role` varchar(128) NOT NULL
) AUTO_INCREMENT=1;

ALTER TABLE `router_role`
 ADD PRIMARY KEY (`role`),
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_role`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `router_role`
 ADD CONSTRAINT `ROLE_PARENT_FK` FOREIGN KEY (`parentFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_command`;

CREATE TABLE IF NOT EXISTS `router_command` (
 `id` int(11) NOT NULL,
 `command` varchar(128) NOT NULL,
 `class` text NOT NULL,
 `archived` int(1) NOT NULL DEFAULT '0'
) AUTO_INCREMENT=1;

ALTER TABLE `router_command`
 ADD PRIMARY KEY (`command`),
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_command`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_command_role`;

CREATE TABLE IF NOT EXISTS `router_command_role` (
 `id` int(11) NOT NULL,
 `commandFK` int(11) NOT NULL,
 `roleFK` int(11) NOT NULL,
 `roleValidatorClass` text
) AUTO_INCREMENT=1;

ALTER TABLE `router_command_role`
 ADD PRIMARY KEY (`commandFK`,`roleFK`),
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_command_role`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `router_command_role`
 ADD CONSTRAINT `COMMAND_COMMAND_FK` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
 ADD CONSTRAINT `COMMAND_ROLE_FK`    FOREIGN KEY (`roleFK`)    REFERENCES `router_role`    (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_user`;

CREATE TABLE IF NOT EXISTS `router_user` (
 `id` int(11) NOT NULL,
 `AMIUser` varchar(128) NOT NULL,
 `AMIPass` varchar(128) NOT NULL,
 `clientDN` varchar(512) NOT NULL,
 `issuerDN` varchar(512) NOT NULL,
 `firstName` varchar(128) NOT NULL,
 `lastName` varchar(128) NOT NULL,
 `email` varchar(128) NOT NULL,
 `valid` int(1) NOT NULL DEFAULT '1'
) AUTO_INCREMENT=1;

ALTER TABLE `router_user`
 ADD PRIMARY KEY (`AMIUser`),
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_user`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_user_role`;

CREATE TABLE IF NOT EXISTS `router_user_role` (
 `id` int(11) NOT NULL,
 `userFK` int(11) NOT NULL,
 `roleFK` int(11) NOT NULL,
 `roleValidatorClass` text
) AUTO_INCREMENT=1;

ALTER TABLE `router_user_role`
 ADD PRIMARY KEY (`userFK`,`roleFK`),
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_user_role`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `router_user_role`
 ADD CONSTRAINT `USER_USER_FK` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
 ADD CONSTRAINT `USER_ROLE_FK` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_search_interface`;

CREATE TABLE "router_search_interface" (
  `id` int(11) NOT NULL,
  `interface` varchar(128) NOT NULL,
  `catalog` varchar(128) NOT NULL,
  `entity` varchar(128) NOT NULL,
  `archived` int(1) NOT NULL DEFAULT '0',
);

ALTER TABLE `router_search_interface`
 ADD PRIMARY KEY ("interface"),
 ADD UNIQUE KEY "id_UNIQUE" ("id");

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_search_criteria`;

CREATE TABLE IF NOT EXISTS `router_search_criteria` (
  `interfaceFK` int(11) NOT NULL,
  `catalog` varchar(128) NOT NULL,
  `entity` varchar(128) NOT NULL,
  `field` varchar(128) NOT NULL,
  `type` int(2) NOT NULL DEFAULT '0'
);

ALTER TABLE `router_search_criteria`
 ADD PRIMARY KEY (`interfaceFK`,`catalog`,`entity`,`field`);

ALTER TABLE `router_search_criteria`
 ADD CONSTRAINT `INTERFACE_INTERFACE_FK` FOREIGN KEY ("interfaceFK") REFERENCES "router_search_interface" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- --------------------------------------------------------
-- --------------------------------------------------------
