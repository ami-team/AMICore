package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class ListCatalogs extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public ListCatalogs(Map<String, String> arguments, int transactionID)
	{
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception
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
