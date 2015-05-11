package net.hep.ami.role;

public interface UserRoleValidatorInterface {
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
