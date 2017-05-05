package net.hep.ami.jdbc.driver;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.mql.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.driver.annotation.*;

public abstract class AbstractDriver implements Querier
{
	/*---------------------------------------------------------------------*/

	protected Jdbc.Type m_jdbcType;
	protected String m_jdbcProto;
	protected String m_jdbcClass;
	protected String m_jdbcUrl;
	protected String m_user;
	protected String m_pass;

	/*---------------------------------------------------------------------*/

	private Connection m_connection;

	private final Map<String, Statement> m_statementMap = new HashMap<>();

	/*---------------------------------------------------------------------*/

	protected String m_internalCatalog;
	protected String m_externalCatalog;

	/*---------------------------------------------------------------------*/

	public AbstractDriver(@Nullable String catalog, String jdbcUrl, String user, String pass) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET ANNOTATION                                                  */
		/*-----------------------------------------------------------------*/

		Jdbc annotation = getClass().getAnnotation(Jdbc.class);

		if(annotation == null)
		{
			throw new Exception("annotation `Jdbc` not found for driver `" + getClass().getName() + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CREATE CONNECTION                                               */
		/*-----------------------------------------------------------------*/

		m_jdbcType = annotation.type();
		m_jdbcProto = annotation.proto();
		m_jdbcClass = annotation.clazz();
		m_jdbcUrl = jdbcUrl;
		m_user = user;
		m_pass = pass;

		m_connection = ConnectionPoolSingleton.getConnection(
			catalog,
			m_jdbcClass,
			m_jdbcUrl,
			m_user,
			m_pass
		);

		/*-----------------------------------------------------------------*/
		/* CREATE STATEMENT                                                */
		/*-----------------------------------------------------------------*/

		m_statementMap.put("@", m_connection.createStatement());

		/*-----------------------------------------------------------------*/
		/* GET CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		boolean internalCatalogFound = true;

		try
		{
			m_internalCatalog = m_connection.getCatalog();

			if(m_internalCatalog == null)
			{
				m_internalCatalog = catalog;
				internalCatalogFound = false;
			}
		}
		catch(Exception e)
		{
			m_internalCatalog = catalog;
			internalCatalogFound = false;
		}

		/*-----------------------------------------------------------------*/

		if(internalCatalogFound)
		{
			try
			{
				m_externalCatalog = SchemaSingleton.internalCatalogToExternalCatalog(m_internalCatalog);
			}
			catch(Exception e)
			{
				m_externalCatalog = catalog;
			}
		}
		else
		{
			m_externalCatalog = catalog;
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static class FieldType
	{
		String name;
		Integer size;
		Integer digits;

		public FieldType(String _name, int _size, int _digits)
		{
			name = _name;
			size = _size;
			digits = _digits;
		}
	}

	/*---------------------------------------------------------------------*/

	public abstract FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract String patch(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	private void _checkMQL() throws Exception
	{
		if(m_jdbcType != Jdbc.Type.SQL)
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeQuery(String sql) throws Exception
	{
		try
		{
			String SQL = patch(sql);

			return new RowSet(m_statementMap.get("@").executeQuery(SQL), sql, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
	{
		_checkMQL();

		try
		{
			String sql = parser.parse(mql, this.m_externalCatalog), SQL = patch(sql);

			return new RowSet(m_statementMap.get("@").executeQuery(SQL), sql, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		try
		{
			String SQL = patch(sql);

			return m_statementMap.get("@").executeUpdate(SQL);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql) throws Exception
	{
		try
		{
			String SQL = patch(sql);

			PreparedStatement result = (PreparedStatement) m_statementMap.get(SQL);

			if(result == null)
			{
				m_statementMap.put(SQL, result = m_connection.prepareStatement(SQL));
			}

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql, String[] columnNames) throws Exception
	{
		try
		{
			String SQL = patch(sql);

			PreparedStatement result = (PreparedStatement) m_statementMap.get(SQL);

			if(result == null)
			{
				m_statementMap.put(SQL, result = m_connection.prepareStatement(SQL, columnNames));
			}

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql, e);
		}
	}

	/*---------------------------------------------------------------------*/

	public void commit() throws Exception
	{
		if(m_connection.getAutoCommit() == false)
		{
			m_connection.commit();
		}
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception
	{
		if(m_connection.getAutoCommit() == false)
		{
			m_connection.rollback();
		}
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception
	{
		try
		{
			if(m_connection.getAutoCommit() == false)
			{
				m_connection.commit();
			}
		}
		finally
		{
			for(Statement statement: m_statementMap.values())
			{
				try
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
				catch(SQLException e)
				{
					/* IGNORE */
				}
			}

			m_connection.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception
	{
		try
		{
			if(m_connection.getAutoCommit() == false)
			{
				m_connection.rollback();
			}
		}
		finally
		{
			for(Statement statement: m_statementMap.values())
			{
				try
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
				catch(SQLException e)
				{
					/* IGNORE */
				}
			}

			m_connection.close();
		}
	}

	/*---------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	public Connection getConnection()
	{
		return m_connection;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getInternalCatalog()
	{
		return m_internalCatalog;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getExternalCatalog()
	{
		return m_externalCatalog;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Jdbc.Type getJdbcType()
	{
		return m_jdbcType;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcProto()
	{
		return m_jdbcProto;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcClass()
	{
		return m_jdbcClass;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJdbcUrl()
	{
		return m_jdbcUrl;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getUser()
	{
		return m_user;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getPass()
	{
		return m_pass;
	}

	/*---------------------------------------------------------------------*/
}
