package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

public class Iterable implements java.lang.Iterable<Row>
{
	/*---------------------------------------------------------------------*/

	private RowSet m_rowSet;

	/*---------------------------------------------------------------------*/

	private int m_i;
	private int m_limit;
	private int m_offset;

	/*---------------------------------------------------------------------*/

	protected Iterable(RowSet rowSet) throws Exception
	{
		/*-----------------------------------------------------------------*/

		m_rowSet = rowSet;

		/*-----------------------------------------------------------------*/

		m_i = 0;
		m_limit = Integer.MAX_VALUE;
		m_offset = 0x0000000000000000;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	protected Iterable(RowSet rowSet, int limit, int offset) throws Exception
	{
		/*-----------------------------------------------------------------*/

		m_rowSet = rowSet;

		/*-----------------------------------------------------------------*/

		m_i = 0;
		m_limit = limit;
		m_offset = offset;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Iterator<Row> iterator()
	{
		/*-----------------------------------------------------------------*/

		try
		{
			m_rowSet.m_resultSet.beforeFirst();
		}
		catch(SQLException e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/

		try
		{
			while(m_i++ < m_offset && m_rowSet.m_resultSet.next());
		}
		catch(SQLException e)
		{
			throw new RuntimeException(e.getMessage());
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
					return m_i++ < m_limit && m_rowSet.m_resultSet.next();
				}
				catch(SQLException e)
				{
					throw new RuntimeException(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/

			@Override
			public Row next()
			{
				try
				{
					return new Row(m_rowSet, m_rowSet.getCurrentValue());
				}
				catch(SQLException e)
				{
					throw new RuntimeException(e.getMessage());
				}
			}

			/*-------------------------------------------------------------*/
		};

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
