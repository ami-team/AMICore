package net.hep.ami.jdbc;

import java.util.*;
import java.util.regex.*;

import net.hep.ami.*;
import net.hep.ami.utility.parser.*;

public final class RowSetIterable implements Iterable<Row>
{
	/*---------------------------------------------------------------------*/

	private static final Pattern s_numberPattern = Pattern.compile(".*(?:INT|FLOAT|DOUBLE|SERIAL|DECIMAL|NUMERIC).*", Pattern.CASE_INSENSITIVE);

	/*---------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	/*---------------------------------------------------------------------*/

	private final int m_limit;
	private final int m_offset;

	private int m_i;

	/*---------------------------------------------------------------------*/

	protected RowSetIterable(RowSet rowSet) throws Exception
	{
		this(rowSet, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	protected RowSetIterable(RowSet rowSet, int limit, int offset) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if((m_rowSet = rowSet) == null)
		{
			throw new NullPointerException();
		}

		rowSet.lock();

		/*-----------------------------------------------------------------*/

		m_limit = limit;
		m_offset = offset;

		m_i = 0;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Iterator<Row> iterator()
	{
		/*-----------------------------------------------------------------*/

		try
		{
			while(m_i++ < m_offset && m_rowSet.m_resultSet.next()) { /* DO NOTHING  */ }
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		/*-----------------------------------------------------------------*/

		return new Iterator<Row>()
		{
			/*-------------------------------------------------------------*/

			private boolean m_hasNext = false;

			/*-------------------------------------------------------------*/

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

			/*-------------------------------------------------------------*/

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

			/*-------------------------------------------------------------*/
		};

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static List<Row> getAll(RowSet rowSet) throws Exception
	{
		return getAll(rowSet, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public static List<Row> getAll(RowSet rowSet, int limit, int offset) throws Exception
	{
		rowSet.lock();

		List<Row> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 10000);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < offset && rowSet.m_resultSet.next(); i++)
		{ /* DO NOTHING */ }
		for(int i = 0; i < limit && rowSet.m_resultSet.next(); i++)
		{
			if(maxNumberOfRows == 0)
			{
				break;
			}

			maxNumberOfRows--;

			result.add(new Row(rowSet));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStringBuilder(RowSet rowSet) throws Exception
	{
		return getStringBuilder(rowSet, null, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStringBuilder(RowSet rowSet, @Nullable String type) throws Exception
	{
		return getStringBuilder(rowSet, type, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStringBuilder(RowSet rowSet, @Nullable String type, int limit, int offset) throws Exception
	{
		rowSet.lock();

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/
		/* DESCRIPTIONS                                                    */
		/*-----------------------------------------------------------------*/

		boolean q;
		boolean statable;
		boolean groupable;

		StringBuilder descrs = new StringBuilder();

		for(int i = 0; i < rowSet.m_numberOfFields; i++)
		{
			q = "N/A".equals(rowSet.m_fieldEntities[i]) == false;

			statable = q && s_numberPattern.matcher(rowSet.m_fieldTypes[i]).matches();

			groupable = q /* TODO */;

			descrs.append("<fieldDescription catalog=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldCatalogs[i]))
			      .append("\" entity=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldEntities[i]))
			      .append("\" field=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldNames[i]))
			      .append("\" label=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldLabels[i]))
			      .append("\" type=\"")
			      .append(Utility.escapeHTML(rowSet.m_fieldTypes[i]))
			      .append("\" statable=\"")
			      .append(statable ? "true" : "false")
			      .append("\" groupable=\"")
			      .append(groupable ? "true" : "false")
			      .append("\"><![CDATA[")
			      .append("N/A")
			      .append("]]></fieldDescription>")
			;
		}

		/*-----------------------------------------------------------------*/
		/* ROWS                                                            */
		/*-----------------------------------------------------------------*/

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 10000);

		/*-----------------------------------------------------------------*/

		boolean isComplet = true;

		StringBuilder rows = new StringBuilder().append("<sql><![CDATA[").append(rowSet.m_sql).append("]]></sql>")
		                                        .append("<mql><![CDATA[").append(rowSet.m_mql).append("]]></mql>")
		                                        .append("<ast><![CDATA[").append(rowSet.m_ast).append("]]></ast>")
		;

		for(int i = 0; i < offset && rowSet.m_resultSet.next(); i++)
		{ /* DO NOTHING */ }
		for(int i = 0; i < limit && rowSet.m_resultSet.next(); i++)
		{
			if(maxNumberOfRows == 0)
			{
				isComplet = false;

				break;
			}

			maxNumberOfRows--;

			rows.append(new Row(rowSet).toStringBuilder());
		}

		/*-----------------------------------------------------------------*/
		/* RESULT                                                          */
		/*-----------------------------------------------------------------*/

		if(type == null)
		{
			result.append("<fieldDescriptions>");
		}
		else
		{
			result.append("<fieldDescriptions rowset=\"").append(Utility.escapeHTML(type)).append("\">");
		}

		result.append(descrs)
		      .append("</fieldDescriptions>")
		;

		/*-----------------------------------------------------------------*/

		if(type == null)
		{
			result.append("<rowset complet=\"").append(isComplet).append("\">");
		}
		else
		{
			result.append("<rowset type=\"").append(Utility.escapeHTML(type)).append("\" complet=\"").append(isComplet).append("\">");
		}

		result.append(rows)
		      .append("</rowset>")
		;

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
