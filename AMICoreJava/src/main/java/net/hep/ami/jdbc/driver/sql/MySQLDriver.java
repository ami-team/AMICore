package net.hep.ami.jdbc.driver.sql;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import net.hep.ami.jdbc.driver.*;

import net.hep.ami.jdbc.query.sql.Tokenizer;
import net.hep.ami.utility.TextFile;
import org.jetbrains.annotations.*;

@Slf4j
@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:mysql",
	clazz = "com.mysql.cj.jdbc.Driver",
	flags = DriverMetadata.FLAG_BACKSLASH_ESCAPE | DriverMetadata.FLAG_HAS_CATALOG | DriverMetadata.FLAG_HAS_DUAL
)

public class MySQLDriver extends AbstractDriver
{
	/*----------------------------------------------------------------------------------------------------------------*/

	static final String JSON_PATHS_TEMPLATE;
	static final String JSON_VALUES_TEMPLATE;

	static
	{
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();

		try(InputStream inputStream = AbstractDriver.class.getResourceAsStream("/sql/mysql/json_paths.sql"))
		{
			TextFile.read(stringBuilder1, inputStream);
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}

		try(InputStream inputStream = AbstractDriver.class.getResourceAsStream("/sql/mysql/json_values.sql"))
		{
			TextFile.read(stringBuilder2, inputStream);
		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
		}

		JSON_PATHS_TEMPLATE = stringBuilder1.toString();
		JSON_VALUES_TEMPLATE = stringBuilder2.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public MySQLDriver(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @NotNull String user, @NotNull String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void setupSession(@NotNull String db, @NotNull String tz) throws Exception
	{
		if("UTC".equalsIgnoreCase(tz))
		{
			tz = "+00:00";
		}

		try(Statement statement = m_connection.createStatement())
		{
			statement.executeQuery("USE `" + db + "`");

			statement.executeQuery("SET time_zone = '" + tz + "'");

			statement.executeQuery("SET sql_mode = 'ANSI_QUOTES'");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Pattern JSON_PATHS_SUBSTITUTION = Pattern.compile("\\{%JSON_PATHS,(.*),(.*),(.*)%\\}");
	private static final Pattern JSON_VALUES_SUBSTITUTION = Pattern.compile("\\{%JSON_VALUES,(.*),(.*),(.*)%\\}");

	@Override
	public String patchSQL(@NotNull String sql) throws Exception
	{
		Matcher m;

		/**/ if((m = JSON_PATHS_SUBSTITUTION.matcher(sql)).find())
		{
			String primaryKey = m.group(1);
			String field = m.group(2);
			String path = m.group(3);

			Tokenizer.XQLParts partInfo = Tokenizer.splitXQL(sql);

			List<String> select = partInfo.getSelect();
			List<String> from = partInfo.getFrom();
			List<String> where = partInfo.getWhere();

			return JSON_PATHS_TEMPLATE.replace("{%primaryKey%}", primaryKey)
					.replace("{%field%}", field)
					.replace("{%path%}", path)
					.replace("{%select%}", String.join("", select).replace(m.group(), "a.JPATH AS \"PATH\""))
					.replace("{%from%}", String.join("", from))
					.replace("{%where%}", where != null ? String.join("", where) : "1 = 1")
			;
		}
		else if((m = JSON_VALUES_SUBSTITUTION.matcher(sql)).find())
		{
			String primaryKey = m.group(1);
			String field = m.group(2);
			String path = m.group(3);

			Tokenizer.XQLParts partInfo = Tokenizer.splitXQL(sql);

			List<String> select = partInfo.getSelect();
			List<String> from = partInfo.getFrom();
			List<String> where = partInfo.getWhere();

			return JSON_VALUES_TEMPLATE.replace("{%primaryKey%}", primaryKey)
					.replace("{%field%}", field)
					.replace("{%path%}", path)
					.replace("{%select%}", String.join("", select).replace(m.group(), "a.JVALUE AS \"VALUE\""))
					.replace("{%from%}", String.join("", from))
					.replace("{%where%}", where != null ? String.join("", where) : "1 = 1")
			;
		}
		else
		{
			return sql; /* MySQL/MariaDB is the default */
		}

	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
