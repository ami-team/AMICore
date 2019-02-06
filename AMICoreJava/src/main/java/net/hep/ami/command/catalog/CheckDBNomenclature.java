package net.hep.ami.command.catalog;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.reflexion.*;

@CommandMetadata(role = "AMI_ADMIN", visible = false, secured = false)
public class CheckDBNomenclature extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_regex = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");

	/*---------------------------------------------------------------------*/

	public CheckDBNomenclature(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
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

		for(String catalog: SchemaSingleton.getExternalCatalogNames())
		{
			for(String table: SchemaSingleton.getTableNames(catalog))
			{
				columns = SchemaSingleton.getEntityInfo(catalog, table).values();

				total1 += columns.size();

				for(SchemaSingleton.Column column: columns)
				{
					if(s_regex.matcher(column.name).matches() == false)
					{
						result.append("Column name ").append(column.toString()).append(" should match with regular expression " + s_regex.toString() + ".\\n");
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

		Collection<SchemaSingleton.FrgnKeys> frgnKeys;

		result.append("<field name=\"report\"><![CDATA[");

		for(String catalog: SchemaSingleton.getExternalCatalogNames())
		{
			for(String table: SchemaSingleton.getTableNames(catalog))
			{
				frgnKeys = SchemaSingleton.getForwardFKs(catalog, table).values();

				total2 += frgnKeys.size();

				for(SchemaSingleton.FrgnKeys frgnKey: frgnKeys)
				{
					if(frgnKey.get(0).fkColumn.endsWith("FK") == false)
					{
						result.append("Foreign key `").append(frgnKey.get(0).fkTable).append("`.`").append(frgnKey.get(0).fkColumn).append("` should end with `FK`.\\n");
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
		return "Check the DB naming rules.";
	}

	/*---------------------------------------------------------------------*/
}
