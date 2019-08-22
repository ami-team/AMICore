package net.hep.ami.jdbc.query;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public final class XQLInsert
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

	private final Set<String> m_insertSet = new LinkedHashSet<>();

	private final List<String> m_fieldList = new ArrayList<>();

	private final List<String> m_valueList = new ArrayList<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	public XQLInsert(@NotNull Mode mode)
	{
		m_mode = mode;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String _toString(@NotNull Object x)
	{
		return (m_mode == Mode.MQL && x instanceof QId) ? ((QId) x).toString(QId.MASK_CATALOG_ENTITY_FIELD, QId.MASK_CATALOG_ENTITY_FIELD) : x.toString();
	}

	/*---------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public XQLInsert addInsertPart(@Nullable CharSequence updatePart)
	{
		if(updatePart != null)
		{
			m_insertSet.add(updatePart.toString());
		}

		return this;
	}

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public XQLInsert addInsertPart(@Nullable Collection<?> updatePart)
	{
		if(updatePart != null)
		{
			m_insertSet.addAll(updatePart.stream().map(Object::toString).collect(Collectors.toSet()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
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

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
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

			m_fieldList.addAll(fieldPart.stream().map(this::_toString).collect(Collectors.toList()));
			m_valueList.addAll(valuePart.stream().map(Object::toString).collect(Collectors.toList()));
		}

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public Set<String> getInsertCollection()
	{
		return m_insertSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public List<String> getFieldCollection()
	{
		return m_fieldList;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public List<String> getValueCollection()
	{
		return m_valueList;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getInsertPart()
	{
		return String.join(", ", m_insertSet);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
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
			result.append("INSERT ").append(getFieldValuePart());
		}
		else
		{
			result.append("INSERT INTO ").append(getInsertPart()).append(" ").append(getFieldValuePart());
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
