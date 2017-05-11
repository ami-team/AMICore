package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class AMIHashMap<U> implements Map<String, U>, Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -3027398317853307355L;

	/*---------------------------------------------------------------------*/

	private static class StringComparator<U> implements Comparator<U>
	{
		@Override
		public int compare(U o1, U o2)
		{
			String s1 = o1.toString();
			String s2 = o2.toString();

			return s1.compareTo(s2);
		}
	}

	/*---------------------------------------------------------------------*/

	private static class EntryComparator<U, V> implements Comparator<Map.Entry<U, V>>
	{
		@Override
		public int compare(Map.Entry<U, V> o1, Map.Entry<U, V> o2)
		{
			String s1 = o1.getKey().toString();
			String s2 = o2.getKey().toString();

			return s1.compareTo(s2);
		}
	}

	/*---------------------------------------------------------------------*/

	public enum Type
	{
		TREE_MAP,
		HASH_MAP,
		LINKED_HASH_MAP,
		CONCURENT_HASH_MAP
	};

	/*---------------------------------------------------------------------*/

	private final boolean m_isOrdered;

	private final boolean m_isCaseInsensitive;

	private final Map<String, U> m_underlyingMap;

	/*---------------------------------------------------------------------*/

	public AMIHashMap()
	{
		this(Type.CONCURENT_HASH_MAP, true, false);
	}

	/*---------------------------------------------------------------------*/

	public AMIHashMap(Type type, boolean isOrdered, boolean isCaseInsensitive)
	{
		m_isOrdered = isOrdered;

		m_isCaseInsensitive = isCaseInsensitive;

		switch(type)
		{
			case TREE_MAP:
				m_underlyingMap = new TreeMap<>();
				break;

			case HASH_MAP:
				m_underlyingMap = new HashMap<>();
				break;

			case LINKED_HASH_MAP:
				m_underlyingMap = new LinkedHashMap<>();
				break;

			case CONCURENT_HASH_MAP:
				m_underlyingMap = new ConcurrentHashMap<>();
				break;

			default:
				throw new RuntimeException("invalid ap type");
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int size()
	{
		return m_underlyingMap.size();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean isEmpty()
	{
		return m_underlyingMap.isEmpty();
	}

	/*---------------------------------------------------------------------*/

	private String _key(Object key)
	{
		return m_isCaseInsensitive ? key.toString()
		                                .toLowerCase()
		                           : key.toString()
		;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean containsKey(Object key)
	{
		return m_underlyingMap.containsKey(_key(key));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean containsValue(Object value)
	{
		return m_underlyingMap.containsValue(value);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public U remove(Object key)
	{
		return m_underlyingMap.remove(_key(key));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public U get(Object key)
	{
		return m_underlyingMap.get(_key(key));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public U put(String key, U value)
	{
		return m_underlyingMap.put(
			_key(key),
			value
		);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void putAll(Map<? extends String, ? extends U> map)
	{
		for(final Entry<? extends String, ? extends U> entry: map.entrySet())
		{
			m_underlyingMap.put(
				_key(entry.getKey()),
				entry.getValue()
			);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void clear()
	{
		m_underlyingMap.clear();
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Set<Map.Entry<String, U>> entrySet()
	{
		Set<Map.Entry<String, U>> result;

		if(m_isOrdered)
		{
			result = new TreeSet<>(new EntryComparator<>());

			result.addAll(m_underlyingMap.entrySet());
		}
		else
		{
			result = m_underlyingMap.entrySet();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Set<String> keySet()
	{
		Set<String> result;

		if(m_isOrdered)
		{
			result = new TreeSet<>(new StringComparator<>());

			result.addAll(m_underlyingMap.keySet());
		}
		else
		{
			result = m_underlyingMap.keySet();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public Collection<U> values()
	{
		return m_underlyingMap.values();
	}

	/*---------------------------------------------------------------------*/
}
