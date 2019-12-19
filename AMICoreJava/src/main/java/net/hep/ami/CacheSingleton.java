package net.hep.ami;

import java.net.*;
import java.util.*;

import net.spy.memcached.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class CacheSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

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
		if(s_memcachedClient != null)
		{
			s_memcachedClient.shutdown();
		}

		if(ConfigSingleton.getProperty("memcached_enabled", false))
		{
			try
			{
				System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");

				s_memcachedClient = new MemcachedClient(new InetSocketAddress(
					ConfigSingleton.getProperty("memcached_host", "localhost"),
					ConfigSingleton.getProperty("memcached_port", 11211))
				);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);

				s_memcachedClient = null;
			}
		}
		else
		{
			s_memcachedClient = null;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("_, _ -> param2")
	public static Object set(@NotNull String key, @Nullable Object value)
	{
		if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.set(key, ConfigSingleton.getProperty("memcached_expiration", 3600), value);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}

		return value;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public static Object get(@NotNull String key)
	{
		if(s_memcachedClient != null)
		{
			try
			{
				return s_memcachedClient.get(key);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}

		return null;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void flush()
	{
		if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.flush();
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void flush(int delay)
	{
		if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.flush(delay);
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder getStatus()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"memcached\">");

		if(s_memcachedClient != null)
		{
			for(Map.Entry<SocketAddress, Map<String, String>> entry1: s_memcachedClient.getStats().entrySet())
			{
				result.append("<row>");

				result.append("<field name=\"addr\"><![CDATA[").append(entry1.getKey()).append("]]></field>");

				entry1.getValue().forEach((x, y) -> result.append("<field name=\"").append(Utility.escapeHTML(x)).append("\"><![CDATA[").append(y).append("]]></field>"));

				result.append("</row>");
			}
		}
		else
		{
			result.append("<row>");
			result.append("<field name=\"addr\"><![CDATA[]]></field>");
			result.append("</row>");
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
