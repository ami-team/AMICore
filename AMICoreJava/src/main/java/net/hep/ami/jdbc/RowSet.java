package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.jdbc.reflexion.*;

import org.jetbrains.annotations.*;

public class RowSet
{
	/*----------------------------------------------------------------------------------------------------------------*/

	protected final ResultSet m_resultSet;

	protected final int m_flags;

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
	protected final boolean[] m_fieldHidden;
	protected final boolean[] m_fieldAdminOnly;
	protected final boolean[] m_fieldCrypted;
	protected final boolean[] m_fieldPrimary;
	protected final boolean[] m_fieldJson;
	protected final boolean[] m_fieldAutomatic;
	protected final boolean[] m_fieldCreated;
	protected final boolean[] m_fieldCreatedBy;
	protected final boolean[] m_fieldModified;
	protected final boolean[] m_fieldModifiedBy;
	/**/
	protected final boolean[] m_fieldStatable;
	protected final boolean[] m_fieldGroupable;
	/**/
	protected final String[] m_fieldWebLinkScript;
	/**/
	protected final boolean[] m_fieldMedia;
	protected final boolean[] m_fieldBase64;
	protected final String[] m_fieldMIME;
	protected final String[] m_fieldCtrl;
	/**/
	protected final String[] m_fieldDescription;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected final Map<String, Integer> m_nameIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);
	protected final Map<String, Integer> m_labelIndices = new AMIMap<>(AMIMap.Type.HASH_MAP, false, true);

	/*----------------------------------------------------------------------------------------------------------------*/

	private final DateTime m_amiDateTime = new DateTime();

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
		this(resultSet, null, null, 0x00, null, null, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public RowSet(@NotNull ResultSet resultSet, @Nullable String defaultExternalCatalog, @Nullable String defaultEntity, int flags, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		String defaultInternalCatalog = SchemaSingleton.externalCatalogToInternalCatalog_noException(defaultExternalCatalog, null);

		m_resultSet = resultSet;

		m_flags = flags;

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
		m_fieldHidden = new boolean[m_numberOfFields];
		m_fieldAdminOnly = new boolean[m_numberOfFields];
		m_fieldCrypted = new boolean[m_numberOfFields];
		m_fieldPrimary = new boolean[m_numberOfFields];
		m_fieldJson = new boolean[m_numberOfFields];
		m_fieldAutomatic = new boolean[m_numberOfFields];
		m_fieldCreated = new boolean[m_numberOfFields];
		m_fieldCreatedBy = new boolean[m_numberOfFields];
		m_fieldModified = new boolean[m_numberOfFields];
		m_fieldModifiedBy = new boolean[m_numberOfFields];
		/**/
		m_fieldStatable = new boolean[m_numberOfFields];
		m_fieldGroupable = new boolean[m_numberOfFields];
		/**/
		m_fieldWebLinkScript = new String[m_numberOfFields];
		/**/
		m_fieldMedia = new boolean[m_numberOfFields];
		m_fieldBase64 = new boolean[m_numberOfFields];
		m_fieldMIME = new String[m_numberOfFields];
		m_fieldCtrl = new String[m_numberOfFields];
		/**/
		m_fieldDescription = new String[m_numberOfFields];

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

			try { type = SchemaSingleton.Column.jdbcTypesToAMITypes(
			      	resultSetMetaData.getColumnTypeName(i + 1),
			      	resultSetMetaData.getColumnType(i + 1),
					resultSetMetaData.getScale(i + 1)
			      );
			} catch(Exception e) { type = null; }

			/*--------------------------------------------------------------------------------------------------------*/
			/* RESOLVE ALIASES IF NEEDED                                                                              */
			/*--------------------------------------------------------------------------------------------------------*/

			if(!Empty.is(label, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY)
			   &&
			   (
				   Empty.is(internalCatalog, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY)
				   ||
				   Empty.is(/*-*/entity/*-*/, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY)
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

			/**/ if(!Empty.is(defaultInternalCatalog, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) && defaultInternalCatalog.equalsIgnoreCase(internalCatalog))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* TRIVIAL CASE                                                                                       */
				/*----------------------------------------------------------------------------------------------------*/

				externalCatalog = defaultExternalCatalog;

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if(!Empty.is(defaultExternalCatalog, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) && !Empty.is(defaultEntity, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* NON-TRIVIAL CASE                                                                                   */
				/*----------------------------------------------------------------------------------------------------*/

				try
				{
					Resolution resolution = AutoJoinSingleton.resolve(defaultExternalCatalog, defaultEntity, new QId(null, entity, name).toString());

					if(Empty.is(internalCatalog, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) || internalCatalog.equals(resolution.getInternalQId().getCatalog()))
					{
						externalCatalog = resolution.getExternalQId().getCatalog();
						entity = resolution.getExternalQId().getEntity();
						name = resolution.getExternalQId().getField();
					}
					else
					{
						externalCatalog = defaultExternalCatalog;
					}
				}
				catch(Exception e)
				{
					externalCatalog = defaultExternalCatalog;
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else
			{
				externalCatalog = defaultExternalCatalog;
			}

			/*--------------------------------------------------------------------------------------------------------*/
			/* SAVE METADATA TO ROWSET                                                                                */
			/*--------------------------------------------------------------------------------------------------------*/

			m_fieldCatalogs[i] = !Empty.is(externalCatalog, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) ? externalCatalog : "N/A";
			m_fieldEntities[i] = !Empty.is(entity, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) ? entity : "N/A";
			m_fieldNames[i] = !Empty.is(name, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) ? name : "N/A";
			m_fieldLabels[i] = !Empty.is(label, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) ? label : "N/A";
			m_fieldTypes[i] = !Empty.is(type, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) ? type : "N/A";

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
			    	!Empty.is(defaultExternalCatalog, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) && !defaultExternalCatalog.equalsIgnoreCase(m_fieldCatalogs[i])
			    	||
			    	!Empty.is(/*-*/defaultEntity/*-*/, Empty.STRING_JAVA_NULL | Empty.STRING_EMPTY) && !/*-*/defaultEntity/*-*/.equalsIgnoreCase(m_fieldEntities[i])
			   )
			 ) {
				m_fieldLabels[i] = m_fieldNames_i;
			}

			/*--------------------------------------------------------------------------------------------------------*/

			try
			{
				SchemaSingleton.Column column = SchemaSingleton.getFieldInfo(m_fieldCatalogs[i], m_fieldEntities[i], m_fieldNames[i]);

				m_fieldTypes[i] = column.type;
				/**/
				m_fieldHidden[i] = column.hidden;
				m_fieldAdminOnly[i] = column.adminOnly;
				m_fieldCrypted[i] = column.crypted;
				m_fieldPrimary[i] = column.primary;
				m_fieldJson[i] = column.json;
				m_fieldAutomatic[i] = column.automatic;
				m_fieldCreated[i] = column.created;
				m_fieldCreatedBy[i] = column.createdBy;
				m_fieldModified[i] = column.modified;
				m_fieldModifiedBy[i] = column.modifiedBy;
				/**/
				m_fieldStatable[i] = column.statable;
				m_fieldGroupable[i] = column.groupable;
				/**/
				m_fieldWebLinkScript[i] = column.webLinkScript;
				/**/
				m_fieldMedia[i] = column.media;
				m_fieldBase64[i] = column.base64;
				m_fieldMIME[i] = column.mime;
				m_fieldCtrl[i] = column.ctrl;
				/**/
				m_fieldDescription[i] = column.description;
			}
			catch(Exception e)
			{
				m_fieldHidden[i] = false;
				m_fieldAdminOnly[i] = false;
				m_fieldCrypted[i] = false;
				m_fieldPrimary[i] = false;
				m_fieldJson[i] = false;
				m_fieldAutomatic[i] = false;
				m_fieldCreated[i] = false;
				m_fieldCreatedBy[i] = false;
				m_fieldModified[i] = false;
				m_fieldModifiedBy[i] = false;
				/**/
				m_fieldStatable[i] = false;
				m_fieldGroupable[i] = false;
				/**/
				m_fieldWebLinkScript[i] = "@NULL";
				/**/
				m_fieldMedia[i] = false;
				m_fieldBase64[i] = false;
				m_fieldMIME[i] = "@NULL";
				m_fieldCtrl[i] = "@NULL";
				/**/
				m_fieldDescription[i] = "N/A";
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

	public boolean isACatalog(@Nullable String catalogName)
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

	public boolean isAnEntity(@Nullable String entityName)
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

	public boolean isAField(@Nullable String fieldName)
	{
		for(String name: m_fieldNames)
		{
			if(name.equalsIgnoreCase(fieldName))
			{
				return true;
			}
		}

		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isALabel(@Nullable String fieldLabel)
	{
		for(String label: m_fieldLabels)
		{
			if(label.equalsIgnoreCase(fieldLabel))
			{
				return true;
			}
		}

		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isAType(@Nullable String fieldType)
	{
		for(String type: m_fieldTypes)
		{
			if(type.equalsIgnoreCase(fieldType))
			{
				return true;
			}
		}

		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public boolean isANameOrLabel(@Nullable String name)
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
	protected String[] getCurrentRow() throws SQLException
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String[] result = new String[m_numberOfFields];

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < m_numberOfFields; i++)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			/**/ if("TIMESTAMP".equals(m_fieldTypes[i]))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* TIMESTAMP & DATETIME                                                                               */
				/*----------------------------------------------------------------------------------------------------*/

				try
				{
					java.sql.Timestamp timestamp = m_resultSet.getTimestamp(i + 1);

					result[i] = (timestamp != null) ? m_amiDateTime.formatTimestamp(timestamp)
					                                : m_resultSet.getString(i + 1)
					;
				}
				catch(SQLException e)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("DATE".equals(m_fieldTypes[i]))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* DATE                                                                                               */
				/*----------------------------------------------------------------------------------------------------*/

				try
				{
					java.sql.Date date = m_resultSet.getDate(i + 1);

					result[i] = (date != null) ? m_amiDateTime.formatDate(date)
					                           : m_resultSet.getString(i + 1)
					;
				}
				catch(SQLException e)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
			else if("TIME".equals(m_fieldTypes[i]))
			{
				/*----------------------------------------------------------------------------------------------------*/
				/* TIME                                                                                               */
				/*----------------------------------------------------------------------------------------------------*/

				try
				{
					java.sql.Time time = m_resultSet.getTime(i + 1);

					result[i] = (time != null) ? m_amiDateTime.formatTime(time)
					                           : m_resultSet.getString(i + 1)
					;
				}
				catch(SQLException e)
				{
					result[i] = m_resultSet.getString(i + 1);
				}

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
				if((m_flags & Querier.FLAG_ADMIN) != 0)
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

						if((m_flags & Querier.FLAG_HIDE_BIG_CONTENT) != 0 && result[i].length() > ConfigSingleton.getProperty("max_value_size", 4096))
						{
							result[i] = /*---------*/ "@LONG" /*---------*/;
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
				else
				{
					if((m_flags & Querier.FLAG_HIDE_BIG_CONTENT) != 0 && result[i].length() > ConfigSingleton.getProperty("max_value_size", 4096))
					{
						result[i] = /*---------*/ "@LONG" /*---------*/;
					}
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
		if((m_flags & Querier.FLAG_SHOW_LINKS) != 0)
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
