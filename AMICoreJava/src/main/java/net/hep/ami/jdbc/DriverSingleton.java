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

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(DriverSingleton.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class DriverDescr
	{
		@NotNull private final DriverMetadata.Type type;
		@NotNull private final String proto;
		@NotNull private final String clazz;
		/*----*/ private final int flags;
		@NotNull private final String driverClazz;
		@NotNull private final Constructor<?> Constructor;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, DriverDescr> s_drivers = new AMIMap<>(AMIMap.Type.HASH_MAP, true, true);

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
				LOG.error(LogSingleton.FATAL, "for driver `{}`", className, e);
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
			new DriverDescr(
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
	public static DriverSingleton.DriverDescr getDriverDescr(@NotNull String jdbcUrl) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		Matcher m = s_protocolPattern.matcher(jdbcUrl);

		if(!m.find())
		{
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		DriverDescr result = s_drivers.get(m.group(1));

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
			return (AbstractDriver) getDriverDescr(jdbcUrl).getConstructor().newInstance(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags);
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
		return getDriverDescr(jdbcUrl).getType();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getProto(@NotNull String jdbcUrl) throws Exception
	{
		return getDriverDescr(jdbcUrl).getProto();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String getClass(@NotNull String jdbcUrl) throws Exception
	{
		return getDriverDescr(jdbcUrl).getClazz();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static int getFlags(@NotNull String jdbcUrl) throws Exception
	{
		return getDriverDescr(jdbcUrl).getFlags();
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

		for(DriverDescr driverDescr : s_drivers.values())
		{
			result.append("<row>")
			      .append("<field name=\"jdbcType\"><![CDATA[").append(driverDescr.getType()).append("]]></field>")
			      .append("<field name=\"jdbcProto\"><![CDATA[").append(driverDescr.getProto()).append("]]></field>")
			      .append("<field name=\"jdbcClass\"><![CDATA[").append(driverDescr.getClazz()).append("]]></field>")
			      .append("<field name=\"jdbcFlags\"><![CDATA[").append(driverDescr.getFlags()).append("]]></field>")
			      .append("<field name=\"driverClass\"><![CDATA[").append(driverDescr.getDriverClazz()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
