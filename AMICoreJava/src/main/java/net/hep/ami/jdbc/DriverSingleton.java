package net.hep.ami.jdbc;

import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

public class DriverSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class Tuple extends Tuple5<DriverMetadata.Type, String, String, String, Constructor<?>>
	{
		private static final long serialVersionUID = 3082894522888817449L;

		public Tuple(DriverMetadata.Type _x, String _y, String _z, String _t, Constructor<?> _u)
		{
			super(_x, _y, _z, _t, _u);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_drivers = new AMIMap<>(AMIMap.Type.HASH_MAP, true, true);

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
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not add drivers", e);
		}

		try
		{
			ConfigSingleton.readDataBase();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not read config", e);
		}
	}

	/*---------------------------------------------------------------------*/

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

	/*---------------------------------------------------------------------*/

	private static void addDriver(String className) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<?> clazz = ClassSingleton.forName(className);

		if(ClassSingleton.extendsClass(clazz, AbstractDriver.class) == false)
		{
			return;
		}

		/*-----------------------------------------------------------------*/
		/* ADD DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		DriverMetadata jdbc = clazz.getAnnotation(DriverMetadata.class);

		if(jdbc == null)
		{
			throw new Exception("no `Jdbc` annotation for driver `" + className + "`");
		}

		/*-----------------------------------------------------------------*/

		s_drivers.put(
			jdbc.proto()
			,
			new Tuple(
				jdbc.type(),
				jdbc.proto(),
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

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Tuple getTuple(String jdbcUrl) throws Exception
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
		try
		{
			return (AbstractDriver) getTuple(jdbcUrl).u.newInstance(externalCatalog, internalCatalog, jdbcUrl, user, pass);
		}
		catch(InvocationTargetException e)
		{
			throw (Exception) e.getCause();
		}
	}

	/*---------------------------------------------------------------------*/

	public static boolean isTypeOf(String jdbcUrl, DriverMetadata.Type jdbcType) throws Exception
	{
		return getTuple(jdbcUrl).x == jdbcType;
	}

	/*---------------------------------------------------------------------*/

	public static String getKey(String internalCatalog, String jdbcUrl, String user, String pass) throws Exception
	{
		return internalCatalog + "%" + jdbcUrl + "%" + user + "%" + pass;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listDrivers()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		for(Tuple tuple: s_drivers.values())
		{
			result.append("<row>")
			      .append("<field name=\"jdbcType\"><![CDATA[").append(tuple.x).append("]]></field>")
			      .append("<field name=\"jdbcProto\"><![CDATA[").append(tuple.y).append("]]></field>")
			      .append("<field name=\"jdbcClass\"><![CDATA[").append(tuple.z).append("]]></field>")
			      .append("<field name=\"driverClass\"><![CDATA[").append(tuple.t).append("]]></field>")
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
