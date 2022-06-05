package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class LocalizeIP extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public LocalizeIP(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String ip = arguments.get("ip");

		if(Empty.is(ip, Empty.STRING_NULL_EMPTY_BLANK))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		LocalizationSingleton.Localization localization = LocalizationSingleton.localizeIP(getQuerier("self"), ip);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset type=\"localization\">").append("<row>")
		                          .append("<field name=\"ContinentCode\"><![CDATA[").append(localization.getContinentCode()).append("]]></field>")
		                          .append("<field name=\"CountryCode\"><![CDATA[").append(localization.getCountryCode()).append("]]></field>")
		                          .append("</row>").append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Localize an IP address.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-ip=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
