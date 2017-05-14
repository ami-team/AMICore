package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:mariadb",
	clazz = "org.mariadb.jdbc.Driver"
)

public class MariaDBDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public MariaDBDriver(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception
	{
		return fieldType; /* MySQL/MariaDB is the default */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		return sql; /* MySQL/MariaDB is the default */
	}

	/*---------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("deprecation")
	public void setDB(String db) throws Exception
	{
		getStatement().executeQuery("USE `" + db + "`;");
	}

	/*---------------------------------------------------------------------*/
}
