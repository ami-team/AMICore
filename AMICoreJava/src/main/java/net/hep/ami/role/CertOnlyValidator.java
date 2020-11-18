package net.hep.ami.role;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class CertOnlyValidator
{
	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public boolean check(
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
