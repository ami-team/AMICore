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

		private final String m_select;

		private final Set<String> m_from = new TreeSet<>();
		private final Set<String> m_where = new TreeSet<>();

		/*-----------------------------------------------------------------*/

		public Select(String select)
		{
			m_select = select;
		}

		/*-----------------------------------------------------------------*/

		public void addFrom(String from)
		{
			m_from.add(from);
		}

		/*-----------------------------------------------------------------*/

		public void addWhere(String where)
		{
			m_where.add(where);
		}

		/*-----------------------------------------------------------------*/

		public void addAll(Select conds)
		{
			m_from.addAll(conds.m_from);
			m_where.addAll(conds.m_where);
		}

		/*-----------------------------------------------------------------*/

		public String getSelectPart()
		{
			return m_select;
		}

		/*-----------------------------------------------------------------*/

		public String getFromPart()
		{
			return String.join(",", m_from);
		}

		/*-----------------------------------------------------------------*/

		public String getWherePart()
		{
			return String.join(" AND ", m_where);
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			return new StringBuffer().append("SELECT ").append(getSelectPart()).append(" FROM ").append(getFromPart()).append(" WHERE ").append(getWherePart()).toString();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final class Islets extends HashMap<String, Select>
	{
		/*-----------------------------------------------------------------*/

		private static final long serialVersionUID = 5606411046465630272L;

		/*-----------------------------------------------------------------*/

		public Islets()
		{
			super();
		}

		/*-----------------------------------------------------------------*/

		public Select getIslet(String pk, String fk)
		{
			Select result = get(pk);

			if(result == null)
			{
				result = new Select(fk);

				put(pk, result);
			}

			return result;
		}

		/*-----------------------------------------------------------------*/

		public List<String> toList()
		{
			String isletName;
			Select select;

			List<String> result = new ArrayList<>();

			for(Map.Entry<String, Select> entry: entrySet())
			{
				isletName = entry.getKey();
				select = entry.getValue();

				if(DUMMY.equals(isletName))
				{
					result.add(select.getWherePart());
				}
				else
				{
					result.add(isletName + "=(" + select.toString() + ")");
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

		public Joins()
		{
			super();
		}

		/*-----------------------------------------------------------------*/

		public Islets getJoin(String joinKey)
		{
			Islets result = get(joinKey);

			if(result == null)
			{
				result = new Islets();

				put(joinKey, result);
			}

			return result;
		}

		/*-----------------------------------------------------------------*/

		public SQL toSQL()
		{
			String jointName;
			Islets islets;

			List<String> result1 = new ArrayList<>();
			List<String> result2 = new ArrayList<>();

			for(Map.Entry<String, Islets> entry: entrySet())
			{
				jointName = entry.getKey();
				islets = entry.getValue();

				if(DUMMY.equals(jointName))
				{
					result2.add(islets.toString());
				}
				else
				{
					result1.add(jointName + " ON (" + islets.toString() + ")");
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
			from = String.join(" ", _from);
			where = String.join(" AND ", _where);
		}

		/*-----------------------------------------------------------------*/

		public String toString()
		{
			StringBuilder stringBuilder = new StringBuilder();

			if(from.isEmpty() == false)
			{
				stringBuilder.append(" FROM ").append(from);
			}

			if(where.isEmpty() == false)
			{
				stringBuilder.append(" WHERE ").append(where);
			}

			return stringBuilder.toString();
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
