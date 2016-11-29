package net.hep.ami.jdbc;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.annotation.*;

public class RouterBuilder
{
	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public RouterBuilder() throws Exception
	{
		m_driver = DriverSingleton.getConnection(
			"self",
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);
	}

	/*---------------------------------------------------------------------*/

	public RouterBuilder(@Nullable String catalog, String jdbcUrl, String user, String pass) throws Exception
	{
		m_driver = DriverSingleton.getConnection(
			catalog,
			jdbcUrl,
			user,
			pass
		);
	}

	/*---------------------------------------------------------------------*/

	public void build() throws Exception
	{
		/*-------------------------------*/
		/* ROUTER_CONFIG                 */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_config` ("					+
			"  `id` INT(11),"												+
			"  `paramName` VARCHAR(128),"									+
			"  `paramValue` VARCHAR(512)"									+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_config`"									+
			"  ADD CONSTRAINT `pk_router_config` PRIMARY KEY (`id`),"		+
			"  ADD CONSTRAINT `uk1_router_config` UNIQUE KEY (`paramName`)"	+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_CATALOG                */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_catalog` ("					+
			"  `id` INT(11),"												+
			"  `catalog` VARCHAR(128),"										+
			"  `jdbcUrl` VARCHAR(512),"										+
			"  `user` VARCHAR(128),"										+
			"  `pass` VARCHAR(128),"										+
			"  `archived` INT(1) DEFAULT '0',"								+
			"  `jsonSerialization` TEXT"									+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_catalog`"									+
			"  ADD CONSTRAINT `pk_router_catalog` PRIMARY KEY (`id`)"		+
			"  ADD CONSTRAINT `uk1_router_catalog` UNIQUE KEY (`catalog`)"	+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_ROLE                   */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_role` ("					+
			"  `id` INT(11),"												+
			"  `lft` INT(11),"												+
			"  `rgt` INT(11),"												+
			"  `role` VARCHAR(128),"										+
			"  `roleValidatorClass` VARCHAR(512)"							+
			")"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_role`"										+
			"  ADD CONSTRAINT `pk_router_role` PRIMARY KEY (`id`),"			+
			"  ADD CONSTRAINT `uk1_router_role` UNIQUE KEY (`role`)"		+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COMMAND                */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command` ("					+
			"  `id` INT(11),"												+
			"  `command` VARCHAR(128),"										+
			"  `class` VARCHAR(512),"										+
			"  `archived` INT(1) DEFAULT '0'"								+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_command`"									+
			"  ADD CONSTRAINT `pk_router_command` PRIMARY KEY (`id`),"		+
			"  ADD CONSTRAINT `uk1_router_command` UNIQUE KEY (`command`)"	+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COMMAND_ROLE           */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command_role` ("			+
			"  `id` INT(11),"												+
			"  `commandFK` INT(11),"										+
			"  `roleFK` INT(11)"											+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_command_role`"								+
			"  ADD CONSTRAINT `pk_router_command` PRIMARY KEY (`id`),"		+
			"  ADD CONSTRAINT `uk1_router_command` UNIQUE KEY (`commandFK`, `roleFK`)," +
			"  ADD CONSTRAINT `fk1_router_command` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION," +
			"  ADD CONSTRAINT `fk2_router_command` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_USER                   */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user` ("					+
			"  `id` INT(11),"												+
			"  `AMIUser` VARCHAR(128),"										+
			"  `AMIPass` VARCHAR(128),"										+
			"  `clientDN` VARCHAR(512),"									+
			"  `issuerDN` VARCHAR(512),"									+
			"  `firstName` VARCHAR(128),"									+
			"  `lastName` VARCHAR(128),"									+
			"  `email` VARCHAR(128),"										+
			"  `country` VARCHAR(128),"										+
			"  `valid` INT(1) DEFAULT '1',"									+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_user`"										+
			"  ADD CONSTRAINT `pk_router_user` PRIMARY KEY (`id`),"			+
			"  ADD CONSTRAINT `uk1_router_user` UNIQUE KEY (`AMIUser`)"		+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_USER_ROLE              */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user_role` ("				+
			"  `id` INT(11),"												+
			"  `userFK` INT(11),"											+
			"  `roleFK` INT(11)"											+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_user_role`"								+
			"  ADD CONSTRAINT `pk_router_user` PRIMARY KEY (`id`),"			+
			"  ADD CONSTRAINT `uk1_router_user` UNIQUE KEY (`userFK`, `roleFK`)," +
			"  ADD CONSTRAINT `fk1_router_command` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION," +
			"  ADD CONSTRAINT `fk2_router_command` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_SEARCH_INTERFACE       */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_search_interface` ("						+
			"  `id` INT(11),"												+
			"  `interface` VARCHAR(128),"									+
			"  `catalog` VARCHAR(128),"										+
			"  `entity` VARCHAR(128),"										+
			"  `archived` INT(1) DEFAULT '0'"								+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_search_interface`"							+
			"  ADD CONSTRAINT `pk_router_search_interface` PRIMARY KEY (`id`)," +
			"  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`interface`)" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_SEARCH_CRITERIA        */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_search_criteria` ("						+
			"  `id` INT(11),"												+
			"  `interfaceFK` INT(11),"										+
			"  `entity` VARCHAR(512),"										+
			"  `field` VARCHAR(512),"										+
			"  `alias` VARCHAR(512) DEFAULT '',"							+
			"  `type` INT(11) DEFAULT '1',"									+
			"  `rank` INT(11) DEFAULT '0',"									+
			"  `mask` INT(11) DEFAULT '0'"									+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_search_interface`"							+
			"  ADD CONSTRAINT `pk_router_search_interface` PRIMARY KEY (`id`)," +
			"  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`interfaceFK`, `entity`, `field`)," +
			"  ADD CONSTRAINT `fk1_router_search_interface` FOREIGN KEY (`interfaceFK`) REFERENCES `router_search_interface` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COUNTRY_BLOCK          */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_country_blocks_ipv4` ("					+
			"  `id` INT(11),"												+
			"  `network` VARCHAR(32),"										+
			"  `rangeBegin` BIGINT(64),"									+
			"  `rangeEnd` BIGINT(64),"										+
			"  `geoFK` INT(11)"												+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv4`"						+
			"  ADD CONSTRAINT `pk_router_country_blocks_ipv4` PRIMARY KEY (`id`)," +
			"  ADD CONSTRAINT `uk1_router_country_blocks_ipv4` UNIQUE KEY (`network`)," +
			"  ADD CONSTRAINT `fk1_router_country_blocks_ipv4` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COUNTRY_BLOCK          */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_country_blocks_ipv6` ("					+
			"  `id` INT(11),"												+
			"  `network` VARCHAR(64),"										+
			"  `rangeBegin` DECIMAL(38,0),"									+
			"  `rangeEnd` DECIMAL(38,0),"									+
			"  `geoFK` INT(11)"												+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv6`"						+
			"  ADD CONSTRAINT `pk_router_country_blocks_ipv6` PRIMARY KEY (`id`)," +
			"  ADD CONSTRAINT `uk1_router_country_blocks_ipv6` UNIQUE KEY (`network`)," +
			"  ADD CONSTRAINT `fk1_router_country_blocks_ipv6` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COUNTRY_LOCATIONS      */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_country_locations` ("						+
			"  `id` INT(11),"												+
			"  `continentCode` VARCHAR(2),"									+
			"  `countryCode` VARCHAR(2)"									+
			");"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_country_locations`"						+
			"  ADD CONSTRAINT `pk_router_country_locations` PRIMARY KEY (`id`)," +
			"  ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`continentCode`, `countryCode`)" +
			";"
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
