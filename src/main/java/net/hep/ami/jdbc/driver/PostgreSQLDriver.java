package net.hep.ami.jdbc.driver;

public class PostgreSQLDriver extends DriverAbstractClass {
	/*---------------------------------------------------------------------*/

	public PostgreSQLDriver(String jdbc_url, String user, String pass) throws Exception {

		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJDBCDriver() {

		return "org.postgresql.Driver";
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void useDB(String db) throws Exception {

		/* TODO */
	}

	/*---------------------------------------------------------------------*/
}
