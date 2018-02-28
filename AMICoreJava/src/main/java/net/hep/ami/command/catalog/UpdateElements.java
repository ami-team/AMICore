package net.hep.ami.command.catalog;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.mql.MQLToSQL;
import net.hep.ami.jdbc.reflexion.structure.QId;
import net.hep.ami.command.*;

public class UpdateElements extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public UpdateElements(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
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

		String[] _fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                   : new String[] {}
		;

		String[] _values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
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

		if(catalog == null || entity == null || _fields.length == 0 || _fields.length != _values.length || keyFields.length != keyValues.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();

		for(int i = 0; i < _fields.length; i++)
		{
			fields.add(new QId(_fields[i]).toString());
			values.add("'" + _values[i].trim().replace("'", "''") + "'");
		}

		/*-----------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(int i = 0; i < keyFields.length; i++)
		{
			whereList.add(new QId(keyFields[i]).toString() + " = '" + keyValues[i].trim().replace("'", "''") + "'");
		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{
			whereList.add(where);
		}

		/*-----------------------------------------------------------------*/

		stringBuilder.append("UPDATE (").append(String.join(", ", fields)).append(") VALUES (").append(String.join(", ", values)).append(")");

		if(whereList.isEmpty() == false)
		{
			stringBuilder.append(" WHERE ").append(String.join(" AND ", whereList));
		}

		/*-----------------------------------------------------------------*/

		String mql = stringBuilder.toString();

		String sql = MQLToSQL.parse(catalog, entity, mql);

		/*-----------------------------------------------------------------*/

		Update result = getQuerier(catalog).executeMQLUpdate(entity, mql);

		/*-----------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Update one or more elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
