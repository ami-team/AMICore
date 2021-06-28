package net.hep.ami.command.catalog;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_WRITER", visible = true, secured = false)
public class UpdateElements extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public UpdateElements(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		String[] fields;
		String[] values;

		if(arguments.containsKey("single"))
		{
			fields = new String[] {arguments.get("fields")};

			values = new String[] {arguments.get("values")};
		}
		else
		{
			fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
			                                         : new String[] {}
			;

			values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
			                                         : new String[] {}
			;
		}

		String[] keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
		                                                        : new String[] {}
		;

		String[] keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
		                                                        : new String[] {}
		;

		String where = arguments.get("where");

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length || keyFields.length != keyValues.length || (keyFields.length == 0 && where == null))
		{
			throw new Exception("invalid usage");
		}

		/*----------------------------------------------------------------------------------------------------------------*/

		XQLUpdate query;

		try
		{
			query = new XQLUpdate(XQLUpdate.Mode.MQL).addUpdatePart(new QId(catalog, entity, null).toString(QId.MASK_CATALOG_ENTITY))
			                                         .addFieldValuePart(
															Arrays.stream(fields).map(QId::parseQId_RuntimeException).collect(Collectors.toList()),
															IntStream.range(0, values.length).mapToObj(i -> "?" + i).collect(Collectors.toList())
			                                          )
			;
		}
		catch(RuntimeException e)
		{
			throw new Exception(e);
		}

		List<String> whereList = new ArrayList<>();

		for(int i = 0; i < keyFields.length; i++)
		{
			whereList.add(QId.parseQId(keyFields[i], QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD) + " = '" + keyValues[i].trim().replace("'", "''") + "'");
		}

		query.addWherePart(whereList)
		     .addWherePart(where)
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return getQuerier(catalog).executeMQLUpdate(entity, query.toString(), (Object) values).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Update one or more elements.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? (-single)? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
