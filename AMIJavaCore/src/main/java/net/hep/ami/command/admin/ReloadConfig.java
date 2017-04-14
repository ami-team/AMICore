package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class ReloadConfig extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ReloadConfig(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		LogSingleton.reload();
		ConfigSingleton.reload();
		ConverterSingleton.reload();

		RoleSingleton.reload();
		CommandSingleton.reload();

		DriverSingleton.reload();
		CatalogSingleton.reload();

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Reload the configuration.";
	}

	/*---------------------------------------------------------------------*/
}
