package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class AMIMap<U, V> implements Map<U, V>, Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 5473885106100385148L;

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
	}

	/*---------------------------------------------------------------------*/

	private final boolean m_isOrdered;

	private final Map<U, V> m_underlyingMap;

	private final Map<Object, Object> m_lowerCaseToOrigCaseMap;

	/*---------------------------------------------------------------------*/

	public AMIMap(Type type, boolean isOrdered, boolean isCaseInsensitive)
	{
		m_isOrdered = isOrdered;

		m_lowerCaseToOrigCaseMap = isCaseInsensitive ? new HashMap<>() : null;

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
				throw new RuntimeException("invalid map type");
		}
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> newSet(Type type, boolean isOrdered, boolean isCaseInsensitive)
	{
		return Collections.newSetFromMap(new AMIMap<>(type, isOrdered, isCaseInsensitive));
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

	private Object _getKey(Object key)
	{
		if(m_lowerCaseToOrigCaseMap != null && key instanceof String)
		{
			Object tmp = m_lowerCaseToOrigCaseMap.get(((String) key).toLowerCase());

			if(tmp != null)
			{
				key = tmp;
			}
		}

		return key;
	}

	/*---------------------------------------------------------------------*/

	private Object _setKey(Object key)
	{
		if(m_lowerCaseToOrigCaseMap != null && key instanceof String)
		{
			m_lowerCaseToOrigCaseMap.put(((String) key).toLowerCase(), key);

		//	if(key != null)
		//	{
		//		key = key;
		//	}
		}

		return key;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean containsKey(Object key)
	{
		return m_underlyingMap.containsKey(_getKey(key));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public boolean containsValue(Object value)
	{
		return m_underlyingMap.containsValue(value);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public V remove(Object key)
	{
		return m_underlyingMap.remove(_getKey(key));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public V get(Object key)
	{
		return m_underlyingMap.get(_getKey(key));
	}

	/*---------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("unchecked")
	public V put(U key, V value)
	{
		return m_underlyingMap.put(
			(U) _setKey(key),
			value
		);
	}

	/*---------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("unchecked")
	public void putAll(Map<? extends U, ? extends V> map)
	{
		for(final Entry<? extends U, ? extends V> entry: map.entrySet())
		{
			m_underlyingMap.put(
				(U) _setKey(entry.getKey()),
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
	public Set<Map.Entry<U, V>> entrySet()
	{
		Set<Map.Entry<U, V>> result;

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
	public Set<U> keySet()
	{
		Set<U> result;

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
	public Collection<V> values()
	{
		Collection<V> result;

		if(m_isOrdered)
		{
			result = new ArrayList<>();

			for(U key: keySet())
			{
				result.add(m_underlyingMap.get(key));
			}
		}
		else
		{
			result = m_underlyingMap.values();
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return m_underlyingMap.toString();
	}

	/*---------------------------------------------------------------------*/
}
