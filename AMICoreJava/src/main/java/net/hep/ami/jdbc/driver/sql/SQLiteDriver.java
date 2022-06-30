package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

import org.sqlite.Function;
import org.sqlite.SQLiteConnection;

import java.sql.SQLException;
import java.sql.Statement;

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
		try
		{

			Function.create(this.m_connection.unwrap(SQLiteConnection.class),
					"CONCAT",
					new Function() {
						@Override
						protected void xFunc() throws SQLException {
							result(value_text(0)+value_text(1));
						}
					}
			);
		}
		catch (Exception e)
		{
 			//System.out.println("error "+ e.getMessage());
		}

		try
		{

			Function.create(this.m_connection.unwrap(SQLiteConnection.class),
					"STDDEV",
					new Function() {
						@Override
						protected void xFunc() throws SQLException {
							result("N/A");
						}
					}
			);
		}
		catch (Exception e)
		{
			//System.out.println("error "+ e.getMessage());
		}

		try(Statement statement = m_connection.createStatement())
		{
			statement.execute("PRAGMA foreign_keys=ON'");
		}
		catch (Exception e)
		{

		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String patchSQL(@NotNull String sql)
	{
		return sql.replaceAll("`" + this.m_internalCatalog + "`.","");
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
