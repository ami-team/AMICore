package net.hep.ami.role;

import lombok.*;

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

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Bean
	{
		@NotNull private String amiLogin;
		@NotNull private String amiPasswordOld;
		@NotNull private String amiPasswordNew;
		@Nullable private String clientDN;
		@Nullable private String issuerDN;
		@Nullable private String firstName;
		@Nullable private String lastName;
		@Nullable private String email;
		@Nullable private String ssoUser;
		@Nullable private String json;
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
