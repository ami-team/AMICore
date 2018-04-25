package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

/**
 * Blablabla 
 * 
 */

public interface Querier
{
	/*---------------------------------------------------------------------*/

	public void setReadOnly(boolean readOnly) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Converts a MQL query to a SQL query.
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 *
	 * @return The generated SQL query.
	 */

	public String mqlToSQL(String entity, String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Converts a MQL query to an Abstract Syntax Tree (AST).
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 *
	 * @return The generated AST.
	 */

	public String mqlToAST(String entity, String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Executes a MQL query, typically a <code>SELECT</code> statement, and returns a net.hep.ami.jdbc.RowSet object.
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 * @param args... The arguments referenced by the format specifiers (character '?') in the MQL query.
	 *
	 * @return The generated net.hep.ami.jdbc.RowSet object.
	 */

	public RowSet executeMQLQuery(String entity, String mql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Executes a SQL query, typically a <code>SELECT</code> statement, and returns a net.hep.ami.jdbc.RowSet object.
	 *
	 * @param sql The SQL query.
	 * @param args... The arguments referenced by the format specifiers (character '?') in the SQL query.
	 *
	 * @return The generated net.hep.ami.jdbc.RowSet object.
	 */

	public RowSet executeSQLQuery(String sql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Executes a MQL query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a net.hep.ami.jdbc.Update object.
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 * @param args... The arguments referenced by the format specifiers (character '?') in the MQL query.
	 *
	 * @return The generated net.hep.ami.jdbc.Update object.
	 */

	public Update executeMQLUpdate(String entity, String mql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Executes a SQL query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a net.hep.ami.jdbc.Update object.
	 *
	 * @param sql The SQL query.
	 * @param args... The arguments referenced by the format specifiers (character '?') in the SQL query.
	 *
	 * @return The generated net.hep.ami.jdbc.Update object.
	 */

	public Update executeSQLUpdate(String sql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Creates a PreparedStatement object for sending parameterized SQL statements to the database.
	 *
	 * @param sql The SQL query.
	 * @param returnGeneratedKeys Indicates whether auto-generated keys should be returned.
	 * @param columnNames The list of auto-generated key names or <code>null</code> for getting them all.
	 *
	 * @return The new PreparedStatement object.
	 */

	public PreparedStatement prepareStatement(String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames) throws Exception;

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the internal catalog name of this querier.
	 *
	 * @return The internal catalog name.
	 */

	public String getInternalCatalog();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the external catalog name of this querier.
	 *
	 * @return The external catalog name.
	 */

	public String getExternalCatalog();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the database type (SQL or NoSQL) of this querier.
	 *
	 * @return The database type.
	 */

	public DriverMetadata.Type getJdbcType();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC protocol of this querier.
	 *
	 * @return The JDBC protocol.
	 */

	public String getJdbcProto();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC class of this querier.
	 *
	 * @return The JDBC class.
	 */

	public String getJdbcClass();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC URL of this querier.
	 *
	 * @return The JDBC URL.
	 */

	public String getJdbcUrl();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the database user name of this querier.
	 *
	 * @return The database user name.
	 */

	public String getUser();

	/*---------------------------------------------------------------------*/

	/**
	 * Retrieves the database password of this querier.
	 *
	 * @return The database password.
	 */

	public String getPass();

	/*---------------------------------------------------------------------*/
}
