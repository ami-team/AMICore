package net.hep.ami.jdbc.obj;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class UpdateObj
{
	/*---------------------------------------------------------------------*/

	private final Set<String> m_updateSet = new LinkedHashSet<>();

	private final Set<String> m_setSet = new LinkedHashSet<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	public UpdateObj addUpdatePart(CharSequence updatePart)
	{
		m_updateSet.add(updatePart.toString());

		return this;
	}

	public UpdateObj addUpdatePart(Collection<?> updatePart)
	{
		m_updateSet.addAll(updatePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addSetPart(CharSequence setFieldPart, CharSequence setValuePart)
	{
		m_setSet.add(setFieldPart.toString() + " = " + setValuePart.toString());

		return this;
	}

	public UpdateObj addSetFieldPart(List<?> setFieldPart, List<?> setValuePart)
	{
		final int length = Math.min(
			setFieldPart.size(),
			setValuePart.size()
		);

		for(int i = 0; i < length; i++)
		{
			m_setSet.add(setFieldPart.get(i).toString() + " = " + setValuePart.get(i).toString());
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addWherePart(CharSequence wherePart)
	{
		m_whereSet.add(wherePart.toString());

		return this;
	}

	public UpdateObj addWherePart(Collection<?> wherePart)
	{
		if(wherePart.isEmpty() == false)
		{
			m_whereSet.add(
				wherePart.stream().map(x -> x.toString()).collect(Collectors.joining(" AND "))
			);
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addWholeQuery(UpdateObj query)
	{
		this.m_updateSet.addAll(query.m_updateSet);

		this.m_setSet.addAll(query.m_setSet);

		this.m_whereSet.addAll(query.m_whereSet);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getUpdateCollection()
	{
		return m_updateSet;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getSetCollection()
	{
		return m_setSet;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getWhereCollection()
	{
		return m_whereSet;
	}

	/*---------------------------------------------------------------------*/

	public String getUpdatePart()
	{
		return String.join(", ", m_whereSet);
	}

	/*---------------------------------------------------------------------*/

	public String getSetPart()
	{
		return String.join(", ", m_setSet);

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

		if(m_updateSet.isEmpty() == false) {
			result.append("UPDATE ").append(getUpdatePart());
		}

		if(m_setSet.isEmpty() == false) {
			result.append(" SET ").append(getSetPart());
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
