package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:mysql",
	clazz = "com.mysql.jdbc.Driver"
)

public class MySQLDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public MySQLDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("deprecation")
	public void setDB(String db) throws Exception
	{
		getStatement().executeQuery("USE `" + db + "`;");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql; /* MySQL/MariaDB is the default */
	}

	/*---------------------------------------------------------------------*/
}
