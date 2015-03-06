package net.hep.ami.jdbc.driver;

import java.sql.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.pool.*;
import net.hep.ami.jdbc.driver.annotation.*;

public abstract class DriverAbstractClass implements QuerierInterface {
	/*---------------------------------------------------------------------*/

	protected Connection m_connection = null;
	protected Statement m_statement = null;

	/*---------------------------------------------------------------------*/

	protected String m_jdbcProto = null;
	protected String m_jdbcClass = null;
	protected String m_jdbcUrl = null;
	protected String m_user = null;
	protected String m_pass = null;
	protected String m_db = null;

	/*---------------------------------------------------------------------*/

	protected java.util.concurrent.atomic.AtomicInteger m_refCnt = new java.util.concurrent.atomic.AtomicInteger(1);

	/*---------------------------------------------------------------------*/

	public DriverAbstractClass(String jdbcUrl, String user, String pass) throws Exception {
		/*-----------------------------------------------------------------*/

		Jdbc annotation = getClass().getAnnotation(Jdbc.class);

		if(annotation == null) {
			throw new Exception("no `Jdbc` annotation for driver `" + getClass().getName() + "`");
		}

		String jdbcProto = annotation.proto();
		String jdbcClass = annotation.clazz();

		/*-----------------------------------------------------------------*/

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

		m_statement = m_connection.createStatement();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeQuery(String sql) throws Exception {

		return new QueryResult(m_statement.executeQuery(sql));
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeGLiteQuery(String sql) throws Exception {

		return new QueryResult(m_statement.executeQuery(sql));
	}

	/*---------------------------------------------------------------------*/

	public void executeUpdate(String sql) throws Exception {

		m_statement.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	public void commit() throws Exception {

		if(m_connection.getAutoCommit() == false) {
			m_connection.commit();
		}
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception {

		if(m_connection.getAutoCommit() == false) {
			m_connection.rollback();
		}
	}

	/*---------------------------------------------------------------------*/

	public void retain() {

		m_refCnt.incrementAndGet();
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception {

		int value = m_refCnt.decrementAndGet();

		if(value < 0) {
			throw new Exception("internal reference counter error for `<DriverInterface>.commitAndRelease()`");
		}

		if(m_connection.getAutoCommit() == false) {
			m_connection.commit();
		}

		if(value == 0) {

			try {
				m_statement.close();
			} finally {
				m_connection.close();
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception {

		int value = m_refCnt.decrementAndGet();

		if(value < 0) {
			throw new Exception("internal reference counter error for `<DriverInterface>.commitAndRelease()`");
		}

		if(m_connection.getAutoCommit() == false) {
			m_connection.rollback();
		}

		if(value == 0) {

			try {
				m_statement.close();
			} finally {
				m_connection.close();
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcProto() {

		return m_jdbcProto;
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcClass() {

		return m_jdbcClass;
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcUrl() {

		return m_jdbcUrl;
	}

	/*---------------------------------------------------------------------*/

	public String getUser() {

		return m_user;
	}

	/*---------------------------------------------------------------------*/

	public String getPass() {

		return m_pass;
	}

	/*---------------------------------------------------------------------*/

	public String getDB() {

		return m_db;
	}

	/*---------------------------------------------------------------------*/
}
