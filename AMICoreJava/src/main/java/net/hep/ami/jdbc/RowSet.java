package net.hep.ami.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;

import net.hep.ami.jdbc.reflexion.SchemaSingleton;

public class RowSet
{
	/*---------------------------------------------------------------------*/

	protected final ResultSet m_resultSet;

	private final String m_sql;
	private final String m_mql;
	private final String m_ast;

	/*---------------------------------------------------------------------*/

	private final int m_numberOfFields;

	protected final String[] m_fieldCatalogs;
	protected final String[] m_fieldEntities;
	protected final String[] m_fieldNames;
	protected final String[] m_fieldTypes;

	/*---------------------------------------------------------------------*/

	protected final Map<String, Integer> m_fieldIndices = new HashMap<>();

	/*---------------------------------------------------------------------*/

	private final SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		m_resultSet = resultSet;

		m_sql = sql;
		m_mql = mql;
		m_ast = ast;

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
				try
				{
					m_fieldCatalogs[i] = SchemaSingleton.internalCatalogToExternalCatalog(
						resultSetMetaData.getSchemaName(i + 1)
					);
				}
				catch(Exception e2)
				{
					m_fieldCatalogs[i] = "N/A";
				}
			}

			/*-------------------------------------------------------------*/

			m_fieldEntities[i] = resultSetMetaData.getTableName(i + 1).toLowerCase();
			m_fieldNames[i] = resultSetMetaData.getColumnLabel(i + 1).toLowerCase();
			m_fieldTypes[i] = resultSetMetaData.getColumnTypeName(i + 1).toUpperCase();

			/*-------------------------------------------------------------*/

			m_fieldIndices.put(m_fieldNames[i], i);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
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

	public String getCatalogOfField(int fieldIndex)
	{
		return (fieldIndex < m_numberOfFields) ? m_fieldCatalogs[fieldIndex]
		                                       : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getEntityOfField(int fieldIndex)
	{
		return (fieldIndex < m_numberOfFields) ? m_fieldEntities[fieldIndex]
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

		result.append("<sql><![CDATA[");
		if(m_sql != null) result.append(m_sql);
		result.append("]]></sql>");

		result.append("<mql><![CDATA[");
		if(m_mql != null) result.append(m_mql);
		result.append("]]></mql>");

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
