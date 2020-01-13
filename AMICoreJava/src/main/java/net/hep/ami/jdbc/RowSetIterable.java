package net.hep.ami.jdbc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@SuppressWarnings("StatementWithEmptyBody")
public final class RowSetIterable implements Iterable<Row>
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final int m_limit;
	private final int m_offset;

	/*----------------------------------------------------------------------------------------------------------------*/

	private int m_i;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected RowSetIterable(@NotNull RowSet rowSet) throws Exception
	{
		this(rowSet, Integer.MAX_VALUE, 0);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	protected RowSetIterable(@NotNull RowSet rowSet, int limit, int offset) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		rowSet.setLocked();

		/*------------------------------------------------------------------------------------------------------------*/

		m_rowSet = rowSet;

		m_limit = limit;
		m_offset = offset;

		m_i = 0;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public Iterator<Row> iterator()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			while(m_i++ < m_offset && m_rowSet.m_resultSet.next()) { /* DO NOTHING */ }
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new Iterator<>()
		{
			/*--------------------------------------------------------------------------------------------------------*/

			private boolean m_hasNext = false;

			/*--------------------------------------------------------------------------------------------------------*/

			@Override
			public boolean hasNext()
			{
				try
				{
					return m_hasNext = (m_i++ < m_limit && m_rowSet.m_resultSet.next());
				}
				catch(Exception e)
				{
					m_hasNext = false;

					throw new RuntimeException(e);
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			@NotNull
			@Override
			public Row next()
			{
				if(m_hasNext)
				{
					try
					{
						return new Row(m_rowSet);
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}

				throw new NoSuchElementException();
			}

			/*--------------------------------------------------------------------------------------------------------*/
		};

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<Row> getAll(@NotNull RowSet rowSet) throws Exception
	{
		return getAll(rowSet, Integer.MAX_VALUE, 0);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static List<Row> getAll(@NotNull RowSet rowSet, int limit, int offset) throws Exception
	{
		rowSet.setLocked();

		List<Row> result = new ArrayList<>();

		/*------------------------------------------------------------------------------------------------------------*/

		int nb = ConfigSingleton.getProperty("max_number_of_rows", 10000);

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < offset && rowSet.m_resultSet.next(); i++)
		{ /* DO NOTHING */ }
		for(int i = 0; i < limit && rowSet.m_resultSet.next(); i++)
		{
			if(nb == 0)
			{
				rowSet.setTruncated();

				break;
			}

			nb--;

			result.add(new Row(rowSet));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder getStringBuilder(@NotNull RowSet rowSet) throws Exception
	{
		return getStringBuilder(rowSet, null, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder getStringBuilder(@NotNull RowSet rowSet, @Nullable String type) throws Exception
	{
		return getStringBuilder(rowSet, type, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder getStringBuilder(@NotNull RowSet rowSet, @Nullable String type, @Nullable Integer totalNumberOfRows) throws Exception
	{
		AMIStringBuilder result = new AMIStringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
		/* FIELD DESCRIPTIONS                                                                                         */
		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder descrs = new StringBuilder();

		for(int i = 0; i < rowSet.m_numberOfFields; i++)
		{
			descrs.append("<fieldDescription catalog=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldCatalogs[i]))
			      .append("\" entity=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldEntities[i]))
			      .append("\" field=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldNames[i]))
			      .append("\" label=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldLabels[i]))
			      .append("\" type=")
			      .append(rowSet.m_fieldTypes[i])
			      .append("\" hidden=")
			      .append(rowSet.m_fieldHidden[i] ? "true" : "false")
			      .append("\" adminOnly=\"")
			      .append(rowSet.m_fieldAdminOnly[i] ? "true" : "false")
			      .append("\" crypted=\"")
			      .append(rowSet.m_fieldCrypted[i] ? "true" : "false")
			      .append("\" primary=\"")
			      .append(rowSet.m_fieldPrimary[i] ? "true" : "false")
			      .append("\" readable=\"")
			      .append(rowSet.m_fieldReadable[i] ? "true" : "false")
			      .append("\" automatic=\"")
			      .append(rowSet.m_fieldAutomatic[i] ? "true" : "false")
			      .append("\" created=\"")
			      .append(rowSet.m_fieldCreated[i] ? "true" : "false")
			      .append("\" createdBy=\"")
			      .append(rowSet.m_fieldCreatedBy[i] ? "true" : "false")
			      .append("\" modified=\"")
			      .append(rowSet.m_fieldModified[i] ? "true" : "false")
			      .append("\" modifiedBy=\"")
			      .append(rowSet.m_fieldModifiedBy[i] ? "true" : "false")
			      .append("\" statable=\"")
			      .append(rowSet.m_fieldStatable[i] ? "true" : "false")
			      .append("\" groupable=\"")
			      .append(rowSet.m_fieldGroupable[i] ? "true" : "false")
			      .append("\" displayable=\"")
			      .append(rowSet.m_fieldDisplayable[i] ? "true" : "false")
			      .append("\" base64=\"")
			      .append(rowSet.m_fieldBase64[i] ? "true" : "false")
			      .append("\" mime=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldMIME[i]))
			      .append("\" ctrl=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldCtrl[i]))
			      .append("\"><![CDATA[")
			      .append(rowSet.m_fieldDescription[i])
			      .append("]]></fieldDescription>")
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ROWSET                                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		final int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 10000);

		/*-----------------------------------------------------------------*/

		StringBuilder rows = new StringBuilder().append("<sql><![CDATA[").append(rowSet.m_sql).append("]]></sql>")
		                                        .append("<mql><![CDATA[").append(rowSet.m_mql).append("]]></mql>")
		                                        .append("<ast><![CDATA[").append(rowSet.m_ast).append("]]></ast>")
		;

		for(int nb = maxNumberOfRows; rowSet.m_resultSet.next(); nb--)
		{
			if(nb > 0)
			{
				rows.append(new Row(rowSet).toStringBuilder());
			}
			else
			{
				rowSet.setTruncated();

				break;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* RESULT                                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		rowSet.setLocked();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<fieldDescriptions").appendIf(type != null, " rowset=\"", Utility.escapeHTML(type), "\"").append(">")
		      .append(descrs)
		      .append("</fieldDescriptions>")

		      .append("<rowset").appendIf(type != null, " type=\"", Utility.escapeHTML(type), "\"").append(" truncated=\"").append(rowSet.isTruncated()).append("\" maxNumberOfRows=\"").append(maxNumberOfRows).append("\"").appendIf(totalNumberOfRows != null, " totalNumberOfRows=\"", totalNumberOfRows, "\"").append(">")
		      .append(rows)
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toStringBuilder();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
