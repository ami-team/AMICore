package net.hep.ami.jdbc.query.obj;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class DeleteObj
{
	/*---------------------------------------------------------------------*/

	private final Set<String> m_deleteSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	public DeleteObj addDeletePart(CharSequence selecPart)
	{
		m_deleteSet.add(selecPart.toString());

		return this;
	}

	public DeleteObj addDeletePart(Collection<?> selecPart)
	{
		m_deleteSet.addAll(selecPart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public DeleteObj addWherePart(CharSequence wherePart)
	{
		m_whereSet.add(wherePart.toString());

		return this;
	}

	public DeleteObj addWherePart(Collection<?> wherePart)
	{
		m_whereSet.addAll(wherePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public DeleteObj addWholeQuery(DeleteObj query)
	{
		m_deleteSet.addAll(query.m_deleteSet);

		m_whereSet.addAll(query.m_whereSet);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getDeleteCollection()
	{
		return m_deleteSet;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getWhereCollection()
	{
		return m_whereSet;
	}

	/*---------------------------------------------------------------------*/

	public String getDeletePart()
	{
		return String.join(", ", m_deleteSet);
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

		result.append("DELETE FROM ").append(getDeletePart());

		if(m_whereSet.isEmpty() == false)
		{
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
