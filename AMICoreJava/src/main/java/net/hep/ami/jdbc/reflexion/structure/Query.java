package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Query
{
	/*---------------------------------------------------------------------*/

	private final List<String> m_selectPart = new ArrayList<>();

	private final Set<String> m_fromPart = new LinkedHashSet<>();

	private final Set<String> m_wherePart = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	private boolean m_isDistinct = false;

	/*---------------------------------------------------------------------*/

	public Query addSelectPart(String selecPart)
	{
		m_selectPart.add(selecPart);

		return this;
	}

	public Query addSelectPart(Collection<String> selecPart)
	{
		m_selectPart.addAll(selecPart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addFromPart(String fromPart)
	{
		m_fromPart.add(fromPart);

		return this;
	}

	public Query addFromPart(Collection<String> fromPart)
	{
		m_fromPart.addAll(fromPart);

		return this;
	}

	/*-----------------------------------------------------------------*/

	public Query addWherePart(String wherePart)
	{
		m_wherePart.add(wherePart);

		return this;
	}

	public Query addWherePart(Collection<String> wherePart)
	{
		m_wherePart.addAll(wherePart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addWholeQuery(Query select)
	{
		this.m_selectPart.addAll(select.m_selectPart);

		this.m_fromPart.addAll(select.m_fromPart);

		this.m_wherePart.addAll(select.m_wherePart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public void setDistinct(boolean isDistinct)
	{
		m_isDistinct = isDistinct;
	}

	/*---------------------------------------------------------------------*/

	public String getSelectPart()
	{
		return String.join(", ", m_selectPart);
	}

	/*---------------------------------------------------------------------*/

	public String getFromPart()
	{
		return String.join(", ",  m_fromPart);
	}

	/*---------------------------------------------------------------------*/

	public String getWherePart()
	{
		return String.join(" AND ", m_wherePart);
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return toStringBuilder(null).toString();
	}

	/*---------------------------------------------------------------------*/

	public String toString(@Nullable String extra)
	{
		return toStringBuilder(extra).toString();
	}

	/*---------------------------------------------------------------------*/

	public StringBuffer toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*---------------------------------------------------------------------*/

	public StringBuffer toStringBuilder(@Nullable String extra)
	{
		StringBuffer result = new StringBuffer();

		/*-----------------------------------------------------------------*/

		final String SELECT = m_isDistinct ? "SELECT DISTINCT " : "SELECT ";

		/*-----------------------------------------------------------------*/

		if(m_selectPart.isEmpty() == false) {
			result.append(SELECT).append(getSelectPart());
		}

		if(m_fromPart.isEmpty() == false) {
			result.append(" FROM ").append(getFromPart());
		}

		if(m_wherePart.isEmpty() == false) {
			result.append(" WHERE ").append(getWherePart());
		}

		/*-----------------------------------------------------------------*/

		if(extra != null)
		{
			result.append(extra);
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
