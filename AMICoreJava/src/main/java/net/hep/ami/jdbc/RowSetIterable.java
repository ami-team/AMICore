package net.hep.ami.jdbc;

import java.util.*;

import net.hep.ami.*;

public final class RowSetIterable implements Iterable<Row>
{
	/*---------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	/*---------------------------------------------------------------------*/

	private final int m_limit;
	private final int m_offset;

	/*---------------------------------------------------------------------*/

	private int m_i;

	private boolean m_hasNext;

	/*---------------------------------------------------------------------*/

	protected RowSetIterable(RowSet rowSet) throws Exception
	{
		this(rowSet, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	protected RowSetIterable(RowSet rowSet, int limit, int offset) throws Exception
	{
		rowSet.lock();

		/*-----------------------------------------------------------------*/

		m_rowSet = rowSet;

		/*-----------------------------------------------------------------*/

		m_limit = limit;
		m_offset = offset;

		/*-----------------------------------------------------------------*/

		m_i = 0;

		m_hasNext = false;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Iterator<Row> iterator()
	{
		/*-----------------------------------------------------------------*/

		try
		{
			while(m_i++ < m_offset && m_rowSet.m_resultSet.next());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
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
				/*---------------------------------------------------------*/

				if(m_hasNext == false)
				{
					throw new NoSuchElementException();
				}

				/*---------------------------------------------------------*/

				try
				{
					return new Row(m_rowSet);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}

				/*---------------------------------------------------------*/
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

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < offset && rowSet.m_resultSet.next(); i++)
		{ }
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

		if(type == null)
		{
			result.append("<rowset>");
		}
		else
		{
			result.append("<rowset type=\"" + type + "\">");
		}

		/*-----------------------------------------------------------------*/

		result.append("<sql><![CDATA[").append(rowSet.m_sql).append("]]></sql>")
		      .append("<mql><![CDATA[").append(rowSet.m_mql).append("]]></mql>")
		      .append("<ast><![CDATA[").append(rowSet.m_ast).append("]]></ast>")
		;

		/*-----------------------------------------------------------------*/

		int maxNumberOfRows = ConfigSingleton.getProperty("max_number_of_rows", 1000);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < offset && rowSet.m_resultSet.next(); i++)
		{ }
		for(int i = 0; i < limit && rowSet.m_resultSet.next(); i++)
		{
			if(maxNumberOfRows == 0)
			{
				break;
			}

			maxNumberOfRows--;

			result.append(new Row(rowSet).toStringBuilder());
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
