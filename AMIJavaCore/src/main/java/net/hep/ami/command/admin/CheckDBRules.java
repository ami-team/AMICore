package net.hep.ami.command.admin;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.introspection.*;

public class CheckDBRules extends CommandAbstractClass {
	/*---------------------------------------------------------------------*/

	private static final Pattern m_regex = Pattern.compile("[a-z][a-zA-Za-z0-9]*");

	/*---------------------------------------------------------------------*/

	public CheckDBRules(Map<String, String> arguments, int transactionID) {
		super(arguments, transactionID);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main() throws Exception {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		int score1 = 0;
		int total1 = 0;

		Collection<SchemaSingleton.Column> columns;

		result.append("<field name=\"report\"><![CDATA[");

		for(String catalog: SchemaSingleton.getCatalogNames()) {

			for(String table: SchemaSingleton.getTableNames(catalog)) {

				columns = SchemaSingleton.getColumns(catalog, table).values();

				total1 += columns.size();

				for(SchemaSingleton.Column column: columns) {

					if(column.name.equals("AMIUser") == false
					   &&
					   column.name.equals("AMIPass") == false
					   &&
					   m_regex.matcher(column.name).matches() == false
					 ) {
						result.append("Column name `" + column.table + "`.`" + column.name + "` should be in lowerCamelCase.\\n");
					} else {
						score1++;
					}
				}
			}
		}

		result.append("]]></field>");

		float ratio1 = (total1 > 0) ? (100.0f * (float) score1 / (float) total1) : 0.0f;

		result.append("<field name=\"score\"><![CDATA[" + score1 + "]]></field>");
		result.append("<field name=\"total\"><![CDATA[" + total1 + "]]></field>");
		result.append("<field name=\"ratio\"><![CDATA[" + ratio1 + "]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		int score2 = 0;
		int total2 = 0;

		Collection<SchemaSingleton.FrgnKey> frgnKeys;

		result.append("<field name=\"report\"><![CDATA[");

		for(String catalog: SchemaSingleton.getCatalogNames()) {

			for(String table: SchemaSingleton.getTableNames(catalog)) {

				frgnKeys = SchemaSingleton.getFgnKeys(catalog, table).values();

				total2 += frgnKeys.size();

				for(SchemaSingleton.FrgnKey frgnKey: frgnKeys) {

					if(frgnKey.fkColumn.endsWith("FK") == false) {
						result.append("Foreign key `" + frgnKey.fkTable + "`.`" + frgnKey.fkColumn + "` should be sufixed with 'FK'.\\n");
					} else {
						score2++;
					}
				}
			}
		}

		result.append("]]></field>");

		float ratio2 = (total2 > 0) ? (100.0f * (float) score2 / (float) total2) : 0.0f;

		result.append("<field name=\"score\"><![CDATA[" + score2 + "]]></field>");
		result.append("<field name=\"total\"><![CDATA[" + total2 + "]]></field>");
		result.append("<field name=\"ratio\"><![CDATA[" + ratio2 + "]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help() {

		return "Check conventions.";
	}

	/*---------------------------------------------------------------------*/
}
