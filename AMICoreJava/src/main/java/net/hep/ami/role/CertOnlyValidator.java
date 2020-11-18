package net.hep.ami.role;

import org.jetbrains.annotations.*;

abstract public class CertOnlyValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public void check(
		@NotNull String amiLogin,
		@NotNull String amiPassword,
		@Nullable String clientDN,
		@Nullable String issuerDN

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
