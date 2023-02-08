package net.hep.ami;

import java.net.*;
import java.util.*;

import net.spy.memcached.*;

import redis.clients.jedis.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class CacheSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(CacheSingleton.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	static private JedisPooled s_redisClient = null;

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
			s_redisClient.getPool().close();
		}

		if(s_memcachedClient != null)
		{
			s_memcachedClient.shutdown();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		s_redisClient = null;

		s_memcachedClient = null;

		/*------------------------------------------------------------------------------------------------------------*/

		boolean redisEnabled = ConfigSingleton.getProperty("redis_enabled", false);

		boolean memcachedEnabled = ConfigSingleton.getProperty("memcached_enabled", false);

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/**/ if(redisEnabled)
			{
				s_redisClient = new JedisPooled(new HostAndPort(
					ConfigSingleton.getProperty("redis_host", "localhost"),
					ConfigSingleton.getProperty("redis_port", 6379)
				));
			}
			else if(memcachedEnabled)
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
			try
			{
				s_redisClient.set(key, value); s_redisClient.expire(key, ConfigSingleton.getProperty("redis_expiration", 3600));
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
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(s_redisClient != null)
		{
			try
			{
				return s_redisClient.get(key).toString();
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
				return s_memcachedClient.get(key).toString();
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return null;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void delete(@NotNull String key)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(s_redisClient != null)
		{
			try
			{
				s_redisClient.del(key);
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
			try
			{
				/* TODO */
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
			result.append("<row>");
			result.append("</row>");
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
				result.append("<row>");

				result.append("<field name=\"address\"><![CDATA[").append(entry.getKey()).append("]]></field>");

				entry.getValue().forEach((x, y) -> result.append("<field name=\"").append(Utility.escapeHTML(x)).append("\"><![CDATA[").append(y).append("]]></field>"));

				result.append("</row>");
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
