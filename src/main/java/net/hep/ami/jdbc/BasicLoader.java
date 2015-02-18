package net.hep.ami.jdbc;

import net.hep.ami.jdbc.driver.*;

public class BasicLoader implements JdbcInterface {
	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public BasicLoader(String jdbc_url, String user, String pass) throws Exception {

		jdbc_url = jdbc_url.trim();

		/*  */ if(jdbc_url.startsWith("jdbc:mysql")) {
			m_driver = new MySQLDriver(jdbc_url, user, pass);
		} else if(jdbc_url.startsWith("jdbc:oracle")) {
			m_driver = new OracleDriver(jdbc_url, user, pass);
		} else if(jdbc_url.startsWith("jdbc:postgresql")) {
			m_driver = new PostgreSQLDriver(jdbc_url, user, pass);
		} else {
			throw new Exception("unknown SQL driver");
		}
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

	public String getJDBCDriver() {

		return m_driver.getJDBCDriver();
	}

	/*---------------------------------------------------------------------*/

	public String getJDBCURL() {

		return m_driver.getJDBCURL();
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
