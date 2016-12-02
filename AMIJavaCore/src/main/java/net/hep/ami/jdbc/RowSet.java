package net.hep.ami.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;

import net.hep.ami.*;

public class RowSet
{
	/*---------------------------------------------------------------------*/

	protected ResultSet m_resultSet;

	private String m_sql;
	private String m_ast;

	/*---------------------------------------------------------------------*/

	private int m_numberOfFields;

	/*---------------------------------------------------------------------*/

	protected String[] m_fieldTables;
	protected String[] m_fieldNames;
	protected String[] m_fieldTypes;

	protected final Map<String, Integer> m_fieldIndices = new HashMap<String, Integer>();

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String sql, @Nullable String ast) throws Exception
	{
		m_resultSet = resultSet;

		m_sql = sql;
		m_ast = ast;

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

	private static final SimpleDateFormat s_simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

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

				result[i] = s_simpleDateFormat.format(m_resultSet.getTime(i + 1));

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

				result[i] = s_simpleDateFormat.format(m_resultSet.getDate(i + 1));

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

				result[i] = s_simpleDateFormat.format(m_resultSet.getTimestamp(i + 1));

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

	public Iterable iter(int limit, int offset) throws Exception
	{
		return new Iterable(this, limit, offset);
	}

	/*---------------------------------------------------------------------*/

	public List<Row> getAll() throws Exception
	{
		return getAll(Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public List<Row> getAll(final int limit, final int offset) throws Exception
	{
		List<Row> result = new ArrayList<Row>();

		/*-----------------------------------------------------------------*/

		try
		{
			m_resultSet.beforeFirst();
		}
		catch(SQLException e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < offset && m_resultSet.next(); i++)
		;
		for(int i = 0; i < limit && m_resultSet.next(); i++)
		{
			if(maxNumberOfRows == 0)
			{
				break;
			}

			maxNumberOfRows--;

			result.add(new Row(this, getCurrentValue()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder() throws Exception
	{
		return toStringBuilder(null, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(@Nullable String type) throws Exception
	{
		return toStringBuilder(type, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(@Nullable String type, final int limit, final int offset) throws Exception
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

		result.append("<sql><![CDATA[");
		if(m_sql != null) result.append(m_sql);
		result.append("]]></sql>");

		result.append("<ast><![CDATA[");
		if(m_ast != null) result.append(m_ast);
		result.append("]]></ast>");

		/*-----------------------------------------------------------------*/

		try
		{
			m_resultSet.beforeFirst();
		}
		catch(SQLException e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < offset && m_resultSet.next(); i++)
		;
		for(int i = 0; i < limit && m_resultSet.next(); i++)
		{
			if(maxNumberOfRows == 0)
			{
				break;
			}

			maxNumberOfRows--;

			result.append(new Row(this, getCurrentValue()).toStringBuilder());
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
