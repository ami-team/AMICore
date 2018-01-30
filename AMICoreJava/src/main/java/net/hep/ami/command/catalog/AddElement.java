package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.mql.*;
import net.hep.ami.jdbc.reflexion.structure.*;

public class AddElement extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public AddElement(Map<String, String> arguments, long transactionId)
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

		if(catalog == null || entity == null || _fields.length == 0 || _fields.length != _values.length)
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

		stringBuilder.append("INSERT (").append(String.join(", ", fields)).append(") VALUES (").append(String.join(", ", values)).append(")");

		/*-----------------------------------------------------------------*/

		String mql = stringBuilder.toString();

		System.out.println("mql: " + mql);

		String sql = MQLToSQL.parse(catalog, entity, mql);
		String ast = MQLToAST.parse(catalog, entity, mql);

		System.out.println("sql: " + sql);

		/*-----------------------------------------------------------------*/

/*		PreparedStatement statement = getQuerier(catalog).prepareStatement(sql, ast);

		statement.execute();
*/
		/*-----------------------------------------------------------------*/
/*
		ResultSet resultSet = statement.getGeneratedKeys();

		long generatedKey = resultSet.next() ? resultSet.getLong(1) : 0;
*/
		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<sql><![CDATA[").append(sql).append("]]></sql>")
		                          .append("<mql><![CDATA[").append(mql).append("]]></mql>")
//		                          .append("<ast><![CDATA[").append(ast).append("]]></ast>")
//		                          .append("<rowset><row><field name=\"generatedKey\"><![CDATA[").append(generatedKey).append("]]></field></row></rowset>")
		                          .append("<info><![CDATA[1 element inserted with success]]></info>")
		;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Add element.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? -fields=\"\" -values=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
