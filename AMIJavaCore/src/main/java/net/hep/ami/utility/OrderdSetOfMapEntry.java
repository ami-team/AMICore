package net.hep.ami.utility;

import java.util.*;

public class OrderdSetOfMapEntry<K> extends TreeSet<Map.Entry<String, K>>
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 2167983233072566427L;

	/*---------------------------------------------------------------------*/

	private static class MapEntryComparator<K> implements Comparator<Map.Entry<String, K>>
	{
		@Override
		public int compare(Map.Entry<String, K> entry1, Map.Entry<String, K> entry2)
		{
			return entry1.getKey().compareTo(entry2.getKey());
		}
	}

	/*---------------------------------------------------------------------*/

	public OrderdSetOfMapEntry(Collection<? extends Map.Entry<String, K>> collection)
	{
		super(new MapEntryComparator<K>());

		addAll(collection);
	}

	/*---------------------------------------------------------------------*/
}
