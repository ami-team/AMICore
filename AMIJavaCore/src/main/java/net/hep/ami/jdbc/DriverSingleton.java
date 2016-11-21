package net.hep.ami.jdbc;

import java.util.*;
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

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		s_drivers.clear();

		addDrivers();
	}

	/*---------------------------------------------------------------------*/

	public static void addDrivers()
	{
		Set<String> classeNames = ClassFinder.findClassNames("net.hep.ami.jdbc.driver");

		for(String className: classeNames)
		{
			try
			{
				addDriver(className);
			}
			catch(Exception e)
			{
				LogSingleton.defaultLogger.error(e.getMessage());
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

		/* QUOI FAIRE SI clazz EST NULL ??? */

		/*-----------------------------------------------------------------*/
		/* ADD DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		if(ClassFinder.extendsClass(clazz, DriverAbstractClass.class))
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
						String.class
					)
				)
			);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String jdbcUrl, String user, String pass) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET PROTOCOL                                                    */
		/*-----------------------------------------------------------------*/

		int index = jdbcUrl.indexOf("://");

		if(index < 0)
		{
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		String jdbcProto = jdbcUrl.substring(0, index);

		/*-----------------------------------------------------------------*/
		/* GET DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		Tuple tuple = s_drivers.get(jdbcProto);

		if(tuple == null)
		{
			throw new Exception("unknown JDBC protocol `" + jdbcProto + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CONNECTION                                                      */
		/*-----------------------------------------------------------------*/

		return tuple.t.newInstance(
			jdbcUrl,
			user,
			pass
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static boolean isType(String jdbcUrl, Jdbc.Type jdbcType) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* GET PROTOCOL                                                    */
		/*-----------------------------------------------------------------*/

		int index = jdbcUrl.indexOf("://");

		if(index < 0)
		{
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		String jdbcProto = jdbcUrl.substring(0, index);

		/*-----------------------------------------------------------------*/
		/* GET DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		Tuple tuple = s_drivers.get(jdbcProto);

		if(tuple == null)
		{
			throw new Exception("unknown JDBC protocol `" + jdbcProto + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CONNECTION                                                      */
		/*-----------------------------------------------------------------*/

		return tuple.x == jdbcType;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listDrivers()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		Set<Map.Entry<String, Tuple>> entrySet = new TreeSet<Map.Entry<String, Tuple>>(new MapEntryKeyComparator());

		entrySet.addAll(s_drivers.entrySet());

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
				"<field name=\"jdbcType\"><![CDATA[" + jdbcType + "]]></field>"
				+
				"<field name=\"jdbcClass\"><![CDATA[" + jdbcClass + "]]></field>"
				+
				"<field name=\"jdbcProto\"><![CDATA[" + jdbcProto + "]]></field>"
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
