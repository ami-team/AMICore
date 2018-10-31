package net.hep.ami;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.utility.*;

public class RoleSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Class<?>> s_roleValidators = new AMIMap<>();
	private static final Map<String, Class<?>> s_userValidators = new AMIMap<>();

	/*---------------------------------------------------------------------*/

	private RoleSingleton() {}

	/*---------------------------------------------------------------------*/

	private static Set<String> _getCommandRoles(Querier querier, String command) throws Exception
	{
		Set<String> result = new HashSet<>();

		PreparedStatement statement = querier.prepareStatement(
			"SELECT `router_role`.`role` FROM `router_command`, `router_command_role`, `router_role` WHERE" +
			/*-------------------------------------------------------------*/
			/* SELECT COMMAND                                              */
			/*-------------------------------------------------------------*/
			" `router_command`.`command` = ?" +
			/*-------------------------------------------------------------*/
			/* SELECT ROLE                                                 */
			/*-------------------------------------------------------------*/
			" AND `router_command_role`.`commandFK` = `router_command`.`id`" +
			" AND `router_command_role`.`roleFK` = `router_role`.`id`",
			/*-------------------------------------------------------------*/
			false,
			null
		);

		statement.setString(1, command);

		try
		{
			ResultSet resultSet = statement.executeQuery();

			while(resultSet.next())
			{
				result.add(resultSet.getString(1));
			}
		}
		finally
		{
			statement.close();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static Set<String> _getUserRoles(Querier querier, String amiUser, String amiPass) throws Exception
	{
		Set<String> result = new HashSet<>();

		PreparedStatement statement = querier.prepareStatement(
			"SELECT `router_role`.`role` FROM `router_user`, `router_user_role`, `router_role` WHERE" +
			/*-------------------------------------------------------------*/
			/* SELECT USER                                                 */
			/*-------------------------------------------------------------*/
			" `router_user`.`AMIUser` = ? AND `router_user`.`AMIPass` = ? AND `router_user`.`valid` != 0" +
			/*-------------------------------------------------------------*/
			/* SELECT ROLE                                                 */
			/*-------------------------------------------------------------*/
			" AND `router_user_role`.`userFK` = `router_user`.`id`" +
			" AND `router_user_role`.`roleFK` = `router_role`.`id`",
			/*-------------------------------------------------------------*/
			false,
			null
		);

		statement.setString(1, amiUser);
		statement.setString(2, amiPass);

		try
		{
			ResultSet resultSet = statement.executeQuery();

			while(resultSet.next())
			{
				result.add(resultSet.getString(1));
			}
		}
		finally
		{
			statement.close();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> checkRoles(Querier querier, String command, Map<String, String> arguments, boolean check) throws Exception
	{
		/*---------------------------------*/

		if("GetSessionInfo".equals(command)
		   ||
		   "ResetPassword".equals(command)
		   ||
		   "AddUser".equals(command)
		 ) {
			return new HashSet<>();
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

		Set<String> commandRoles = _getCommandRoles(
			querier,
			command
		);

		Set<String> userRoles = _getUserRoles(
			querier,
			amiUser,
			amiPass
		);

		/*-----------------------------------------------------------------*/
		/* CHECK ROLE                                                      */
		/*-----------------------------------------------------------------*/

		if(check)
		{
			boolean isGuestCommand = commandRoles.contains("AMI_GUEST");
			boolean isAdminUser    = userRoles   .contains("AMI_ADMIN");

			if(isGuestCommand == false
			   &&
			   isAdminUser == false
			   &&
			   Collections.disjoint(commandRoles, userRoles)
			 ) {
				throw new Exception("wrong role for command `" + command + "`");
			}
			else
			{
				checkCommand(/* TODO */ null /* TODO */, command, userRoles, arguments);
			}
		}

		/*-----------------------------------------------------------------*/

		return userRoles;
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

	public static void checkCommand(String validator, String command, Set <String> userRoles, Map<String, String> arguments) throws Exception
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
			isOk = (boolean) clazz.getMethod("check", clazz).invoke(null, command, userRoles, arguments);
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
