package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import org.antlr.v4.runtime.misc.*;

import net.hep.ami.ConfigSingleton;
import net.hep.ami.utility.*;

public class QueryResult
{
	/*---------------------------------------------------------------------*/

	static final int m_maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

	/*---------------------------------------------------------------------*/

	private final Map<String, Integer> m_fieldIndices = new HashMap<String, Integer>();

	/*---------------------------------------------------------------------*/

	private String[] m_tables;
	private String[] m_fields;
	private String[] m_types;

	/*---------------------------------------------------------------------*/

	private String[][] m_rows;

	/*---------------------------------------------------------------------*/

	private String m_ast;
	private String m_sql;

	/*---------------------------------------------------------------------*/

	public QueryResult(ResultSet resultSet, @Nullable String ast, @Nullable String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET METADATA                                                    */
		/*-----------------------------------------------------------------*/

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		/*-----------------------------------------------------------------*/
		/* INITIALIZE DATA STRUCTURES                                      */
		/*-----------------------------------------------------------------*/

		final int numberOfColumns = resultSetMetaData.getColumnCount();

		m_tables = new String[numberOfColumns];
		m_fields = new String[numberOfColumns];
		m_types = new String[numberOfColumns];

		for(int i = 0; i < numberOfColumns; i++)
		{
			m_tables[i] = resultSetMetaData.getTableName(i + 1);
			m_fields[i] = resultSetMetaData.getColumnLabel(i + 1);
			m_types[i] = resultSetMetaData.getColumnTypeName(i + 1);

			m_fieldIndices.put(m_fields[i], i);
		}

		/*-----------------------------------------------------------------*/
		/* GET RESULT                                                      */
		/*-----------------------------------------------------------------*/

		int numberOfRows = 0;

		List<String[]> list = new LinkedList<String[]>();

		while(resultSet.next() && numberOfRows < m_maxNumberOfRows)
		{
			String[] row = new String[numberOfColumns];

			for(int j = 0; j < numberOfColumns; j++)
			{
				/**/ if(m_types[j].equals("TIME"))
				{
					/*-----------------------------------------------------*/
					/* TIME                                                */
					/*-----------------------------------------------------*/

					row[j] = resultSet.getTime(j + 1).toString();

					/*-----------------------------------------------------*/
				}
				else if(m_types[j].equals("DATE"))
				{
					/*-----------------------------------------------------*/
					/* DATE                                                */
					/*-----------------------------------------------------*/

					row[j] = DateFormater.format(resultSet.getDate(j + 1));

					if(row[j] == null)
					{
						row[j] = resultSet.getString(j + 1);
					}

					/*-----------------------------------------------------*/
				}
				else if(m_types[j].equals("TIMESTAMP"))
				{
					/*-----------------------------------------------------*/
					/* TIMESTAMP                                           */
					/*-----------------------------------------------------*/

					row[j] = DateFormater.format(resultSet.getTimestamp(j + 1));

					if(row[j] == null)
					{
						row[j] = resultSet.getString(j + 1);
					}

					/*-----------------------------------------------------*/
				}
				else
				{
					/*-----------------------------------------------------*/
					/* DEFAULT                                             */
					/*-----------------------------------------------------*/

					row[j] = resultSet.getString(j + 1);

					/*-----------------------------------------------------*/
				}
			}

			list.add(row);

			numberOfRows++;
		}

		/*-----------------------------------------------------------------*/

		int i = 0;

		m_rows = new String[numberOfRows][numberOfColumns];

		for(String[] row: list)
		{
			m_rows[i++] = row;
		}

		/*-----------------------------------------------------------------*/

		m_ast = ast;
		m_sql = sql;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public QueryResult(ResultSet resultSet) throws Exception
	{
		this(resultSet, null, null);
	}

	/*---------------------------------------------------------------------*/

	public String[] getFields()
	{
		return m_fields;
	}

	/*---------------------------------------------------------------------*/

	public String[] getTables()
	{
		return m_tables;
	}

	/*---------------------------------------------------------------------*/

	public String[] getTypes()
	{
		return m_types;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfFields()
	{
		return m_fields.length;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfTables()
	{
		return m_tables.length;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfTypes()
	{
		return m_types.length;
	}

	/*---------------------------------------------------------------------*/

	public int getNumberOfRows()
	{
		return m_rows.length;
	}

	/*---------------------------------------------------------------------*/

	public boolean isATable(String tableName)
	{
		for(String table: m_tables)
		{
			if(table.equals(tableName))
			{
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public boolean isAField(String fieldName)
	{
		for(String field: m_fields)
		{
			if(field.equals(fieldName))
			{
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public boolean isAType(String typeName)
	{
		for(String type: m_types)
		{
			if(type.equals(typeName))
			{
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public String getTableForColumn(int columnIndex)
	{
		return (columnIndex < m_tables.length) ? m_tables[columnIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getFieldForColumn(int columnIndex)
	{
		return (columnIndex < m_fields.length) ? m_fields[columnIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getTypeForColumn(int columnIndex)
	{
		return (columnIndex < m_types.length) ? m_types[columnIndex]
		                                      : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getValue(int rowIndex, int fieldIndex)
	{
		return (rowIndex < m_rows.length && fieldIndex < m_fields.length) ? m_rows[rowIndex][fieldIndex]
		                                                                  : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getValue(int rowIndex, String fieldName)
	{
		return (rowIndex < m_rows.length && m_fieldIndices.containsKey(fieldName)) ? m_rows[rowIndex][m_fieldIndices.get(fieldName)]
		                                                                           : null
		;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(m_ast != null)
		{
			result.append("<ast>");
			result.append(m_ast);
			result.append("</ast>");
		}

		if(m_sql != null)
		{
			result.append("<sql>");
			result.append(m_sql);
			result.append("</sql>");
		}

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(String[] row: m_rows)
		{
			result.append("<row>");

			final int numberOfRows = row.length;

			for(int i = 0; i < numberOfRows; i++)
			{
				result.append("<field table=\"" + m_tables[i] + "\" name=\"" + m_fields[i] + "\" type=\"" + m_types[i] + "\"><![CDATA[" + row[i] + "]]></field>");
			}

			result.append("</row>");
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return toStringBuilder().toString();
	}

	/*---------------------------------------------------------------------*/
}
