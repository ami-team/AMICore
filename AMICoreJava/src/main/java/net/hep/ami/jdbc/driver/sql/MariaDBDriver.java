package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.utility.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:mariadb",
	clazz = "org.mariadb.jdbc.Driver"
)

public class MariaDBDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public MariaDBDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String AMIUser, boolean isAdmin) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, isAdmin);
	}

	/*---------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("deprecation")
	public void setDB(String db) throws Exception
	{
		getStatement().executeQuery("SET SESSION sql_mode = 'NO_BACKSLASH_ESCAPES';");

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
