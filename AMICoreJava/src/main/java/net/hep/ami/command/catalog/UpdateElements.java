package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.Tokenizer;
import net.hep.ami.utility.Tuple2;
import net.hep.ami.command.*;

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

		String where = arguments.get("where");

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length || keyFields.length != keyValues.length || (keyFields.length == 0 && where == null))
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		Tuple2<String, List<String>> tuple = Tokenizer.format2(query.toString(), values);

		/*-----------------------------------------------------------------*/

		String mql = tuple.x;

		Querier querier = getQuerier(catalog);

		String sql = querier.mqlToSQL(entity, mql);
		String ast = querier.mqlToAST(entity, mql);

		/*-----------------------------------------------------------------*/

		PreparedStatement statement = querier.prepareStatement(sql, false, true, null);

		for(int i = 0; i < tuple.y.size(); i++)
		{
			statement.setString(i + 1, tuple.y.get(i));
		}

		/*-----------------------------------------------------------------*/

		final int nbOfUpdatedRows;

		try
		{
			nbOfUpdatedRows = statement.executeUpdate();
		}
		catch(SQLException e)
		{
			throw new SQLException(e.getMessage() + " for SQL query: " + sql, e);
		}

		/*-----------------------------------------------------------------*/

		return new Update(nbOfUpdatedRows, mql, sql, ast).toStringBuilder();

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
