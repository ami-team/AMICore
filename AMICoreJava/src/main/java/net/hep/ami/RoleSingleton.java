package net.hep.ami;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.utility.*;

public class RoleSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Class<?>> s_roleValidators = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);
	private static final Map<String, Class<?>> s_userValidators = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

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

		statement.setString(1, /*---------------------*/(amiUser));
		statement.setString(2, SecuritySingleton.encrypt(amiPass));

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

	private static String _getPassFromUser(Querier querier, String amiUser) throws Exception
	{
		String result;

		PreparedStatement statement = querier.prepareStatement(
			"SELECT `AMIPass` FROM `router_user` WHERE `router_user`.`AMIUser` = ?",
			false,
			null
		);

		statement.setString(1, amiUser);

		try
		{
			ResultSet resultSet = statement.executeQuery();

			if(resultSet.next())
			{
				result = SecuritySingleton.decrypt(resultSet.getString(1));
			}
			else
			{
				throw new Exception("user `" + amiUser + "` is not registered in AMI");
			}
		}
		finally
		{
			statement.close();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> checkRoles(Querier querier, String command, Map<String, String> arguments, String validatorClass, boolean check) throws Exception
	{
		/*---------------------------------*/

		String changeUser = arguments.remove("changeUser");

		if(changeUser == null && (
				"GetSessionInfo".equals(command)
				||
				"ResetPassword".equals(command)
				||
				"AddUser".equals(command)
		   )
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
			if(check)
			{
				throw new Exception("user not authenticated");
			}

			amiUser = ConfigSingleton.getProperty("admin_user");
			amiPass = ConfigSingleton.getProperty("admin_pass");
		}

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

		if(changeUser != null && userRoles.contains("AMI_SUDOER"))
		{
			amiPass = _getPassFromUser(querier, amiUser = changeUser);

			arguments.put("AMIUser", amiUser);
			arguments.put("AMIPass", amiPass);

			userRoles = _getUserRoles(
				querier,
				amiUser,
				amiPass
			);
		}

		/*-----------------------------------------------------------------*/
		/* CHECK ROLE                                                      */
		/*-----------------------------------------------------------------*/

		if(check)
		{
			boolean isGuestCommand = commandRoles.contains("AMI_GUEST");
			boolean isAdminUser    = userRoles   .contains("AMI_ADMIN");

			if(isGuestCommand
			   ||
			   isAdminUser
			   ||
			   Collections.disjoint(commandRoles, userRoles) == false
			 ) {
				checkCommand(validatorClass, command, userRoles, arguments);
			}
			else
			{
				throw new Exception("wrong role for command `" + command + "`, expected role(s) " + commandRoles + ", found role(s) " + userRoles + ", for user `" + amiUser + "`");
			}
		}

		/*-----------------------------------------------------------------*/

		return userRoles;
	}

	/*---------------------------------------------------------------------*/

	public static void checkCommand(String validatorClass, String command, Set <String> userRoles, Map<String, String> arguments) throws Exception
	{
		if(validatorClass == null || validatorClass.isEmpty())
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = s_roleValidators.get(validatorClass);

		if(clazz == null)
		{
			clazz = ClassSingleton.forName(validatorClass);

			if(ClassSingleton.extendsClass(clazz, CommandValidator.class))
			{
				throw new Exception("class `" + validatorClass + "` doesn't extend `CommandValidator`");
			}

			s_roleValidators.put(validatorClass, clazz);
		}

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
			throw new Exception("could not execute command validator `" + validatorClass + "`", e);
		}

		if(isOk == false)
		{
			throw new Exception("operation not authorized");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void checkNewUser(String validatorClass, String amiLogin, String amiPassword, @Nullable String clientDN, @Nullable String issuerDN, String firstName, String lastName, String email) throws Exception
	{
		if(validatorClass == null || validatorClass.isEmpty())
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = s_userValidators.get(validatorClass);

		if(clazz == null)
		{
			clazz = ClassSingleton.forName(validatorClass);

			if(ClassSingleton.extendsClass(clazz, NewUserValidator.class))
			{
				throw new Exception("class `" + validatorClass + "` doesn't extend `NewUserValidator`");
			}

			s_userValidators.put(validatorClass, clazz);
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
			throw new Exception("could not execute user validator `" + validatorClass + "`", e);
		}

		if(isOk == false)
		{
			throw new Exception("operation not authorized");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
