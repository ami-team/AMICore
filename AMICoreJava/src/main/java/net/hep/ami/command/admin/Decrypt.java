package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = true)
public class Decrypt extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public Decrypt(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		return new StringBuilder("<info><![CDATA[" + SecuritySingleton.decrypt(string) + "]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Decrypt a string.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-string=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
