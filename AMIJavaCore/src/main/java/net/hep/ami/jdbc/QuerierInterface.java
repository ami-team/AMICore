package net.hep.ami.jdbc;

public interface QuerierInterface
{
	/*---------------------------------------------------------------------*/

	public QueryResult executeSQLQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public QueryResult executeMQLQuery(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public int executeSQLUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public int executeMQLUpdate(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public String getInternalCatalog();

	/*---------------------------------------------------------------------*/

	public String getExternalCatalog();

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
