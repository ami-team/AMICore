package net.hep.ami.role;

import java.util.*;

import org.jetbrains.annotations.*;

abstract public class CommandValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public void check(
		@NotNull String command,
		@NotNull Set<String> userRoles,
		@NotNull Map<String, String> arguments

	) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
