package net.hep.ami.command.admin;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = true)
public class UpdateConfig extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateConfig(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String separator = arguments.containsKey("separator") ? Pattern.quote(arguments.get("separator"))
		                                                      : ","
		;

		String[] _names = arguments.containsKey("names") ? arguments.get("names").split(separator, -1)
		                                                 : new String[] {}
		;

		String[] _values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                   : new String[] {}
		;

		if(_names.length != _values.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*-----------------------------------------------------------------*/

		String name;
		String value;

		for(int i = 0; i < _names.length; i++)
		{
			name = _names[i].trim();
			value = _values[i].trim();

			if(name.isEmpty() == false)
			{
				if("::null::".equals(value))
				{
					ConfigSingleton.removeProperty(name);

					ConfigSingleton.removePropertyInDataBase(querier, name);
				}
				else
				{
					ConfigSingleton.setProperty(name, value);

					ConfigSingleton.setPropertyInDataBase(querier, name, value, m_AMIUser);
				}
			}
		}

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Update the global configuration.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-separator=\",\")? -names=\"\" -values=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
