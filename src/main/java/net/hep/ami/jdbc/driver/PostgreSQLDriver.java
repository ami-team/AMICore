package net.hep.ami.jdbc.driver;

@Jdbc(
	className = "org.postgresql.Driver",
	prefix = "jdbc:postgresql"
)

public class PostgreSQLDriver extends DriverAbstractClass {
	/*---------------------------------------------------------------------*/

	public PostgreSQLDriver(String jdbc_url, String user, String pass) throws Exception {

		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void useDB(String db) throws Exception {

		/* TODO */
	}

	/*---------------------------------------------------------------------*/
}
