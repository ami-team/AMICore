package net.hep.ami.command.catalog;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_WRITER", visible = true, secured = false)
public class RemoveElements extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RemoveElements(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? Pattern.quote(arguments.get("separator"))
		                                                      : ","
		;

		String[] keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
		                                                        : new String[] {}
		;

		String[] keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
		                                                        : new String[] {}
		;

		String where = arguments.get("where");

		if(catalog == null || entity == null || keyFields.length != keyValues.length || (keyFields.length == 0 && where == null))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		XQLDelete query = new XQLDelete(XQLDelete.Mode.MQL);

		List<String> whereList = new ArrayList<>();

		for(int i = 0; i < keyFields.length; i++)
		{
			whereList.add(QId.parseQId(keyFields[i], QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD) + " = '" + keyValues[i].trim().replace("'", "''") + "'");
		}

		query.addWherePart(whereList)
		     .addWherePart(where)
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return getQuerier(catalog).executeMQLUpdate(entity, query.toString()).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Remove one or more elements.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? -keyFields=\"\" -keyValues=\"\" (-where=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
