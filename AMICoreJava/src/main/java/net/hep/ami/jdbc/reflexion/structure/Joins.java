package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Joins
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "§";

	/*---------------------------------------------------------------------*/

	private final Map<String, Map<String, Query>> m_map = new LinkedHashMap<>();

	/*---------------------------------------------------------------------*/

	public Query getQuery(String fkTable, String pkTable)
	{
		/*----------------------------------------------------------------*/

		Map<String, Query> result1 = m_map.get(fkTable);

		if(result1 == null)
		{
			result1 = new LinkedHashMap<>();

			m_map.put(fkTable, result1);
		}

		/*----------------------------------------------------------------*/

		Query result2 = result1.get(pkTable);

		if(result2 == null)
		{
			result2 = new Query();

			result1.put(pkTable, result2);
		}

		/*----------------------------------------------------------------*/

		return result2;
	}

	/*---------------------------------------------------------------------*/

	public Set<Map.Entry<String, Map<String, Query>>> entrySet()
	{
		return m_map.entrySet();
	}

	/*---------------------------------------------------------------------*/

	public boolean isEmpty()
	{
		return m_map.isEmpty();
	}

	/*---------------------------------------------------------------------*/

	public Query toQuery()
	{
		QId fQId;
		QId pQId;

		String fkTable;
		String pkTable;

		Query result = new Query();

		for(Map.Entry<String, Map<String, Query>> entry1: m_map.entrySet())
		{
			fkTable = entry1.getKey();

			for(Map.Entry<String, Query> entry2: entry1.getValue().entrySet())
			{
				pkTable = entry2.getKey();

				if(DUMMY.equals(fkTable))
				{
					result.addWholeQuery(entry2.getValue());
				}
				else
				{
					fQId = new QId(fkTable, QId.Deepness.TABLE);
					pQId = new QId(pkTable, QId.Deepness.TABLE);

					result.addFromPart(fQId.toString()).addFromPart(pQId.toString()).addWherePart(entry2.getValue().getWherePart());
				}
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return toQuery().toString();
	}

	/*---------------------------------------------------------------------*/
}
