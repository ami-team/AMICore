package net.hep.ami.jdbc;

import java.sql.*;

import net.hep.ami.jdbc.driver.*;

public interface Querier
{
	/*---------------------------------------------------------------------*/

	public String mqlToSQL(String mql, String entity) throws Exception;

	/*---------------------------------------------------------------------*/

	public String mqlToAST(String mql, String entity) throws Exception;

	/*---------------------------------------------------------------------*/

	public RowSet executeMQLQuery(String mql, String entity) throws Exception;

	/*---------------------------------------------------------------------*/

	public RowSet executeSQLQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public int executeSQLUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement prepareStatement(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public PreparedStatement prepareStatement(String sql, @Nullable String[] columnNames) throws Exception;

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
