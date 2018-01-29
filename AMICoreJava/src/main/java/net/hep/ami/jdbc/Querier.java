package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

public interface Querier
{
	/*---------------------------------------------------------------------*/

	public String mqlToSQL(String entity, String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public String mqlToAST(String entity, String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public RowSet executeMQLQuery(String entity, String mql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	public RowSet executeSQLQuery(String sql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	public Update executeMQLUpdate(String entity, String mql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	public Update executeSQLUpdate(String sql, Object... args) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement prepareStatement(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws Exception;

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
