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
	/*----------------------------------------------------------------------------------------------------------------*/

	protected final ResultSet m_resultSet;
	protected final boolean m_isAdmin;
	protected final boolean m_links;

	protected final String m_sql;
	protected final String m_mql;
	protected final String m_ast;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final int m_numberOfFields;

	/*----------------------------------------------------------------------------------------------------------------*/

	/* From JDBC */

	protected final String[] m_fieldCatalogs;
	protected final String[] m_fieldEntities;
	protected final String[] m_fieldNames;
	protected final String[] m_fieldLabels;
	protected final String[] m_fieldTypes;

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final Map<String, Integer> m_nameIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);
	protected final Map<String, Integer> m_labelIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);

	/*----------------------------------------------------------------------------------------------------------------*/

	private final DateFormat m_datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	private final DateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private final DateFormat m_timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

	/*----------------------------------------------------------------------------------------------------------------*/

	private final WebLinkCache m_webLinkScripts = new WebLinkCache();

	/*----------------------------------------------------------------------------------------------------------------*/

	private boolean m_truncated = false;

	private boolean m_lock = false;

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private static String buildNameOrLabel(@Nullable String catalog, @Nullable String entity, @Nullable String field)
	{
		List<String> result = new ArrayList<>();

		if(catalog != null && !catalog.isEmpty()) {
			result.add(catalog);
		}

		if(entity != null && !entity.isEmpty()) {
			result.add(entity);
		}

		if(field != null && !field.isEmpty()) {
			result.add(field);
		}

		return String.join(".", result);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public RowSet(@NotNull ResultSet resultSet) throws Exception
	{
		this(resultSet, null, null, false, false, null, null, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public RowSet(@NotNull ResultSet resultSet, @Nullable String defaultExternalCatalog, @Nullable String defaultEntity, boolean isAdmin, boolean links, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		String defaultInternalCatalog = SchemaSingleton.externalCatalogToInternalCatalog_noException(defaultExternalCatalog, null);

		m_resultSet = resultSet;
		m_isAdmin = isAdmin;
		m_links = links;

		m_sql = sql != null ? sql : "";
		m_mql = mql != null ? mql : "";
		m_ast = ast != null ? ast : "";

		/*------------------------------------------------------------------------------------------------------------*/
		/* PARSE SQL                                                                                                  */
		/*------------------------------------------------------------------------------------------------------------*/

		Tuple5<Map<QId, QId>, List<Boolean>, Map<QId, QId>, List<Boolean>, Map<QId, QId>> aliasInfo = Tokenizer.extractAliasInfo(m_sql);

		/*------------------------------------------------------------------------------------------------------------*/
		/* GET METADATA                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		/*------------------------------------------------------------------------------------------------------------*/
		/* INITIALIZE DATA STRUCTURES                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		m_numberOfFields = resultSetMetaData.getColumnCount();

		/*------------------------------------------------------------------------------------------------------------*/

		/* From JDBC */

		m_fieldCatalogs = new String[m_numberOfFields];
		m_fieldEntities = new String[m_numberOfFields];
		m_fieldNames = new String[m_numberOfFields];
		m_fieldLabels = new String[m_numberOfFields];
		m_fieldTypes = new String[m_numberOfFields];

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
		/* FILL DATA STRUCTURES                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		QId qId;

		String m_fieldNames_i;

		String externalCatalog, internalCatalog, entity, name, label, type;

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* GET MEDATADA FROM JDBC                                                                                 */
			/*--------------------------------------------------------------------------------------------------------*/

			try { internalCatalog = resultSetMetaData.getCatalogName(i + 1); } catch(Exception e) { internalCatalog = null; }

			try { entity = resultSetMetaData.getTableName(i + 1); } catch(Exception e) { entity = null; }

			try { name = resultSetMetaData.getColumnName(i + 1); } catch(Exception e) { name = null; }

			try { label = resultSetMetaData.getColumnLabel(i + 1); } catch(Exception e) { label = null; }

			try { type = resultSetMetaData.getColumnTypeName(i + 1); } catch(Exception e) { type = null; }

			/*--------------------------------------------------------------------------------------------------------*/
			/* RESOLVE ALIASES IF NEEDED                                                                              */
			/*--------------------------------------------------------------------------------------------------------*/

			if(label != null && !label.isEmpty()
			   &&
			   (
				   internalCatalog == null || internalCatalog.isEmpty()
				   ||
				   /*-*/entity/*-*/ == null || /*-*/entity/*-*/.isEmpty()
			   )
			 ) {
				try
				{
					qId = QId.parseQId(label, QId.Type.FIELD);

					for(Map.Entry<QId, QId> entry : aliasInfo.x.entrySet())
					{
						if(qId.matches(entry.getKey()))
						{
							/**/
							if(entry.getValue().is(QId.MASK_CATALOG_ENTITY_FIELD))
							{
								internalCatalog = entry.getValue().getCatalog();
								entity = entry.getValue().getEntity();
								name = entry.getValue().getField();
							}
							else if(entry.getValue().is(QId.MASK_ENTITY_FIELD))
							{
								entity = entry.getValue().getEntity();
								name = entry.getValue().getField();
							}
							else if(entry.getValue().is(QId.MASK_FIELD))
							{
								name = entry.getValue().getField();
							}

							break;
						}
					}
				}
				catch(Exception e)
				{
					/* IGNORE */
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* RESOLVE EXTERNAL CATALOG IF NEEDED                                                                     */
			/*--------------------------------------------------------------------------------------------------------*/

			if(defaultInternalCatalog != null && !defaultInternalCatalog.isEmpty()
			   &&
			   defaultInternalCatalog.equalsIgnoreCase(internalCatalog)
			 ) {
				/*----------------------------------------------------------------------------------------------------*/
				/* TRIVIAL CASE                                                                                       */
				/*----------------------------------------------------------------------------------------------------*/

				externalCatalog = defaultExternalCatalog;

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if(defaultExternalCatalog != null && !defaultExternalCatalog.isEmpty()
			        &&
			        ((defaultEntity)) != null && !((defaultEntity)).isEmpty()
			 ) {
				/*----------------------------------------------------------------------------------------------------*/
				/* NON-TRIVIAL CASE                                                                                   */
				/*----------------------------------------------------------------------------------------------------*/

				try
				{
					Resolution resolution = AutoJoinSingleton.resolve(defaultExternalCatalog, defaultEntity, new QId(null, entity, name).toString());

					if(internalCatalog == null || internalCatalog.isEmpty() || internalCatalog.equals(resolution.getInternalQId().getCatalog()))
					{
						externalCatalog = resolution.getExternalQId().getCatalog();
						entity = resolution.getExternalQId().getEntity();
						name = resolution.getExternalQId().getField();
					}
					else
					{
						externalCatalog = null;
					}
				}
				catch(Exception e)
				{
					externalCatalog = null;
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				externalCatalog = null;
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* SAVE METADATA TO ROWSET                                                                                */
			/*--------------------------------------------------------------------------------------------------------*/

			m_fieldCatalogs[i] = externalCatalog != null && !externalCatalog.isEmpty() ? externalCatalog : "N/A";
			m_fieldEntities[i] = entity != null && !entity.isEmpty() ? entity : "N/A";
			m_fieldNames[i] = name != null && !name.isEmpty() ? name : "N/A";
			m_fieldLabels[i] = label != null && !label.isEmpty() ? label : "N/A";
			m_fieldTypes[i] = type != null && !type.isEmpty() ? type : "N/A";

			/*--------------------------------------------------------------------------------------------------------*/

			m_fieldNames_i = buildNameOrLabel(
				m_fieldCatalogs[i],
				m_fieldEntities[i],
				m_fieldNames[i]
			);

			/*--------------------------------------------------------------------------------------------------------*/

			if(aliasInfo.y.size() == m_numberOfFields && !aliasInfo.y.get(i)
			   &&
			   (
				   defaultExternalCatalog != null && !defaultExternalCatalog.equalsIgnoreCase(m_fieldCatalogs[i])
				   ||
				   /*-*/defaultEntity/*-*/ != null && !/*-*/defaultEntity/*-*/.equalsIgnoreCase(m_fieldEntities[i])
			   )
			 ) {
				m_fieldLabels[i] = m_fieldNames_i;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			try
			{
				SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(m_fieldCatalogs[i], m_fieldEntities[i], m_fieldNames[i]);

				/**/

				m_fieldTypes[i] = column.type;

				/**/

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
				m_fieldRank[i] = i;
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

			/*--------------------------------------------------------------------------------------------------------*/

			if("self".equals(defaultExternalCatalog))
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

			/*--------------------------------------------------------------------------------------------------------*/

			if("ORACLE_ROWNUM".equals(m_fieldNames[i]))
			{
				m_fieldHidden[i] = true;
				m_fieldAutomatic[i] = true;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			m_nameIndices.put(m_fieldNames_i, i);
			m_labelIndices.put(m_fieldLabels[i], i);

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public ResultSet getResultSet()
	{
		return m_resultSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getSQL()
	{
		return m_sql;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getMQL()
	{
		return m_mql;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getAST()
	{
		return m_ast;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isAnEntity(String entityName)
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

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isANameOrLabel(String name)
	{
		return m_nameIndices.containsKey(name)
		       ||
		       m_labelIndices.containsKey(name)
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public int getNumberOfFields()
	{
		return m_numberOfFields;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getCatalogOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldCatalogs[fieldIndex];
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getEntityOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldEntities[fieldIndex];
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getNameOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldNames[fieldIndex];
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getLabelOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldLabels[fieldIndex];
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getTypeOfField(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_fieldTypes[fieldIndex];
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String formatTimestamp(@NotNull java.sql.Timestamp timestamp)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder().append(m_datetimeFormat.format(timestamp));

		/*------------------------------------------------------------------------------------------------------------*/

		final int precision = ConfigSingleton.getProperty("timestamp_precision", 6);

		/*------------------------------------------------------------------------------------------------------------*/

		if(precision >= 1 && precision <= 9)
		{
			String ms = String.valueOf(Math.ceil(timestamp.getNanos() / Math.pow(10, 9 - precision)));

			String pad = "0".repeat(Math.max(0, precision - ms.length()));

			result.append(".").append(pad).append(ms);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	protected String[] getCurrentRow() throws SQLException
	{
		java.util.Date date;

		String[] result = new String[m_numberOfFields];

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			/**/ if(m_fieldTypes[i].toUpperCase().startsWith("TIMESTAMP")
			        ||
			        m_fieldTypes[i].toUpperCase().startsWith("DATETIME")
			 ) {
				/*----------------------------------------------------------------------------------------------------*/
				/* TIMESTAMP & DATETIME                                                                               */
				/*----------------------------------------------------------------------------------------------------*/

				date = m_resultSet.getTimestamp(i + 1);

				result[i] = (date != null) ? formatTimestamp((java.sql.Timestamp) date)
				                           : m_resultSet.getString(i + 1)
				;

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("DATE".equalsIgnoreCase(m_fieldTypes[i]))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* DATE                                                                                               */
				/*----------------------------------------------------------------------------------------------------*/

				date = m_resultSet.getDate(i + 1);

				result[i] = (date != null) ? m_dateFormat.format(date)
				                           : m_resultSet.getString(i + 1)
				;

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("TIME".equalsIgnoreCase(m_fieldTypes[i]))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* TIME                                                                                               */
				/*----------------------------------------------------------------------------------------------------*/

				date = m_resultSet.getTime(i + 1);

				result[i] = (date != null) ? m_timeFormat.format(date)
				                           : m_resultSet.getString(i + 1)
				;

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* DEFAULT                                                                                            */
				/*----------------------------------------------------------------------------------------------------*/

				result[i] = m_resultSet.getString(i + 1);

				/*----------------------------------------------------------------------------------------------------*/
			}

			/*--------------------------------------------------------------------------------------------------------*/

			if(m_fieldAdminOnly[i] || m_fieldCrypted[i])
			{
				if(m_isAdmin)
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

			/*--------------------------------------------------------------------------------------------------------*/
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	protected String processWebLink(int fieldIndex, @NotNull Row row)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	protected void setTruncated() throws Exception
	{
		if(m_lock)
		{
			throw new Exception("rowset already read");
		}

		m_truncated = true;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isTruncated()
	{
		return m_truncated;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected void setLocked() throws Exception
	{
		if(m_lock)
		{
			throw new Exception("rowset already read");
		}

		m_lock = true;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isLocked()
	{
		return m_lock;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public RowSetIterable iterate() throws Exception
	{
		return new RowSetIterable(this);
	}

	@NotNull
	public RowSetIterable iterate(int limit, int offset) throws Exception
	{
		return new RowSetIterable(this, limit, offset);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public List<Row> getAll() throws Exception
	{
		return RowSetIterable.getAll(this);
	}

	@NotNull
	public List<Row> getAll(int limit, int offset) throws Exception
	{
		return RowSetIterable.getAll(this, limit, offset);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder() throws Exception
	{
		return RowSetIterable.getStringBuilder(this);
	}

	@NotNull
	public StringBuilder toStringBuilder(@Nullable String type) throws Exception
	{
		return RowSetIterable.getStringBuilder(this, type);
	}

	@NotNull
	public StringBuilder toStringBuilder(@Nullable String type, @Nullable Integer totalNumberOfRows) throws Exception
	{
		return RowSetIterable.getStringBuilder(this, type, totalNumberOfRows);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
