package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import net.hep.ami.utility.DateFormater;

public class QueryResult {
	/*---------------------------------------------------------------------*/

	private String[] m_tables = null;
	private String[] m_fields = null;
	private String[] m_types = null;

	/*---------------------------------------------------------------------*/

	private String[][] m_rows = null;

	/*---------------------------------------------------------------------*/

	private HashMap<String, Integer> m_fieldIndices = new HashMap<String, Integer>();

	/*---------------------------------------------------------------------*/

	public QueryResult(ResultSet resultSet) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET METADATA                                                    */
		/*-----------------------------------------------------------------*/

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		/*-----------------------------------------------------------------*/
		/* INITIALIZE DATA STRUCTURES                                      */
		/*-----------------------------------------------------------------*/

		int numberOfColumn = resultSetMetaData.getColumnCount();

		m_tables = new String[numberOfColumn];
		m_fields = new String[numberOfColumn];
		m_types = new String[numberOfColumn];

		for(int i = 0; i < numberOfColumn; i++) {

			m_tables[i] = resultSetMetaData.getTableName(i + 1);
			m_fields[i] = resultSetMetaData.getColumnName(i + 1);
			m_types[i] = resultSetMetaData.getColumnTypeName(i + 1);

			m_fieldIndices.put(m_fields[i], i);
		}

		/*-----------------------------------------------------------------*/
		/* GET RESULT                                                      */
		/*-----------------------------------------------------------------*/

		int numberOfRows = 0;

		LinkedList<String[]> linkedList = new LinkedList<String[]>();

		while(resultSet.next()) {

			String[] row = new String[numberOfColumn];

			for(int i = 0; i < numberOfColumn; i++) {

				/****/ if(m_types[i].equals("TIME")) {
					/*-----------------------------------------------------*/
					/* TIME                                                */
					/*-----------------------------------------------------*/

					row[i] = resultSet.getTime(i + 1).toString();

					/*-----------------------------------------------------*/
				} else if(m_types[i].equals("DATE")) {
					/*-----------------------------------------------------*/
					/* DATE                                                */
					/*-----------------------------------------------------*/

					row[i] = DateFormater.format(resultSet.getDate(i + 1));
					if(row[i] == null) {
						row[i] = resultSet.getString(i + 1);
					}

					/*-----------------------------------------------------*/
				} else if(m_types[i].equals("TIMESTAMP")) {
					/*-----------------------------------------------------*/
					/* TIMESTAMP                                           */
					/*-----------------------------------------------------*/

					row[i] = DateFormater.format(resultSet.getTimestamp(i + 1));
					if(row[i] == null) {
						row[i] = resultSet.getString(i + 1);
					}

					/*-----------------------------------------------------*/
				} else {
					/*-----------------------------------------------------*/
					/* DEFAULT                                             */
					/*-----------------------------------------------------*/

					row[i] = resultSet.getString(i + 1);

					/*-----------------------------------------------------*/
				}
			}

			linkedList.add(row);

			numberOfRows++;
		}

		/*-----------------------------------------------------------------*/

		int i = 0;

		m_rows = new String[numberOfRows][numberOfColumn];

		for(String[] row: linkedList) {

			m_rows[i++] = row;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public String[] getFields() {

		return m_fields;
	}

	/*---------------------------------------------------------------------*/

	public String[] getTables() {

		return m_tables;
	}

	/*---------------------------------------------------------------------*/

	public String[] getTypes() {

		return m_types;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfFields() {

		return m_fields.length;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfTables() {

		return m_tables.length;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfTypes() {

		return m_types.length;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfRows() {

		return m_rows.length;
	}

	/*---------------------------------------------------------------------*/

	public boolean isATable(String tableName) {

		for(String table: m_tables) {

			if(table.equals(tableName)) {
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public boolean isAField(String fieldName) {

		for(String field: m_fields) {

			if(field.equals(fieldName)) {
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public boolean isAType(String typeName) {

		for(String type: m_types) {

			if(type.equals(typeName)) {
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public String getTableForColumn(int columnIndex) {

		return (columnIndex < m_tables.length) ? m_tables[columnIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getFieldForColumn(int columnIndex) {

		return (columnIndex < m_fields.length) ? m_fields[columnIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getTypeForColumn(int columnIndex) {

		return (columnIndex < m_types.length) ? m_types[columnIndex]
		                                      : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getFieldValueForRow(int rowIndex, String fieldName) {

		return (rowIndex < m_rows.length && m_fieldIndices.containsKey(fieldName)) ? m_rows[rowIndex][m_fieldIndices.get(fieldName)]
		                                                                           : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getValue(int rowIndex, int fieldIndex) {

		return (rowIndex < m_rows.length && fieldIndex < m_fields.length) ? m_rows[rowIndex][fieldIndex]
		                                                                  : null
		;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(String[] row: m_rows) {

			result.append("<row>");

			for(int i = 0; i < row.length; i++) result.append("<field table=\"" + m_tables[i] + "\" name=\"" + m_fields[i] + "\" type=\"" + m_types[i] + "\"><![CDATA[" + row[i] + "]]></field>");

			result.append("</row>");
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public String toString() {

		return toStringBuilder().toString();
	}

	/*---------------------------------------------------------------------*/
}
