package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Query
{
	/*---------------------------------------------------------------------*/

	private final List<String> m_selectList = new ArrayList<>();

	private final Set<String> m_fromSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	private boolean m_isDistinct = false;

	/*---------------------------------------------------------------------*/

	public Query addSelectPart(String selecPart)
	{
		m_selectList.add(selecPart);

		return this;
	}

	public Query addSelectPart(Collection<String> selecPart)
	{
		m_selectList.addAll(selecPart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addFromPart(String fromPart)
	{
		m_fromSet.add(fromPart);

		return this;
	}

	public Query addFromPart(Collection<String> fromPart)
	{
		m_fromSet.addAll(fromPart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addWherePart(String wherePart)
	{
		m_whereSet.add(wherePart);

		return this;
	}

	public Query addWherePart(Collection<String> wherePart)
	{
		m_whereSet.addAll(wherePart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addWholeQuery(Query query)
	{
		this.m_selectList.addAll(query.m_selectList);

		this.m_fromSet.addAll(query.m_fromSet);

		this.m_whereSet.addAll(query.m_whereSet);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public void setDistinct(boolean isDistinct)
	{
		m_isDistinct = isDistinct;
	}

	/*---------------------------------------------------------------------*/

	public List<String> getSelectCollection()
	{
		return m_selectList;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getFromCollection()
	{
		return m_fromSet;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getWhereCollection()
	{
		return m_whereSet;
	}

	/*---------------------------------------------------------------------*/

	public String getSelectPart()
	{
		return String.join(", ", m_selectList);
	}

	/*---------------------------------------------------------------------*/

	public String getFromPart()
	{
		return String.join(", ",  m_fromSet);
	}

	/*---------------------------------------------------------------------*/

	public String getWherePart()
	{
		return String.join(" AND ", m_whereSet);
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

		if(m_selectList.isEmpty() == false) {
			result.append(SELECT).append(getSelectPart());
		}

		if(m_fromSet.isEmpty() == false) {
			result.append(" FROM ").append(getFromPart());
		}

		if(m_whereSet.isEmpty() == false) {
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
