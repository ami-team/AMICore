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

	public MariaDBDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String AMIUser, String timeZone, boolean isAdmin, boolean links) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, isAdmin, links);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setupSession(String db, String tz) throws Exception
	{
		if("UTC".equalsIgnoreCase(tz))
		{
			tz = "+00:00";
		}

		m_statement.executeQuery("USE `" + db + "`");

		m_statement.executeQuery("SET time_zone = '" + tz + "'");

		m_statement.executeQuery("SET SESSION sql_mode = 'ANSI_QUOTES'");
		m_statement.executeQuery("SET SESSION sql_mode = 'NO_BACKSLASH_ESCAPES'");
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql; /* MySQL/MariaDB is the default */
	}

	/*---------------------------------------------------------------------*/
}
