package net.hep.ami.jdbc.reflexion;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class Query
{
	/*---------------------------------------------------------------------*/

	private final List<String> m_selectList = new ArrayList<>();

	private final Set<String> m_fromSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	private boolean m_isDistinct = false;

	/*---------------------------------------------------------------------*/

	public Query addSelectPart(CharSequence selecPart)
	{
		m_selectList.add(selecPart.toString());

		return this;
	}

	public Query addSelectPart(Collection<?> selecPart)
	{
		m_fromSet.addAll(selecPart.stream().map(x -> x.toString()).collect(Collectors.toList()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addFromPart(CharSequence fromPart)
	{
		m_fromSet.add(fromPart.toString());

		return this;
	}

	public Query addFromPart(Collection<?> fromPart)
	{
		m_fromSet.addAll(fromPart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Query addWherePart(CharSequence wherePart)
	{
		m_whereSet.add(wherePart.toString());

		return this;
	}

	public Query addWherePart(Collection<?> wherePart)
	{
		if(wherePart.isEmpty() == false)
		{
			m_whereSet.add(
				"(" + wherePart.stream().map(x -> x.toString()).collect(Collectors.joining(" AND ")) + ")"
			);
		}

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

	public String toString(@Nullable CharSequence extra)
	{
		return toStringBuilder(extra).toString();
	}

	/*---------------------------------------------------------------------*/

	public StringBuffer toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*---------------------------------------------------------------------*/

	public StringBuffer toStringBuilder(@Nullable CharSequence extra)
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
