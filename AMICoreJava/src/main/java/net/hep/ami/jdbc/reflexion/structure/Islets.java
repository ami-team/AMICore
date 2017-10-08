package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Islets
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "@";

	/*---------------------------------------------------------------------*/

	private final Map<String, Map<String, Joins>> m_map = new LinkedHashMap<>();

	/*---------------------------------------------------------------------*/

	public Joins getJoins(String fkColumn, String pkColumn)
	{
		/*----------------------------------------------------------------*/

		Map<String, Joins> result1 = m_map.get(fkColumn);

		if(result1 == null)
		{
			result1 = new LinkedHashMap<>();

			m_map.put(fkColumn, result1);
		}

		/*----------------------------------------------------------------*/

		Joins result2 = result1.get(pkColumn);

		if(result2 == null)
		{
			result2 = new Joins();

			result1.put(pkColumn, result2);
		}

		/*----------------------------------------------------------------*/

		return result2;
	}

	/*---------------------------------------------------------------------*/

	public Set<Map.Entry<String, Map<String, Joins>>> entrySet()
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
		String fkField;
		String pkField;

		Query result = new Query();

		for(Map.Entry<String, Map<String, Joins>> entry1: m_map.entrySet())
		{
			fkField = entry1.getKey();

			for(Map.Entry<String, Joins> entry2: entry1.getValue().entrySet())
			{
				pkField = entry2.getKey();

				if(DUMMY.equals(fkField))
				{
					result.addWholeQuery(entry2.getValue().toQuery());
				}
				else
				{
					result.addFromPart(new QId(fkField).toString(QId.Deepness.TABLE)).addWherePart(fkField + "=(" + entry2.getValue().toQuery().addSelectPart(pkField) + ")");
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
