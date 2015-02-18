package net.hep.ami.jdbc.driver;

public class MySQLDriver extends DriverAbstractClass {
	/*---------------------------------------------------------------------*/

	public MySQLDriver(String jdbc_url, String user, String pass) throws Exception {

		super(jdbc_url, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String getJDBCDriver() {

		return "org.gjt.mm.mysql.Driver";
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void useDB(String db) throws Exception {

		executeUpdate("use " + db);

		m_db = db;
	}

	/*---------------------------------------------------------------------*/
}
