package net.hep.ami.jdbc.query.obj;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class InsertObj
{
	/*---------------------------------------------------------------------*/

	private final Set<String> m_insertSet = new LinkedHashSet<>();

	private final List<String> m_fieldList = new ArrayList<>();

	private final List<String> m_valueList = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public InsertObj addInsertPart(CharSequence updatePart)
	{
		m_insertSet.add(updatePart.toString());

		return this;
	}

	public InsertObj addInsertPart(Collection<?> updatePart)
	{
		m_insertSet.addAll(updatePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public InsertObj addFieldValuePart(CharSequence fieldPart, CharSequence valuePart)
	{
		m_fieldList.add(fieldPart.toString());
		m_valueList.add(valuePart.toString());

		return this;
	}

	public InsertObj addFieldValuePart(Collection<?> fieldPart, Collection<?> valuePart) throws Exception
	{
		if(fieldPart.size() != valuePart.size())
		{
			throw new Exception("bad number of values");
		}

		m_fieldList.addAll(fieldPart.stream().map(x -> x.toString()).collect(Collectors.toList()));
		m_valueList.addAll(valuePart.stream().map(x -> x.toString()).collect(Collectors.toList()));

		return this;
	}

	/*---------------------------------------------------------------------*/

	public InsertObj addWholeQuery(InsertObj query)
	{
		m_insertSet.addAll(query.m_insertSet);

		m_fieldList.addAll(query.m_fieldList);

		m_valueList.addAll(query.m_valueList);

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getInsertCollection()
	{
		return m_insertSet;
	}

	/*---------------------------------------------------------------------*/

	public List<String> getFieldCollection()
	{
		return m_fieldList;
	}

	/*---------------------------------------------------------------------*/

	public List<String> getValueCollection()
	{
		return m_valueList;
	}

	/*---------------------------------------------------------------------*/

	public String getInsertPart()
	{
		return String.join(", ", m_insertSet);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldValuePart()
	{
		return new StringBuilder().append("(")
		                          .append(String.join(", ", m_fieldList))
		                          .append(") VALUES (")
		                          .append(String.join(", ", m_valueList))
		                          .append(")")
		                          .toString()
		;

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

		result.append("INSERT INTO ").append(getInsertPart()).append(getFieldValuePart());

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
