package net.hep.ami.jdbc;

import java.sql.*;

public interface QuerierInterface
{
	/*---------------------------------------------------------------------*/

	public RowSet executeQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public RowSet executeMQLQuery(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public int executeUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public int executeMQLUpdate(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement sqlPrepareStatement(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement mqlPrepareStatement(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement sqlPrepareStatement(String sql, String columnNames[]) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement mqlPrepareStatement(String mql, String columnNames[]) throws Exception;

	/*---------------------------------------------------------------------*/

	public String getInternalCatalog();

	/*---------------------------------------------------------------------*/

	public String getExternalCatalog();

	/*---------------------------------------------------------------------*/

	public DBType getJdbcType();

	/*---------------------------------------------------------------------*/

	public String getJdbcProto();

	/*---------------------------------------------------------------------*/

	public String getJdbcClass();

	/*---------------------------------------------------------------------*/

	public String getJdbcUrl();

	/*---------------------------------------------------------------------*/

	public String getUser();

	/*---------------------------------------------------------------------*/

	public String getPass();

	/*---------------------------------------------------------------------*/
}
