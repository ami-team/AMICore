package net.hep.ami.role;

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

	public static class Bean
	{
		@NotNull public String amiLogin;
		@NotNull public String amiPasswordOld;
		@NotNull public String amiPasswordNew;
		@Nullable public String clientDN;
		@Nullable public String issuerDN;
		@Nullable public String firstName;
		@Nullable public String lastName;
		@Nullable public String email;
		@Nullable public String ssoUser;
		@Nullable public String json;

		public Bean(@NotNull String _amiLogin, @NotNull String _amiPasswordOld, @NotNull String _amiPasswordNew, @Nullable String _clientDN, @Nullable String _issuerDN, @Nullable String _firstName, @Nullable String _lastName, @Nullable String _email, @Nullable String _ssoUser, @Nullable String _json)
		{
			amiLogin = _amiLogin;
			amiPasswordOld = _amiPasswordOld;
			amiPasswordNew = _amiPasswordNew;
			clientDN = _clientDN;
			issuerDN = _issuerDN;
			firstName = _firstName;
			lastName = _lastName;
			email = _email;
			ssoUser = _ssoUser;
			json = _json;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	abstract public boolean check(
		@NotNull Mode mode,
		@NotNull Bean bean

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
