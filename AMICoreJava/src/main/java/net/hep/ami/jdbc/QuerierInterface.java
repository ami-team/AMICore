package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.annotation.*;

public interface QuerierInterface
{
	/*---------------------------------------------------------------------*/

	public RowSet executeQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public RowSet executeMQLQuery(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public int executeUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement sqlPrepareStatement(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement sqlPrepareStatement(String sql, String columnNames[]) throws Exception;

	/*---------------------------------------------------------------------*/

	public String getInternalCatalog();

	/*---------------------------------------------------------------------*/

	public String getExternalCatalog();

	/*---------------------------------------------------------------------*/

	public Jdbc.Type getJdbcType();

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
