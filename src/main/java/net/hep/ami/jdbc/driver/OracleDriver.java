package net.hep.ami.jdbc.driver;

@Jdbc(
	proto = "jdbc:oracle",
	clazz = "oracle.jdbc.driver.OracleDriver"
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
