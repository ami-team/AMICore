package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class ListCatalogs extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public ListCatalogs(Set<String> roles, Map<String, String> arguments, long transactionId)
	{
		super(roles, arguments, transactionId);
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
		return "List the available catalogs.";
	}

	/*---------------------------------------------------------------------*/
}
