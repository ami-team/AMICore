package net.hep.ami;

import java.util.*;

import net.hep.ami.role.*;
import net.hep.ami.utility.*;

public class RoleSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Class<RoleValidatorInterface>> m_roleValisators = new HashMap<String, Class<RoleValidatorInterface>>();
	private static final Map<String, Class<UserValidatorInterface>> m_userValisators = new HashMap<String, Class<UserValidatorInterface>>();

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
		/* ADD ROLE VALIDATOR                                              */
		/*-----------------------------------------------------------------*/

		/**/ if(ClassFinder.extendsClass(clazz, RoleValidatorInterface.class))
		{
			m_roleValisators.put(clazz.getName(), (Class<RoleValidatorInterface>) clazz);
		}

		/*-----------------------------------------------------------------*/
		/* ADD ROLE VALIDATOR                                              */
		/*-----------------------------------------------------------------*/

		else if(ClassFinder.extendsClass(clazz, UserValidatorInterface.class))
		{
			m_userValisators.put(clazz.getName(), (Class<UserValidatorInterface>) clazz);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static boolean checkRole(String validator, String command, Map<String, String> arguments) throws Exception
	{
		if(validator.isEmpty())
		{
			return true;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<RoleValidatorInterface> clazz = m_roleValisators.get(validator);

		if(clazz == null)
		{
			throw new Exception("could not find role validator `" + validator + "`");
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                               */
		/*-----------------------------------------------------------------*/

		try
		{
			return (Boolean) clazz.getMethod("check", clazz).invoke(null, validator, command);
		}
		catch(Exception e)
		{
			throw new Exception("could not call user validator `" + validator + "`");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static boolean checkUser(String validator, String amiLogin, String amiPassword, String clientDN, String issuerDN, String firstName, String lastName, String email) throws Exception
	{
		if(validator.isEmpty())
		{
			return true;
		}

		/*-----------------------------------------------------------------*/
		/* GET VALIDATOR                                                   */
		/*-----------------------------------------------------------------*/

		Class<UserValidatorInterface> clazz = m_userValisators.get(validator);

		if(clazz == null)
		{
			throw new Exception("could not find user validator `" + validator + "`");
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE VALIDATOR                                               */
		/*-----------------------------------------------------------------*/

		try
		{
			return (Boolean) clazz.getMethod("check", clazz).invoke(null, amiLogin, amiPassword, clientDN, issuerDN, firstName, lastName, email);
		}
		catch(Exception e)
		{
			throw new Exception("could not call user validator `" + validator + "`");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
