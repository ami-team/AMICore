package net.hep.ami.jdbc;

import java.sql.*;
import java.util.*;

public class Iterable implements java.lang.Iterable<Row>
{
	/*---------------------------------------------------------------------*/

	private RowSet m_rowSet;

	/*---------------------------------------------------------------------*/

	protected Iterable(RowSet rowSet) throws Exception
	{
		m_rowSet = rowSet;
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
					return m_rowSet.m_resultSet.next();
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
