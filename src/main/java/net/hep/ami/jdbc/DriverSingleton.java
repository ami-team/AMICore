package net.hep.ami.jdbc;

import java.util.*;
import java.util.Map.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

public class DriverSingleton {
	/*---------------------------------------------------------------------*/

	private static class DriverTuple extends Tuple3<String, String, Constructor<DriverAbstractClass>> {

		public DriverTuple(String _x, String _y, Constructor<DriverAbstractClass> _z) {
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static Map<String, DriverTuple> m_drivers = new HashMap<String, DriverTuple>();

	/*---------------------------------------------------------------------*/

	private static final Class<?>[] m_ctor = new Class<?>[] {
		String.class,
		String.class,
		String.class,
	};

	/*---------------------------------------------------------------------*/

	static {

		ClassFinder classFinder = new ClassFinder("net.hep.ami.jdbc.driver");

		for(String className: classFinder.getClassList()) {

			try {
				addDriver(className);

			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.CRITICAL, e.getMessage());
			}
		}
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addDriver(String className) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET CLASS OBJECT                                                */
		/*-----------------------------------------------------------------*/

		Class<DriverAbstractClass> clazz = (Class<DriverAbstractClass>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		if(ClassFinder.extendsClass(clazz, DriverAbstractClass.class)) {

			Jdbc jdbc = clazz.getAnnotation(Jdbc.class);

			if(jdbc == null) {
				throw new Exception("no `Jdbc` annotation for driver `" + clazz.getName() + "`");
			}

			m_drivers.put(
				jdbc.proto()
				,
				new DriverTuple(
					jdbc.clazz(),
					clazz.getName(),
					clazz.getConstructor(m_ctor)
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

		jdbcUrl = jdbcUrl.trim();

		int index = jdbcUrl.indexOf("://");

		if(index < 0) {
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		String jdbcProto = jdbcUrl.substring(0, index);

		/*-----------------------------------------------------------------*/
		/* CHECK DRIVER                                                    */
		/*-----------------------------------------------------------------*/

		if(m_drivers.containsKey(jdbcProto) == false) {
			throw new Exception("unknown JDBC protocol `" + jdbcProto + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CREATE DRIVER                                                   */
		/*-----------------------------------------------------------------*/

		return m_drivers.get(jdbcProto).z.newInstance(new Object[] {
			jdbcUrl,
			user,
			pass
		});

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String jdbcUrl, String user, String pass, String name) throws Exception {

		DriverAbstractClass result = getConnection(jdbcUrl, user, pass);

		result.useDB(name);

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listDrivers() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Entry<String, DriverTuple> entry: m_drivers.entrySet()) {

			String jdbcProto = entry.getKey();

			String jdbcClass = entry.getValue().x;
			String driverClass = entry.getValue().y;

			result.append(
				"<row>"
				+
				"<field name=\"jdbcProto\"><![CDATA[" + jdbcProto + "]]></field>"
				+
				"<field name=\"jdbcClass\"><![CDATA[" + jdbcClass + "]]></field>"
				+
				"<field name=\"driverClass\"><![CDATA[" + driverClass + "]]></field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
