package net.hep.ami.role;

import java.util.*;

import net.hep.ami.utility.*;

abstract public class CommandValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public boolean check(
		@NotNull String command,
		@NotNull Set<String> userRoles,
		@NotNull Map<String, String> arguments

	) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	String help()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
