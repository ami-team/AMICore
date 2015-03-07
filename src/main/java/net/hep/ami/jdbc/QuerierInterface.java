package net.hep.ami.jdbc;

public interface QuerierInterface {
	/*---------------------------------------------------------------------*/

	public QueryResult executeSQLQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public QueryResult executeGLiteQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public void executeUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public String getJdbcProto();

	/*---------------------------------------------------------------------*/

	public String getJdbcClass();

	/*---------------------------------------------------------------------*/

	public String getJdbcUrl();

	/*---------------------------------------------------------------------*/

	public String getCatalog();

	/*---------------------------------------------------------------------*/

	public String getUser();

	/*---------------------------------------------------------------------*/

	public String getPass();

	/*---------------------------------------------------------------------*/
}
