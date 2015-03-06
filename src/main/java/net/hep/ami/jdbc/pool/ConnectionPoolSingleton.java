package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;

import org.apache.tomcat.jdbc.pool.*;

public class ConnectionPoolSingleton {
	/*---------------------------------------------------------------------*/

	private static final int m_initialSizeDefault = 10;
	private static final int m_maxActiveDefault = 100;
	private static final int m_minIdleDefault = 10;
	private static final int m_maxIdleDefault = 100;

	private static final int m_timeBetweenEvictionRunsMillisDefault = 5000;
	private static final int m_minEvictableIdleTimeMillisDefault = 30000;
	private static final int m_validationIntervalDefault = 30000;
	private static final int m_maxWaitDefault = 10000;

	/*---------------------------------------------------------------------*/

	private static Map<String, DataSource> m_pools = new HashMap<String, DataSource>();

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(String jdbc_driver, String jdbc_url, String user, String pass) throws Exception {

		return getDataSource(
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass

		).getConnection();
	}

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(String jdbc_driver, String jdbc_url, String user, String pass, int initialSize, int maxActive, int minIdle, int maxIdle, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, int validationInterval, int maxWait) throws Exception {

		return getDataSource(
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass,
			/* POOL - CONTENT */
			initialSize,
			maxActive,
			minIdle,
			maxIdle,
			/* POOL - TIMING */
			timeBetweenEvictionRunsMillis,
			minEvictableIdleTimeMillis,
			validationInterval,
			maxWait

		).getConnection();
	}

	/*---------------------------------------------------------------------*/

	public static DataSource getDataSource(String jdbc_driver, String jdbc_url, String user, String pass) {

		return getDataSource(
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass,
			/* POOL - CONTENT */
			ConfigSingleton.getProperty("initial_size", m_initialSizeDefault),
			ConfigSingleton.getProperty("max_active", m_maxActiveDefault),
			ConfigSingleton.getProperty("min_idle", m_minIdleDefault),
			ConfigSingleton.getProperty("max_idle", m_maxIdleDefault),
			/* POOL - TIMING */
			ConfigSingleton.getProperty("time_between_eviction_runs_millis", m_timeBetweenEvictionRunsMillisDefault),
			ConfigSingleton.getProperty("min_evictable_idle_time_millis", m_minEvictableIdleTimeMillisDefault),
			ConfigSingleton.getProperty("validation_interval", m_validationIntervalDefault),
			ConfigSingleton.getProperty("max_wait", m_maxWaitDefault)
		);
	}

	/*---------------------------------------------------------------------*/

	private static DataSource getDataSource(String jdbc_driver, String jdbc_url, String user, String pass, int initialSize, int maxActive, int minIdle, int maxIdle, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, int validationInterval, int maxWait) {

		DataSource result;

		String key = jdbc_url + "@" + user;

		synchronized(ConnectionPoolSingleton.class) {

		/**/	result = m_pools.get(key);
		/**/
		/**/	if(result == null) {
		/**/		/*-----------------------------------------------------*/
		/**/		/* CREATE POOL PROPERTIES                              */
		/**/		/*-----------------------------------------------------*/
		/**/
		/**/		PoolProperties poolProperties = new PoolProperties();
		/**/
		/**/		/*---------------------------*/
		/**/		/* DATABASE                  */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setDriverClassName(jdbc_driver);
		/**/		poolProperties.setUrl(jdbc_url);
		/**/		poolProperties.setUsername(user);
		/**/		poolProperties.setPassword(pass);
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - CONTENT            */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setInitialSize(initialSize);
		/**/		poolProperties.setMaxActive(maxActive);
		/**/		poolProperties.setMinIdle(minIdle);
		/**/		poolProperties.setMaxIdle(maxIdle);
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - TIMING             */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		/**/		poolProperties.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		/**/		poolProperties.setValidationInterval(validationInterval);
		/**/		poolProperties.setMaxWait(maxWait);
		/**/
		/**/		/*---------------------------*/
		/**/		/* TESTS                     */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setTestOnBorrow(true);				/* The indication of whether objects will be validated before being borrowed from the pool. */
		/**/		poolProperties.setTestOnReturn(true);				/* The indication of whether objects will be validated after being returned to the pool. */
		/**/		poolProperties.setTestOnConnect(false);				/* Set to true if query validation should take place the first time on a connection. */
		/**/		poolProperties.setTestWhileIdle(true);				/* Set to true if query validation should take place while the connection is idle. */
		/**/
		/**/		poolProperties.setValidationQuery("SELECT 1");
		/**/
		/**/		/*---------------------------*/
		/**/		/* ABANDONED CONNECTIONS     */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setLogAbandoned(true);
		/**/		poolProperties.setRemoveAbandoned(true);
		/**/		poolProperties.setRemoveAbandonedTimeout(60);
		/**/
		/**/		/*-----------------------------------------------------*/
		/**/		/* CREATE DATA SOURCE                                  */
		/**/		/*-----------------------------------------------------*/
		/**/
		/**/		m_pools.put(key, result = new DataSource());
		/**/
		/**/		result.setPoolProperties(poolProperties);
		/**/
		/**/		SchemaSingleton.readSchema(result);
		/**/
		/**/		/*-----------------------------------------------------*/
		/**/	}

		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String getStatus() {

		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<Result><rowset>");

		/*-----------------------------------------------------------------*/

		for(DataSource entry: m_pools.values()) {

			result.append(
				"<row>"
				+
				"<field name=\"url\">" + entry.getUrl() + "</field>"
				+
				"<field name=\"name\">" + entry.getUsername() + "</field>"
				+
				"<field name=\"poolSize\">" + entry.getPoolSize() + "</field>"
				+
				"<field name=\"minIdle\">" + entry.getMinIdle() + "</field>"
				+
				"<field name=\"maxIdle\">" + entry.getMaxIdle() + "</field>"
				+
				"<field name=\"maxActive\">" + entry.getMaxActive() + "</field>"
				+
				"<field name=\"timeBetweenEvictionRunsMillis\">" + entry.getTimeBetweenEvictionRunsMillis() + "</field>"
				+
				"<field name=\"minEvictableIdleTimeMillis\">" + entry.getMinEvictableIdleTimeMillis() + "</field>"
				+
				"<field name=\"validationInterval\">" + entry.getValidationInterval() + "</field>"
				+
				"<field name=\"maxWait\">" + entry.getMaxWait() + "</field>"
				+
				"<field name=\"numIdle\">" + entry.getNumIdle() + "</field>"
				+
				"<field name=\"numActive\">" + entry.getNumActive() + "</field>"
				+
				"</row>"
			);
		}

		/*-----------------------------------------------------------------*/

		result.append("</rowset></Result>");

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
