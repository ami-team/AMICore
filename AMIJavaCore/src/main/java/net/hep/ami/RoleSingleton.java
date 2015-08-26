package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.utility.*;

public class RoleSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Class<CommandValidatorInterface>> m_roleValisators = new HashMap<String, Class<CommandValidatorInterface>>();
	private static final Map<String, Class<NewUserValidatorInterface>> m_userValisators = new HashMap<String, Class<NewUserValidatorInterface>>();

	/*---------------------------------------------------------------------*/

	static
	{
		Set<String> classeNames = ClassFinder.findClassNames("net.hep.ami.role");

		for(String className: classeNames)
		{
			try
			{
				addValidator(className);
			}
			catch(Exception e)
			{
				LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
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
		/* ADD COMMANDE VALIDATOR                                          */
		/*-----------------------------------------------------------------*/

		/**/ if(ClassFinder.extendsClass(clazz, CommandValidatorInterface.class))
		{
			m_roleValisators.put(clazz.getName(), (Class<CommandValidatorInterface>) clazz);
		}

		/*-----------------------------------------------------------------*/
		/* ADD NEW USER VALIDATOR                                          */
		/*-----------------------------------------------------------------*/

		else if(ClassFinder.extendsClass(clazz, NewUserValidatorInterface.class))
		{
			m_userValisators.put(clazz.getName(), (Class<NewUserValidatorInterface>) clazz);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void checkRoles(String command, Map<String, String> arguments) throws Exception
	{
		/*---------------------------------*/
		/* BYPASS                          */
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

		AMIPass = Cryptography.encrypt(AMIPass);

		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier(
			ConfigSingleton.getProperty("jdbc_url"),
			ConfigSingleton.getProperty("router_user"),
			ConfigSingleton.getProperty("router_pass")
		);

		QueryResult queryResult;

		try
		{
			queryResult = basicQuerier.executeSQLQuery(
				"SELECT `node`.`validatorClass` FROM `router_command`,`router_user`,`router_command_role`,`router_user_role`,`router_role` AS `tree`,`router_role` AS `node` WHERE" +
				/*---------------------------------------------------------*/
				/* SELECT COMMAND                                          */
				/*---------------------------------------------------------*/
				"	"+" `router_command`.`command`='" + command + "'" +
				/*---------------------------------------------------------*/
				/* SELECT USER                                             */
				/*---------------------------------------------------------*/
				"	AND `router_user`.`AMIUser`='" + AMIUser + "'" +
				"	AND `router_user`.`AMIPass`='" + AMIPass + "'" +
				/*---------------------------------------------------------*/
				/* SELECT COMMAND ROLE                                     */
				/*---------------------------------------------------------*/
				"	AND `router_command_role`.`commandFK`=`router_command`.`id`" +
				/*---------------------------------------------------------*/
				/* SELECT USER ROLE                                        */
				/*---------------------------------------------------------*/
				"	AND `router_user_role`.`userFK`=`router_user`.`id`" +
				/*---------------------------------------------------------*/
				/* SELECT ROLE                                             */
				/*---------------------------------------------------------*/
				"	AND `router_command_role`.`roleFK`=`tree`.`id`" +
				"	AND `router_user_role`.`roleFK`=`node`.`id`" +
				/*---------------------------------------------------------*/
				"	AND `node`.`lft` BETWEEN `tree`.`lft` AND `tree`.`rgt`" +
				/*---------------------------------------------------------*/
				"	ORDER BY `node`.`lft` DESC"
				/*---------------------------------------------------------*/
			);
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* GET NUMBER OF PROPERTIES                                        */
		/*-----------------------------------------------------------------*/

		final int numberOfRows = queryResult.getNumberOfRows();

		if(numberOfRows == 0)
		{
			throw new Exception("wrong role");
		}

		/*-----------------------------------------------------------------*/
		/* CHECK VALIDATOR                                                 */
		/*-----------------------------------------------------------------*/

		String validatorClass = queryResult.getValue(0, "validatorClass");

		checkCommand(validatorClass, command, arguments);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void checkCommand(String validator, String command, Map<String, String> arguments) throws Exception
	{
		if(validator.isEmpty())
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<CommandValidatorInterface> clazz = m_roleValisators.get(validator);

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
			isOk = (Boolean) clazz.getMethod("check", clazz).invoke(null, validator, command);
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
		if(validator.isEmpty())
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<NewUserValidatorInterface> clazz = m_userValisators.get(validator);

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
			isOk = (Boolean) clazz.getMethod("check", clazz).invoke(null, amiLogin, amiPassword, clientDN, issuerDN, firstName, lastName, email);
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
