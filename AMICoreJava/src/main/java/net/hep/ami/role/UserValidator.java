package net.hep.ami.role;

import org.jetbrains.annotations.*;

abstract public class UserValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public enum Mode {
		ADD,
		ATTACH,
		DETACH,
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public void check(
		@NotNull Mode mode,
		@NotNull String amiLogin,
		@NotNull String amiPassword,
		@Nullable String clientDN,
		@Nullable String issuerDN,
		@NotNull String firstName,
		@NotNull String lastName,
		@NotNull String email

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
