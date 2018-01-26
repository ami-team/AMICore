package net.hep.ami.command.catalog;

import java.sql.*;
import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.mql.*;
import net.hep.ami.jdbc.reflexion.*;

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

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
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
			fields.add(_fields[i]);
			values.add(_values[i]);
		}

		ExtraSingleton.patchFields(catalog, entity, fields, values, m_AMIUser, ExtraSingleton.Mode.ADD);

		/*-----------------------------------------------------------------*/


		for(int i = 0; i < fields.size(); i++)
		{

		}


		/*-----------------------------------------------------------------*/

		String mql = stringBuilder.toString();

		String sql = MQLToSQL.parseInsert(catalog, entity, mql);

		/*-----------------------------------------------------------------*/

		PreparedStatement statement = getQuerier(catalog).prepareStatement(sql, null);

		statement.execute();

		/*-----------------------------------------------------------------*/

		ResultSet resultSet = statement.getGeneratedKeys();

		long generatedKey = resultSet.next() ? resultSet.getLong(1) : 0;

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<mql><![CDATA[").append(mql).append("]]></mql>")
		                          .append("<sql><![CDATA[").append(sql).append("]]></sql>")
		                          .append("<rowset><row><field name=\"generatedKey\"><![CDATA[").append(generatedKey).append("]]></field></row></rowset>")
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
