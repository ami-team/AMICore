package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import org.antlr.v4.runtime.misc.*;

public class RowSet
{
	/*---------------------------------------------------------------------*/

	protected ResultSet m_resultSet;

	private String m_ast;
	private String m_sql;

	/*---------------------------------------------------------------------*/

	private int m_numberOfFields;

	/*---------------------------------------------------------------------*/

	protected String[] m_fieldTables;
	protected String[] m_fieldNames;
	protected String[] m_fieldTypes;

	protected final Map<String, Integer> m_fieldIndices = new HashMap<String, Integer>();

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet) throws Exception
	{
		this(resultSet, null, null);
	}

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String ast, @Nullable String sql) throws Exception
	{
		m_resultSet = resultSet;

		m_ast = ast;
		m_sql = sql;

		/*-----------------------------------------------------------------*/
		/* GET METADATA                                                    */
		/*-----------------------------------------------------------------*/

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		/*-----------------------------------------------------------------*/
		/* INITIALIZE DATA STRUCTURES                                      */
		/*-----------------------------------------------------------------*/

		m_numberOfFields = resultSetMetaData.getColumnCount();

		m_fieldTables = new String[m_numberOfFields];
		m_fieldNames = new String[m_numberOfFields];
		m_fieldTypes = new String[m_numberOfFields];

		/*-----------------------------------------------------------------*/
		/* FILL DATA STRUCTURES                                            */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			m_fieldTables[i] = resultSetMetaData.getTableName(i + 1);
			m_fieldNames[i] = resultSetMetaData.getColumnLabel(i + 1);
			m_fieldTypes[i] = resultSetMetaData.getColumnTypeName(i + 1);

			m_fieldIndices.put(m_fieldNames[i], i);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public ResultSet getResultSet()
	{
		return m_resultSet;
	}

	/*---------------------------------------------------------------------*/

	public String getAST()
	{
		return m_ast;
	}

	/*---------------------------------------------------------------------*/

	public String getSQL()
	{
		return m_sql;
	}

	/*---------------------------------------------------------------------*/

	public boolean isATable(String tableName)
	{
		for(String table: m_fieldTables)
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
		for(String field: m_fieldNames)
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
		for(String type: m_fieldTypes)
		{
			if(type.equals(typeName))
			{
				return true;
			}
		}

		return false;
	}


	/*---------------------------------------------------------------------*/

	public int getNumberOfFields()
	{
		return m_numberOfFields;
	}

	/*---------------------------------------------------------------------*/

	public String getTableOfField(int fieldIndex)
	{
		return (fieldIndex < m_numberOfFields) ? m_fieldTables[fieldIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getNameOfField(int fieldIndex)
	{
		return (fieldIndex < m_numberOfFields) ? m_fieldNames[fieldIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getTypeOfField(int fieldIndex)
	{
		return (fieldIndex < m_numberOfFields) ? m_fieldTypes[fieldIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	protected String[] getCurrentValue() throws SQLException
	{
		String[] result = new String[m_numberOfFields];

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/**/ if(m_fieldTypes[i].equalsIgnoreCase("TIME"))
			{
				/*---------------------------------------------------------*/
				/* TIME                                                    */
				/*---------------------------------------------------------*/

				result[i] = DateFormater.format(m_resultSet.getTime(i + 1));

				if(result[i] == null)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*---------------------------------------------------------*/
			}
			else if(m_fieldTypes[i].equalsIgnoreCase("DATE"))
			{
				/*---------------------------------------------------------*/
				/* DATE                                                    */
				/*---------------------------------------------------------*/

				result[i] = DateFormater.format(m_resultSet.getDate(i + 1));

				if(result[i] == null)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*---------------------------------------------------------*/
			}
			else if(m_fieldTypes[i].equalsIgnoreCase("TIMESTAMP"))
			{
				/*---------------------------------------------------------*/
				/* TIMESTAMP                                               */
				/*---------------------------------------------------------*/

				result[i] = DateFormater.format(m_resultSet.getTimestamp(i + 1));

				if(result[i] == null)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/
				/* DEFAULT                                                 */
				/*---------------------------------------------------------*/

				result[i] = m_resultSet.getString(i + 1);

				/*---------------------------------------------------------*/
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Iterable iter() throws Exception
	{
		return new Iterable(this);
	}

	/*---------------------------------------------------------------------*/

	public List<Row> getAll() throws Exception
	{
		List<Row> result = new ArrayList<Row>();

		/*-----------------------------------------------------------------*/

		m_resultSet.beforeFirst();

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

		/*-----------------------------------------------------------------*/

		int i = 0;

		while(m_resultSet.next())
		{
			if(i++ < maxNumberOfRows)
			{
				result.add(new Row(this, getCurrentValue()));
			}
			else
			{
				break;
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(@Nullable String type) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(type == null)
		{
			result.append("<rowset>");
		}
		else
		{
			result.append("<rowset type=\"" + type + "\">");
		}

		/*-----------------------------------------------------------------*/

		result.append("<ast>");
		if(m_ast != null) result.append(m_ast);
		result.append("</ast>");

		result.append("<sql>");
		if(m_sql != null) result.append(m_sql);
		result.append("</sql>");

		/*-----------------------------------------------------------------*/

		m_resultSet.beforeFirst();

		final int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

		/*-----------------------------------------------------------------*/

		int i = 0;

		while(m_resultSet.next())
		{
			if(i++ < maxNumberOfRows)
			{
				result.append(new Row(this, getCurrentValue()).toStringBuilder());
			}
			else
			{
				break;
			}
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder() throws Exception
	{
		return toStringBuilder(null);
	}

	/*---------------------------------------------------------------------*/
}
