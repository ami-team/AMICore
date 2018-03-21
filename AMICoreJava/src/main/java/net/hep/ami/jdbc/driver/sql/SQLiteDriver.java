package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:sqlite",
	clazz = "org.sqlite.JDBC"
)

public class SQLiteDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public SQLiteDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* DO NOTHING */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql;
	}

	/*---------------------------------------------------------------------*/
}
