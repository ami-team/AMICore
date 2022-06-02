package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class AMIMap<U, V> implements Map<U, V>, Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = 5473885106100385148L;

	/*----------------------------------------------------------------------------------------------------------------*/

	public enum Type
	{
		TREE_MAP,
		HASH_MAP,
		LINKED_HASH_MAP,
		CONCURRENT_HASH_MAP
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private final boolean m_isOrdered;

	private final Map<U, V> m_underlyingMap;

	private final Map<String, String> m_lowerCaseToOrigCaseMap;

	/*----------------------------------------------------------------------------------------------------------------*/

	public AMIMap(@NotNull Type type, boolean isOrdered, boolean isCaseInsensitive)
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

			case CONCURRENT_HASH_MAP:
				m_underlyingMap = new ConcurrentHashMap<>();
				break;

			default:
				throw new RuntimeException("invalid map type");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Set<String> newSet(@NotNull Type type, boolean isOrdered, boolean isCaseInsensitive)
	{
		return Collections.newSetFromMap(new AMIMap<>(type, isOrdered, isCaseInsensitive));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public int size()
	{
		return m_underlyingMap.size();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public boolean isEmpty()
	{
		return m_underlyingMap.isEmpty();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private Object _getKey(@NotNull Object key)
	{
		if(m_lowerCaseToOrigCaseMap != null && key instanceof String)
		{
			key = m_lowerCaseToOrigCaseMap.getOrDefault(((String) key).toLowerCase(), (String) key);
		}

		return key;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private Object _setKey(@NotNull Object key)
	{
		if(m_lowerCaseToOrigCaseMap != null && key instanceof String)
		{
			m_lowerCaseToOrigCaseMap.put(((String) key).toLowerCase(), (String) key);
		}

		return key;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public boolean containsKey(Object key)
	{
		return m_underlyingMap.containsKey(_getKey(key));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public boolean containsValue(Object value)
	{
		return m_underlyingMap.containsValue(value);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Override
	public V remove(@NotNull Object key)
	{
		return m_underlyingMap.remove(_getKey(key));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Override
	public V get(@NotNull Object key)
	{
		return m_underlyingMap.get(_getKey(key));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public V put(@NotNull U key, @Nullable V value)
	{
		return m_underlyingMap.put(
			(U) _setKey(key),
			value
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	@SuppressWarnings("unchecked")
	public void putAll(@NotNull Map<? extends U, ? extends V> map)
	{
		for(final Entry<? extends U, ? extends V> entry: map.entrySet())
		{
			m_underlyingMap.put(
				(U) _setKey(entry.getKey()),
				entry.getValue()
			);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public void clear()
	{
		m_underlyingMap.clear();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Set<Map.Entry<U, V>> entrySet()
	{
		Set<Map.Entry<U, V>> result;

		if(m_isOrdered)
		{
			result = new TreeSet<>((o1, o2) -> {

				String s1 = o1.getKey().toString();
				String s2 = o2.getKey().toString();

				return s1.compareTo(s2);
			});

			result.addAll(m_underlyingMap.entrySet());
		}
		else
		{
			result = m_underlyingMap.entrySet();
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public Set<U> keySet()
	{
		Set<U> result;

		if(m_isOrdered)
		{
			result = new TreeSet<>((o1, o2) -> {

				String s1 = o1.toString();
				String s2 = o2.toString();

				return s1.compareTo(s2);
			});

			result.addAll(m_underlyingMap.keySet());
		}
		else
		{
			result = m_underlyingMap.keySet();
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String toString()
	{
		return Utility.object2json(m_underlyingMap).toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
