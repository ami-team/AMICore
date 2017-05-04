package net.hep.ami.role;

@FunctionalInterface
public interface NewUserValidator
{
	/*---------------------------------------------------------------------*/

	public boolean check(
		String amiLogin,
		String amiPassword,
		String clientDN,
		String issuerDN,
		String firstName,
		String lastName,
		String email

	)  throws Exception;

	/*---------------------------------------------------------------------*/
}
