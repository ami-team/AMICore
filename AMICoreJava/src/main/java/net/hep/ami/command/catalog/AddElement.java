package net.hep.ami.command.catalog;

import java.sql.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;

@CommandMetadata(role = "AMI_ADMIN", visible = true, secured = false)
public class AddElement extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AddElement(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
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

		String separator = Pattern.quote(arguments.getOrDefault("separator", ","));

		String[] fields = arguments.containsKey("fields") ? arguments.get("fields").split(separator, -1)
		                                                  : new String[] {}
		;

		String[] values = arguments.containsKey("values") ? arguments.get("values").split(separator, -1)
		                                                  : new String[] {}
		;

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		XQLInsert query;

		try
		{
			query = new XQLInsert(XQLInsert.Mode.MQL).addInsertPart(new QId(catalog, entity, null).toString(QId.MASK_CATALOG_ENTITY))
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
					case "oracle.jdbc.OracleClob":
						statement.setString(i + 1, value);
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

		ResultSet resultSet = statement.getGeneratedKeys();

		try
		{
			if(resultSet.next())
			{
				return new Update(nbOfUpdatedRows, resultSet.getString(1), mql, sql, ast).toStringBuilder();
			}
			else
			{
				return new Update(nbOfUpdatedRows, mql, sql, ast).toStringBuilder();
			}
		}
		catch(SQLException e)
		{
			return new Update(nbOfUpdatedRows, mql, sql, ast).toStringBuilder();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Add one element.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? -fields=\"\" -values=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
