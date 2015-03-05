package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	proto = "jdbc:postgresql",
	clazz = "org.postgresql.Driver"
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
