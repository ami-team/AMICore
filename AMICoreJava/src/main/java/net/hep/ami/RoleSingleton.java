package net.hep.ami;

import lombok.*;

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

	@Getter
	@Setter
	@AllArgsConstructor
	private static final class CommandValidatorDescr
	{
		@NotNull private String name;
		@NotNull private String help;
		@NotNull private Constructor<CommandValidator> constructor;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	private static final class UserValidatorDescr
	{
		@NotNull private String name;
		@NotNull private String help;
		@NotNull private Constructor<UserValidator> constructor;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, CommandValidatorDescr> s_commandRoleValidators = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);
	private static final Map<String, UserValidatorDescr> s_userRoleValidators = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

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

	/*----------------------------------------------------------------------------------------------------------------*/

	@SuppressWarnings("unchecked")
	private static void addRoleValidator(@NotNull String className) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET ROLE VALIDATOR                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(className);

		if((clazz.getModifiers() & Modifier.ABSTRACT) != 0x00)
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD ROLE VALIDATOR                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(ClassSingleton.extendsClass(clazz, CommandValidator.class))
		{
			s_commandRoleValidators.put(
				className,
				new CommandValidatorDescr(
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
				new UserValidatorDescr(
					className,
					clazz.getMethod("help").invoke(null).toString(),
					(Constructor<UserValidator>) clazz.getConstructor()
				)
			);
		}

		/*------------------------------------------------------------------------------------------------------------*/
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
	private static Set<String> _getUserRoles(@NotNull Querier querier, @NotNull String amiUser) throws Exception
	{
		Set<String> result = new HashSet<>();

		try(PreparedStatement statement = querier.sqlPreparedStatement("router_user",
			/*--------------------------------------------------------------------------------------------------------*/
			"SELECT `router_role`.`role` FROM `router_user`, `router_user_role`, `router_role` WHERE" +
			/*--------------------------------------------------------------------------------------------------------*/
			/* SELECT USER                                                                                            */
			/*--------------------------------------------------------------------------------------------------------*/
			" `router_user`.`AMIUser` = ?0 AND `router_user`.`valid` != 0" +
			/*--------------------------------------------------------------------------------------------------------*/
			/* SELECT ROLE                                                                                            */
			/*--------------------------------------------------------------------------------------------------------*/
			" AND `router_user_role`.`userFK` = `router_user`.`id`" +
			" AND `router_user_role`.`roleFK` = `router_role`.`id`",
			/*--------------------------------------------------------------------------------------------------------*/
			false,
			null,
			true,
			amiUser
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

		if(amiUser == null)
		{
			if(check)
			{
				throw new Exception("user not authenticated");
			}
			else
			{
				amiUser = ConfigSingleton.getProperty("admin_user");
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
			amiUser
		);

		/*------------------------------------------------------------------------------------------------------------*/

		if(changeUser != null && userRoles.contains("AMI_SUDOER"))
		{
			arguments.put("AMIUser", amiUser);

			userRoles = _getUserRoles(
				querier,
				amiUser
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
		if(Empty.is(validatorClass, Empty.STRING_NULL_EMPTY_BLANK))
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET VALIDATOR                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		CommandValidatorDescr commandValidatorDescr = s_commandRoleValidators.get(validatorClass.trim());

		if(commandValidatorDescr == null)
		{
			throw new Exception("could not find command role validator `" + validatorClass + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		CommandValidator validator;

		try
		{
			validator = commandValidatorDescr.getConstructor().newInstance();
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

	public static boolean checkUser(@Nullable String validatorClass, @NotNull UserValidator.Mode mode, @NotNull UserValidator.Bean bean) throws Exception
	{
		if(Empty.is(validatorClass, Empty.STRING_NULL_EMPTY_BLANK))
		{
			return true;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET VALIDATOR                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		UserValidatorDescr userValidatorDescr = s_userRoleValidators.get(validatorClass.trim());

		if(userValidatorDescr == null)
		{
			throw new Exception("could not find user role validator `" + validatorClass + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator validator;

		try
		{
			validator = userValidatorDescr.getConstructor().newInstance();
		}
		catch(Exception e)
		{
			throw new Exception("could not instanciate user role validator `" + validatorClass + "`", e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return validator.check(mode, bean);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listRoleValidator()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"commandRoleValidator\">");

		for(CommandValidatorDescr tuple: s_commandRoleValidators.values())
		{
			result.append("<row>")
			     .append("<field name=\"class\"><![CDATA[").append(tuple.getName()).append("]]></field>")
			     .append("<field name=\"help\"><![CDATA[").append(tuple.getHelp()).append("]]></field>")
			     .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"UserRoleValidator\">");

		for(UserValidatorDescr tuple: s_userRoleValidators.values())
		{
			result.append("<row>")
					.append("<field name=\"class\"><![CDATA[").append(tuple.getName()).append("]]></field>")
					.append("<field name=\"help\"><![CDATA[").append(tuple.getHelp()).append("]]></field>")
					.append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
