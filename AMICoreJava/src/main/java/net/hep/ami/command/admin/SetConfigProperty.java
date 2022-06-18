package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class SetConfigProperty extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SetConfigProperty(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String name = arguments.get("name");
		String value = arguments.get("value");

		if(name == null
		   ||
		   value == null
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		ConfigSingleton.setProperty(name, value);

		int nb = ConfigSingleton.setPropertyInDataBase(getQuerier("self"), name, value, m_AMIUser);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			nb > 0 ? "<info><![CDATA[done with success]]></info>"
			       : "<error><![CDATA[nothing done]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Set a global configuration property.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-name=\"\" -value=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
