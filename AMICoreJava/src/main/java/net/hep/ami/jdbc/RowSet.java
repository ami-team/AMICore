package net.hep.ami.jdbc;

import java.sql.*;
import java.text.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.*;
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
	protected final String[] m_fieldLabels;
	protected final String[] m_fieldTypes;

	/*---------------------------------------------------------------------*/

	protected final int[] m_fieldRank;
	protected final boolean[] m_fieldHidden;
	protected final boolean[] m_fieldAdminOnly;
	protected final boolean[] m_fieldCrypted;
	protected final boolean[] m_fieldPrimary;
	protected final boolean[] m_fieldCreated;
	protected final boolean[] m_fieldCreatedBy;
	protected final boolean[] m_fieldModified;
	protected final boolean[] m_fieldModifiedBy;
	protected final boolean[] m_fieldStatable;
	protected final boolean[] m_fieldGroupable;
	protected final String[] m_fieldDescription;
	protected final String[] m_fieldWebLinkScript;

	/*---------------------------------------------------------------------*/

	protected final Map<String, Integer> m_labelIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);

	/*---------------------------------------------------------------------*/

	private final DateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	private WebLinkCache m_webLinkScripts = null;

	private boolean m_incomplete = false;

	private boolean m_lock = false;

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet) throws Exception
	{
		this(resultSet, null, null, null, null);
	}

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String defaultCatalog, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
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

		/*-----------------------------------------------------------------*/

		/* From JDBC */

		m_fieldCatalogs = new String[m_numberOfFields];
		m_fieldEntities = new String[m_numberOfFields];
		m_fieldNames = new String[m_numberOfFields];
		m_fieldLabels = new String[m_numberOfFields];
		m_fieldTypes = new String[m_numberOfFields];

		/*-----------------------------------------------------------------*/

		/* From AMI */

		m_fieldRank = new int[m_numberOfFields];
		m_fieldHidden = new boolean[m_numberOfFields];
		m_fieldAdminOnly = new boolean[m_numberOfFields];
		m_fieldCrypted = new boolean[m_numberOfFields];
		m_fieldPrimary = new boolean[m_numberOfFields];
		m_fieldCreated = new boolean[m_numberOfFields];
		m_fieldCreatedBy = new boolean[m_numberOfFields];
		m_fieldModified = new boolean[m_numberOfFields];
		m_fieldModifiedBy = new boolean[m_numberOfFields];
		m_fieldStatable = new boolean[m_numberOfFields];
		m_fieldGroupable = new boolean[m_numberOfFields];
		m_fieldDescription = new String[m_numberOfFields];
		m_fieldWebLinkScript = new String[m_numberOfFields];

		/*-----------------------------------------------------------------*/
		/* FILL DATA STRUCTURES                                            */
		/*-----------------------------------------------------------------*/

		Tuple3<Map<QId, QId>, Set<QId>, Set<QId>> labelToFieldMap = null;

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/*-------------------------------------------------------------*/

			try
			{
				m_fieldCatalogs[i] = SchemaSingleton.internalCatalogToExternalCatalog_noException(resultSetMetaData.getCatalogName(i + 1), "");

				if(m_fieldEntities[i].isEmpty())
				{
					m_fieldCatalogs[i] = "N/A";
				}
			}
			catch(Exception e1)
			{
				m_fieldCatalogs[i] =  "N/A";
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
				m_fieldNames[i] = resultSetMetaData.getColumnName(i + 1);

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
				m_fieldLabels[i] = resultSetMetaData.getColumnLabel(i + 1);

				if(m_fieldLabels[i].isEmpty())
				{
					m_fieldLabels[i] = "N/A";
				}
			}
			catch(Exception e)
			{
				m_fieldLabels[i] = "N/A";
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

			if((
				"N/A".equals(m_fieldCatalogs[i]) == true
				||
				"N/A".equals(m_fieldEntities[i]) == true
			   )
			   &&
			   "N/A".equals(m_fieldLabels[i]) == false
			 ) {
				if(labelToFieldMap == null)
				{
					labelToFieldMap = Tokenizer.buildLabelToFieldMap(sql);
				}

				resolveLabel(labelToFieldMap, defaultCatalog, i, sql);
			}

			/*-------------------------------------------------------------*/

			try
			{
				SchemaSingleton.Column column = SchemaSingleton.getColumn(m_fieldCatalogs[i], m_fieldEntities[i], m_fieldNames[i]);

				m_fieldRank[i] = column.rank;
				m_fieldHidden[i] = column.hidden;
				m_fieldAdminOnly[i] = column.adminOnly;
				m_fieldCrypted[i] = column.crypted;
				m_fieldPrimary[i] = column.primary;
				m_fieldCreated[i] = column.created;
				m_fieldCreatedBy[i] = column.createdBy;
				m_fieldModified[i] = column.modified;
				m_fieldModifiedBy[i] = column.modifiedBy;
				m_fieldStatable[i] = column.statable;
				m_fieldGroupable[i] = column.groupable;
				m_fieldDescription[i] = column.description;
				m_fieldWebLinkScript[i] = column.webLinkScript;
			}
			catch(Exception e)
			{
				m_fieldRank[i] = 0;
				m_fieldHidden[i] = false;
				m_fieldAdminOnly[i] = false;
				m_fieldCrypted[i] = false;
				m_fieldPrimary[i] = false;
				m_fieldCreated[i] = false;
				m_fieldCreatedBy[i] = false;
				m_fieldModified[i] = false;
				m_fieldModifiedBy[i] = false;
				m_fieldStatable[i] = false;
				m_fieldGroupable[i] = false;
				m_fieldDescription[i] = "N/A";
				m_fieldWebLinkScript[i] = "@NULL";
			}

			/*-------------------------------------------------------------*/

			if("self".equals(defaultCatalog))
			{
				m_fieldCrypted[i] = (
					"paramName".equals(m_fieldNames[i])
					||
					"paramValue".equals(m_fieldNames[i])
					||
					"user".equals(m_fieldNames[i])
					||
					"pass".equals(m_fieldNames[i])
					||
					"clientDN".equals(m_fieldNames[i])
					||
					"issuerDN".equals(m_fieldNames[i])
				);
			}

			/*-------------------------------------------------------------*/

			m_labelIndices.put(m_fieldLabels[i], i);

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void resolveLabel(Tuple3<Map<QId, QId>, Set<QId>, Set<QId>> labelToFieldMap, String defaultCatalog, int fieldIndex, String sql)
	{
		/*-----------------------------------------------------------------*/

		QId qId;

		try
		{
			qId = QId.parseQId(m_fieldLabels[fieldIndex], QId.Type.FIELD);

			for(Map.Entry<QId, QId> entry: labelToFieldMap.x.entrySet())
			{
				if(qId.matches(entry.getKey()))
				{
					qId = entry.getValue();

					break;
				}
			}
		}
		catch(Exception e)
		{
			/* IGNORE */

			return;
		}

		/*-----------------------------------------------------------------*/

		if(qId.is(QId.MASK_CATALOG_ENTITY_FIELD) == false)
		{
			for(QId table: labelToFieldMap.y)
			{
				try
				{
					QId resolvedQId = AutoJoinSingleton.resolve(defaultCatalog, table.getEntity(), qId).getExternalQId();

					m_fieldCatalogs[fieldIndex] = SchemaSingleton.internalCatalogToExternalCatalog_noException(resolvedQId.getCatalog(), "N/A");
					m_fieldEntities[fieldIndex] = resolvedQId.getEntity();
					m_fieldNames[fieldIndex] = resolvedQId.getField();

					break;
				}
				catch(Exception e)
				{
					/* IGNORE */

					return;
				}
			}
		}
		else
		{
			m_fieldCatalogs[fieldIndex] = SchemaSingleton.internalCatalogToExternalCatalog_noException(qId.getCatalog(), "N/A");
			m_fieldEntities[fieldIndex] = qId.getEntity();
			m_fieldNames[fieldIndex] = qId.getField();
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

	public String getLabelOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldLabels[fieldIndex];
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

				Timestamp timestamp = m_resultSet.getTimestamp(i + 1);

				if(timestamp != null)
				{
					result[i] = timestamp.toString();

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

			if(result[i] != null)
			{
				if(m_fieldCrypted[i])
				{
					try
					{
						result[i] = SecuritySingleton.decrypt(result[i]);
					}
					catch(Exception e)
					{
						result[i] = /*-----------*/ "" /*-----------*/;
					}
				}
			}
			else
			{
				result[i] = /*-----------*/ "" /*-----------*/;
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	protected String processWebLink(int fieldIndex, Row row)
	{
		String webLinkScript = m_fieldWebLinkScript[fieldIndex];

		if(webLinkScript == null || webLinkScript.isEmpty() || "@NULL".equalsIgnoreCase(webLinkScript))
		{
			return "";
		}
		else
		{
			if(m_webLinkScripts == null)
			{
				m_webLinkScripts = new WebLinkCache();
			}

			return m_webLinkScripts.processWebLink(
				webLinkScript,
				m_fieldCatalogs[fieldIndex],
				m_fieldEntities[fieldIndex],
				m_fieldNames[fieldIndex],
				row
			);
		}
	}

	/*---------------------------------------------------------------------*/

	protected void setIncomplete() throws Exception
	{
		if(m_lock)
		{
			throw new Exception("rowset already read");
		}

		m_incomplete = true;
	}

	/*---------------------------------------------------------------------*/

	public boolean isTruncated()
	{
		return m_incomplete;
	}

	/*---------------------------------------------------------------------*/

	protected void setLocked() throws Exception
	{
		if(m_lock)
		{
			throw new Exception("rowset already read");
		}

		m_lock = true;
	}

	/*---------------------------------------------------------------------*/

	public boolean isLocked()
	{
		return m_lock;
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
