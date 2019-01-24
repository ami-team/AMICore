package net.hep.ami.jdbc.query.obj;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

public class UpdateObj
{
	/*---------------------------------------------------------------------*/

	public enum Mode
	{
		SQL,
		MQL
	}

	/*---------------------------------------------------------------------*/

	private Mode m_mode = Mode.SQL;

	/*---------------------------------------------------------------------*/

	private final Set<String> m_updateSet = new LinkedHashSet<>();

	private final List<String> m_fieldList = new ArrayList<>();

	private final List<String> m_valueList = new ArrayList<>();

	private final Set<String> m_whereSet = new LinkedHashSet<>();

	/*---------------------------------------------------------------------*/

	public UpdateObj setMode(Mode mode)
	{
		m_mode = mode;

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addUpdatePart(@Nullable CharSequence updatePart)
	{
		if(updatePart != null)
		{
			m_updateSet.add(updatePart.toString());
		}

		return this;
	}

	public UpdateObj addUpdatePart(@Nullable Collection<?> updatePart)
	{
		if(updatePart != null)
		{
			m_updateSet.addAll(updatePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addFieldValuePart(@Nullable CharSequence fieldPart, @Nullable CharSequence valuePart)
	{
		if(fieldPart != null
		   &&
		   valuePart != null
		 ) {
			m_fieldList.add(fieldPart.toString());
			m_valueList.add(valuePart.toString());
		}

		return this;
	}

	public UpdateObj addFieldValuePart(@Nullable Collection<?> fieldPart, @Nullable Collection<?> valuePart) throws Exception
	{
		if(fieldPart != null
		   &&
		   valuePart != null
		 ) {
			if(fieldPart.size() != valuePart.size())
			{
				throw new Exception("bad number of values");
			}

			m_fieldList.addAll(fieldPart.stream().map(x -> x.toString()).collect(Collectors.toList()));
			m_valueList.addAll(valuePart.stream().map(x -> x.toString()).collect(Collectors.toList()));
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addWherePart(@Nullable CharSequence wherePart)
	{
		if(wherePart != null)
		{
			m_whereSet.add(wherePart.toString());
		}

		return this;
	}

	public UpdateObj addWherePart(@Nullable Collection<?> wherePart)
	{
		if(wherePart != null)
		{
			m_whereSet.addAll(wherePart.stream().map(x -> x.toString()).collect(Collectors.toSet()));
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public UpdateObj addWholeQuery(@Nullable UpdateObj query)
	{
		if(query != null)
		{
			m_updateSet.addAll(query.m_updateSet);

			m_fieldList.addAll(query.m_fieldList);

			m_valueList.addAll(query.m_valueList);

			m_whereSet.addAll(query.m_whereSet);
		}

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getUpdateCollection()
	{
		return m_updateSet;
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

	public Set<String> getWhereCollection()
	{
		return m_whereSet;
	}

	/*---------------------------------------------------------------------*/

	public String getUpdatePart()
	{
		return String.join(", ", m_updateSet);
	}

	/*---------------------------------------------------------------------*/

	public String getSetPart()
	{
		if(m_mode == Mode.MQL)
		{
			/*-------------------------------------------------------------*/

			return new StringBuilder().append("(")
			                          .append(String.join(", ", m_fieldList))
			                          .append(") VALUES (")
			                          .append(String.join(", ", m_valueList))
			                          .append(")")
			                          .toString()
			;

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/

			StringBuilder stringBuilder = new StringBuilder();

			final int length = Math.min(
				m_fieldList.size(),
				m_valueList.size()
			);

			for(int i = 0; i < length; i++)
			{
				if(i > 0)
				{
					stringBuilder.append(", ");
				}

				stringBuilder.append(m_fieldList.get(i).toString())
				             .append( " = ")
				             .append(m_valueList.get(i).toString())
				;
			}

			return stringBuilder.toString();

			/*-------------------------------------------------------------*/
		}
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

		if(m_mode == Mode.MQL)
		{
			result.append("UPDATE ").append(getSetPart());
		}
		else
		{
			result.append("UPDATE ").append(getUpdatePart()).append(" SET ").append(getSetPart());
		}

		/*-----------------------------------------------------------------*/

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
