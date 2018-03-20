package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class LocalizeIP extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public LocalizeIP(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		String ip = arguments.get("ip");

		if(ip == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		LocalizationSingleton.Localization localization = LocalizationSingleton.localizeIP(getQuerier("self"), ip);

		/*-----------------------------------------------------------------*/

		result.append(
			"<rowset type=\"localization\">"
			+
			"<row>"
			+
			"<field name=\"ContinentCode\"><![CDATA[" + localization.continentCode + "]]></field>"
			+
			"<field name=\"CountryCode\"><![CDATA[" + localization.countryCode + "]]></field>"
			+
			"</row>"
			+
			"</rowset>"
		);

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Localize an IP address.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-ip=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
