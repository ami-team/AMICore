package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.reflexion.SchemaSingleton;

public class Islets
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "@";

	/*---------------------------------------------------------------------*/

	private final Map<String, Map<String, List<Query>>> m_map = new LinkedHashMap<>();

	/*---------------------------------------------------------------------*/

	public List<Query> getQuery(String from, String to)
	{
		/*-----------------------------------------------------------------*/

		Map<String, List<Query>> result1 = m_map.get(from);

		if(result1 == null)
		{
			result1 = new LinkedHashMap<>();

			m_map.put(from, result1);
		}

		/*-----------------------------------------------------------------*/

		List<Query> result2 = result1.get(to);

		if(result2 == null)
		{
			result2 = new ArrayList<>();

			result1.put(to, result2);
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
		QId  fromQId ;
/*		QId   toQId  ;
 */
		String from;
		String  to ;

		Query result = new Query();

		for(Map.Entry<String, Map<String, List<Query>>> entry1: m_map.entrySet())
		{
			from = entry1.getKey();

			fromQId = new QId(from, QId.Deepness.COLUMN);

			for(Map.Entry<String, List<Query>> entry2: entry1.getValue().entrySet())
			{
				to = entry2.getKey();

				if(DUMMY.equals(from))
				{
					for(Query query: entry2.getValue())
					{
						result.addWholeQuery(query);
					}
				}
				else
				{
					fromQId = new QId(from, QId.Deepness.COLUMN);
/*					 toQId  = new QId( to , QId.Deepness.COLUMN);
 */
					result.addFromPart(fromQId.toString(QId.Deepness.TABLE))
					      .addWherePart(fromQId.toString(QId.Deepness.COLUMN) + " IN (" + entry2.getValue().stream().map(x -> x.toString()).collect(Collectors.joining(" UNION ")) + ")")
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
