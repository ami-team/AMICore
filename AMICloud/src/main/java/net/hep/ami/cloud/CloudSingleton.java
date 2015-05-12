package net.hep.ami.cloud;

import java.util.*;
import java.util.Map.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.cloud.driver.*;
import net.hep.ami.cloud.driver.annotation.*;

public class CloudSingleton {
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple2<String, Constructor<DriverInterface>> {

		public Tuple(String _x, Constructor<DriverInterface> _y) {
			super(_x, _y);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> m_drivers = new HashMap<String, Tuple>();

	/*---------------------------------------------------------------------*/

	static {

		Set<String> classNames = ClassFinder.findClassNames("net.hep.ami.cloud.driver");

		for(String className: classNames) {

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

		Class<DriverInterface> clazz = (Class<DriverInterface>) Class.forName(className);

		/*-----------------------------------------------------------------*/
		/* ADD DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		if(ClassFinder.implementsInterface(clazz, DriverInterface.class)) {

			Engine engine = clazz.getAnnotation(Engine.class);

			if(engine == null) {
				throw new Exception("no `Engine` annotation for driver `" + clazz.getName() + "`");
			}

			m_drivers.put(
				engine.name()
				,
				new Tuple(
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

	public static DriverInterface getConnection(String name, String endpoint, String identity, String credential) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET DRIVER                                                      */
		/*-----------------------------------------------------------------*/

		Tuple tuple = m_drivers.get(name);

		if(tuple == null) {
			throw new Exception("unknown engine name `" + name + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CONNECTION                                                      */
		/*-----------------------------------------------------------------*/

		return tuple.y.newInstance(
			endpoint,
			identity,
			credential
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder listDrivers() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(Entry<String, Tuple> entry: m_drivers.entrySet()) {

			String engineName = entry.getKey();

			String engineClass = entry.getValue().x;

			result.append(
				"<row>"
				+
				"<field name=\"engineName\"><![CDATA[" + engineName + "]]></field>"
				+
				"<field name=\"engineClass\"><![CDATA[" + engineClass + "]]></field>"
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
