package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

/**
 * AMI Querier Interface
 * 
 */

public interface Querier
{
	/*----------------------------------------------------------------------------------------------------------------*/

	void setReadOnly(boolean readOnly) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Converts a MQL query to a SQL query.
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 *
	 * @return The generated SQL query.
	 */

	String mqlToSQL(String entity, String mql) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Converts a MQL query to an Abstract Syntax Tree (AST).
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 *
	 * @return The generated AST.
	 */

	String mqlToAST(String entity, String mql) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a MQL query, typically a <code>SELECT</code> statement, and returns a <code>net.hep.ami.jdbc.RowSet</code> object.
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the MQL query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.RowSet</code> object.
	 */

	RowSet executeMQLQuery(String entity, String mql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a SQL query, typically a <code>SELECT</code> statement, and returns a <code>net.hep.ami.jdbc.RowSet</code> object.
	 *
	 * @param sql The SQL query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the SQL query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.RowSet</code> object.
	 */

	RowSet executeSQLQuery(String entity, String sql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a raw query, typically a <code>SELECT</code> statement, and returns a <code>net.hep.ami.jdbc.RowSet</code> object.
	 *
	 * @param raw The raw query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.RowSet</code> object.
	 */

	RowSet executeRawQuery(String entity, String raw, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a MQL query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a <code>net.hep.ami.jdbc.Update</code> object.
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the MQL query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.Update</code> object.
	 */

	Update executeMQLUpdate(String entity, String mql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a SQL query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a <code>net.hep.ami.jdbc.Update</code> object.
	 *
	 * @param sql The SQL query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the SQL query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.Update</code> object.
	 */

	Update executeSQLUpdate(String sql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a raw query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a <code>net.hep.ami.jdbc.Update</code> object.
	 *
	 * @param raw The raw query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.Update</code> object.
	 */

	Update executeRawUpdate(String raw, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Creates a PreparedStatement object for sending parameterized SQL statements to the database.
	 *
	 * @param sql The SQL query.
	 * @param isRawQuery Indicates whether sql is a raw query or not.
	 * @param returnGeneratedKeys Indicates whether auto-generated keys should be returned.
	 * @param columnNames The list of auto-generated key names or <code>null</code> for getting them all.
	 *
	 * @return The new PreparedStatement object.
	 */

	PreparedStatement preparedStatement(String sql, boolean isRawQuery, boolean returnGeneratedKeys, String[] columnNames) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	Connection getConnection();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * @deprecated (for internal use only)
	 */

	@Deprecated
	Statement getStatement();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the internal catalog name of this querier.
	 *
	 * @return The internal catalog name.
	 */

	String getInternalCatalog();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the external catalog name of this querier.
	 *
	 * @return The external catalog name.
	 */

	String getExternalCatalog();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the database type (SQL or NoSQL) of this querier.
	 *
	 * @return The database type.
	 */

	DriverMetadata.Type getJdbcType();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC protocol of this querier.
	 *
	 * @return The JDBC protocol.
	 */

	String getJdbcProto();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC class of this querier.
	 *
	 * @return The JDBC class.
	 */

	String getJdbcClass();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC flags of this querier.
	 *
	 * @return The JDBC flags.
	 */

	int getJdbcFlags();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC URL of this querier.
	 *
	 * @return The JDBC URL.
	 */

	String getJdbcUrl();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the database user name of this querier.
	 *
	 * @return The database user name.
	 */

	String getUser();

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the database password of this querier.
	 *
	 * @return The database password.
	 */

	String getPass();

	/*----------------------------------------------------------------------------------------------------------------*/
}
