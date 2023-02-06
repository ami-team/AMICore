package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.role.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true)
public class ChangePassword extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public ChangePassword(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String amiLogin = arguments.get("amiLogin");
		String amiPasswordOld = arguments.get("amiPasswordOld");
		String amiPasswordNew = arguments.get("amiPasswordNew");

		if(Empty.is(amiLogin, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(amiPasswordOld, Empty.STRING_NULL_EMPTY_BLANK)
		   ||
		   Empty.is(amiPasswordNew, Empty.STRING_NULL_EMPTY_BLANK)
		 ) {
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET QUERIER                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getAdminQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/
		/* CHECK CREDENTIALS                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		String sql = "SELECT `AMIUser`, `AMIPass` FROM `router_user` WHERE `AMIUser` = ?0";

		List<Row> rowList = querier.executeSQLQuery("router_user", sql, amiLogin).getAll();

		if(rowList.size() != 1)
		{
			throw new Exception("user `" + amiLogin + "` not registered in AMI");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Row row = rowList.get(0);

		String currentUsername = row.getValue(0);
		String currentPassword = row.getValue(1);

		SecuritySingleton.checkPassword(currentUsername, amiPasswordOld, currentPassword);

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE BEAN                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		UserValidator.Bean bean = new UserValidator.Bean(
			currentUsername,
			null,
			amiPasswordOld,
			amiPasswordNew,
			m_clientDN,
			m_issuerDN,
			null,
			null,
			null,
			null
		);

		boolean valid = RoleSingleton.checkUser(
			ConfigSingleton.getProperty("user_password_validator_class"),
			UserValidator.Mode.PASSWORD,
			bean
		);

		/*------------------------------------------------------------------------------------------------------------*/
		/* UPDATE PASSWORD                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		Update update = querier.executeSQLUpdate("router_user", "UPDATE `router_user` SET `AMIPass` = ?^1, `valid` = ?2 WHERE `AMIUser` = ?0",
			bean.getAmiUsername(),
			bean.getPasswordNew(),
			valid ? 1 : 0
		);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder(
			update.getNbOfUpdatedRows() > 0 ? "<info><![CDATA[done with success]]></info>"
			                                : "<error><![CDATA[bad user and/or password]]></error>"
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Change user password.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-amiLogin=\"\" -amiPasswordOld=\"\" -amiPasswordNew=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
