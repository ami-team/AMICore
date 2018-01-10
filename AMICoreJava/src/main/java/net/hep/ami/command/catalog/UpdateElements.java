package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.reflexion.structure.*;

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

		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();

		for(int i = 0; i < _fields.length; i++)
		{
			fields.add(_fields[i]);
			values.add(_values[i]);
		}

		ExtraSingleton.patchFields(catalog, entity, fields, values, m_AMIUser, ExtraSingleton.Mode.UPDATE);

		/*-----------------------------------------------------------------*/

		Islets islets;

		/*-----------------------------------------------------------------*/

		islets = new Islets();

		for(int i = 0; i < fields.size(); i++)
		{
			AutoJoinSingleton.resolve(
				islets,
				catalog,
				entity,
				fields.get(i),
				values.get(i)
			);
		}

		/*-----------------------------------------------------------------*/

		List<String> setList = new ArrayList<>();

		for(String comp: islets.toQuery().getWherePart().split(" AND ", -1))
		{
			comp = comp.substring(comp.indexOf('.') + 1);
			comp = comp.substring(comp.indexOf('.') + 1);

			setList.add(comp);
		}

		/*-----------------------------------------------------------------*/

		islets = new Islets();

		for(int i = 0; i < keyFields.length; i++)
		{
			AutoJoinSingleton.resolve(
				islets,
				catalog,
				entity,
				keyFields[i],
				keyValues[i]
			);
		}

		/*-----------------------------------------------------------------*/

		List<String> whereList = new ArrayList<>();

		for(String comp: islets.toQuery().getWhereCollection())
		{
			comp = comp.substring(comp.indexOf('.') + 1);
			comp = comp.substring(comp.indexOf('.') + 1);

			whereList.add(comp);
		}

		/*-----------------------------------------------------------------*/

		if(where.isEmpty() == false)
		{
			whereList.add(where);
		}

		/*-----------------------------------------------------------------*/

		whereList.add("1=1");

		/*-----------------------------------------------------------------*/

		String sql = new StringBuilder().append("UPDATE `" + entity + "` SET ").append(String.join(",", setList)).append(" WHERE ").append(String.join(" AND ", whereList)).toString();

		/*-----------------------------------------------------------------*/

		int nb = getQuerier(catalog).executeSQLUpdate(sql);

		/*-----------------------------------------------------------------*/

		return new StringBuilder().append("<sql><![CDATA[").append(sql).append("]]></sql>")
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
