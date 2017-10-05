package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Islets
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "§";

	/*---------------------------------------------------------------------*/

	private final Map<String, Query> m_map = new LinkedHashMap<>();

	private final String m_pkTable;

	/*---------------------------------------------------------------------*/

	public Islets(String pkTable)
	{
		m_pkTable = pkTable;
	}

	/*---------------------------------------------------------------------*/

	public Query getIslet(String fkColumn, String pkColumn)
	{
		Query result = m_map.get(fkColumn);

		if(result == null)
		{
			result = new Query().addSelectPart(pkColumn);

			m_map.put(fkColumn, result);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<Map.Entry<String, Query>> entrySet()
	{
		return m_map.entrySet();
	}

	/*---------------------------------------------------------------------*/

	public boolean isEmpty()
	{
		return m_map.isEmpty();
	}

	/*---------------------------------------------------------------------*/

	public String getPKTable()
	{
		return m_pkTable;
	}

	/*---------------------------------------------------------------------*/

	public List<String> toList()
	{
		String fkField;
		Query select;

		List<String> result = new ArrayList<>();

		for(Map.Entry<String, Query> entry: m_map.entrySet())
		{
			fkField = entry.getKey();
			select = entry.getValue();

			if(DUMMY.equals(fkField))
			{
				result.add(select.getWherePart());
			}
			else
			{
				result.add(fkField + "=(" + select.toString() + ")");
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return String.join(" AND ", toList());
	}

	/*---------------------------------------------------------------------*/
}
