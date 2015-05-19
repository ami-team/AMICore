package net.hep.ami.jdbc;

import net.hep.ami.*;
import net.hep.ami.jdbc.driver.*;

public class RouterBuilder {
	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public RouterBuilder() throws Exception {

		m_driver = DriverSingleton.getConnection(
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);
	}

	/*---------------------------------------------------------------------*/

	public void build() throws Exception {
		/*-----------------------------------------------------------------*/
		/* ROUTER_CONFIG                                                   */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_config` ("					+
			" `name` VARCHAR(512) NOT NULL,"								+
			" `value` VARCHAR(512) NOT NULL"								+
			") DEFAULT CHARSET=utf8;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_CATALOG                                                  */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_catalog` ("					+
			" `id` INT(11) NOT NULL,"										+
			" `catalog` VARCHAR(512) NOT NULL,"								+
			" `jdbcUrl` VARCHAR(512) NOT NULL,"								+
			" `user` VARCHAR(512) NOT NULL,"								+
			" `pass` VARCHAR(512) NOT NULL,"								+
			" `archived` int(1) NOT NULL DEFAULT '0',"						+
			" `jsonSerialization` text"										+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_ROLE                                                     */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_role` ("					+
			" `id` INT(11) NOT NULL,"										+
			" `parentFK` INT(11) NOT NULL,"									+
			" `role` VARCHAR(512) NOT NULL,"								+
			" `roleValidatorClass` TEXT"									+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COMMAND                                                  */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command` ("					+
			" `id` INT(11) NOT NULL,"										+
			" `command` VARCHAR(512) NOT NULL,"								+
			" `class` TEXT NOT NULL,"										+
			" `archived` INT(1) NOT NULL DEFAULT '0'"						+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COMMAND_ROLE                                             */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command_role` ("			+
			" `id` INT(11) NOT NULL,"										+
			" `commandFK` INT(11) NOT NULL,"								+
			" `roleFK` INT(11) NOT NULL"									+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_USER                                                     */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user` ("					+
			" `id` int(11) NOT NULL,"										+
			" `AMIUser` VARCHAR(512) NOT NULL,"								+
			" `AMIPass` VARCHAR(512) NOT NULL,"								+
			" `clientDN` VARCHAR(512) NOT NULL,"							+
			" `issuerDN` VARCHAR(512) NOT NULL,"							+
			" `firstName` VARCHAR(512) NOT NULL,"							+
			" `lastName` VARCHAR(512) NOT NULL,"							+
			" `email` VARCHAR(512) NOT NULL,"								+
			" `valid` int(1) NOT NULL DEFAULT '1'"							+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_USER_ROLE                                                */
		/*-----------------------------------------------------------------*/

		m_driver.executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user_role` ("				+
			" `id` INT(11) NOT NULL,"										+
			" `userFK` INT(11) NOT NULL,"									+
			" `roleFK` INT(11) NOT NULL"									+
			") DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;"
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
