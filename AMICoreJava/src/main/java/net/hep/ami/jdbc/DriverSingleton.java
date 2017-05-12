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

	private static class Tuple extends Tuple4<Jdbc.Type, String, String, Constructor<AbstractDriver>>
	{
		public Tuple(Jdbc.Type _x, String _y, String _z, Constructor<AbstractDriver> _t)
		{
			super(_x, _y, _z, _t);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_drivers = new AMIHashMap<>();

	/*---------------------------------------------------------------------*/

	private static final Pattern s_protocolPattern = Pattern.compile(
		"^\\s*(\\w+:\\w+)"
	);

	/*---------------------------------------------------------------------*/

	private DriverSingleton() {}

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
			LogSingleton.root.error(LogSingleton.FATAL, "could not add drivers: " + e.getMessage(), e);
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
				LogSingleton.root.error(LogSingleton.FATAL, "for driver `" + className + "`: " + e.getMessage(), e);
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

		Class<AbstractDriver> clazz = (Class<AbstractDriver>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		if(ClassSingleton.extendsClass(clazz, AbstractDriver.class))
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
						String.class,
						String.class
					)
				)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static Tuple getDriver(String jdbcUrl) throws Exception
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

	public static AbstractDriver getConnection(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		return getDriver(jdbcUrl).t.newInstance(externalCatalog, internalCatalog, jdbcUrl, user, pass);
	}

	/*---------------------------------------------------------------------*/

	public static boolean isTypeOf(String jdbcUrl, Jdbc.Type jdbcType) throws Exception
	{
		return getDriver(jdbcUrl).x == jdbcType;
	}

	/*---------------------------------------------------------------------*/

	public static String getKey(String internalCatalog, String jdbcUrl, String user) throws Exception
	{
		return internalCatalog + "@" + jdbcUrl + "@" + user;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listDrivers()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		String jdbcProto;
		Jdbc.Type jdbcType;
		String jdbcClass;
		String driverClass;

		for(Map.Entry<String, Tuple> entry: s_drivers.entrySet())
		{
			jdbcProto = entry.getKey();
			jdbcType = entry.getValue().x;
			jdbcClass = entry.getValue().y;
			driverClass = entry.getValue().z;

			result.append("<row>")
			      .append("<field name=\"jdbcProto\"><![CDATA[").append(jdbcProto).append("]]></field>")
			      .append("<field name=\"jdbcType\"><![CDATA[").append(jdbcType).append("]]></field>")
			      .append("<field name=\"jdbcClass\"><![CDATA[").append(jdbcClass).append("]]></field>")
			      .append("<field name=\"driverClass\"><![CDATA[").append(driverClass).append("]]></field>")
			      .append("</row>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
