package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;

import org.apache.tomcat.jdbc.pool.*;

public class ConnectionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static final int m_initialSize = ConfigSingleton.getProperty("initial_size", 10);
	private static final int m_maxActive = ConfigSingleton.getProperty("max_active", 100);
	private static final int m_minIdle = ConfigSingleton.getProperty("min_idle", 10);
	private static final int m_maxIdle = ConfigSingleton.getProperty("max_idle", 100);

	private static final int m_timeBetweenEvictionRunsMillis = ConfigSingleton.getProperty("time_between_eviction_runs_millis", 5000);
	private static final int m_minEvictableIdleTimeMillis = ConfigSingleton.getProperty("min_evictable_idle_time_millis", 30000);
	private static final int m_validationInterval = ConfigSingleton.getProperty("validation_interval", 30000);
	private static final int m_maxWait = ConfigSingleton.getProperty("max_wait", 10000);

	/*---------------------------------------------------------------------*/

	private static final Map<String, DataSource> m_pools = new HashMap<String, DataSource>();

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(String jdbc_driver, String jdbc_url, String user, String pass) throws Exception
	{
		return getDataSource(
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass,
			/* POOL - CONTENT */
			m_initialSize,
			m_maxActive,
			m_minIdle,
			m_maxIdle,
			/* POOL - TIMING */
			m_timeBetweenEvictionRunsMillis,
			m_minEvictableIdleTimeMillis,
			m_validationInterval,
			m_maxWait

		).getConnection();
	}

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(String jdbc_driver, String jdbc_url, String user, String pass, int initialSize, int maxActive, int minIdle, int maxIdle, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, int validationInterval, int maxWait) throws Exception
	{
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

	public static DataSource getDataSource(String jdbc_driver, String jdbc_url, String user, String pass)
	{
		return getDataSource(
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass,
			/* POOL - CONTENT */
			m_initialSize,
			m_maxActive,
			m_minIdle,
			m_maxIdle,
			/* POOL - TIMING */
			m_timeBetweenEvictionRunsMillis,
			m_minEvictableIdleTimeMillis,
			m_validationInterval,
			m_maxWait
		);
	}

	/*---------------------------------------------------------------------*/

	private static DataSource getDataSource(String jdbc_driver, String jdbc_url, String user, String pass, int initialSize, int maxActive, int minIdle, int maxIdle, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, int validationInterval, int maxWait)
	{
		DataSource result;

		String key = jdbc_url + "@" + user;

		synchronized(ConnectionPoolSingleton.class)
		{
		/**/	result = m_pools.get(key);
		/**/
		/**/	if(result == null)
		/**/	{
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
		/**/
		/**/		/*---------------------------*/
		/**/
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
		/**/		poolProperties.setTestOnConnect(true);				/* Set to true if query validation should take place the first time on a connection. */
		/**/		poolProperties.setTestWhileIdle(true);				/* Set to true if query validation should take place while the connection is idle. */
		/**/
		/**/		poolProperties.setValidationQuery("SELECT 1");
		/**/
		/**/		/*---------------------------*/
		/**/		/* ABANDONED CONNECTIONS     */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setLogAbandoned(true);
		/**/		poolProperties.setRemoveAbandoned(false);			/* A revoir !!! */
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
		/**/		/*-----------------------------------------------------*/
		/**/	}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStatus()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"connectionPool\">");

		/*-----------------------------------------------------------------*/

		for(DataSource entry: m_pools.values())
		{
			result.append(
				"<row>"
				+
				"<field name=\"url\">" + entry.getDefaultCatalog() + "</field>"
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

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
