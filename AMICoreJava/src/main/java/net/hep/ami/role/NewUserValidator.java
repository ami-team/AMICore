package net.hep.ami.role;

import net.hep.ami.utility.*;

abstract public class NewUserValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public boolean check(
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
	String help()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
