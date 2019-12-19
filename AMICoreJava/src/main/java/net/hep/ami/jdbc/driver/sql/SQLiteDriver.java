package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:sqlite",
	clazz = "org.sqlite.JDBC",
	flags = 0
)

public class SQLiteDriver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SQLiteDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz)
	{
		/* DO NOTHING */
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql)
	{
		String result =  sql.replaceAll("`" + this.m_internalCatalog + "`.","").replaceAll("TIMESTAMP\\('", "datetime('").replaceAll(",\\s*'YYYY-MM-DD[^']*'","");
		//System.out.println(result);
	return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
