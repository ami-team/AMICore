package net.hep.ami.utility;

import java.util.*;

public class ConcurrentHashMap<U, V> extends java.util.concurrent.ConcurrentHashMap<U, V>
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -1020700776762564817L;

	/*---------------------------------------------------------------------*/

	private static class AlphabeticalComparator<U, V> implements java.util.Comparator<Map.Entry<U, V>>
	{
		@Override
		public int compare(Map.Entry<U, V> entry1, Map.Entry<U, V> entry2)
		{
			String s1 = entry1.getKey().toString();
			String s2 = entry2.getKey().toString();

			return s1.compareTo(s2);
		}
	}

	/*---------------------------------------------------------------------*/

	public ConcurrentHashMap()
	{
		super();
	}

	/*---------------------------------------------------------------------*/

	public ConcurrentHashMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	/*---------------------------------------------------------------------*/

	public ConcurrentHashMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/*---------------------------------------------------------------------*/

	public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
	{
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	/*---------------------------------------------------------------------*/

	public ConcurrentHashMap(Map<? extends U, ? extends V> m)
	{
		super(m);
	}

	/*---------------------------------------------------------------------*/

	public Set<Map.Entry<U, V>> entrySet()
	{
		Set<Map.Entry<U, V>> result = new TreeSet<>(new AlphabeticalComparator<>());

		result.addAll(super.entrySet());

		return result;
	}

	/*---------------------------------------------------------------------*/
}
