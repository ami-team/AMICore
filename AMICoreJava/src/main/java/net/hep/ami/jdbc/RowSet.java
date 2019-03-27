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
	protected final boolean m_isAdmin;
	protected final boolean m_links;

	protected final String m_sql;
	protected final String m_mql;
	protected final String m_ast;

	/*---------------------------------------------------------------------*/

	protected final int m_numberOfFields;

	/*---------------------------------------------------------------------*/

	/* From JDBC */

	protected final String[] m_fieldCatalogs;
	protected final String[] m_fieldEntities;
	protected final String[] m_fieldNames;
	protected final String[] m_fieldLabels;
	protected final String[] m_fieldTypes;

	/*---------------------------------------------------------------------*/

	/* From AMI */

	protected final int[] m_fieldRank;
	protected final boolean[] m_fieldHidden;
	protected final boolean[] m_fieldAdminOnly;
	protected final boolean[] m_fieldCrypted;
	protected final boolean[] m_fieldPrimary;
	protected final boolean[] m_fieldReadable;
	protected final boolean[] m_fieldAutomatic;
	protected final boolean[] m_fieldCreated;
	protected final boolean[] m_fieldCreatedBy;
	protected final boolean[] m_fieldModified;
	protected final boolean[] m_fieldModifiedBy;
	protected final boolean[] m_fieldStatable;
	protected final boolean[] m_fieldGroupable;
	protected final boolean[] m_fieldDisplayable;
	protected final boolean[] m_fieldBase64;
	protected final String[] m_fieldMIME;
	protected final String[] m_fieldCtrl;
	protected final String[] m_fieldDescription;
	protected final String[] m_fieldWebLinkScript;

	/*---------------------------------------------------------------------*/

	protected final Map<String, Integer> m_nameIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);
	protected final Map<String, Integer> m_labelIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);

	/*---------------------------------------------------------------------*/

	private final DateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	private final WebLinkCache m_webLinkScripts = new WebLinkCache();

	/*---------------------------------------------------------------------*/

	private boolean m_truncated = false;

	private boolean m_lock = false;

	/*---------------------------------------------------------------------*/

	private static String buildNameOrLabel(@Nullable String catalog, @Nullable String entity, @Nullable String field)
	{
		List<String> result = new ArrayList<>();

		if(catalog != null && "N/A".equals(catalog) == false) {
			result.add(catalog);
		}

		if(entity != null && "N/A".equals(entity) == false) {
			result.add(entity);
		}

		if(field != null && "N/A".equals(field) == false) {
			result.add(field);
		}

		return String.join(".", result);
	}

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet) throws Exception
	{
		this(resultSet, null, null, false, false, null, null, null);
	}

	/*---------------------------------------------------------------------*/

	public RowSet(ResultSet resultSet, @Nullable String defaultCatalog, @Nullable String defaultEntity, boolean isAdmin, boolean links, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		m_resultSet = resultSet;
		m_isAdmin = isAdmin;
		m_links = links;

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
		m_fieldReadable = new boolean[m_numberOfFields];
		m_fieldAutomatic = new boolean[m_numberOfFields];
		m_fieldCreated = new boolean[m_numberOfFields];
		m_fieldCreatedBy = new boolean[m_numberOfFields];
		m_fieldModified = new boolean[m_numberOfFields];
		m_fieldModifiedBy = new boolean[m_numberOfFields];
		m_fieldStatable = new boolean[m_numberOfFields];
		m_fieldGroupable = new boolean[m_numberOfFields];
		m_fieldDisplayable = new boolean[m_numberOfFields];
		m_fieldBase64 = new boolean[m_numberOfFields];
		m_fieldMIME = new String[m_numberOfFields];
		m_fieldCtrl = new String[m_numberOfFields];
		m_fieldDescription = new String[m_numberOfFields];
		m_fieldWebLinkScript = new String[m_numberOfFields];

		/*-----------------------------------------------------------------*/
		/* FILL DATA STRUCTURES                                            */
		/*-----------------------------------------------------------------*/

		String m_fieldNames_i;

		Tuple3<Map<QId, QId>, Set<QId>, Set<QId>> labelToFieldMap = null;

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/*-------------------------------------------------------------*/

			try
			{
				m_fieldCatalogs[i] = SchemaSingleton.internalCatalogToExternalCatalog_noException(resultSetMetaData.getCatalogName(i + 1), "");

				if(m_fieldCatalogs[i].isEmpty())
				{
					m_fieldCatalogs[i] = "N/A";
				}
			}
			catch(Exception e1)
			{
				m_fieldCatalogs[i] = "N/A";
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

			/* FOR ORACLE */

			if(defaultCatalog != null
			   &&
			   (
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

			if(m_fieldNames[i].equalsIgnoreCase(m_fieldLabels[i])
			   &&
			   (
			       defaultCatalog != null && defaultCatalog.equalsIgnoreCase(m_fieldCatalogs[i]) == false
			       ||
			       defaultEntity != null && defaultEntity.equalsIgnoreCase(m_fieldEntities[i]) == false
			   )
			 ) {
				m_fieldLabels[i] = buildNameOrLabel(
					m_fieldCatalogs[i],
					m_fieldEntities[i],
					m_fieldLabels[i]
				);
			}

			m_fieldNames_i = buildNameOrLabel(
				m_fieldCatalogs[i],
				m_fieldEntities[i],
				m_fieldNames[i]
			);

			/*-------------------------------------------------------------*/

			try
			{
				SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(m_fieldCatalogs[i], m_fieldEntities[i], m_fieldNames[i]);

				m_fieldRank[i] = column.rank;
				m_fieldHidden[i] = column.hidden;
				m_fieldAdminOnly[i] = column.adminOnly;
				m_fieldCrypted[i] = column.crypted;
				m_fieldPrimary[i] = column.primary;
				m_fieldReadable[i] = column.readable;
				m_fieldAutomatic[i] = column.automatic;
				m_fieldCreated[i] = column.created;
				m_fieldCreatedBy[i] = column.createdBy;
				m_fieldModified[i] = column.modified;
				m_fieldModifiedBy[i] = column.modifiedBy;
				m_fieldStatable[i] = column.statable;
				m_fieldGroupable[i] = column.groupable;
				m_fieldDisplayable[i] = column.displayable;
				m_fieldBase64[i] = column.base64;
				m_fieldMIME[i] = column.mime;
				m_fieldCtrl[i] = column.ctrl;
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
				m_fieldReadable[i] = false;
				m_fieldAutomatic[i] = false;
				m_fieldCreated[i] = false;
				m_fieldCreatedBy[i] = false;
				m_fieldModified[i] = false;
				m_fieldModifiedBy[i] = false;
				m_fieldStatable[i] = false;
				m_fieldGroupable[i] = false;
				m_fieldDisplayable[i] = false;
				m_fieldBase64[i] = false;
				m_fieldMIME[i] = "@NULL";
				m_fieldCtrl[i] = "@NULL";
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

			m_nameIndices.put(m_fieldNames_i, i);
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

	public boolean isANameOrLabel(String name)
	{
		return m_nameIndices.containsKey(name)
		       ||
		       m_labelIndices.containsKey(name)
		;
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
			/*-------------------------------------------------------------*/

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

			/*-------------------------------------------------------------*/

			if(m_fieldAdminOnly[i] || m_fieldCrypted[i])
			{
				if(m_isAdmin == true)
				{
					if(result[i] == null)
					{
						result[i] = /*---------*/ "@NULL" /*---------*/;
					}
					else
					{
						if(m_fieldCrypted[i])
						{
							try
							{
								result[i] = SecuritySingleton.decrypt(result[i]);
							}
							catch(Exception e)
							{
								result[i] = /*---------*/ "@NULL" /*---------*/;
							}
						}
					}
				}
				else
				{
					result[i] = /*---------*/ "@NOGO" /*---------*/;
				}
			}
			else
			{
				if(result[i] == null)
				{
					result[i] = /*---------*/ "@NULL" /*---------*/;
				}
			}

			/*-------------------------------------------------------------*/
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	protected String processWebLink(int fieldIndex, Row row)
	{
		if(m_links)
		{
			return m_webLinkScripts.processWebLink(
				m_fieldWebLinkScript[fieldIndex],
				m_fieldCatalogs[fieldIndex],
				m_fieldEntities[fieldIndex],
				m_fieldNames[fieldIndex],
				this,
				row
			);
		}

		return "";
	}

	/*---------------------------------------------------------------------*/

	protected void setTruncated() throws Exception
	{
		if(m_lock)
		{
			throw new Exception("rowset already read");
		}

		m_truncated = true;
	}

	/*---------------------------------------------------------------------*/

	public boolean isTruncated()
	{
		return m_truncated;
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

	public StringBuilder toStringBuilder(@Nullable String type, @Nullable Integer totalNumberOfRows) throws Exception
	{
		return RowSetIterable.getStringBuilder(this, type, totalNumberOfRows);
	}

	/*---------------------------------------------------------------------*/
}
