package net.hep.ami.jdbc.pool;

import lombok.*;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;

import com.zaxxer.hikari.*;

import org.jetbrains.annotations.*;

public class ConnectionPoolSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	private static final class Tuple
	{
		@NotNull private final HikariDataSource dataSource;
		@NotNull private final HikariPoolMXBean poolMXBean;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final javax.management.MBeanServer s_beanServer = java.lang.management.ManagementFactory.getPlatformMBeanServer();

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, Tuple> s_pools = new HashMap<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private ConnectionPoolSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static Connection getConnection(@Nullable String catalog, @NotNull String jdbcDriver, @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(!ConfigSingleton.getProperty("pool_enabled", true) || !ConfigSingleton.getSystemProperty("ami.pool.enabled", true))
		{
			return DriverManager.getConnection(jdbcUrl, user, pass);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple tuple;

		String key = user + "@" + jdbcUrl;

		synchronized(ConnectionPoolSingleton.class)
		{
		/**/	tuple = s_pools.get(key);
		/**/
		/**/	if(tuple == null)
		/**/	{
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/		/* CREATE POOL PROPERTIES                                                                         */
		/**/		/*------------------------------------------------------------------------------------------------*/
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
		/**/		config.setMinimumIdle(ConfigSingleton.getProperty("pool_min_idle", 10));
		/**/
		/**/		config.setMaximumPoolSize(ConfigSingleton.getProperty("pool_max_size", 50));
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
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/		/* CREATE DATA SOURCE                                                                             */
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/
		/**/		HikariDataSource dataSource = new HikariDataSource(config);
		/**/
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/		/* REGISTER DATA SOURCE                                                                           */
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/
		/**/		s_pools.put(key, tuple = new Tuple(dataSource, javax.management.JMX.newMXBeanProxy(s_beanServer, new javax.management.ObjectName("com.zaxxer.hikari:type=Pool (" + dataSource.getPoolName() + ")"), HikariPoolMXBean.class)));
		/**/
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/	}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return tuple.getDataSource().getConnection();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static StringBuilder getStatus()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"connectionPool\">");

		for(Tuple value: s_pools.values())
		{
			result.append("<row>")
			      .append("<field name=\"name\">").append(value.getDataSource().getPoolName()).append("</field>")
			      .append("<field name=\"minIdle\">").append(value.getDataSource().getMinimumIdle()).append("</field>")
			      .append("<field name=\"maxSize\">").append(value.getDataSource().getMaximumPoolSize()).append("</field>")
			      .append("<field name=\"numIdle\">").append(value.getPoolMXBean().getIdleConnections()).append("</field>")
			      .append("<field name=\"numActive\">").append(value.getPoolMXBean().getActiveConnections()).append("</field>")
			      .append("<field name=\"connTimeout\">").append(value.getDataSource().getConnectionTimeout()).append("</field>")
			      .append("<field name=\"idleTimeout\">").append(value.getDataSource().getIdleTimeout()).append("</field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
