package net.hep.ami.role;

import net.hep.ami.utility.*;

@FunctionalInterface
public interface NewUserValidator
{
	/*---------------------------------------------------------------------*/

	public boolean check(
		String amiLogin,
		String amiPassword,
		@Nullable String clientDN,
		@Nullable String issuerDN,
		String firstName,
		String lastName,
		String email

	)  throws Exception;

	/*---------------------------------------------------------------------*/
}
