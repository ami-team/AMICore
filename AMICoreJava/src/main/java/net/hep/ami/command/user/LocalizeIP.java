package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

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
		StringBuilder result = new StringBuilder();

		String ip = arguments.get("ip");

		if(ip == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		LocalizationSingleton.Localization localization = LocalizationSingleton.localizeIP(getQuerier("self"), ip);

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"localization\">")
		      .append("<row>")
		      .append("<field name=\"ContinentCode\"><![CDATA[").append(localization.continentCode).append("]]></field>")
		      .append("<field name=\"CountryCode\"><![CDATA[").append(localization.countryCode).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Localize an IP address.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-ip=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
