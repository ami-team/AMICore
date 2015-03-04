package net.hep.ami.jdbc;

import java.util.*;
import java.lang.reflect.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;
import net.hep.ami.jdbc.driver.*;

public class BasicLoader implements JdbcInterface {
	/*---------------------------------------------------------------------*/

	private static Map<String, Constructor<DriverAbstractClass>> m_drivers = new HashMap<String, Constructor<DriverAbstractClass>>();

	/*---------------------------------------------------------------------*/

	private static final Class<?>[] m_ctor = new Class<?>[] { String.class, String.class, String.class };

	/*---------------------------------------------------------------------*/

	static {

		ClassFinder classFinder = new ClassFinder("net.hep.ami.jdbc.driver");

		for(String className: classFinder.getClassList()) {

			try {
				addDriverClass(className);
			} catch(Exception e) {
				LogSingleton.log(LogSingleton.LogLevel.ERROR, e.getMessage());
			}
		}
	}

	/*---------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	/*---------------------------------------------------------------------*/

	private static void addDriverClass(String className) throws Exception {

		Class<DriverAbstractClass> clazz = (Class<DriverAbstractClass>) Class.forName(className);

		if(isDriverClass(clazz)) {

			Jdbc annotation = clazz.getAnnotation(Jdbc.class);
			if(annotation == null) {
				throw new Exception("no `Jdbc` annotation for driver `" + clazz.getName() + "`");
			}

			m_drivers.put(
				annotation.protocol(),
				clazz.getConstructor(m_ctor)
			);
		}
	}

	/*---------------------------------------------------------------------*/

	private static boolean isDriverClass(Class<?> clazz) {

		boolean result = false;

		while((clazz = clazz.getSuperclass()) != null) {

			if(clazz == DriverAbstractClass.class) {
				result = true;
				break;
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private DriverAbstractClass m_driver;

	/*---------------------------------------------------------------------*/

	public BasicLoader(String jdbcUrl, String user, String pass) throws Exception {
		/*-----------------------------------------------------------------*/
		/* GET PROTOCOL                                                    */
		/*-----------------------------------------------------------------*/

		jdbcUrl = jdbcUrl.trim();

		int index = jdbcUrl.indexOf("://");

		if(index < 0) {
			throw new Exception("invalid JDBC URL `" + jdbcUrl + "`");
		}

		String jdbcProtocol = jdbcUrl.substring(0, index);

		/*-----------------------------------------------------------------*/
		/* CHECK DRIVER                                                    */
		/*-----------------------------------------------------------------*/

		if(m_drivers.containsKey(jdbcProtocol) == false) {
			throw new Exception("invalid JDBC protocol `" + jdbcProtocol + "`");
		}

		/*-----------------------------------------------------------------*/
		/* CREATE DRIVER                                                   */
		/*-----------------------------------------------------------------*/

		m_driver = m_drivers.get(jdbcProtocol).newInstance(new Object[] {
			jdbcUrl,
			user,
			pass
		});

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public void useDB(String db) throws Exception {

		m_driver.useDB(db);
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeQuery(String sql) throws Exception {

		return m_driver.executeQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	public QueryResult executeGLiteQuery(String sql) throws Exception {

		return m_driver.executeGLiteQuery(sql);
	}

	/*---------------------------------------------------------------------*/

	public void executeUpdate(String sql) throws Exception {

		m_driver.executeUpdate(sql);
	}

	/*---------------------------------------------------------------------*/

	public void commit() throws Exception {

		m_driver.commit();
	}

	/*---------------------------------------------------------------------*/

	public void rollback() throws Exception {

		m_driver.rollback();
	}

	/*---------------------------------------------------------------------*/

	public void retain() {

		m_driver.retain();
	}

	/*---------------------------------------------------------------------*/

	public void commitAndRelease() throws Exception {

		m_driver.commitAndRelease();
	}

	/*---------------------------------------------------------------------*/

	public void rollbackAndRelease() throws Exception {

		m_driver.rollbackAndRelease();
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcClassName() {

		return m_driver.getJdbcClassName();
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcProtocol() {

		return m_driver.getJdbcProtocol();
	}

	/*---------------------------------------------------------------------*/

	public String getJdbcUrl() {

		return m_driver.getJdbcUrl();
	}

	/*---------------------------------------------------------------------*/

	public String getUser() {

		return m_driver.getUser();
	}

	/*---------------------------------------------------------------------*/

	public String getPass() {

		return m_driver.getPass();
	}

	/*---------------------------------------------------------------------*/

	public String getDB() {

		return m_driver.getDB();
	}

	/*---------------------------------------------------------------------*/
}
