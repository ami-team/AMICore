package net.hep.ami.jdbc;

import java.sql.Connection;
import java.sql.Statement;

import net.hep.ami.jdbc.driver.*;

public class BasicLoader implements JdbcInterface {
	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public BasicLoader(String catalog) throws Exception {

		m_driver = CatalogSingleton.getConnection(catalog);
	}

	/*---------------------------------------------------------------------*/

	public BasicLoader(String jdbcUrl, String user, String pass) throws Exception {

		m_driver = ConnectionSingleton.getConnection(jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	public BasicLoader(String jdbcUrl, String user, String pass, String name) throws Exception {

		m_driver = ConnectionSingleton.getConnection(jdbcUrl, user, pass, name);
	}

	/*---------------------------------------------------------------------*/

	public void useDB(String db) throws Exception {

		m_driver.useDB(db);
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeQuery(String sql) throws Exception {

		return m_driver.executeQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeGLiteQuery(String sql) throws Exception {

		return m_driver.executeGLiteQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	public void executeUpdate(String sql) throws Exception {

		m_driver.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	public void commit() throws Exception {

		m_driver.commit();
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception {

		m_driver.rollback();
	}

	/*---------------------------------------------------------------------*/

	public void retain() {

		m_driver.retain();
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception {

		m_driver.commitAndRelease();
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception {

		m_driver.rollbackAndRelease();
	}

	/*---------------------------------------------------------------------*/

	@Deprecated
	public Connection getConnection() {

		return m_driver.getConnection();
	}

	/*---------------------------------------------------------------------*/

	@Deprecated
	public Statement getStatement() {

		return m_driver.getStatement();
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcProto() {

		return m_driver.getJdbcProto();
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcClass() {

		return m_driver.getJdbcClass();
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcUrl() {

		return m_driver.getJdbcUrl();
	}

	/*---------------------------------------------------------------------*/

	public String getUser() {

		return m_driver.getUser();
	}

	/*---------------------------------------------------------------------*/

	public String getPass() {

		return m_driver.getPass();
	}

	/*---------------------------------------------------------------------*/

	public String getDB() {

		return m_driver.getDB();
	}

	/*---------------------------------------------------------------------*/
}
