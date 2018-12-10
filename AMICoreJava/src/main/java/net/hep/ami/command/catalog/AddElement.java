package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.obj.*;
import net.hep.ami.utility.parser.*;
import net.hep.ami.command.*;

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

		InsertObj query;

		try
		{
			query = new InsertObj().addInsertPart(new QId(catalog, entity, null).toString(QId.MASK_CATALOG_ENTITY))
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

		/*-----------------------------------------------------------------*/

		String mql = query.setMode(InsertObj.Mode.MQL).toString();

		Querier querier = getQuerier(catalog);

		String sql = querier.mqlToSQL(entity, mql);
		String ast = querier.mqlToAST(entity, mql);
System.out.println(sql);
		/*-----------------------------------------------------------------*/

		PreparedStatement statement = querier.prepareStatement(sql, true, null);

		statement.execute();

		/*-----------------------------------------------------------------*/

		ResultSet resultSet = statement.getGeneratedKeys();

		long generatedKey = resultSet.next() ? resultSet.getLong(1) : 0;

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<sql><![CDATA[").append(sql).append("]]></sql>")
		                          .append("<mql><![CDATA[").append(mql).append("]]></mql>")
		                          .append("<ast><![CDATA[").append(ast).append("]]></ast>")
		                          .append("<rowset><row><field name=\"generatedKey\"><![CDATA[").append(generatedKey).append("]]></field></row></rowset>")
		                          .append("<info><![CDATA[1 element inserted with success]]></info>")
		;
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
