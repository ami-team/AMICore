package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import com.zaxxer.hikari.*;

public class ConnectionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static final class Tuple extends Tuple2<HikariDataSource, HikariPoolMXBean>
	{
		private static final long serialVersionUID = 315517018319324046L;

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

	public static Connection getConnection(@Nullable String catalog, String jdbcDriver, String jdbcUrl, String user, String pass) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(ConfigSingleton.getProperty("pool_enabled", true) == false)
		{
			return DriverManager.getConnection(jdbcUrl, user, pass);
		}

		/*-----------------------------------------------------------------*/

		Tuple tuple;

		String key = user + "@" + jdbcUrl;

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
		/**/		HikariConfig config = new HikariConfig();
		/**/
		/**/		/*---------------------------*/
		/**/		/* POOL - DATABASE           */
		/**/		/*---------------------------*/
		/**/
		/**/		config.setDriverClassName(jdbcDriver);
		/**/		config.setJdbcUrl(jdbcUrl);
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
		/**/			config.setPoolName(catalog.replace(":", "|"));
		/**/		}
		/**/
		/**/		config.setMaximumPoolSize(ConfigSingleton.getProperty("pool_size", 10));
		/**/
		/**/		config.setConnectionTimeout(ConfigSingleton.getProperty("pool_conn_timeout", 30000));
		/**/
		/**/		config./**/setIdleTimeout/**/(ConfigSingleton.getProperty("pool_idle_timeout", 600000));
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

		/*-----------------------------------------------------------------*/

		return tuple.x.getConnection();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static StringBuilder getStatus()
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset type=\"connectionPool\">");

		/*-----------------------------------------------------------------*/

		for(Tuple value: s_pools.values())
		{
			result.append("<row>")
			      .append("<field name=\"poolName\">").append(value.x.getPoolName()).append("</field>")
			      .append("<field name=\"poolSize\">").append(value.x.getMaximumPoolSize()).append("</field>")
			      .append("<field name=\"numIdle\">").append(value.y.getIdleConnections()).append("</field>")
			      .append("<field name=\"numActive\">").append(value.y.getActiveConnections()).append("</field>")
			      .append("<field name=\"connTimeout\">").append(value.x.getConnectionTimeout()).append("</field>")
			      .append("<field name=\"idleTimeout\">").append(value.x.getIdleTimeout()).append("</field>")
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
