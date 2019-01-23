package net.hep.ami.command.catalog;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.utility.parser.*;

@CommandMetadata(role = "AMI_ADMIN", visible = true, secured = false)
public class UpdateElements extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateElements(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String separator = arguments.containsKey("separator") ? Pattern.quote(arguments.get("separator"))
		                                                      : ","
		;

		String[] fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                  : new String[] {}
		;

		String[] values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                  : new String[] {}
		;

		String[] keyFields = arguments.containsKey("keyFields") ? arguments.get("keyFields").split(separator, -1)
		                                                        : new String[] {}
		;

		String[] keyValues = arguments.containsKey("keyValues") ? arguments.get("keyValues").split(separator, -1)
		                                                        : new String[] {}
		;

		String where = arguments.containsKey("where") ? arguments.get("where").trim()
		                                              : ""
		;

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		boolean isAdmin = m_userRoles.contains("AMI_ADMIN");

		/*-----------------------------------------------------------------*/

		UpdateObj query;

		try
		{
			query = new UpdateObj().addUpdatePart(new QId(catalog, entity, null).toString(QId.MASK_CATALOG_ENTITY))
			                       .addFieldValuePart(
										Arrays.stream(fields).map(QId::parseQId_RuntimeException).collect(Collectors.toList()),
										Arrays.stream(values).map( x -> Utility.textToSqlVal(x) ).collect(Collectors.toList())
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
			whereList.add(QId.parseQId(keyFields[i], QId.Type.FIELD).toString(QId.MASK_CATALOG_ENTITY_FIELD) + " = '" + keyValues[i].trim().replace("'", "''") + "'");
		}

		query.addWherePart(whereList);

		/*-----------------------------------------------------------------*/

		return getQuerier(catalog).executeMQLUpdate(entity, m_AMIUser, isAdmin, query.setMode(UpdateObj.Mode.MQL).toString(where + " @ " + Utility.textToSqlVal(m_AMIUser))).toStringBuilder();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Update one or more elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
