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
			"  `id` INT(11) NOT NULL,"										+
			"  `paramName` VARCHAR(512) NOT NULL,"							+
			"  `paramValue` VARCHAR(512) NOT NULL"							+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_config`"									+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD PRIMARY KEY (`paramName`)"								+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_CATALOG                */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_catalog` ("					+
			"  `id` INT(11) NOT NULL,"										+
			"  `catalog` VARCHAR(512) NOT NULL,"							+
			"  `jdbcUrl` VARCHAR(512) NOT NULL,"							+
			"  `user` VARCHAR(512) NOT NULL,"								+
			"  `pass` VARCHAR(512) NOT NULL,"								+
			"  `schema` TEXT"												+
			"  `archived` INT(1) NOT NULL DEFAULT '0',"						+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_catalog`"									+
			"  ADD UNIQUE KEY (`id`)"										+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_ROLE                   */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_role` ("					+
			"  `id` INT(11) NOT NULL,"										+
			"  `lft` INT(11) NOT NULL,"										+
			"  `rgt` INT(11) NOT NULL,"										+
			"  `role` VARCHAR(512) NOT NULL,"								+
			"  `roleValidatorClass` TEXT"									+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_role`"										+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD PRIMARY KEY (`role`)"									+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COMMAND                */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command` ("					+
			"  `id` INT(11) NOT NULL,"										+
			"  `command` VARCHAR(512) NOT NULL,"							+
			"  `class` TEXT NOT NULL,"										+
			"  `archived` INT(1) NOT NULL DEFAULT '0'"						+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_command`"									+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD PRIMARY KEY (`command`)"									+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COMMAND_ROLE           */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command_role` ("			+
			"  `id` INT(11) NOT NULL,"										+
			"  `commandFK` INT(11) NOT NULL,"								+
			"  `roleFK` INT(11) NOT NULL"									+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_command_role`"								+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD CONSTRAINT FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION," +
			"  ADD CONSTRAINT FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_USER                   */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user` ("					+
			"  `id` INT(11) NOT NULL,"										+
			"  `AMIUser` VARCHAR(512) NOT NULL,"							+
			"  `AMIPass` VARCHAR(512) NOT NULL,"							+
			"  `clientDN` VARCHAR(512) NOT NULL,"							+
			"  `issuerDN` VARCHAR(512) NOT NULL,"							+
			"  `firstName` VARCHAR(512) NOT NULL,"							+
			"  `lastName` VARCHAR(512) NOT NULL,"							+
			"  `email` VARCHAR(512) NOT NULL,"								+
			"  `geoFK` INT(11) NOT NULL,"									+
			"  `valid` INT(1) NOT NULL DEFAULT '1',"						+
			"  `lastModif` TIMESTAMP NOT NULL"								+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_user`"										+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD PRIMARY KEY (`AMIUser`),"								+
			"  ADD CONSTRAINT FOREIGN KEY (`geonameFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION," +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_USER_ROLE              */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user_role` ("				+
			"  `id` INT(11) NOT NULL,"										+
			"  `userFK` INT(11) NOT NULL,"									+
			"  `roleFK` INT(11) NOT NULL"									+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_user_role`"								+
			"  ADD UNIQUE KEY (`id`)"										+
			"  ADD CONSTRAINT FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION," +
			"  ADD CONSTRAINT FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_SEARCH_INTERFACE       */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_search_interface` ("						+
			"  `id` INT(11) NOT NULL,"										+
			"  `interface` VARCHAR(512) NOT NULL,"							+
			"  `catalog` VARCHAR(512) NOT NULL,"							+
			"  `entity` VARCHAR(512) NOT NULL,"								+
			"  `archived` INT(1) NOT NULL DEFAULT '0'"						+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_search_interface`"							+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD PRIMARY KEY (`interface`)"								+
			";"
		);

		/*-------------------------------*/
		/* ROUTER_SEARCH_CRITERIA        */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_search_criteria` ("						+
			"  `id` INT(11) NOT NULL,"										+
			"  `interfaceFK` INT(11) NOT NULL,"								+
			"  `entity` VARCHAR(512) NOT NULL,"								+
			"  `field` VARCHAR(512) NOT NULL,"								+
			"  `alias` VARCHAR(512) NOT NULL DEFAULT '',"					+
			"  `type` INT(11) NOT NULL DEFAULT '1',"						+
			"  `rank` INT(11) NOT NULL DEFAULT '0'"							+
			") DEFAULT CHARSET=utf8;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_search_interface`"							+
			"  ADD UNIQUE KEY (`id`),"										+
			"  ADD PRIMARY KEY (`interfaceFK`, `entity`, `field`)"			+
			"  ADD CONSTRAINT FOREIGN KEY (`interfaceFK`) REFERENCES `router_search_interface` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COUNTRY_BLOCK          */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_country_blocks_ipv4` ("					+
			"  `network` VARCHAR(32) NOT NULL,"								+
			"  `rangeBegin` INT(4) NOT NULL,"								+
			"  `rangeEnd` INT(4) NOT NULL,"									+
			"  `geoFK` INT(11) NOT NULL,"									+
			") DEFAULT CHARSET=utf8;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv4`"						+
			"  ADD PRIMARY KEY (`network`),"								+
			"  ADD CONSTRAINT FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION," +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COUNTRY_BLOCK          */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_country_blocks_ipv6` ("					+
			"  `network` VARCHAR(64) NOT NULL,"								+
			"  `rangeBegin` INT(16) NOT NULL,"								+
			"  `rangeEnd` INT(16) NOT NULL,"								+
			"  `geoFK` INT(11) NOT NULL,"									+
			") DEFAULT CHARSET=utf8;"
		);

		m_driver.executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv6`"						+
			"  ADD PRIMARY KEY (`network`),"								+
			"  ADD CONSTRAINT FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION," +
			";"
		);

		/*-------------------------------*/
		/* ROUTER_COUNTRY_LOCATIONS      */
		/*-------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE `router_country_locations` ("						+
			"  `id` INT(11) NOT NULL,"										+
			"  `continentCode` VARCHAR(2) NOT NULL,"						+
			"  `countryCode` VARCHAR(2) NOT NULL,"							+
			") DEFAULT CHARSET=utf8;"
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		try
		{
			RouterBuilder rb = new RouterBuilder("self", "jdbc:mysql://localhost:3306/router", "root", "root");

			rb.build();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
