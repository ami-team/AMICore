package net.hep.ami.role;

import java.util.*;

public interface RoleValidatorInterface {
	/*---------------------------------------------------------------------*/

	public boolean check(
		String command,
		Map<String, String> arguments
	);

	/*---------------------------------------------------------------------*/
}
