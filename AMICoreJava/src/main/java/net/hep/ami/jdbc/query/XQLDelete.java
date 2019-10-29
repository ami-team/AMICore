package net.hep.ami.jdbc.query;

import java.util.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

public final class XQLDelete
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public enum Mode
	{
		SQL,
		MQL
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private final Mode m_mode;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final Set<String> m_deleteSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	public XQLDelete(@NotNull Mode mode)
	{
		m_mode = mode;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLDelete addDeletePart(@Nullable CharSequence selectPart)
	{
		if(selectPart != null)
		{
			m_deleteSet.add(selectPart.toString());
		}

		return this;
	}

	@NotNull
	@Contract(pure = true)
	public XQLDelete addDeletePart(@Nullable Collection<?> selectPart)
	{
		if(selectPart != null)
		{
			m_deleteSet.addAll(selectPart.stream().map(Object::toString).collect(Collectors.toSet()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public XQLDelete addWherePart(@Nullable CharSequence wherePart)
	{
		if(wherePart != null)
		{
			m_whereSet.add(wherePart.toString());
		}

		return this;
	}

	@NotNull
	@Contract(pure = true)
	public XQLDelete addWherePart(@Nullable Collection<?> wherePart)
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
	public XQLDelete addWholeQuery(@Nullable XQLDelete query)
	{
		if(query != null)
		{
			m_deleteSet.addAll(query.m_deleteSet);

			m_whereSet.addAll(query.m_whereSet);
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public Set<String> getDeleteCollection()
	{
		return m_deleteSet;
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
	public String getDeletePart()
	{
		return String.join(", ", m_deleteSet);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getWherePart()
	{
		return String.join(" AND ", m_whereSet);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String toString()
	{
		return toStringBuilder(null).toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String toString(@Nullable CharSequence extra)
	{
		return toStringBuilder(extra).toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder(@Nullable CharSequence extra)
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		if(m_mode == Mode.MQL)
		{
			result.append("DELETE");
		}
		else
		{
			result.append("DELETE FROM ").append(getDeletePart());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(!m_whereSet.isEmpty())
		{
			result.append(" WHERE ").append(getWherePart());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(extra != null)
		{
			result.append(extra);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
