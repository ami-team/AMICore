package net.hep.ami.jdbc;

import lombok.*;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

public class DriverSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class Tuple
	{
		@NotNull DriverMetadata.Type type;
		@NotNull String proto;
		@NotNull String clazz;
		/*----*/ int flags;
		@NotNull String driverClazz;
		@NotNull Constructor<?> Constructor;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_drivers = new AMIMap<>(AMIMap.Type.HASH_MAP, true, true);

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Pattern s_protocolPattern = Pattern.compile(
		"^\\s*(\\w+:\\w+)"
	);

	/*----------------------------------------------------------------------------------------------------------------*/

	private DriverSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		s_drivers.clear();

		DriverSingleton.addDrivers();

		try
		{
			ConfigSingleton.readDataBase();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not read config", e);
		}

		CacheSingleton.reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void addDrivers()
	{
		for(String className: ClassSingleton.findClassNames("net.hep.ami.jdbc.driver"))
		{
			try
			{
				addDriver(className);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(LogSingleton.FATAL, "for driver `{}`", className, e);
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addDriver(@NotNull String className) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(className);

		if(!ClassSingleton.extendsClass(clazz, AbstractDriver.class))
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ADD DRIVER                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		DriverMetadata jdbc = clazz.getAnnotation(DriverMetadata.class);

		if(jdbc == null)
		{
			throw new Exception("no `Jdbc` annotation for driver `" + className + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_drivers.put(
			jdbc.proto()
			,
			new Tuple(
				jdbc.type(),
				jdbc.proto(),
				jdbc.clazz(),
				jdbc.flags(),
				clazz.getName(),
				clazz.getConstructor(
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					int.class
				)
			)
		);

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Tuple getTuple(@NotNull String jdbcUrl) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Matcher m = s_protocolPattern.matcher(jdbcUrl);

		if(!m.find())
		{
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple result = s_drivers.get(m.group(1));

		if(result == null)
		{
			throw new Exception("unknown JDBC protocol `" + m.group(1) + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static AbstractDriver getConnection(@Nullable String externalCatalog, @NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags) throws Exception
	{
		try
		{
			return (AbstractDriver) getTuple(jdbcUrl).getConstructor().newInstance(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
		}
		catch(InvocationTargetException e)
		{
			throw (Exception) e.getCause();
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static DriverMetadata.Type getType(@NotNull String jdbcUrl) throws Exception
	{
		return getTuple(jdbcUrl).getType();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getProto(@NotNull String jdbcUrl) throws Exception
	{
		return getTuple(jdbcUrl).getProto();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getClass(@NotNull String jdbcUrl) throws Exception
	{
		return getTuple(jdbcUrl).getClazz();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static int getFlags(@NotNull String jdbcUrl) throws Exception
	{
		return getTuple(jdbcUrl).getFlags();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String getKey(@NotNull String internalCatalog, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass)
	{
		return internalCatalog + "%" + jdbcUrl + "%" + (user == null ? "N/A" : user) + "%" + (pass == null ? "N/A" : pass);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listDrivers()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"drivers\">");

		for(Tuple tuple: s_drivers.values())
		{
			result.append("<row>")
			      .append("<field name=\"jdbcType\"><![CDATA[").append(tuple.getType()).append("]]></field>")
			      .append("<field name=\"jdbcProto\"><![CDATA[").append(tuple.getProto()).append("]]></field>")
			      .append("<field name=\"jdbcClass\"><![CDATA[").append(tuple.getClazz()).append("]]></field>")
			      .append("<field name=\"jdbcFlags\"><![CDATA[").append(tuple.getFlags()).append("]]></field>")
			      .append("<field name=\"driverClass\"><![CDATA[").append(tuple.getDriverClazz()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
