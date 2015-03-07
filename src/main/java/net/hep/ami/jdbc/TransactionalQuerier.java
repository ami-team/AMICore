package net.hep.ami.jdbc;

import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionalQuerier implements QuerierInterface {
	/*---------------------------------------------------------------------*/

	private int m_transactionID;

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String catalog, int transactionID) throws Exception {

		m_driver = TransactionPoolSingleton.getConnection(catalog, m_transactionID = transactionID);
	}

	/*---------------------------------------------------------------------*/

	public TransactionalQuerier(String jdbcUrl, String user, String pass, int transactionID) throws Exception {

		m_driver = TransactionPoolSingleton.getConnection(jdbcUrl, user, pass, m_transactionID = transactionID);
	}

	/*---------------------------------------------------------------------*/

	public int getTransactionID() {

		return m_transactionID;
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
