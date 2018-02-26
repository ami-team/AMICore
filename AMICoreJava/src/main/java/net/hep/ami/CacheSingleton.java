package net.hep.ami;

import java.net.*;

import net.spy.memcached.*;

public class CacheSingleton
{
	/*---------------------------------------------------------------------*/

	private static final MemcachedClient s_memcachedClient = newMemcachedClient();

	/*---------------------------------------------------------------------*/

	static private MemcachedClient newMemcachedClient()
	{
		try
		{
			System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");

			return new MemcachedClient(new InetSocketAddress(
				ConfigSingleton.getProperty("memcached_host", "localhost"),
				ConfigSingleton.getProperty("memcached_port", 11211))
			);
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);
		}

		return null;
	}

	/*---------------------------------------------------------------------*/

	private CacheSingleton() {}

	/*---------------------------------------------------------------------*/

	public static Object put(String key, Object value)
	{
		if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.add(key, ConfigSingleton.getProperty("memcached_expiration", 3600), value); return value;
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}

		return null;
	}

	/*---------------------------------------------------------------------*/

	public static Object get(String key)
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

	/*---------------------------------------------------------------------*/

	public static String remove(String key)
	{
		if(s_memcachedClient != null)
		{
			try
			{
				s_memcachedClient.delete(key).getKey();
			}
			catch(Exception e)
			{
				LogSingleton.root.error(e.getMessage(), e);
			}
		}

		return null;
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
