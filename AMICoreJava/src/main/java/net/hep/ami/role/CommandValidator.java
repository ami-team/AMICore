package net.hep.ami.role;

import java.util.*;

@FunctionalInterface
public interface CommandValidator
{
	/*---------------------------------------------------------------------*/

	public boolean check(
		String command,
		Map<String, String> arguments

	)  throws Exception;

	/*---------------------------------------------------------------------*/
}
