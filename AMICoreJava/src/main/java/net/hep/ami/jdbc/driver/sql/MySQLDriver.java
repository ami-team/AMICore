package net.hep.ami.jdbc.driver.sql;

import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

@Jdbc(
	type = Jdbc.Type.SQL,
	proto = "jdbc:mysql",
	clazz = "com.mysql.jdbc.Driver"
)

public class MySQLDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	public MySQLDriver(String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception
	{
		return fieldType; /* MySQL is the default */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patch(String sql) throws Exception
	{
		return sql; /* MySQL is the default */
	}

	/*---------------------------------------------------------------------*/

	public void setDB(String db) throws Exception
	{
		m_statementMap.get("@").executeQuery("USE " + db + ";");
	}

	/*---------------------------------------------------------------------*/
}
