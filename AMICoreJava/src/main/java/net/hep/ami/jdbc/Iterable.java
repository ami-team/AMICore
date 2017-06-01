package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;

public final class Iterable implements java.lang.Iterable<Row>
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

	protected Iterable(RowSet rowSet) throws Exception
	{
		rowSet.lock();

		/*-----------------------------------------------------------------*/

		m_rowSet = rowSet;

		/*-----------------------------------------------------------------*/

		m_limit = Integer.MAX_VALUE;
		m_offset = 0x0000000000000000;

		/*-----------------------------------------------------------------*/

		m_i = 0;

		m_hasNext = false;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	protected Iterable(RowSet rowSet, int limit, int offset) throws Exception
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
		catch(SQLException e)
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
				catch(SQLException e)
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
					return new Row(m_rowSet, m_rowSet.getCurrentValue());
				}
				catch(SQLException e)
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

			result.add(new Row(rowSet, rowSet.getCurrentValue()));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStringBuilder(RowSet rowSet) throws Exception
	{
		return getStringBuffer(rowSet, Integer.MAX_VALUE, 0);
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStringBuffer(RowSet rowSet, int limit, int offset) throws Exception
	{
		rowSet.lock();

		StringBuilder result = new StringBuilder();

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

			result.append(new Row(rowSet, rowSet.getCurrentValue()).toStringBuilder());
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
