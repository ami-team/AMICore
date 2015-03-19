package net.hep.ami.jdbc.driver;

import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	proto = "jdbc:sqlite",
	clazz = "org.sqlite.JDBC"
)

public class SQLiteDriver extends DriverAbstractClass {
	/*---------------------------------------------------------------------*/

	public SQLiteDriver(String jdbc_url, String user, String pass) throws Exception {

		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/
}
