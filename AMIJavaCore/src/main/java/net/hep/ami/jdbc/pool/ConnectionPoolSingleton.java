package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;

import org.apache.tomcat.jdbc.pool.*;

public class ConnectionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static final int s_initialSize = ConfigSingleton.getProperty("initial_size", 1);
	// The initial number of connections that are created when the pool is started.

	private static final int s_maxActive = ConfigSingleton.getProperty("max_active", 100);
	// The maximum number of active connections that can be allocated from this pool at the same time.

	private static final int s_minIdle = ConfigSingleton.getProperty("min_idle", 10);
	// The minimum number of established connections that should be kept in the pool at all times.

	private static final int s_maxIdle = ConfigSingleton.getProperty("max_idle", 25);
	// The maximum number of connections that should be kept in the pool at all times.

	/*---------------------------------------------------------------------*/

	private static final int s_timeBetweenEvictionRunsMillis = ConfigSingleton.getProperty("time_between_eviction_runs_millis", 5000);
	// The number of milliseconds to sleep between runs of the idle connection validation/cleaner thread. This value should not be set under 1 second. It dictates how often we check for idle, abandoned connections, and how often we validate idle connections.

	private static final int s_minEvictableIdleTimeMillis = ConfigSingleton.getProperty("min_evictable_idle_time_millis", 30000);
	// The minimum amount of time an object may sit idle in the pool before it is eligible for eviction.

	private static final int s_validationInterval = ConfigSingleton.getProperty("validation_interval", 30000);
	// Avoid excess validation, only run validation at most at this frequency - time in milliseconds.

	private static final int s_maxWait = ConfigSingleton.getProperty("max_wait", 30000);
	// The maximum number of milliseconds that the pool will wait before exception.

	/*---------------------------------------------------------------------*/

	private static final Map<String, DataSource> s_pools = new HashMap<String, DataSource>();

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass) throws Exception
	{
		return getDataSource(
			catalog,
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass,
			/* POOL - CONTENT */
			s_initialSize,
			s_maxActive,
			s_minIdle,
			s_maxIdle,
			/* POOL - TIMING */
			s_timeBetweenEvictionRunsMillis,
			s_minEvictableIdleTimeMillis,
			s_validationInterval,
			s_maxWait

		).getConnection();
	}

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass, int initialSize, int maxActive, int minIdle, int maxIdle, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, int validationInterval, int maxWait) throws Exception
	{
		return getDataSource(
			catalog,
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

	public static DataSource getDataSource(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass)
	{
		return getDataSource(
			catalog,
			/* DATABASE */
			jdbc_driver,
			jdbc_url,
			user,
			pass,
			/* POOL - CONTENT */
			s_initialSize,
			s_maxActive,
			s_minIdle,
			s_maxIdle,
			/* POOL - TIMING */
			s_timeBetweenEvictionRunsMillis,
			s_minEvictableIdleTimeMillis,
			s_validationInterval,
			s_maxWait
		);
	}

	/*---------------------------------------------------------------------*/

	private static DataSource getDataSource(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass, int initialSize, int maxActive, int minIdle, int maxIdle, int timeBetweenEvictionRunsMillis, int minEvictableIdleTimeMillis, int validationInterval, int maxWait)
	{
		DataSource result;

		String key = user + "@" + jdbc_url;

		synchronized(ConnectionPoolSingleton.class)
		{
		/**/	result = s_pools.get(key);
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
		/**/		poolProperties.setUrl(jdbc_url);
		/**/		poolProperties.setUsername(user);
		/**/		poolProperties.setPassword(pass);
		/**/
		/**/		poolProperties.setDefaultAutoCommit(false);
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - NAME               */
		/**/		/*---------------------------*/
		/**/
		/**/		poolProperties.setName((catalog == null) ? UUID.randomUUID().toString() : catalog);
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
		/**/		/* CONNECTION TESTS          */
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
		/**/		poolProperties.setRemoveAbandoned(false);
		/**/		poolProperties.setLogAbandoned(false);
		/**/
		/**/		/*-----------------------------------------------------*/
		/**/		/* CREATE DATA SOURCE                                  */
		/**/		/*-----------------------------------------------------*/
		/**/
		/**/		s_pools.put(key, result = new DataSource());
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

		for(DataSource value: s_pools.values())
		{
			result.append(
				"<row>"
				+
				"<field name=\"name\">" + value.getName() + "</field>"
				+
				"<field name=\"poolSize\">" + value.getPoolSize() + "</field>"
				+
				"<field name=\"minIdle\">" + value.getMinIdle() + "</field>"
				+
				"<field name=\"maxIdle\">" + value.getMaxIdle() + "</field>"
				+
				"<field name=\"maxActive\">" + value.getMaxActive() + "</field>"
				+
				"<field name=\"timeBetweenEvictionRunsMillis\">" + value.getTimeBetweenEvictionRunsMillis() + "</field>"
				+
				"<field name=\"minEvictableIdleTimeMillis\">" + value.getMinEvictableIdleTimeMillis() + "</field>"
				+
				"<field name=\"validationInterval\">" + value.getValidationInterval() + "</field>"
				+
				"<field name=\"maxWait\">" + value.getMaxWait() + "</field>"
				+
				"<field name=\"numIdle\">" + value.getNumIdle() + "</field>"
				+
				"<field name=\"numActive\">" + value.getNumActive() + "</field>"
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
