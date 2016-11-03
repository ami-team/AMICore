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

		Jdbc annotation = getClass().getAnnotation(Jdbc.class);

		if(annotation == null)
		{
			throw new Exception("no `Jdbc` annotation for driver `" + getClass().getName() + "`");
		}

		Jdbc.Type jdbcType = annotation.type();
		String jdbcProto = annotation.proto();
		String jdbcClass = annotation.clazz();

		/*-----------------------------------------------------------------*/
		/* CREATE CONNECTION                                               */
		/*-----------------------------------------------------------------*/

		m_jdbcType = jdbcType;
		m_jdbcProto = jdbcProto;
		m_jdbcClass = jdbcClass;
		m_jdbcUrl = jdbcUrl;
		m_user = user;
		m_pass = pass;

		m_connection = ConnectionPoolSingleton.getConnection(
			m_jdbcClass,
			m_jdbcUrl,
			m_user,
			m_pass
		);

		m_connection.setAutoCommit(false);

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

		public FieldType(String _name, int _size)
		{
			name = _name;
			size = _size;
		}
	}

	/*---------------------------------------------------------------------*/

	public abstract FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract FieldType amiTypeToJDBCType(FieldType fieldType) throws Exception;

	/*---------------------------------------------------------------------*/

	public abstract String patch(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeQuery(String sql) throws Exception
	{
		try
		{
			return new RowSet(
				m_statementList.get(0).executeQuery(patch(sql)), sql, null
			);
		}
		catch(Exception e)
		{
			throw new Exception(e.getMessage() + " for query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public RowSet executeMQLQuery(String mql) throws Exception
	{
		if(m_jdbcType != Jdbc.Type.SQL)
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}

		try
		{
			String sql = SelectParser.parse(mql, this);
			String ast = /* TODO */ null /* TODO */;

			return new RowSet(
				m_statementList.get(0).executeQuery(patch(sql)), sql, ast
			);
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
			throw new Exception(e.getMessage() + " for query: " + sql);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int executeMQLUpdate(String mql) throws Exception
	{
		if(m_jdbcType != Jdbc.Type.SQL)
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}

		try
		{
			return m_statementList.get(0).executeUpdate(patch(UpdateParser.parse(mql, this)));
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
		if(m_jdbcType != Jdbc.Type.SQL)
		{
			throw new Exception("MQL not supported for driver `" + getClass().getName() + "`");
		}

		try
		{
			PreparedStatement result = (columnNames == null) ? m_connection.prepareStatement(patch(UpdateParser.parse(mql, this)))
			                                                 : m_connection.prepareStatement(patch(UpdateParser.parse(mql, this)), columnNames)
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
			try
			{
				for(Statement statement: m_statementList)
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
			}
			finally
			{
				m_connection.close();
			}
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
			try
			{
				for(Statement statement: m_statementList)
				{
					if(statement.isClosed() == false)
					{
						statement.close();
					}
				}
			}
			finally
			{
				m_connection.close();
			}
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
