package net.hep.ami.command.admin;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = true)
public class UpdateConfig extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public UpdateConfig(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
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

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier("self");

		/*------------------------------------------------------------------------------------------------------------*/

		String name;
		String value;

		List<String> updatedParamNames = new ArrayList<>();

		for(int i = 0; i < _names.length; i++)
		{
			name = _names[i].trim();
			value = _values[i].trim();

			if(!name.isEmpty())
			{
				if("@NULL".equalsIgnoreCase(value))
				{
					ConfigSingleton.removeProperty(name);

					if(ConfigSingleton.removePropertyInDataBase(querier, name) > 0)
					{
						updatedParamNames.add(name);
					}
				}
				else
				{
					ConfigSingleton.setProperty(name, value);

					if(ConfigSingleton.setPropertyInDataBase(querier, name, value, m_AMIUser) > 0)
					{
						updatedParamNames.add(name);
					}
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success, updated parameter(s): " + updatedParamNames + "]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Update the global configuration.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-separator=\",\")? -names=\"\" -values=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
