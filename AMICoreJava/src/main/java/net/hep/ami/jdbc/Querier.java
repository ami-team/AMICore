package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

/**
 * AMI Querier Interface
 *
 */

public interface Querier
{
	/*----------------------------------------------------------------------------------------------------------------*/

	int FLAG_HIDE_BIG_CONTENT = (1 << 0);
	int FLAG_SHOW_LINKS = (1 << 1);
	int FLAG_ADMIN = (1 << 2);

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

	String mqlToSQL(@NotNull String entity, @NotNull String mql) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Converts a MQL query to an Abstract Syntax Tree (AST).
	 *
	 * @param entity The default entity.
	 * @param mql The MQL query.
	 *
	 * @return The generated AST.
	 */

	String mqlToAST(@NotNull String entity, @NotNull String mql) throws Exception;

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

	RowSet executeMQLQuery(@NotNull String entity, @NotNull String mql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a SQL query, typically a <code>SELECT</code> statement, and returns a <code>net.hep.ami.jdbc.RowSet</code> object.
	 *
	 * @param entity The default entity.
	 * @param sql The SQL query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the SQL query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.RowSet</code> object.
	 */

	RowSet executeSQLQuery(@NotNull String entity, @NotNull String sql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a raw query, typically a <code>SELECT</code> statement, and returns a <code>net.hep.ami.jdbc.RowSet</code> object.
	 *
	 * @param entity The default entity.
	 * @param raw The raw query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.RowSet</code> object.
	 */

	RowSet executeRawQuery(@NotNull String entity, @NotNull String raw, Object... args) throws Exception;

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

	Update executeMQLUpdate(@NotNull String entity, @NotNull String mql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a SQL query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a <code>net.hep.ami.jdbc.Update</code> object.
	 *
	 * @param entity The default entity.
	 * @param sql The SQL query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the SQL query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.Update</code> object.
	 */

	Update executeSQLUpdate(@NotNull String entity, @NotNull String sql, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Executes a raw query, typically an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code> statement, and returns a <code>net.hep.ami.jdbc.Update</code> object.
	 *
	 * @param entity The default entity.
	 * @param raw The raw query.
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The generated <code>net.hep.ami.jdbc.Update</code> object.
	 */

	Update executeRawUpdate(@NotNull String entity, @NotNull String raw, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Creates a PreparedStatement object for sending parameterized MQL statements to the database.
	 *
	 * @param entity The default entity.
	 * @param sql The SQL query.
	 * @param returnGeneratedKeys Indicates whether auto-generated keys should be returned.
	 * @param columnNames The list of auto-generated key names or <code>null</code> for getting them all.
	 * @param injectArgs Indicates whether TODO
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The new PreparedStatement object.
	 */

	PreparedStatement mqlPreparedStatement(@NotNull String entity, @NotNull String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Creates a PreparedStatement object for sending parameterized SQL statements to the database.
	 *
	 * @param entity The default entity.
	 * @param sql The SQL query.
	 * @param returnGeneratedKeys Indicates whether auto-generated keys should be returned.
	 * @param columnNames The list of auto-generated key names or <code>null</code> for getting them all.
	 * @param injectArgs Indicates whether TODO
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The new PreparedStatement object.
	 */

	PreparedStatement sqlPreparedStatement(@NotNull String entity, @NotNull String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Creates a PreparedStatement object for sending parameterized RAW statements to the database.
	 *
	 * @param entity The default entity.
	 * @param sql The SQL query.
	 * @param returnGeneratedKeys Indicates whether auto-generated keys should be returned.
	 * @param columnNames The list of auto-generated key names or <code>null</code> for getting them all.
	 * @param injectArgs Indicates whether TODO
	 * @param args The arguments referenced by the format specifiers (character '?') in the raw query.
	 *
	 * @return The new PreparedStatement object.
	 */

	PreparedStatement rawPreparedStatement(@NotNull String entity, @NotNull String sql, boolean returnGeneratedKeys, @Nullable String[] columnNames, boolean injectArgs, Object... args) throws Exception;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Retrieves the JDBC connection.
	 *
	 * @return The JDBC connection.
	 */

	Connection getConnection();

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
