package net.hep.ami.jdbc;

public interface QuerierInterface {
	/*---------------------------------------------------------------------*/

	public QueryResult executeSQLQuery(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public QueryResult executeMQLQuery(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public void executeSQLUpdate(String sql) throws Exception;

	/*---------------------------------------------------------------------*/

	public void executeMQLUpdate(String mql) throws Exception;

	/*---------------------------------------------------------------------*/

	public String getJdbcProto();

	/*---------------------------------------------------------------------*/

	public String getJdbcClass();

	/*---------------------------------------------------------------------*/

	public String getJdbcUrl();

	/*---------------------------------------------------------------------*/

	public String getInternalCatalog();

	/*---------------------------------------------------------------------*/

	public String getExternalCatalog();

	/*---------------------------------------------------------------------*/

	public String getUser();

	/*---------------------------------------------------------------------*/

	public String getPass();

	/*---------------------------------------------------------------------*/
}
