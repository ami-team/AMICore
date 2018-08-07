package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.utility.*;

/* Nested Set Model */

public class RoleSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Class<?>> s_roleValidators = new AMIMap<>();
	private static final Map<String, Class<?>> s_userValidators = new AMIMap<>();

	/*---------------------------------------------------------------------*/

	private RoleSingleton() {}

	/*---------------------------------------------------------------------*/

	public static void checkRoles(Querier querier, String command, Map<String, String> arguments) throws Exception
	{
		/*---------------------------------*/

		if("GetSessionInfo".equals(command)
		   ||
		   "ResetPassword".equals(command)
		   ||
		   "AddUser".equals(command)
		 ) {
			return;
		}

		/*---------------------------------*/

		String amiUser = arguments.get("AMIUser");
		String amiPass = arguments.get("AMIPass");

		if(amiUser == null
		   ||
		   amiPass == null
		 ) {
			throw new Exception("not authenticated");
		}

		amiPass = SecuritySingleton.encrypt(amiPass);

		/*-----------------------------------------------------------------*/
		/* GET ROLE                                                        */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = querier.executeSQLQuery(
			"SELECT `router_role`.`validatorClass` FROM `router_command`, `router_user`, `router_command_role`, `router_user_role`, `router_role` WHERE" +
			/*-------------------------------------------------------------*/
			/* SELECT COMMAND                                              */
			/*-------------------------------------------------------------*/
			" `router_command`.`command` = ?" +
			/*-------------------------------------------------------------*/
			/* SELECT USER                                                 */
			/*-------------------------------------------------------------*/
			" AND `router_user`.`AMIUser` = ?" +
			" AND `router_user`.`AMIPass` = ?" +
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
			" AND `router_command_role`.`roleFK` = `router_role`.`id`" +
			" AND `router_user_role`.`roleFK` = `router_role`.`id`",
			/*-------------------------------------------------------------*/
			command,
			amiUser,
			amiPass
		).getAll();

		if(rowList.isEmpty())
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

	private static Class<?> getRoleValidator(String className) throws Exception
	{
		Class<?> result = s_roleValidators.get(className);

		if(result == null)
		{
			result = ClassSingleton.forName(className);

			if(ClassSingleton.extendsClass(result, CommandValidator.class))
			{
				throw new Exception("class '" + className + "' doesn't extend 'CommandValidator'");
			}

			s_roleValidators.put(className, result);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static Class<?> getUserValidator(String className) throws Exception
	{
		Class<?> result = s_userValidators.get(className);

		if(result == null)
		{
			result = ClassSingleton.forName(className);

			if(ClassSingleton.extendsClass(result, NewUserValidator.class))
			{
				throw new Exception("class '" + className + "' doesn't extend 'NewUserValidator'");
			}

			s_userValidators.put(className, result);
		}

		return result;
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

		Class<?> clazz = getRoleValidator(validator);

		/*-----------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                               */
		/*-----------------------------------------------------------------*/

		boolean isOk;

		try
		{
			isOk = (boolean) clazz.getMethod("check", clazz).invoke(null, command, arguments);
		}
		catch(Exception e)
		{
			throw new Exception("could not execute command validator `" + validator + "`", e);
		}

		if(isOk == false)
		{
			throw new Exception("operation not authorized");
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

		Class<?> clazz = getUserValidator(validator);

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
			throw new Exception("could not execute user validator `" + validator + "`", e);
		}

		if(isOk == false)
		{
			throw new Exception("operation not authorized");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
