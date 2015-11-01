package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import org.antlr.v4.runtime.misc.*;

public class RowSet implements Iterable<Row>
{
	/*---------------------------------------------------------------------*/

	static final int m_maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

	/*---------------------------------------------------------------------*/

	private ResultSet m_resultSet;

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

	private String[] next() throws SQLException
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

	private final RowSet m_this = this;

	/*---------------------------------------------------------------------*/

	@Override
	public Iterator<Row> iterator()
	{
		/*-----------------------------------------------------------------*/

		try
		{
			m_resultSet.beforeFirst();
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e.getMessage());
		}

		/*-----------------------------------------------------------------*/

		return new Iterator<Row>()
		{
			/*-------------------------------------------------------------*/

			@Override
			public boolean hasNext()
			{
				try
				{
					return m_resultSet.next();
				}
				catch(SQLException e)
				{
					throw new RuntimeException(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/

			@Override
			public Row next()
			{
				try
				{
					return new Row(m_this, m_this.next());
				}
				catch(SQLException e)
				{
					throw new RuntimeException(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/
		};

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public List<Row> getAll() throws Exception
	{
		List<Row> result = new ArrayList<Row>();

		/*-----------------------------------------------------------------*/

		m_resultSet.beforeFirst();

		/*-----------------------------------------------------------------*/

		int i = 0;

		while(m_resultSet.next())
		{
			if(i++ < m_maxNumberOfRows)
			{
				result.add(new Row(m_this, m_this.next()));
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

	public StringBuilder toStringBuilder()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<ast>");
		if(m_ast != null) result.append(m_ast);
		result.append("</ast>");

		result.append("<sql>");
		if(m_sql != null) result.append(m_sql);
		result.append("</sql>");

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		int i = 0;

		for(Row row: this)
		{
			if(i++ < m_maxNumberOfRows)
			{
				result.append(row.toStringBuilder());
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
}
