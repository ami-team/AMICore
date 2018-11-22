package net.hep.ami.jdbc.query.obj;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class SelectObj
{
	/*---------------------------------------------------------------------*/

	private final List<String> m_selectList = new ArrayList<>();

	private final Set<String> m_fromSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	private boolean m_isDistinct = false;

	/*---------------------------------------------------------------------*/

	public SelectObj addSelectPart(CharSequence selectPart)
	{
		m_selectList.add(selectPart.toString());

		return this;
	}

	public SelectObj addSelectPart(Collection<?> selectPart)
	{
		m_fromSet.addAll(selectPart.stream().map(x -> x.toString()).collect(Collectors.toList()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public SelectObj addFromPart(CharSequence fromPart)
	{
		m_fromSet.add(fromPart.toString());

		return this;
	}

	public SelectObj addFromPart(Collection<?> fromPart)
	{
		m_fromSet.addAll(fromPart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public SelectObj addWherePart(CharSequence wherePart)
	{
		m_whereSet.add(wherePart.toString());

		return this;
	}

	public SelectObj addWherePart(Collection<?> wherePart)
	{
		m_whereSet.addAll(wherePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public SelectObj addWholeQuery(SelectObj query)
	{
		m_selectList.addAll(query.m_selectList);

		m_fromSet.addAll(query.m_fromSet);

		m_whereSet.addAll(query.m_whereSet);

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

	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(@Nullable CharSequence extra)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(m_isDistinct ? "SELECT DISTINCT " : "SELECT ").append(getSelectPart());

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
