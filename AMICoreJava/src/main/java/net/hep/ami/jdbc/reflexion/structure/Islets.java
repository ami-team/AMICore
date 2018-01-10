package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;
import java.util.stream.*;

public class Islets
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "@";

	/*---------------------------------------------------------------------*/

	private final Map<String, Map<String, List<Query>>> m_map = new LinkedHashMap<>();

	/*---------------------------------------------------------------------*/

	public List<Query> getQuery(String fkColumn, String pkColumn)
	{
		/*-----------------------------------------------------------------*/

		Map<String, List<Query>> result1 = m_map.get(fkColumn);

		if(result1 == null)
		{
			result1 = new LinkedHashMap<>();

			m_map.put(fkColumn, result1);
		}

		/*-----------------------------------------------------------------*/

		List<Query> result2 = result1.get(pkColumn);

		if(result2 == null)
		{
			result2 = new ArrayList<>();

			result1.put(pkColumn, result2);
		}

		/*-----------------------------------------------------------------*/

		return result2;
	}

	/*---------------------------------------------------------------------*/

	public Set<Map.Entry<String, Map<String, List<Query>>>> entrySet()
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

		String fkField;

		Query result = new Query();

		for(Map.Entry<String, Map<String, List<Query>>> entry1: m_map.entrySet())
		{
			fkField = entry1.getKey();

			for(Map.Entry<String, List<Query>> entry2: entry1.getValue().entrySet())
			{
				if(DUMMY.equals(fkField))
				{
					for(Query query: entry2.getValue())
					{
						result.addWholeQuery(query);
					}
				}
				else
				{
					fQId = new QId(fkField, QId.Deepness.COLUMN);

					result.addFromPart(fQId.toString(QId.Deepness.TABLE))
					      .addWherePart(fQId.toString(QId.Deepness.COLUMN) + " IN (" + entry2.getValue().stream().map(x -> x.toString()).collect(Collectors.joining(" UNION ")) + ")")
					;
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
