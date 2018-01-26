package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.mql.*;
import net.hep.ami.jdbc.reflexion.*;

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

		String separator = arguments.containsKey("separator") ? arguments.get("separator")
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
			fields.add(_fields[i]);
			values.add(_values[i]);
		}

		ExtraSingleton.patchFields(catalog, entity, fields, values, m_AMIUser, ExtraSingleton.Mode.UPDATE);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < fields.size(); i++)
		{

		}

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < keyFields.length; i++)
		{

		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{

		}

		/*-----------------------------------------------------------------*/

		String mql = stringBuilder.toString();

		String sql = MQLToSQL.parseUpdate(catalog, entity, mql);

		/*-----------------------------------------------------------------*/

		System.out.println(sql);
		int nb = 0;
//		int nb = getQuerier(catalog).executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<mql><![CDATA[").append(mql).append("]]></mql>")
		                          .append("<sql><![CDATA[").append(sql).append("]]></sql>")
		                          .append("<info><![CDATA[").append(nb).append(" element(s) updated with success]]></info>")
		;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Update elements.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-separator=\"\")? -fields=\"\" -values=\"\" (-keyFields=\"\" -keyValues=\"\")? (-where=\"\")?";
	}

	/*---------------------------------------------------------------------*/
}
