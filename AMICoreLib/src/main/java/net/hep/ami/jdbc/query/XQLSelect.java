package net.hep.ami.jdbc.query;

import java.util.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

public final class XQLSelect
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final List<String> m_selectList = new ArrayList<>();

	private final Set<String> m_fromSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	private final Set<String> m_extraSet = new LinkedHashSet<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	private boolean m_isDistinct = false;

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLSelect addSelectPart(@Nullable CharSequence selectPart)
	{
		if(selectPart != null)
		{
			m_selectList.add(selectPart.toString());
		}

		return this;
	}

	@NotNull
	@Contract(pure = true)
	public XQLSelect addSelectPart(@Nullable Collection<?> selectPart)
	{
		if(selectPart != null)
		{
			m_selectList.addAll(selectPart.stream().map(Object::toString).collect(Collectors.toList()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLSelect addFromPart(@Nullable CharSequence fromPart)
	{
		if(fromPart != null)
		{
			m_fromSet.add(fromPart.toString());
		}

		return this;
	}

	@NotNull
	@Contract(pure = true)
	public XQLSelect addFromPart(@Nullable Collection<?> fromPart)
	{
		if(fromPart != null)
		{
			m_fromSet.addAll(fromPart.stream().map(Object::toString).collect(Collectors.toSet()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLSelect addWherePart(@Nullable CharSequence wherePart)
	{
		if(wherePart != null)
		{
			m_whereSet.add(wherePart.toString());
		}

		return this;
	}

	@NotNull
	@Contract(pure = true)
	public XQLSelect addWherePart(@Nullable Collection<?> wherePart)
	{
		if(wherePart != null)
		{
			m_whereSet.addAll(wherePart.stream().map(Object::toString).collect(Collectors.toSet()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLSelect addExtraPart(@Nullable CharSequence extraPart)
	{
		if(extraPart != null)
		{
			m_extraSet.add(extraPart.toString());
		}

		return this;
	}

	@NotNull
	@Contract(pure = true)
	public XQLSelect addExtraPart(@Nullable Collection<?> extraPart)
	{
		if(extraPart != null)
		{
			m_extraSet.addAll(extraPart.stream().map(Object::toString).collect(Collectors.toSet()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLSelect addWholeQuery(@Nullable XQLSelect query)
	{
		if(query != null)
		{
			m_selectList.addAll(query.m_selectList);

			m_fromSet.addAll(query.m_fromSet);

			m_whereSet.addAll(query.m_whereSet);

			m_extraSet.addAll(query.m_extraSet);
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public void setDistinct(boolean isDistinct)
	{
		m_isDistinct = isDistinct;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public List<String> getSelectCollection()
	{
		return m_selectList;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public Set<String> getFromCollection()
	{
		return m_fromSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public Set<String> getWhereCollection()
	{
		return m_whereSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public Set<String> getExtraCollection()
	{
		return m_extraSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getSelectPart()
	{
		return String.join(", ", m_selectList);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getFromPart()
	{
		return String.join(", ",  m_fromSet);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getWherePart()
	{
		return String.join(" AND ", m_whereSet);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getExtraPart()
	{
		return String.join(" ", m_extraSet);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String toString()
	{
		return toStringBuilder().toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append(m_isDistinct ? "SELECT DISTINCT " : "SELECT ").append(getSelectPart());

		if(!m_fromSet.isEmpty()) {
			result.append(" FROM ").append(getFromPart());
		}

		if(!m_whereSet.isEmpty()) {
			result.append(" WHERE ").append(getWherePart());
		}

		if(!m_extraSet.isEmpty()) {
			result.append(" ").append(getExtraPart());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
