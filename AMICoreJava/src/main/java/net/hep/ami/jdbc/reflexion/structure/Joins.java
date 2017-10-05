package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Joins
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "§";

	/*---------------------------------------------------------------------*/

	private final Map<String, Islets> m_map = new LinkedHashMap<>();

	private final String m_pkCatalog;

	/*---------------------------------------------------------------------*/

	public Joins(String pkCatalog)
	{
		m_pkCatalog = pkCatalog;
	}

	/*---------------------------------------------------------------------*/

	public Islets getJoin(String fkTable, String pkTable)
	{
		Islets result = m_map.get(fkTable);

		if(result == null)
		{
			result = new Islets(pkTable);

			m_map.put(fkTable, result);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<Map.Entry<String, Islets>> entrySet()
	{
		return m_map.entrySet();
	}

	/*---------------------------------------------------------------------*/

	public boolean isEmpty()
	{
		return m_map.isEmpty();
	}

	/*---------------------------------------------------------------------*/

	public String getPKCatalog()
	{
		return m_pkCatalog;
	}

	/*---------------------------------------------------------------------*/

	public Query toQuery()
	{
		String fkTable;
		Islets islets;

		List<String> result1 = new ArrayList<>();
		List<String> result2 = new ArrayList<>();

		for(Map.Entry<String, Islets> entry: m_map.entrySet())
		{
			fkTable = entry.getKey();
			islets = entry.getValue();

			if(DUMMY.equals(fkTable))
			{
				result2.add(islets.toString());
			}
			else
			{
				if(DUMMY.equals(islets.getPKTable()))
				{
					result1.add(fkTable.toString());
				}
				else
				{
					result1.add(fkTable.toString() + " INNER JOIN " + islets.getPKTable() + " ON (" + islets.toString() + ")");
				}
			}
		}

		return new Query().addFromPart(result1)
		                  .addWherePart(result2)
		;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return toQuery().toString();
	}

	/*---------------------------------------------------------------------*/
}
