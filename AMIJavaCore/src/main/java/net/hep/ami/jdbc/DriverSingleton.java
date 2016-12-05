package net.hep.ami.jdbc;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

public class DriverSingleton
{
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple4<Jdbc.Type, String, String, Constructor<DriverAbstractClass>>
	{
		public Tuple(Jdbc.Type _x, String _y, String _z, Constructor<DriverAbstractClass> _t)
		{
			super(_x, _y, _z, _t);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_drivers = new java.util.concurrent.ConcurrentHashMap<String, Tuple>();

	/*---------------------------------------------------------------------*/

	private static final Pattern s_protocolPattern = Pattern.compile(
		"^\\s*(\\w+:\\w+)"
	);

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_drivers.clear();

		try
		{
			DriverSingleton.addDrivers();

			ConfigSingleton.readDataBase();
		}
		catch(Exception e)
		{
			LogSingleton.defaultLogger.fatal(e.getMessage());
		}
	}

	/*---------------------------------------------------------------------*/

	public static void addDrivers()
	{
		Set<String> classeNames = ClassSingleton.findClassNames("net.hep.ami.jdbc.driver");

		for(String className: classeNames)
		{
			try
			{
				addDriver(className);
			}
			catch(Exception e)
			{
				LogSingleton.defaultLogger.fatal("for driver `" + className + "`: " + e.getMessage());
			}
		}
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addDriver(String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<DriverAbstractClass> clazz = (Class<DriverAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		if(ClassSingleton.extendsClass(clazz, DriverAbstractClass.class))
		{
			Jdbc jdbc = clazz.getAnnotation(Jdbc.class);

			if(jdbc == null)
			{
				throw new Exception("no `Jdbc` annotation for driver `" + clazz.getName() + "`");
			}

			s_drivers.put(
				jdbc.proto()
				,
				new Tuple(
					jdbc.type(),
					jdbc.clazz(),
					clazz.getName(),
					clazz.getConstructor(
						String.class,
						String.class,
						String.class,
						String.class
					)
				)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple getDriver(String jdbcUrl) throws Exception
	{
		/*-----------------------------------------------------------------*/

		Matcher m = s_protocolPattern.matcher(jdbcUrl);

		if(m.find() == false)
		{
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		/*-----------------------------------------------------------------*/

		Tuple result = s_drivers.get(m.group(1));

		if(result == null)
		{
			throw new Exception("unknown JDBC protocol `" + m.group(1) + "`");
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(@Nullable String catalog, String jdbcUrl, String user, String pass) throws Exception
	{
		return getDriver(jdbcUrl).t.newInstance(
			catalog,
			jdbcUrl,
			user,
			pass
		);
	}

	/*---------------------------------------------------------------------*/

	public static boolean isType(String jdbcUrl, Jdbc.Type jdbcType) throws Exception
	{
		return getDriver(jdbcUrl).x == jdbcType;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listDrivers()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		OrderdSetOfMapEntry<Tuple> entrySet = new OrderdSetOfMapEntry<Tuple>(s_drivers.entrySet());

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		String jdbcProto;
		Jdbc.Type jdbcType;
		String jdbcClass;
		String driverClass;

		for(Map.Entry<String, Tuple> entry: entrySet)
		{
			jdbcProto = entry.getKey();
			jdbcType = entry.getValue().x;
			jdbcClass = entry.getValue().y;
			driverClass = entry.getValue().z;

			result.append(
				"<row>"
				+
				"<field name=\"jdbcProto\"><![CDATA[" + jdbcProto + "]]></field>"
				+
				"<field name=\"jdbcType\"><![CDATA[" + jdbcType + "]]></field>"
				+
				"<field name=\"jdbcClass\"><![CDATA[" + jdbcClass + "]]></field>"
				+
				"<field name=\"driverClass\"><![CDATA[" + driverClass + "]]></field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
