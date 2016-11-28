package net.hep.ami.jdbc.driver;

import java.sql.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.mql.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.reflexion.*;
import net.hep.ami.jdbc.driver.annotation.*;
import net.hep.ami.utility.annotation.*;

public abstract class DriverAbstractClass implements QuerierInterface
{
	/*---------------------------------------------------------------------*/

	protected Connection m_connection;

	/*---------------------------------------------------------------------*/

	protected final List<Statement> m_statementList = new ArrayList<Statement>();

	/*---------------------------------------------------------------------*/

	protected String m_internalCatalog;
	protected String m_externalCatalog;

	protected Jdbc.Type m_jdbcType;
	protected String m_jdbcProto;
	protected String m_jdbcClass;
	protected String m_jdbcUrl;
	protected String m_user;
	protected String m_pass;

	/*---------------------------------------------------------------------*/

	public DriverAbstractClass(String jdbcUrl, String user, String pass) throws Exception
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
			m_jdbcClass,
			m_jdbcUrl,
			m_user,
			m_pass
		);

		/*-----------------------------------------------------------------*/
		/* CREATE STATEMENT                                                */
		/*-----------------------------------------------------------------*/

		m_statementList.add(m_connection.createStatement());

		/*-----------------------------------------------------------------*/
		/* GET CATALOGS                                                    */
		/*-----------------------------------------------------------------*/

		try
		{
			m_externalCatalog = SchemaSingleton.internalCatalogToExternalCatalog(m_internalCatalog = m_connection.getCatalog());
		}
		catch(Exception e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	static class FieldType
	{
		String name;
		int size;
		int digits;

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

	public abstract FieldType amiTypeToJDBCType(FieldType fieldType) throws Exception;

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
			return new RowSet(m_statementList.get(0).executeQuery(patch(sql)), sql, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
	{
		_checkMQL();

		try
		{
			String sql = SelectParser.parse(mql, this);

			return new RowSet(m_statementList.get(0).executeQuery(patch(sql)), sql, null);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeUpdate(String sql) throws Exception
	{
		try
		{
			return m_statementList.get(0).executeUpdate(patch(sql));
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeMQLUpdate(String mql) throws Exception
	{
		_checkMQL();

		try
		{
			String sql = UpdateParser.parse(mql, this);

			return m_statementList.get(0).executeUpdate(patch(sql));
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query: " + mql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql) throws Exception
	{
		return sqlPrepareStatement(sql, null);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPrepareStatement(String mql) throws Exception
	{
		return mqlPrepareStatement(mql, null);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement sqlPrepareStatement(String sql, @Nullable String columnNames[]) throws Exception
	{
		try
		{
			PreparedStatement result = (columnNames == null) ? m_connection.prepareStatement(patch(sql))
			                                                 : m_connection.prepareStatement(patch(sql), columnNames)
			;

			m_statementList.add(result);

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for SQL query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public PreparedStatement mqlPrepareStatement(String mql, @Nullable String columnNames[]) throws Exception
	{
		_checkMQL();

		try
		{
			String sql = UpdateParser.parse(mql, this);

			PreparedStatement result = (columnNames == null) ? m_connection.prepareStatement(patch(sql))
			                                                 : m_connection.prepareStatement(patch(sql), columnNames)
			;

			m_statementList.add(result);

			return result;
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for MQL query " + mql);
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
			for(Statement statement: m_statementList)
			{
				try
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
				catch(SQLException e) {}
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
			for(Statement statement: m_statementList)
			{
				try
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
				catch(SQLException e) {}
			}

			m_connection.close();
		}
	}

	/*---------------------------------------------------------------------*/

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
