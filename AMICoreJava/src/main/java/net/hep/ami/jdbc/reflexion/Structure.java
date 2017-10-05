package net.hep.ami.jdbc.reflexion;

import java.util.*;

public class Structure
{
	/*---------------------------------------------------------------------*/

	public static final String DUMMY = "§";

	/*---------------------------------------------------------------------*/

	public static final class Select
	{
		/*-----------------------------------------------------------------*/

		private final String m_selectPart;

		private final Set<String> m_fromPart = new TreeSet<>();
		private final Set<String> m_wherePart = new TreeSet<>();

		/*-----------------------------------------------------------------*/

		public Select(String selectPart)
		{
			m_selectPart = selectPart;
		}

		/*-----------------------------------------------------------------*/

		public void addFrom(String fromPart)
		{
			m_fromPart.add(fromPart);
		}

		/*-----------------------------------------------------------------*/

		public void addWhere(String wherePart)
		{
			m_wherePart.add(wherePart);
		}

		/*-----------------------------------------------------------------*/

		public void addAll(Select conds)
		{
			m_fromPart.addAll(conds.m_fromPart);
			m_wherePart.addAll(conds.m_wherePart);
		}

		/*-----------------------------------------------------------------*/

		public String getSelectPart()
		{
			return m_selectPart;
		}

		/*-----------------------------------------------------------------*/

		public String getFromPart()
		{
			return String.join(", ", m_fromPart);
		}

		/*-----------------------------------------------------------------*/

		public String getWherePart()
		{
			return String.join(" AND ", m_wherePart);
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			StringBuffer result = new StringBuffer();

			if(m_selectPart.isEmpty() == false) {
				result.append("SELECT ").append(getSelectPart());
			}

			if(m_fromPart.isEmpty() == false) {
				result.append(" FROM ").append(getFromPart());
			}

			if(m_wherePart.isEmpty() == false) {
				result.append(" WHERE ").append(getWherePart());
			}

			return result.toString();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final class Islets extends HashMap<String, Select>
	{
		/*-----------------------------------------------------------------*/

		private static final long serialVersionUID = 5606411046465630272L;

		/*-----------------------------------------------------------------*/

		private final String m_pkTable;

		/*-----------------------------------------------------------------*/

		public Islets(String pkTable)
		{
			super();

			m_pkTable = pkTable;
		}

		/*-----------------------------------------------------------------*/

		public Select getIslet(String fkColumn, String pkColumn)
		{
			Select result = get(fkColumn);

			if(result == null)
			{
				result = new Select(pkColumn);

				put(fkColumn, result);
			}

			return result;
		}

		/*-----------------------------------------------------------------*/

		public String getPKTable()
		{
			return m_pkTable;
		}

		/*-----------------------------------------------------------------*/

		public List<String> toList()
		{
			String fkField;
			Select select;

			List<String> result = new ArrayList<>();

			for(Map.Entry<String, Select> entry: entrySet())
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

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			return String.join(" AND ", toList());
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final class Joins extends HashMap<String, Islets>
	{
		/*-----------------------------------------------------------------*/

		private static final long serialVersionUID = 5606411046465630272L;

		/*-----------------------------------------------------------------*/

		private final String m_pkCatalog;

		/*-----------------------------------------------------------------*/

		public Joins(String pkCatalog)
		{
			super();

			m_pkCatalog = pkCatalog;
		}

		/*-----------------------------------------------------------------*/

		public Islets getJoin(String fkTable, String pkTable)
		{
			Islets result = get(fkTable);

			if(result == null)
			{
				result = new Islets(pkTable);

				put(fkTable, result);
			}

			return result;
		}

		/*-----------------------------------------------------------------*/

		public String getPKCatalog()
		{
			return m_pkCatalog;
		}

		/*-----------------------------------------------------------------*/

		public SQL toSQL()
		{
			String fkTable;
			Islets islets;

			List<String> result1 = new ArrayList<>();
			List<String> result2 = new ArrayList<>();

			for(Map.Entry<String, Islets> entry: entrySet())
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

			return new SQL(result1, result2);
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			return toSQL().toString();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final class SQL
	{
		/*-----------------------------------------------------------------*/

		public final String from;
		public final String where;

		/*-----------------------------------------------------------------*/

		public SQL(String _from, String _where)
		{
			from = _from;
			where = _where;
		}

		/*-----------------------------------------------------------------*/

		public SQL(List<String> _from, List<String> _where)
		{
			from = String.join(", ", _from);
			where = String.join(" AND ", _where);
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			StringBuilder result = new StringBuilder();

			if(from.isEmpty() == false) {
				result.append(" FROM ").append(from);
			}

			if(where.isEmpty() == false) {
				result.append(" WHERE ").append(where);
			}

			return result.toString();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
