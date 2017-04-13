package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import com.zaxxer.hikari.*;

public class ConnectionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple2<HikariDataSource, HikariPoolMXBean>> s_pools = new HashMap<>();

	/*---------------------------------------------------------------------*/

	private static final javax.management.MBeanServer s_beanServer = java.lang.management.ManagementFactory.getPlatformMBeanServer();

	/*---------------------------------------------------------------------*/

	private ConnectionPoolSingleton() {}

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass) throws Exception
	{
		return getDataSource(catalog, jdbc_driver, jdbc_url, user, pass, null).getConnection();
	}

	/*---------------------------------------------------------------------*/

	public static Connection getConnection(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass, @Nullable Properties properties) throws Exception
	{
		return getDataSource(catalog, jdbc_driver, jdbc_url, user, pass, properties).getConnection();
	}

	/*---------------------------------------------------------------------*/

	private static HikariDataSource getDataSource(@Nullable String catalog, String jdbc_driver, String jdbc_url, String user, String pass, @Nullable Properties properties) throws Exception
	{
		Tuple2<HikariDataSource, HikariPoolMXBean> tuple;

		String key = user + "@" + jdbc_url;

		synchronized(ConnectionPoolSingleton.class)
		{
		/**/	tuple = s_pools.get(key);
		/**/
		/**/	if(tuple == null)
		/**/	{
		/**/		/*-----------------------------------------------------*/
		/**/		/* CREATE POOL PROPERTIES                              */
		/**/		/*-----------------------------------------------------*/
		/**/
		/**/		HikariConfig config = (properties == null) ? new HikariConfig(/*------*/)
		/**/		                                           : new HikariConfig(properties)
		/**/		;
		/**/
		/**/		/*---------------------------*/
		/**/		/* DATABASE                  */
		/**/		/*---------------------------*/
		/**/
		/**/		config.setDriverClassName(jdbc_driver);
		/**/		config.setJdbcUrl(jdbc_url);
		/**/		config.setUsername(user);
		/**/		config.setPassword(pass);
		/**/
		/**/		/*---------------------------*/
		/**/
		/**/		config.setAutoCommit(false);
		/**/		config.setRegisterMbeans(true);
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - NAME               */
		/**/		/*---------------------------*/
		/**/
		/**/		if(catalog != null)
		/**/		{
		/**/			config.setPoolName(catalog);
		/**/		}
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - PROPERTIES         */
		/**/		/*---------------------------*/
		/**/
		/**/		if(properties == null || properties.containsKey("connectionTimeout") == false) {
		/**/			config.setConnectionTimeout(ConfigSingleton.getProperty("connection_timeout", 30000));
		/**/		}
		/**/
		/**/		if(properties == null || properties.containsKey("idleTimeout") == false) {
		/**/			config.setIdleTimeout(ConfigSingleton.getProperty("idle_timeout", 600000));
		/**/		}
		/**/
		/**/		if(properties == null || properties.containsKey("maximumPoolSize") == false) {
		/**/			config.setMaximumPoolSize(ConfigSingleton.getProperty("maximum_pool_size", 10));
		/**/		}
		/**/
		/**/		/*-----------------------------------------------------*/
		/**/		/* CREATE DATA SOURCE                                  */
		/**/		/*-----------------------------------------------------*/
		/**/
		/**/		HikariDataSource dataSource = new HikariDataSource(config);
		/**/
		/**/		/*-----------------------------------------------------*/
		/**/		/* REGISTER DATA SOURCE                                */
		/**/		/*-----------------------------------------------------*/
		/**/
		/**/		s_pools.put(key, tuple = new Tuple2<HikariDataSource, HikariPoolMXBean>(dataSource, javax.management.JMX.newMXBeanProxy(s_beanServer, new javax.management.ObjectName("com.zaxxer.hikari:type=Pool (" + dataSource.getPoolName() + ")"), HikariPoolMXBean.class)));
		/**/
		/**/		/*-----------------------------------------------------*/
		/**/	}
		}

		return tuple.x;
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStatus()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"connectionPool\">");

		/*-----------------------------------------------------------------*/

		String poolName;
		long connectionTimeout;
		long idleTimeout;
		int maximumPoolSize;
		int numIdle;
		int numActive;

		for(Tuple2<HikariDataSource, HikariPoolMXBean> value: s_pools.values())
		{
			poolName = value.x.getPoolName();
			connectionTimeout = value.x.getConnectionTimeout();
			idleTimeout = value.x.getIdleTimeout();
			maximumPoolSize = value.x.getMaximumPoolSize();
			numIdle = value.y.getIdleConnections();
			numActive = value.y.getActiveConnections();

			result.append("<row>")
			      .append("<field name=\"name\">").append(poolName).append("</field>")
			      .append("<field name=\"connectionTimeout\">").append(connectionTimeout).append("</field>")
			      .append("<field name=\"idleTimeout\">").append(idleTimeout).append("</field>")
			      .append("<field name=\"maximumPoolSize\">").append(maximumPoolSize).append("</field>")
			      .append("<field name=\"numIdle\">").append(numIdle).append("</field>")
			      .append("<field name=\"numActive\">").append(numActive).append("</field>")
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
