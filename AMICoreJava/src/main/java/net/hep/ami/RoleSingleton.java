package net.hep.ami;

import java.sql.*;
import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class RoleSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final class CommandValidatorTuple extends Tuple3<String, String, Constructor<CommandValidator>>
	{
		private static final long serialVersionUID = -5126479111138460006L;

		private CommandValidatorTuple(@NotNull String _x, @NotNull String _y, @NotNull Constructor<CommandValidator> _z)
		{
			super(_x, _y, _z);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final class UserValidatorTuple extends Tuple3<String, String, Constructor<UserValidator>>
	{
		private static final long serialVersionUID = 6410545925675367511L;

		private UserValidatorTuple(@NotNull String _x, @NotNull String _y, @NotNull Constructor<UserValidator> _z)
		{
			super(_x, _y, _z);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, CommandValidatorTuple> s_commandRoleValidators = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);
	private static final Map<String, UserValidatorTuple> s_userRoleValidators = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Set<String> s_reservedParams = new HashSet<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private RoleSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		s_reservedParams.add("GetSessionInfo");
		s_reservedParams.add("ResetPassword");
		s_reservedParams.add("AddUser");

		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		s_commandRoleValidators.clear();
		s_userRoleValidators.clear();

		addRoleValidators();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addRoleValidators()
	{
		for(String className: ClassSingleton.findClassNames("net.hep.ami.role"))
		{
			try
			{
				addRoleValidator(className);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(LogSingleton.FATAL, "for role validator `{}`", className, e);
			}
		}
	}

	/*---------------------------------------------------------------------*/

	@SuppressWarnings("unchecked")
	private static void addRoleValidator(@NotNull String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET ROLE VALIDATOR                                              */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(className);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00)
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* ADD ROLE VALIDATOR                                              */
		/*-----------------------------------------------------------------*/

		/**/ if(ClassSingleton.extendsClass(clazz, CommandValidator.class))
		{
			s_commandRoleValidators.put(
				className,
				new CommandValidatorTuple(
					className,
					clazz.getMethod("help").invoke(null).toString(),
					(Constructor<CommandValidator>) clazz.getConstructor()
				)
			);
		}
		else if(ClassSingleton.extendsClass(clazz, UserValidator.class))
		{
			s_userRoleValidators.put(
				className,
				new UserValidatorTuple(
					className,
					clazz.getMethod("help").invoke(null).toString(),
					(Constructor<UserValidator>) clazz.getConstructor()
				)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static Set<String> _getCommandRoles(@NotNull Querier querier, @NotNull String command) throws Exception
	{
		Set<String> result = new HashSet<>();

		try(PreparedStatement statement = querier.sqlPreparedStatement("router_command",
			/*--------------------------------------------------------------------------------------------------------*/
			"SELECT `router_role`.`role` FROM `router_command`, `router_command_role`, `router_role` WHERE" +
			/*--------------------------------------------------------------------------------------------------------*/
			/* SELECT COMMAND                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/
			" `router_command`.`command` = ?0" +
			/*--------------------------------------------------------------------------------------------------------*/
			/* SELECT ROLE                                                                                            */
			/*--------------------------------------------------------------------------------------------------------*/
			" AND `router_command_role`.`commandFK` = `router_command`.`id`" +
			" AND `router_command_role`.`roleFK`    =  `router_role`  .`id`",
			/*--------------------------------------------------------------------------------------------------------*/
			false,
			null,
			true,
			command
		 )) {
			/*--------------------------------------------------------------------------------------------------------*/

			ResultSet resultSet = statement.executeQuery();

			while(resultSet.next())
			{
				result.add(resultSet.getString(1));
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static Set<String> _getUserRoles(@NotNull Querier querier, @NotNull String amiUser, @NotNull String amiPass) throws Exception
	{
		Set<String> result = new HashSet<>();

		try(PreparedStatement statement = querier.sqlPreparedStatement("router_user",
			/*--------------------------------------------------------------------------------------------------------*/
			"SELECT `router_role`.`role` FROM `router_user`, `router_user_role`, `router_role` WHERE" +
			/*--------------------------------------------------------------------------------------------------------*/
			/* SELECT USER                                                                                            */
			/*--------------------------------------------------------------------------------------------------------*/
			" `router_user`.`AMIUser` = ?0 AND `router_user`.`AMIPass` = ?#1 AND `router_user`.`valid` != 0" +
			/*--------------------------------------------------------------------------------------------------------*/
			/* SELECT ROLE                                                                                            */
			/*--------------------------------------------------------------------------------------------------------*/
			" AND `router_user_role`.`userFK` = `router_user`.`id`" +
			" AND `router_user_role`.`roleFK` = `router_role`.`id`",
			/*--------------------------------------------------------------------------------------------------------*/
			false,
			null,
			true,
			amiUser,
			amiPass
		 )) {
			/*--------------------------------------------------------------------------------------------------------*/

			ResultSet resultSet = statement.executeQuery();

			while(resultSet.next())
			{
				result.add(resultSet.getString(1));
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static String _getPassFromUser(@NotNull Querier querier, @NotNull String amiUser) throws Exception
	{
		String result;

		try(PreparedStatement statement = querier.sqlPreparedStatement("router_user",
			/*--------------------------------------------------------------------------------------------------------*/
			"SELECT `AMIPass` FROM `router_user` WHERE `router_user`.`AMIUser` = ?0 AND `router_user`.`valid` != 0",
			/*--------------------------------------------------------------------------------------------------------*/
			false,
			null,
			true,
			amiUser
		 )) {
			/*--------------------------------------------------------------------------------------------------------*/

			ResultSet resultSet = statement.executeQuery();

			if(resultSet.next())
			{
				result = SecuritySingleton.decrypt(resultSet.getString(1));
			}
			else
			{
				throw new Exception("user `" + amiUser + "` is not registered in AMI");
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Set<String> checkRoles(@NotNull Querier querier, @NotNull String command, @NotNull Map<String, String> arguments, @Nullable String validatorClass, boolean check) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String changeUser = arguments.remove("changeUser");

		/*------------------------------------------------------------------------------------------------------------*/

		if(changeUser == null && s_reservedParams.contains(command))
		{
			Set<String> result = new HashSet<>();

			result.add("AMI_GUEST");

			return result;
		}

		/*------------------------------------------------------------------------------------------------------------*/

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
			else
			{
				amiUser = ConfigSingleton.getProperty("admin_user");
				amiPass = ConfigSingleton.getProperty("admin_pass");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET ROLE                                                                                                   */
		/*------------------------------------------------------------------------------------------------------------*/

		Set<String> commandRoles = _getCommandRoles(
			querier,
			command
		);

		Set<String> userRoles = _getUserRoles(
			querier,
			amiUser,
			amiPass
		);

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK ROLE                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		if(check)
		{
			boolean isAdminUser    = userRoles   .contains("AMI_ADMIN");
			boolean isGuestCommand = commandRoles.contains("AMI_GUEST");

			if(isAdminUser
			   ||
			   isGuestCommand
			   ||
			   !Collections.disjoint(commandRoles, userRoles)
			 ) {
				checkCommand(validatorClass, command, userRoles, arguments);
			}
			else
			{
				throw new Exception("wrong role for command `" + command + "`, expected role(s) " + commandRoles + ", found role(s) " + userRoles + ", for user `" + amiUser + "`");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return userRoles;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void checkCommand(@Nullable String validatorClass, @NotNull String command, @NotNull Set <String> userRoles, @NotNull Map<String, String> arguments) throws Exception
	{
		if(Empty.is(validatorClass, Empty.STRING_AMI_NULL | Empty.STRING_BLANK))
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET VALIDATOR                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		CommandValidatorTuple tuple = s_commandRoleValidators.get(validatorClass.trim());

		if(tuple == null)
		{
			throw new Exception("could not find command role validator `" + validatorClass + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		CommandValidator validator;

		try
		{
			validator = tuple.z.newInstance();
		}
		catch(Exception e)
		{
			throw new Exception("could not instanciate command role validator `" + validatorClass + "`", e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		validator.check(command, userRoles, arguments);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void checkUser(@Nullable String validatorClass, @NotNull String amiLogin, @NotNull String amiPassword, @Nullable String clientDN, @Nullable String issuerDN, @NotNull String firstName, @NotNull String lastName, @NotNull String email) throws Exception
	{
		if(Empty.is(validatorClass, Empty.STRING_AMI_NULL | Empty.STRING_BLANK))
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET VALIDATOR                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		UserValidatorTuple tuple = s_userRoleValidators.get(validatorClass.trim());

		if(tuple == null)
		{
			throw new Exception("could not find user role validator `" + validatorClass + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator validator;

		try
		{
			validator = tuple.z.newInstance();
		}
		catch(Exception e)
		{
			throw new Exception("could not instanciate user role validator `" + validatorClass + "`", e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		validator.check(amiLogin, amiPassword, clientDN, issuerDN, firstName, lastName, email);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listRoleValidator()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"commandRoleValidator\">");

		for(CommandValidatorTuple tuple: s_commandRoleValidators.values())
		{
			result.append("<row>")
			     .append("<field name=\"class\"><![CDATA[").append(tuple.x).append("]]></field>")
			     .append("<field name=\"help\"><![CDATA[").append(tuple.y).append("]]></field>")
			     .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"UserRoleValidator\">");

		for(UserValidatorTuple tuple: s_userRoleValidators.values())
		{
			result.append("<row>")
					.append("<field name=\"class\"><![CDATA[").append(tuple.x).append("]]></field>")
					.append("<field name=\"help\"><![CDATA[").append(tuple.y).append("]]></field>")
					.append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
