package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;

public class RoleSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Class<CommandValidatorInterface>> s_roleValidators = new java.util.concurrent.ConcurrentHashMap<String, Class<CommandValidatorInterface>>();
	private static final Map<String, Class<NewUserValidatorInterface>> s_userValidators = new java.util.concurrent.ConcurrentHashMap<String, Class<NewUserValidatorInterface>>();

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_roleValidators.clear();
		s_userValidators.clear();

		addValidators();
	}

	/*---------------------------------------------------------------------*/

	private static void addValidators()
	{
		Set<String> classeNames = ClassSingleton.findClassNames("net.hep.ami.role");

		for(String className: classeNames)
		{
			try
			{
				addValidator(className);
			}
			catch(Exception e)
			{
				LogSingleton.defaultLogger.error(e.getMessage());
			}
		}
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addValidator(String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD COMMAND VALIDATOR                                           */
		/*-----------------------------------------------------------------*/

		/**/ if(ClassSingleton.extendsClass(clazz, CommandValidatorInterface.class))
		{
			s_roleValidators.put(clazz.getName(), (Class<CommandValidatorInterface>) clazz);
		}

		/*-----------------------------------------------------------------*/
		/* ADD NEW USER VALIDATOR                                          */
		/*-----------------------------------------------------------------*/

		else if(ClassSingleton.extendsClass(clazz, NewUserValidatorInterface.class))
		{
			s_userValidators.put(clazz.getName(), (Class<NewUserValidatorInterface>) clazz);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void addRole(QuerierInterface querier, String parent, String role, String roleValidatorClass, boolean insertAfter) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET PARENT ID                                                   */
		/*-----------------------------------------------------------------*/

		String sql = String.format(
			"SELECT `lft`, `rgt`, `rgt` - `lft` - 1 FROM `router_role` WHERE `role` = '%s'",
			parent.replace("'", "''")
		);

		List<Row> rowList = querier.executeQuery(sql).getAll();

		if(rowList.size() != 1)
		{
			throw new Exception("unknown role `" + parent + "`");
		}

		Row row = rowList.get(0);

		/*-----------------------------------------------------------------*/

		String parentLft = row.getValue(0);
		String parentRgt = row.getValue(1);
		String isLeaf = row.getValue(2);

		/*-----------------------------------------------------------------*/
		/* ADD ROLE                                                        */
		/*-----------------------------------------------------------------*/

		if(insertAfter || isLeaf.equals("0"))
		{
			querier.executeUpdate(String.format("UPDATE `router_role` SET `lft` = `lft` + 2 WHERE `lft` > %s",
				parentLft
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `rgt` = `rgt` + 2 WHERE `rgt` > %s",
				parentLft
			));

			querier.executeUpdate(String.format("INSERT INTO `router_role` (`lft`, `rgt`, `role`, `validatorClass`) VALUES (%s + 1, %s + 2, '%s', '%s')",
				parentLft, parentLft,
				role.replace("'", "''"),
				roleValidatorClass.replace("'", "''")
			));
		}
		else
		{
			querier.executeUpdate(String.format("UPDATE `router_role` SET `lft` = `lft` + 2 WHERE `lft` > %s ORDER BY `lft` DESC",
				parentRgt
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `rgt` = `rgt` + 2 WHERE `rgt` > %s ORDER BY `rgt` DESC",
				parentRgt
			));

			querier.executeUpdate(String.format("INSERT INTO `router_role` (`lft`, `rgt`, `role`, `validatorClass`) VALUES (%s + 1, %s + 2, '%s', '%s')",
				parentRgt, parentRgt,
				role.replace("'", "''"),
				roleValidatorClass.replace("'", "''")
			));
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void removeRole(QuerierInterface querier, String role, boolean recursive) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql = String.format(
			"SELECT `lft`, `rgt`, `rgt` - `lft` - 1, `rgt` - `lft` + 1 FROM `router_role` WHERE `role` = '%s'",
			role.replace("'", "''")
		);

		List<Row> rowList = querier.executeQuery(sql).getAll();

		if(rowList.size() != 1)
		{
			throw new Exception("unknown role `" + role + "`");
		}

		Row row = rowList.get(0);

		/*-----------------------------------------------------------------*/

		String roleLft = row.getValue(0);
		String roleRgt = row.getValue(1);
		String isLeaf = row.getValue(2);
		String width = row.getValue(3);

		/*-----------------------------------------------------------------*/
		/* DELETE ROLE                                                     */
		/*-----------------------------------------------------------------*/

		if(recursive || isLeaf.equals("0"))
		{
			querier.executeUpdate(String.format("DELETE FROM `router_role` WHERE `lft` BETWEEN %s AND %s",
				roleLft,
				roleRgt
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `lft` = `lft` - %s WHERE `lft` > %s",
				width,
				roleRgt
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `rgt` = `rgt` - %s WHERE `rgt` > %s",
				width,
				roleRgt
			));
		}
		else
		{
			querier.executeUpdate(String.format("DELETE FROM `router_role` WHERE `lft` = %s AND `rgt` = %s",
				roleLft,
				roleRgt
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `rgt` = `rgt` - 1, `lft` = `lft` - 1 WHERE `lft` BETWEEN %s AND %s",
				roleLft,
				roleRgt
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `lft` = `lft` - %s WHERE `lft` > %s",
				"2",
				roleRgt
			));

			querier.executeUpdate(String.format("UPDATE `router_role` SET `rgt` = `rgt` - %s WHERE `rgt` > %s",
				"2",
				roleRgt
			));
		}
	}

	/*---------------------------------------------------------------------*/

	public static void checkRoles(QuerierInterface querier, String command, Map<String, String> arguments) throws Exception
	{
		/*---------------------------------*/

		if(command.equals("GetSessionInfo")
		   ||
		   command.equals("ResetPassword")
		   ||
		   command.equals("AddUser")
		 ) {
			return;
		}

		/*---------------------------------*/

		String AMIUser = arguments.get("AMIUser");
		String AMIPass = arguments.get("AMIPass");

		if(AMIUser == null
		   ||
		   AMIPass == null
		 ) {
			throw new Exception("not authenticated");
		}

		AMIPass = CryptographySingleton.encrypt(AMIPass);

		/*-----------------------------------------------------------------*/
		/* GET ROLE                                                        */
		/*-----------------------------------------------------------------*/

		String sql = String.format(
			"SELECT `node`.`validatorClass` FROM `router_command`, `router_user`, `router_command_role`, `router_user_role`, `router_role` AS `tree`, `router_role` AS `node` WHERE" +
			/*-------------------------------------------------------------*/
			/* SELECT COMMAND                                              */
			/*-------------------------------------------------------------*/
			" `router_command`.`command` = '%s'" +
			/*-------------------------------------------------------------*/
			/* SELECT USER                                                 */
			/*-------------------------------------------------------------*/
			" AND `router_user`.`AMIUser` = '%s'" +
			" AND `router_user`.`AMIPass` = '%s'" +
			/*-------------------------------------------------------------*/
			/* SELECT COMMAND ROLE                                         */
			/*-------------------------------------------------------------*/
			" AND `router_command_role`.`commandFK` = `router_command`.`id`" +
			/*-------------------------------------------------------------*/
			/* SELECT USER ROLE                                            */
			/*-------------------------------------------------------------*/
			" AND `router_user_role`.`userFK` = `router_user`.`id`" +
			/*-------------------------------------------------------------*/
			/* SELECT ROLE                                                 */
			/*-------------------------------------------------------------*/
			" AND `router_command_role`.`roleFK` = `tree`.`id`" +
			" AND `router_user_role`.`roleFK` = `node`.`id`" +
			/*-------------------------------------------------------------*/
			" AND `node`.`lft` BETWEEN `tree`.`lft` AND `tree`.`rgt`" +
			/*-------------------------------------------------------------*/
			" ORDER BY `node`.`lft` DESC",
			/*-------------------------------------------------------------*/
			command.replace("'", "''"),
			AMIUser.replace("'", "''"),
			AMIPass.replace("'", "''")
		);

		List<Row> rowList = querier.executeQuery(sql).getAll();

		if(rowList.size() != 1)
		{
			throw new Exception("wrong role");
		}

		Row row = rowList.get(0);

		/*-----------------------------------------------------------------*/

		String validatorClass = row.getValue("validatorClass");

		/*-----------------------------------------------------------------*/
		/* CHECK ROLE                                                      */
		/*-----------------------------------------------------------------*/

		checkCommand(validatorClass, command, arguments);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void checkCommand(String validator, String command, Map<String, String> arguments) throws Exception
	{
		if(validator == null || validator.isEmpty())
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<CommandValidatorInterface> clazz = s_roleValidators.get(validator);

		if(clazz == null)
		{
			throw new Exception("could not find command validator `" + validator + "`");
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                               */
		/*-----------------------------------------------------------------*/

		boolean isOk;

		try
		{
			isOk = (boolean) clazz.getMethod("check", clazz).invoke(null, validator, command);
		}
		catch(Exception e)
		{
			throw new Exception("could not execute command validator `" + validator + "`");
		}

		if(isOk == false)
		{
			throw new Exception("the operation is not authorized");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void checkNewUser(String validator, String amiLogin, String amiPassword, String clientDN, String issuerDN, String firstName, String lastName, String email) throws Exception
	{
		if(validator == null || validator.isEmpty())
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<NewUserValidatorInterface> clazz = s_userValidators.get(validator);

		if(clazz == null)
		{
			throw new Exception("could not find user validator `" + validator + "`");
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                               */
		/*-----------------------------------------------------------------*/

		boolean isOk;

		try
		{
			isOk = (boolean) clazz.getMethod("check", clazz).invoke(null, amiLogin, amiPassword, clientDN, issuerDN, firstName, lastName, email);
		}
		catch(Exception e)
		{
			throw new Exception("could not execute user validator `" + validator + "`");
		}

		if(isOk == false)
		{
			throw new Exception("the operation is not authorized");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
