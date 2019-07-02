package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_ADMIN", visible = true, secured = false)
public class AddElement extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddElement(Set<String> userRoles, Map<String, String> arguments, long transactionId)
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

		if(catalog == null || entity == null || fields.length == 0 || fields.length != values.length)
		{
			throw new Exception("invalid usage");
		}

		/*-----------------------------------------------------------------*/

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

		/*-----------------------------------------------------------------*/

		String mql = query.toString();

		Querier querier = getQuerier(catalog);

		String sql = querier.mqlToSQL(entity, mql);
		String ast = querier.mqlToAST(entity, mql);

		/*-----------------------------------------------------------------*/

		Tuple3<String, List<String>, List<Boolean>> tuple = Tokenizer.formatPreparedStatement(sql, values);

		/*-----------------------------------------------------------------*/

		PreparedStatement statement = querier.preparedStatement(tuple.x, false, true, null);

		for(int i = 0; i < tuple.y.size(); i++)
		{
			statement.setString(i + 1, tuple.z.get(i) ? SecuritySingleton.encrypt(tuple.y.get(i))
			                                          : /*---------------------*/(tuple.y.get(i))
			);
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

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add one element.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\",\")? -fields=\"\" -values=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
