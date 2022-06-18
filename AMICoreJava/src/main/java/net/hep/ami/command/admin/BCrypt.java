package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false)
public class BCrypt extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public BCrypt(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String string = arguments.get("string");

		if(string == null)
		{
			throw new Exception("invalid usage");
		}

		return new StringBuilder("<info><![CDATA[").append(SecuritySingleton.bcrypt(string)).append("]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "BCrypt a string.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-string=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
