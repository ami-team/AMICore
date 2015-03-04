package net.hep.ami.jdbc.driver;

@Jdbc(
	className = "oracle.jdbc.driver.OracleDriver",
	protocol = "jdbc:oracle"
)

public class OracleDriver extends DriverAbstractClass {
	/*---------------------------------------------------------------------*/

	public OracleDriver(String jdbc_url, String user, String pass) throws Exception {

		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void useDB(String db) throws Exception {

		/* TODO */
	}

	/*---------------------------------------------------------------------*/
}
