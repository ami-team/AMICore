package net.hep.ami.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.hep.ami.utility.*;
import net.hep.ami.jdbc.reflexion.*;

public class RowSet
{
	/*---------------------------------------------------------------------*/

	protected final ResultSet m_resultSet;

	protected final String m_sql;
	protected final String m_mql;
	protected final String m_ast;

	/*---------------------------------------------------------------------*/

	protected final int m_numberOfFields;

	/*---------------------------------------------------------------------*/

	protected final String[] m_fieldCatalogs;
	protected final String[] m_fieldEntities;
	protected final String[] m_fieldNames;
	protected final String[] m_fieldTypes;

	/*---------------------------------------------------------------------*/

	protected final Map<String, Integer> m_fieldIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);

	/*---------------------------------------------------------------------*/

	private final DateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	private boolean m_lock = false;

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet) throws Exception
	{
		this(resultSet, null, null, null);
	}

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		m_resultSet = resultSet;

		m_sql = sql != null ? sql : "";
		m_mql = mql != null ? mql : "";
		m_ast = ast != null ? ast : "";

		/*-----------------------------------------------------------------*/
		/* GET METADATA                                                    */
		/*-----------------------------------------------------------------*/

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		/*-----------------------------------------------------------------*/
		/* INITIALIZE DATA STRUCTURES                                      */
		/*-----------------------------------------------------------------*/

		m_numberOfFields = resultSetMetaData.getColumnCount();

		m_fieldCatalogs = new String[m_numberOfFields];
		m_fieldEntities = new String[m_numberOfFields];
		m_fieldNames = new String[m_numberOfFields];
		m_fieldTypes = new String[m_numberOfFields];

		/*-----------------------------------------------------------------*/
		/* FILL DATA STRUCTURES                                            */
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/*-------------------------------------------------------------*/

			try
			{
				m_fieldCatalogs[i] = SchemaSingleton.internalCatalogToExternalCatalog(
					resultSetMetaData.getCatalogName(i + 1)
				);
			}
			catch(Exception e1)
			{
				Matcher m = Pattern.compile("[fF][rR][oO][mM]\\s+([a-zA-Z0-0_]+)").matcher(m_sql);

				m_fieldCatalogs[i] = (m != null) ? m.group(1) : "N/A";
			}

			/*-------------------------------------------------------------*/

			try
			{
				m_fieldEntities[i] = resultSetMetaData.getTableName(i + 1);

				if(m_fieldEntities[i].isEmpty())
				{
					m_fieldEntities[i] = "N/A";
				}
			}
			catch(Exception e)
			{
				m_fieldEntities[i] = "N/A";
			}

			/*-------------------------------------------------------------*/

			try
			{
				m_fieldNames[i] = resultSetMetaData.getColumnLabel(i + 1);

				if(m_fieldNames[i].isEmpty())
				{
					m_fieldNames[i] = "N/A";
				}
			}
			catch(Exception e)
			{
				m_fieldNames[i] = "N/A";
			}

			/*-------------------------------------------------------------*/

			try
			{
				m_fieldTypes[i] = resultSetMetaData.getColumnTypeName(i + 1);

				if(m_fieldTypes[i].isEmpty())
				{
					m_fieldTypes[i] = "N/A";
				}
			}
			catch(Exception e)
			{
				m_fieldTypes[i] = "N/A";
			}

			/*-------------------------------------------------------------*/

			m_fieldIndices.put(m_fieldNames[i], i);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public ResultSet getResultSet()
	{
		return m_resultSet;
	}

	/*---------------------------------------------------------------------*/

	public String getSQL()
	{
		return m_sql;
	}

	/*---------------------------------------------------------------------*/

	public String getMQL()
	{
		return m_mql;
	}

	/*---------------------------------------------------------------------*/

	public String getAST()
	{
		return m_ast;
	}

	/*---------------------------------------------------------------------*/

	public boolean isACatalog(String catalogName)
	{
		for(String catalog: m_fieldCatalogs)
		{
			if(catalog.equalsIgnoreCase(catalogName))
			{
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public boolean isAnEtity(String entityName)
	{
		for(String entity: m_fieldEntities)
		{
			if(entity.equalsIgnoreCase(entityName))
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

	public String getCatalogOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldCatalogs[fieldIndex];
	}

	/*---------------------------------------------------------------------*/

	public String getEntityOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldEntities[fieldIndex];
	}

	/*---------------------------------------------------------------------*/

	public String getNameOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldNames[fieldIndex];
	}

	/*---------------------------------------------------------------------*/

	public String getTypeOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldTypes[fieldIndex];
	}

	/*---------------------------------------------------------------------*/

	protected String[] getCurrentRow() throws SQLException
	{
		String[] result = new String[m_numberOfFields];

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/**/ if("TIME".equalsIgnoreCase(m_fieldTypes[i]))
			{
				/*---------------------------------------------------------*/
				/* TIME                                                    */
				/*---------------------------------------------------------*/

				result[i] = m_dateFormat.format(m_resultSet.getTime(i + 1));

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

				result[i] = m_dateFormat.format(m_resultSet.getDate(i + 1));

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

				Timestamp tmsp = m_resultSet.getTimestamp(i + 1);

				if(tmsp != null)
				{
					result[i] = m_dateFormat.format(tmsp);

					if(result[i] == null)
					{
						result[i] = m_resultSet.getString(i + 1);
					}
				}
				else
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

			if(result[i] == null)
			{
				result[i] = "";
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	protected void lock() throws Exception
	{
		if(m_lock)
		{
			throw new Exception("rowset already read");
		}

		m_lock = true;
	}

	/*---------------------------------------------------------------------*/

	public RowSetIterable iterate() throws Exception
	{
		return new RowSetIterable(this);
	}

	public RowSetIterable iterate(int limit, int offset) throws Exception
	{
		return new RowSetIterable(this, limit, offset);
	}

	/*---------------------------------------------------------------------*/

	public List<Row> getAll() throws Exception
	{
		return RowSetIterable.getAll(this);
	}

	public List<Row> getAll(int limit, int offset) throws Exception
	{
		return RowSetIterable.getAll(this, limit, offset);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder() throws Exception
	{
		return RowSetIterable.getStringBuilder(this);
	}

	public StringBuilder toStringBuilder(@Nullable String type) throws Exception
	{
		return RowSetIterable.getStringBuilder(this, type);
	}

	public StringBuilder toStringBuilder(@Nullable String type, int limit, int offset) throws Exception
	{
		return RowSetIterable.getStringBuilder(this, type, limit, offset);
	}

	/*---------------------------------------------------------------------*/
}
