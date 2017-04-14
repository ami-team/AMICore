package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import com.zaxxer.hikari.*;

public class ConnectionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static class Tuple extends Tuple2<HikariDataSource, HikariPoolMXBean>
	{
		public Tuple(HikariDataSource _x, HikariPoolMXBean _y)
		{
			super(_x, _y);
		}
	}

	/*---------------------------------------------------------------------*/

	private static final javax.management.MBeanServer s_beanServer = java.lang.management.ManagementFactory.getPlatformMBeanServer();

	/*---------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_pools = new HashMap<>();

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
		Tuple tuple;

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
		/**/		/* POOL - DATABASE           */
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
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - PROPERTIES         */
		/**/		/*---------------------------*/
		/**/
		/**/		if(catalog != null)
		/**/		{
		/**/			config.setPoolName(catalog);
		/**/		}
		/**/
		/**/		if(properties == null || properties.containsKey("maximumPoolSize") == false) {
		/**/			config.setMaximumPoolSize(ConfigSingleton.getProperty("pool_size", 10));
		/**/		}
		/**/
		/**/		if(properties == null || properties.containsKey("connectionTimeout") == false) {
		/**/			config.setConnectionTimeout(ConfigSingleton.getProperty("conn_timeout", 30000));
		/**/		}
		/**/
		/**/		if(properties == null || properties.containsKey("idleTimeout") == false) {
		/**/			config.setIdleTimeout(ConfigSingleton.getProperty("idle_timeout", 600000));
		/**/		}
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - MONITORING         */
		/**/		/*---------------------------*/
		/**/
		/**/		config.setRegisterMbeans(true);
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
		/**/		s_pools.put(key, tuple = new Tuple(dataSource, javax.management.JMX.newMXBeanProxy(s_beanServer, new javax.management.ObjectName("com.zaxxer.hikari:type=Pool (" + dataSource.getPoolName() + ")"), HikariPoolMXBean.class)));
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
		int poolSize;
		int numIdle;
		int numActive;
		long connTimeout;
		long idleTimeout;

		for(Tuple value: s_pools.values())
		{
			poolName = value.x.getPoolName();
			poolSize = value.x.getMaximumPoolSize();
			numIdle = value.y.getIdleConnections();
			numActive = value.y.getActiveConnections();
			connTimeout = value.x.getConnectionTimeout();
			idleTimeout = value.x.getIdleTimeout();

			result.append("<row>")
			      .append("<field name=\"poolName\">").append(poolName).append("</field>")
			      .append("<field name=\"poolSize\">").append(poolSize).append("</field>")
			      .append("<field name=\"numIdle\">").append(numIdle).append("</field>")
			      .append("<field name=\"numActive\">").append(numActive).append("</field>")
			      .append("<field name=\"connTimeout\">").append(connTimeout).append("</field>")
			      .append("<field name=\"idleTimeout\">").append(idleTimeout).append("</field>")
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
