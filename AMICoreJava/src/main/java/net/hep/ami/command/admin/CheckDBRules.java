package net.hep.ami.command.admin;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

public class CheckDBRules extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_regex = Pattern.compile("[a-z][a-z0-9]*");

	/*---------------------------------------------------------------------*/

	public CheckDBRules(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		int score1 = 0;
		int total1 = 0;

		Collection<SchemaSingleton.Column> columns;

		result.append("<field name=\"report\"><![CDATA[");

		for(String catalog: SchemaSingleton.getCatalogNames())
		{
			for(String table: SchemaSingleton.getTableNames(catalog))
			{
				columns = SchemaSingleton.getColumns(catalog, table).values();

				total1 += columns.size();

				for(SchemaSingleton.Column column: columns)
				{
					if(column.name.equals("AMIUser") == false
					   &&
					   column.name.equals("AMIPass") == false
					   &&
					   s_regex.matcher(column.name).matches() == false
					 ) {
						result.append("Column name `").append(column.table).append("`.`" + column.name).append("` should be in lowerCamelCase.\\n");
					}
					else
					{
						score1++;
					}
				}
			}
		}

		result.append("]]></field>");

		float ratio1 = (total1 > 0) ? (100.0f * (float) score1 / (float) total1) : 0.0f;

		result.append("<field name=\"score\"><![CDATA[").append(score1).append("]]></field>");
		result.append("<field name=\"total\"><![CDATA[").append(total1).append("]]></field>");
		result.append("<field name=\"ratio\"><![CDATA[").append(ratio1).append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		int score2 = 0;
		int total2 = 0;

		Collection<SchemaSingleton.FrgnKey> frgnKeys;

		result.append("<field name=\"report\"><![CDATA[");

		for(String catalog: SchemaSingleton.getCatalogNames())
		{
			for(String table: SchemaSingleton.getTableNames(catalog))
			{
				frgnKeys = SchemaSingleton.getFrgnKeys(catalog, table).values();

				total2 += frgnKeys.size();

				for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
				{
					if(frgnKey.fkColumn.endsWith("fk") == false)
					{
						result.append("Foreign key `").append(frgnKey.fkTable).append("`.`").append(frgnKey.fkColumn).append("` should be sufixed with 'FK'.\\n");
					}
					else
					{
						score2++;
					}
				}
			}
		}

		result.append("]]></field>");

		float ratio2 = (total2 > 0) ? (100.0f * (float) score2 / (float) total2) : 0.0f;

		result.append("<field name=\"score\"><![CDATA[").append(score2).append("]]></field>");
		result.append("<field name=\"total\"><![CDATA[").append(total2).append("]]></field>");
		result.append("<field name=\"ratio\"><![CDATA[").append(ratio2).append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Check conventions.";
	}

	/*---------------------------------------------------------------------*/
}
