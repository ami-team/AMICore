package net.hep.ami;

import java.net.*;
import java.util.*;

import net.spy.memcached.*;

import redis.clients.jedis.*;

import org.jetbrains.annotations.*;

public class CacheSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(CacheSingleton.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	static private JedisPool s_redisClient = null;

	static private MemcachedClient s_memcachedClient = null;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private CacheSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");

		/*------------------------------------------------------------------------------------------------------------*/

		if(s_redisClient != null)
		{
			s_redisClient.close();
		}

		if(s_memcachedClient != null)
		{
			s_memcachedClient.shutdown();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_redisClient = null;

		s_memcachedClient = null;

		/*------------------------------------------------------------------------------------------------------------*/

		String type = ConfigSingleton.getProperty("command_cache_type", "0").trim().toUpperCase();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/**/ if("1".equals(type) || "REDIS".equals(type))
			{
				s_redisClient = new JedisPool(
					ConfigSingleton.getProperty("redis_host", "localhost"),
					ConfigSingleton.getProperty("redis_port", 6379)
				);
			}
			/**/ if("2".equals(type) || "MEMCACHED".equals(type))
			{
				s_memcachedClient = new MemcachedClient(new InetSocketAddress(
					ConfigSingleton.getProperty("memcached_host", "localhost"),
					ConfigSingleton.getProperty("memcached_port", 11211)
				));
			}
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("_, _ -> param2")
	public static String set(@NotNull String key, @Nullable String value)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(s_redisClient != null)
		{
			try(Jedis jedis = s_redisClient.getResource())
			{
				jedis.setex(key, ConfigSingleton.getProperty("redis_expiration", 3600), value);
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		else if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.set(key, ConfigSingleton.getProperty("memcached_expiration", 3600), value);
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return value;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public static String get(@NotNull String key)
	{
		Object result = null;

		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(s_redisClient != null)
		{
			try(Jedis jedis = s_redisClient.getResource())
			{
				result = jedis.get(key);
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		else if(s_memcachedClient != null)
		{
			try
			{
				result = s_memcachedClient.get(key);
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result != null ? result.toString() : null;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void delete(@NotNull String key)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(s_redisClient != null)
		{
			try(Jedis jedis = s_redisClient.getResource())
			{
				jedis.del(key);
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		else if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.delete(key);
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void flush()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(s_redisClient != null)
		{
			try(Jedis jedis = s_redisClient.getResource())
			{
				jedis.flushAll();
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		else if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.flush();
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder getStatus()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"redis\">");

		if(s_redisClient != null)
		{
			try(Jedis jedis = s_redisClient.getResource())
			{
				result.append("<row>")
					  .append("<field name=\"address\"><![CDATA[").append(jedis.getClient().toString()).append("]]></field>")
					  .append("<field name=\"size\"><![CDATA[").append(jedis.dbSize()).append("]]></field>")
				      .append("</row>")
				;
			}
		}
		else
		{
			result.append("<row>");
			result.append("<field name=\"address\"><![CDATA[]]></field>");
			result.append("</row>");
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"memcached\">");

		if(s_memcachedClient != null)
		{
			for(Map.Entry<SocketAddress, Map<String, String>> entry: s_memcachedClient.getStats().entrySet())
			{
				result.append("<row>")
				      .append("<field name=\"address\"><![CDATA[").append(entry.getKey()).append("]]></field>")
				      .append("<field name=\"size\"><![CDATA[").append(entry.getValue().getOrDefault("curr_items", "")).append("]]></field>")
				      .append("</row>")
				;
			}
		}
		else
		{
			result.append("<row>");
			result.append("<field name=\"address\"><![CDATA[]]></field>");
			result.append("</row>");
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
