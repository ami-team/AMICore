package net.hep.ami.role;

import java.util.*;

import org.jetbrains.annotations.*;

abstract public class UserValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public enum Mode {
		ADD,
		ATTACH,
		DETACH,
		PASSWORD,
		INFO,
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public boolean check(
		@NotNull Mode mode,
		@NotNull String amiLogin,
		@NotNull String amiPasswordOld,
		@NotNull String amiPasswordNew,
		@NotNull String clientDN,
		@NotNull String issuerDN,
		@NotNull String firstName,
		@NotNull String lastName,
		@NotNull String email,
		@Nullable Map<String, String> json

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
