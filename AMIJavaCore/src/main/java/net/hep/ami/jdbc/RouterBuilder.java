package net.hep.ami.jdbc;

import net.hep.ami.*;
import net.hep.ami.utility.annotation.*;

public class RouterBuilder extends BasicQuerier
{
	/*---------------------------------------------------------------------*/

	public RouterBuilder(String catalog) throws Exception
	{
		super(catalog);
	}

	/*---------------------------------------------------------------------*/

	public RouterBuilder(@Nullable String catalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(catalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	public void create() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* ROUTER_CONFIG                                                   */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_config` ("																+
			" `id` INT(11),"																							+
			" `paramName` VARCHAR(128),"																				+
			" `paramValue` VARCHAR(512)"																				+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_config`"																				+
			" ADD CONSTRAINT `pk_router_config` PRIMARY KEY (`id`),"													+
			" ADD CONSTRAINT `uk1_router_config` UNIQUE KEY (`paramName`)"												+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_config` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_CATALOG                                                  */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_catalog` ("																+
			" `id` INT(11),"																							+
			" `catalog` VARCHAR(128),"																					+
			" `jdbcUrl` VARCHAR(512),"																					+
			" `user` VARCHAR(128),"																						+
			" `pass` VARCHAR(128),"																						+
			" `archived` INT(1) DEFAULT '0',"																			+
			" `jsonSerialization` TEXT"																					+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_catalog`"																				+
			" ADD CONSTRAINT `pk_router_catalog` PRIMARY KEY (`id`),"													+
			" ADD CONSTRAINT `uk1_router_catalog` UNIQUE KEY (`catalog`)"												+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_catalog` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_ROLE                                                     */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_role` ("																+
			" `id` INT(11),"																							+
			" `lft` INT(11),"																							+
			" `rgt` INT(11),"																							+
			" `role` VARCHAR(128),"																						+
			" `roleValidatorClass` VARCHAR(512)"																		+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_role`"																					+
			" ADD CONSTRAINT `pk_router_role` PRIMARY KEY (`id`),"														+
			" ADD CONSTRAINT `uk1_router_role` UNIQUE KEY (`role`)"														+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_role` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COMMAND                                                  */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command` ("																+
			" `id` INT(11),"																							+
			" `command` VARCHAR(128),"																					+
			" `class` VARCHAR(512)"																					+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_command`"																				+
			" ADD CONSTRAINT `pk_router_command` PRIMARY KEY (`id`),"													+
			" ADD CONSTRAINT `uk1_router_command` UNIQUE KEY (`command`)"												+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_command` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COMMAND_ROLE                                             */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_command_role` ("														+
			" `id` INT(11),"																							+
			" `commandFK` INT(11),"																						+
			" `roleFK` INT(11)"																							+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_command_role`"																			+
			" ADD CONSTRAINT `pk_router_command_role` PRIMARY KEY (`id`),"												+
			" ADD CONSTRAINT `uk1_router_command_role` UNIQUE KEY (`commandFK`, `roleFK`),"								+
			" ADD CONSTRAINT `fk1_router_command_role` FOREIGN KEY (`commandFK`) REFERENCES `router_command` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION," +
			" ADD CONSTRAINT `fk2_router_command_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_command_role` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_USER                                                     */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user` ("																+
			" `id` INT(11),"																							+
			" `AMIUser` VARCHAR(128),"																					+
			" `AMIPass` VARCHAR(128),"																					+
			" `clientDN` VARCHAR(512),"																					+
			" `issuerDN` VARCHAR(512),"																					+
			" `firstName` VARCHAR(128),"																				+
			" `lastName` VARCHAR(128),"																					+
			" `email` VARCHAR(128),"																					+
			" `country` VARCHAR(128),"																					+
			" `valid` INT(1) DEFAULT '1'"																				+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_user`"																					+
			" ADD CONSTRAINT `pk_router_user` PRIMARY KEY (`id`),"														+
			" ADD CONSTRAINT `uk1_router_user` UNIQUE KEY (`AMIUser`)"													+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_user` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_USER_ROLE                                                */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_user_role` ("															+
			" `id` INT(11),"																							+
			" `userFK` INT(11),"																						+
			" `roleFK` INT(11)"																							+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_user_role`"																			+
			" ADD CONSTRAINT `pk_router_user_role` PRIMARY KEY (`id`),"													+
			" ADD CONSTRAINT `uk1_router_user_role` UNIQUE KEY (`userFK`, `roleFK`),"									+
			" ADD CONSTRAINT `fk1_router_user_role` FOREIGN KEY (`userFK`) REFERENCES `router_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION," +
			" ADD CONSTRAINT `fk2_router_user_role` FOREIGN KEY (`roleFK`) REFERENCES `router_role` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_user_role` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_CONVERTER                                                */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE IF NOT EXISTS `router_converter` ("															+
			" `id` INT(11),"																							+
			" `xslt` VARCHAR(512),"																						+
			" `mime` VARCHAR(128)"																						+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_converter`"																			+
			" ADD CONSTRAINT `pk_router_converter` PRIMARY KEY (`id`),"													+
			" ADD CONSTRAINT `uk1_router_converter` UNIQUE KEY (`xslt`)"												+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_converter` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_SEARCH_INTERFACE                                         */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE `router_search_interface` ("																	+
			" `id` INT(11),"																							+
			" `interface` VARCHAR(128),"																				+
			" `catalog` VARCHAR(128),"																					+
			" `entity` VARCHAR(128),"																					+
			" `archived` INT(1) DEFAULT '0'"																			+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_search_interface`"																		+
			" ADD CONSTRAINT `pk_router_search_interface` PRIMARY KEY (`id`),"											+
			" ADD CONSTRAINT `uk1_router_search_interface` UNIQUE KEY (`interface`)"									+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_search_interface` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_SEARCH_CRITERIA                                          */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE `router_search_criteria` ("																	+
			" `id` INT(11),"																							+
			" `interfaceFK` INT(11),"																					+
			" `entity` VARCHAR(512),"																					+
			" `field` VARCHAR(512),"																					+
			" `alias` VARCHAR(512) DEFAULT '',"																			+
			" `type` INT(11) DEFAULT '1',"																				+
			" `rank` INT(11) DEFAULT '0',"																				+
			" `mask` INT(11) DEFAULT '0'"																				+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_search_criteria`"																		+
			" ADD CONSTRAINT `pk_router_search_criteria` PRIMARY KEY (`id`),"											+
			" ADD CONSTRAINT `uk1_router_search_criteria` UNIQUE KEY (`interfaceFK`, `entity`, `field`),"				+
			" ADD CONSTRAINT `fk1_router_search_criteria` FOREIGN KEY (`interfaceFK`) REFERENCES `router_search_interface` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION" +
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_search_criteria` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COUNTRY_LOCATIONS                                        */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE `router_country_locations` ("																	+
			" `id` INT(11),"																							+
			" `continentCode` VARCHAR(2),"																				+
			" `countryCode` VARCHAR(2)"																					+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_country_locations`"																	+
			" ADD CONSTRAINT `pk_router_country_locations` PRIMARY KEY (`id`),"											+
			" ADD CONSTRAINT `uk1_router_country_locations` UNIQUE KEY (`continentCode`, `countryCode`)"				+
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_country_locations` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COUNTRY_BLOCK_IPV4                                       */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE `router_country_blocks_ipv4` ("																+
			" `id` INT(11),"																							+
			" `network` VARCHAR(32),"																					+
			" `rangeBegin` DECIMAL(10, 0),"																				+
			" `rangeEnd` DECIMAL(10, 0),"																				+
			" `geoFK` INT(11)"																							+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv4`"																	+
			" ADD CONSTRAINT `pk_router_country_blocks_ipv4` PRIMARY KEY (`id`),"										+
			" ADD CONSTRAINT `uk1_router_country_blocks_ipv4` UNIQUE KEY (`network`),"									+
			" ADD CONSTRAINT `fk1_router_country_blocks_ipv4` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" +
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv4` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
		/* ROUTER_COUNTRY_BLOCK_IPV6                                       */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"CREATE TABLE `router_country_blocks_ipv6` ("																+
			" `id` INT(11),"																							+
			" `network` VARCHAR(64),"																					+
			" `rangeBegin` DECIMAL(38, 0),"																				+
			" `rangeEnd` DECIMAL(38, 0),"																				+
			" `geoFK` INT(11)"																							+
			");"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv6`"																	+
			" ADD CONSTRAINT `pk_router_country_blocks_ipv6` PRIMARY KEY (`id`),"										+
			" ADD CONSTRAINT `uk1_router_country_blocks_ipv6` UNIQUE KEY (`network`),"									+
			" ADD CONSTRAINT `fk1_router_country_blocks_ipv6` FOREIGN KEY (`geoFK`) REFERENCES `router_country_locations` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" +
			";"
		);

		executeMQLUpdate(
			"ALTER TABLE `router_country_blocks_ipv6` MODIFY COLUMN `id` INT AUTO_INCREMENT;"
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public void fill() throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* SELF                                                            */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"INSERT INTO `router_catalog` (`catalog`, `jdbcUrl`, `user`, `pass`, `archived`, `jsonSerialization`) VALUES" +
			" ('self', '" + getJdbcUrl().replace("'", "''") + "', '" + CryptographySingleton.encrypt(getUser()) + "', '" + CryptographySingleton.encrypt(getPass()) + "', 0, NULL)" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* ROLES                                                           */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"INSERT INTO `router_role` (`lft`, `rgt`, `role`) VALUES" +
			" (0, 3, 'AMI_guest_role')," +
			" (1, 2, 'AMI_admin_role')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* USERS                                                           */
		/*-----------------------------------------------------------------*/

		String emptyDN = CryptographySingleton.encrypt("");

		executeMQLUpdate(
			"INSERT INTO `router_user` (`AMIUser`, `AMIPass`, `clientDN`, `issuerDN`, `firstName`, `lastName`, `email`, `country`, `valid`) VALUES" +
			" ('" + ConfigSingleton.getProperty("admin_user") + "', '" + CryptographySingleton.encrypt(ConfigSingleton.getProperty("admin_pass")) + "', '" + emptyDN + "', '" + emptyDN + "', 'admin', 'admin', 'ami@lpsc.in2p3.fr', 'N/A', 1)," +
			" ('" + ConfigSingleton.getProperty("guest_user") + "', '" + CryptographySingleton.encrypt(ConfigSingleton.getProperty("guest_pass")) + "', '" + emptyDN + "', '" + emptyDN + "', 'guest', 'guest', 'ami@lpsc.in2p3.fr', 'N/A', 1)" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* COMMANDS                                                        */
		/*-----------------------------------------------------------------*/

		for(String className: ClassSingleton.findClassNames("net.hep.ami.command"))
		{
			CommandSingleton.registerCommand(this, className);
		}

		/*-----------------------------------------------------------------*/
		/* CONVERTERS                                                      */
		/*-----------------------------------------------------------------*/

		executeMQLUpdate(
			"INSERT INTO `router_converter` (`xslt`, `mime`) VALUES" +
			" ('/xslt/AMIXmlToText.xsl', 'text/plain')," +
			" ('/xslt/AMIXmlToCsv.xsl', 'text/csv')," +
			" ('/xslt/AMIXmlToJson.xsl', 'application/json')" +
			";"
		);

		/*-----------------------------------------------------------------*/
		/* LOCALIZATION                                                    */
		/*-----------------------------------------------------------------*/

		LocalizationSingleton.fill(this);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
