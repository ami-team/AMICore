package net.hep.ami.utility;

import java.util.*;

public class MapEntryKeyComparator implements Comparator<Map.Entry<String, ?>>
{
	/*---------------------------------------------------------------------*/

	@Override
	public int compare(Map.Entry<String, ?> entry1, Map.Entry<String, ?> entry2)
	{
		return entry1.getKey().compareTo(entry2.getKey());
	}

	/*---------------------------------------------------------------------*/
}
