package net.hep.ami.command.catalog;

import java.sql.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_ADMIN", visible = true, secured = false)
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

		String mql = query.toString();

		Querier querier = getQuerier(catalog);

		String sql = querier.mqlToSQL(entity, mql);
		String ast = querier.mqlToAST(entity, mql);

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple2<String, List<String>> tuple = net.hep.ami.jdbc.query.sql.Formatter.formatPreparedStatement(querier, sql, values);

		/*------------------------------------------------------------------------------------------------------------*/

		PreparedStatement statement = querier.preparedStatement(tuple.x, false, true, null);

		for(int i = 0; i < tuple.y.size(); i++)
		{
			String value = tuple.y.get(i);

			try
			{
				String type = statement.getParameterMetaData().getParameterClassName(i + 1);

				switch(type)
				{
					case "java.lang.String":
						statement.setString(i + 1, value);
						break;
					case "java.sql.Timestamp":
						statement.setTimestamp(i + 1, Timestamp.valueOf(value));
						break;
					case "java.lang.Integer":
						statement.setInt(i + 1, Integer.parseInt(value));
						break;
					case "java.lang.Long":
						statement.setLong(i + 1, Long.parseLong(value));
						break;
					case "java.math.BigDecimal":
						statement.setBigDecimal(i + 1, new BigDecimal(value));
						break;
					case "java.lang.Float":
						statement.setFloat(i + 1, Float.parseFloat(value));
						break;
					case "java.lang.Double":
						statement.setDouble(i + 1, Double.parseDouble(value));
						break;
					default:
						System.out.println(type);
						break;
				}
			}
			catch(SQLException e)
			{
				statement.setString(i + 1, value);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		final int nbOfUpdatedRows;

		try
		{
			nbOfUpdatedRows = statement.executeUpdate();
		}
		catch(SQLException e)
		{
			throw new SQLException(e.getMessage() + " for SQL query: " + sql, e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new Update(nbOfUpdatedRows, mql, sql, ast).toStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Update one or more elements.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
