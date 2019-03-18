package net.hep.ami.jdbc.query;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public final class XQLInsert
{
	/*---------------------------------------------------------------------*/

	public enum Mode
	{
		SQL,
		MQL
	}

	/*---------------------------------------------------------------------*/

	private final Mode m_mode;

	/*---------------------------------------------------------------------*/

	private final Set<String> m_insertSet = new LinkedHashSet<>();

	private final List<String> m_fieldList = new ArrayList<>();

	private final List<String> m_valueList = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public XQLInsert(Mode mode)
	{
		m_mode = mode;
	}

	/*---------------------------------------------------------------------*/

	private String _toString(Object x)
	{
		return (m_mode == Mode.MQL && x instanceof QId) ? ((QId) x).toString(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD) : x.toString();
	}

	/*---------------------------------------------------------------------*/

	public XQLInsert addInsertPart(@Nullable CharSequence updatePart)
	{
		if(updatePart != null)
		{
			m_insertSet.add(updatePart.toString());
		}

		return this;
	}

	public XQLInsert addInsertPart(@Nullable Collection<?> updatePart)
	{
		if(updatePart != null)
		{
			m_insertSet.addAll(updatePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public XQLInsert addFieldValuePart(@Nullable Object fieldPart, @Nullable CharSequence valuePart)
	{
		if(fieldPart != null
		   &&
		   valuePart != null
		 ) {
			m_fieldList.add(_toString(fieldPart));
			m_valueList.add(valuePart.toString());
		}

		return this;
	}

	public XQLInsert addFieldValuePart(@Nullable Collection<?> fieldPart, @Nullable Collection<?> valuePart) throws Exception
	{
		if(fieldPart != null
		   &&
		   valuePart != null
		 ) {
			if(fieldPart.size() != valuePart.size())
			{
				throw new Exception("bad number of values");
			}

			m_fieldList.addAll(fieldPart.stream().map(x -> _toString(x)).collect(Collectors.toList()));
			m_valueList.addAll(valuePart.stream().map(x -> x.toString()).collect(Collectors.toList()));
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public XQLInsert addWholeQuery(@Nullable XQLInsert query)
	{
		if(query != null)
		{
			m_insertSet.addAll(query.m_insertSet);

			m_fieldList.addAll(query.m_fieldList);

			m_valueList.addAll(query.m_valueList);
		}

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

		if(m_mode == Mode.MQL)
		{
			result.append("INSERT ").append(getFieldValuePart());
		}
		else
		{
			result.append("INSERT INTO ").append(getInsertPart()).append(" ").append(getFieldValuePart());
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
