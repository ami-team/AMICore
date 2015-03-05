-- --------------------------------------------------------
-- --------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `router` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE router;

-- --------------------------------------------------------
-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_config`;

CREATE TABLE IF NOT EXISTS `router_config` (
 `name` varchar(64) NOT NULL,
 `value` varchar(512) NOT NULL
);

ALTER TABLE `router_config`
 ADD PRIMARY KEY (`name`);

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_node`;

CREATE TABLE IF NOT EXISTS `router_node` (
 `id` int(11) NOT NULL,
 `name` varchar(64) NOT NULL,
 `url` varchar(512) NOT NULL,
 `service` varchar(64) NOT NULL
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_node`
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_node`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_catalogs`;

CREATE TABLE IF NOT EXISTS `router_catalogs` (
 `id` int(11) NOT NULL,
 `catalog` varchar(128) NOT NULL,
 `jdbcUrl` varchar(512) NOT NULL,
 `user` varchar(128) NOT NULL,
 `pass` varchar(128) NOT NULL,
 `name` varchar(128) NOT NULL,
 `jsonSchema` text
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_catalogs`
 ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_catalogs`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_role`;

CREATE TABLE IF NOT EXISTS `router_role` (
 `id` int(11) NOT NULL,
 `parent` int(11) NOT NULL,
 `name` varchar(128) NOT NULL
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_role`
 ADD PRIMARY KEY (`name`), ADD UNIQUE KEY “id” (`id`), ADD KEY `PARENT_FK_idx` (`parent`);

ALTER TABLE `router_role`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `router_role`
 ADD CONSTRAINT `ROLE_PARENT_FK` FOREIGN KEY (`parent`) REFERENCES `router_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_command`;

CREATE TABLE IF NOT EXISTS `router_command` (
 `id` int(11) NOT NULL,
 `name` varchar(128) NOT NULL,
 `class` text NOT NULL
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_command`
 ADD PRIMARY KEY (`name`), ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_command`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_command_role`;

CREATE TABLE IF NOT EXISTS `router_command_role` (
 `id` int(11) NOT NULL,
 `command` int(11) NOT NULL,
 `role` int(11) NOT NULL,
 `roleValidatorClass` text
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_command_role`
 ADD PRIMARY KEY (`command`,`role`), ADD UNIQUE KEY `id_UNIQUE` (`id`), ADD KEY `COMMAND_ROLE_FK_idx` (`role`);

ALTER TABLE `router_command_role`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `router_command_role`
 ADD CONSTRAINT `COMMAND_COMMAND_FK` FOREIGN KEY (`command`) REFERENCES `router_command` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
 ADD CONSTRAINT `COMMAND_ROLE_FK` FOREIGN KEY (`role`) REFERENCES `router_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_user`;

CREATE TABLE IF NOT EXISTS `router_user` (
 `id` int(11) NOT NULL,
 `AMIUser` varchar(128) NOT NULL,
 `AMIPass` varchar(128) NOT NULL,
 `clientDN` varchar(256) NOT NULL DEFAULT '',
 `issuerDN` varchar(256) NOT NULL DEFAULT '',
 `firstName` varchar(256) NOT NULL,
 `lastName` varchar(256) NOT NULL,
 `email` varchar(256) NOT NULL,
 `valid` int(1) NOT NULL
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_user`
 ADD PRIMARY KEY (`AMIUser`), ADD UNIQUE KEY `id_UNIQUE` (`id`);

ALTER TABLE `router_user`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `router_user_role`;

CREATE TABLE IF NOT EXISTS `router_user_role` (
 `id` int(11) NOT NULL,
 `user` int(11) NOT NULL,
 `role` int(11) NOT NULL
) AUTO_INCREMENT=1 ;

ALTER TABLE `router_user_role`
 ADD PRIMARY KEY (`user`,`role`), ADD UNIQUE KEY `id_UNIQUE` (`id`), ADD KEY `USER_ROLE_FK_idx` (`role`);

ALTER TABLE `router_user_role`
 MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `router_user_role`
 ADD CONSTRAINT `USER_USER_FK` FOREIGN KEY (`user`) REFERENCES `router_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
 ADD CONSTRAINT `USER_ROLE_FK` FOREIGN KEY (`role`) REFERENCES `router_role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

-- --------------------------------------------------------
-- --------------------------------------------------------

