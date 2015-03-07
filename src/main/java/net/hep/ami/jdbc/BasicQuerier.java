package net.hep.ami.jdbc;

import net.hep.ami.jdbc.driver.*;

public class BasicQuerier implements QuerierInterface {
	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public BasicQuerier(String catalog) throws Exception {

		m_driver = CatalogSingleton.getConnection(catalog);
	}

	/*---------------------------------------------------------------------*/

	public BasicQuerier(String jdbcUrl, String user, String pass) throws Exception {

		m_driver = DriverSingleton.getConnection(jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeSQLQuery(String sql) throws Exception {

		return m_driver.executeSQLQuery(sql);
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

	public void commitAndRelease() throws Exception {

		m_driver.commitAndRelease();
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception {

		m_driver.rollbackAndRelease();
	}

	/*---------------------------------------------------------------------*/

	@Deprecated
	public DriverAbstractClass getDriver() {

		return m_driver;
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
