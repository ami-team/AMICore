package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;

public class GetElementInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetElementInfo(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String primaryFieldName = arguments.get("primaryFieldName");
		String primaryFieldValue = arguments.get("primaryFieldValue");

		if(catalog == null || entity == null || primaryFieldName == null || primaryFieldValue == null)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		RowSet rowset = querier.executeMQLQuery(entity, "SELECT `*` WHERE `" + primaryFieldName.replace("`", "``") + "` = '" + primaryFieldValue.replace("'", "''") + "'");

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/


		/*-----------------------------------------------------------------*/

		return new StringBuilder().append(rowset.toStringBuilder("element"))
		;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the element's information.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" -primaryFieldName=\"\" -primaryFieldValue=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
