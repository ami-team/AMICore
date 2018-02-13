package net.hep.ami;

import java.net.*;

import net.spy.memcached.*;

public class CacheSingleton
{
	/*---------------------------------------------------------------------*/

	private static MemcachedClient s_memcachedClient;

	/*---------------------------------------------------------------------*/

	private CacheSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reconnect();
	}

	/*---------------------------------------------------------------------*/

	public static void reconnect()
	{
		try
		{
			s_memcachedClient = new MemcachedClient(new InetSocketAddress(
				ConfigSingleton.getProperty("memcached_host", "localhost"),
				ConfigSingleton.getProperty("memcached_port", 11211)
			));
		}
		catch(Exception e)
		{
			LogSingleton.root.info(e.getMessage(), e);

			s_memcachedClient = null;
		}
	}

	/*---------------------------------------------------------------------*/

	public static boolean isPresent()
	{
		return s_memcachedClient != null;
	}

	/*---------------------------------------------------------------------*/

	public static String put(String key, Object value)
	{
		try
		{
			return s_memcachedClient != null ? s_memcachedClient.add(key, ConfigSingleton.getProperty("memcached_expiration", 3600), value).getKey() : null;
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			return null;
		}
	}

	/*---------------------------------------------------------------------*/

	public static Object get(String key)
	{
		try
		{
			return s_memcachedClient != null ? s_memcachedClient.get(key) : null;
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			return null;
		}
	}

	/*---------------------------------------------------------------------*/

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

	/*---------------------------------------------------------------------*/

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

	/*---------------------------------------------------------------------*/
}
