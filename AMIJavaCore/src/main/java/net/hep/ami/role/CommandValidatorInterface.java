package net.hep.ami.role;

import java.util.*;

public interface CommandValidatorInterface
{
	/*---------------------------------------------------------------------*/

	public boolean check(
		String command,
		Map<String, String> arguments

	)  throws Exception;

	/*---------------------------------------------------------------------*/
}
