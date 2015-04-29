package net.hep.ami;

public interface UserValidatorInterface {
	/*---------------------------------------------------------------------*/

	public boolean check(
		String amiLogin,
		String amiPassword,
		String clientDN,
		String issuerDN,
		String firstName,
		String lastName,
		String email
	);

	/*---------------------------------------------------------------------*/
}
