package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class Query
{
	/*---------------------------------------------------------------------*/

	private final Collection<String> m_selectCollection = new ArrayList<>();

	private final Collection<String> m_fromCollection = new LinkedHashSet<>();

	private final Collection<String> m_whereCollection = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	private boolean m_isDistinct = false;

	/*---------------------------------------------------------------------*/

	public Query addSelectPart(String selecPart)
	{
		m_selectCollection.add(selecPart);

		return this;
	}

	public Query addSelectPart(Collection<String> selecPart)
	{
		m_selectCollection.addAll(selecPart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addFromPart(String fromPart)
	{
		m_fromCollection.add(fromPart);

		return this;
	}

	public Query addFromPart(Collection<String> fromPart)
	{
		m_fromCollection.addAll(fromPart);

		return this;
	}

	/*-----------------------------------------------------------------*/

	public Query addWherePart(String wherePart)
	{
		m_whereCollection.add(wherePart);

		return this;
	}

	public Query addWherePart(Collection<String> wherePart)
	{
		m_whereCollection.addAll(wherePart);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addWholeQuery(Query select)
	{
		this.m_selectCollection.addAll(select.m_selectCollection);

		this.m_fromCollection.addAll(select.m_fromCollection);

		this.m_whereCollection.addAll(select.m_whereCollection);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public void setDistinct(boolean isDistinct)
	{
		m_isDistinct = isDistinct;
	}

	/*---------------------------------------------------------------------*/

	public Collection<String> getSelectCollection()
	{
		return m_selectCollection;
	}

	/*---------------------------------------------------------------------*/

	public Collection<String> getFromCollection()
	{
		return m_fromCollection;
	}

	/*---------------------------------------------------------------------*/

	public Collection<String> getWhereCollection()
	{
		return m_whereCollection;
	}

	/*---------------------------------------------------------------------*/

	public String getSelectPart()
	{
		return String.join(", ", m_selectCollection);
	}

	/*---------------------------------------------------------------------*/

	public String getFromPart()
	{
		return String.join(", ",  m_fromCollection);
	}

	/*---------------------------------------------------------------------*/

	public String getWherePart()
	{
		return String.join(" AND ", m_whereCollection);
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

		if(m_selectCollection.isEmpty() == false) {
			result.append(SELECT).append(getSelectPart());
		}

		if(m_fromCollection.isEmpty() == false) {
			result.append(" FROM ").append(getFromPart());
		}

		if(m_whereCollection.isEmpty() == false) {
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
