package net.hep.ami.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;

public class RowSet
{
	/*---------------------------------------------------------------------*/

	protected final ResultSet m_resultSet;

	private final String m_xql;
	private final String m_ast;

	/*---------------------------------------------------------------------*/

	private final int m_numberOfFields;

	protected final String[] m_fieldTables;
	protected final String[] m_fieldNames;
	protected final String[] m_fieldTypes;

	protected final Map<String, Integer> m_fieldIndices = new HashMap<>();

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String xql, @Nullable String ast) throws Exception
	{
		m_resultSet = resultSet;

		m_xql = xql;
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
		return m_xql;
	}

	/*---------------------------------------------------------------------*/

	public boolean isATable(String tableName)
	{
		for(String table: m_fieldTables)
		{
			if(table.equalsIgnoreCase(tableName))
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
			if(field.equalsIgnoreCase(fieldName))
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
			if(type.equalsIgnoreCase(typeName))
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

	private final SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	protected String[] getCurrentValue() throws SQLException
	{
		String[] result = new String[m_numberOfFields];

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/**/ if("TIME".equalsIgnoreCase(m_fieldTypes[i]))
			{
				/*---------------------------------------------------------*/
				/* TIME                                                    */
				/*---------------------------------------------------------*/

				result[i] = m_simpleDateFormat.format(m_resultSet.getTime(i + 1));

				if(result[i] == null)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*---------------------------------------------------------*/
			}
			else if("DATE".equalsIgnoreCase(m_fieldTypes[i]))
			{
				/*---------------------------------------------------------*/
				/* DATE                                                    */
				/*---------------------------------------------------------*/

				result[i] = m_simpleDateFormat.format(m_resultSet.getDate(i + 1));

				if(result[i] == null)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*---------------------------------------------------------*/
			}
			else if("TIMESTAMP".equalsIgnoreCase(m_fieldTypes[i]))
			{
				/*---------------------------------------------------------*/
				/* TIMESTAMP                                               */
				/*---------------------------------------------------------*/

				result[i] = m_simpleDateFormat.format(m_resultSet.getTimestamp(i + 1));

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
		return Iterable.getList(this);
	}

	/*---------------------------------------------------------------------*/

	public List<Row> getAll(int limit, int offset) throws Exception
	{
		return Iterable.getList(this, limit, offset);
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

	public StringBuilder toStringBuilder(@Nullable String type, int limit, int offset) throws Exception
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

		result.append("<xql><![CDATA[");
		if(m_xql != null) result.append(m_xql);
		result.append("]]></xql>");

		result.append("<ast><![CDATA[");
		if(m_ast != null) result.append(m_ast);
		result.append("]]></ast>");

		/*-----------------------------------------------------------------*/

		result.append(Iterable.getStringBuffer(this, limit, offset));

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
