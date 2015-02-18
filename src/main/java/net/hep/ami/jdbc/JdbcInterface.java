package net.hep.ami.jdbc;

public interface JdbcInterface {
	/*---------------------------------------------------------------------*/

	public void useDB(String db) throws Exception;

	/*---------------------------------------------------------------------*/

	public QueryResult executeQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public QueryResult executeGLiteQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public void executeUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public String getJDBCDriver();

	/*---------------------------------------------------------------------*/

	public String getJDBCURL();

	/*---------------------------------------------------------------------*/

	public String getUser();

	/*---------------------------------------------------------------------*/

	public String getPass();

	/*---------------------------------------------------------------------*/

	public String getDB();

	/*---------------------------------------------------------------------*/
}
