package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class ListCatalogs extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListCatalogs(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		return CatalogSingleton.listCatalogs();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "List catalogs.";
	}

	/*---------------------------------------------------------------------*/
}
