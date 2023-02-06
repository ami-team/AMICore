package net.hep.ami.data;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class Row
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	private final String[] m_values;

	/*----------------------------------------------------------------------------------------------------------------*/

	protected Row(@NotNull RowSet rowSet) throws Exception
	{
		m_values = (m_rowSet = rowSet).getCurrentRow();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public RowSet getRowSet()
	{
		return m_rowSet;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public int getNumberOfFields()
	{
		return m_rowSet.m_numberOfFields;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getCatalog(int fieldIndex) throws Exception
	{
		return m_rowSet.getCatalogOfField(fieldIndex);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getEntity(int fieldIndex) throws Exception
	{
		return m_rowSet.getEntityOfField(fieldIndex);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getName(int fieldIndex) throws Exception
	{
		return m_rowSet.getNameOfField(fieldIndex);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getLabel(int fieldIndex) throws Exception
	{
		return m_rowSet.getLabelOfField(fieldIndex);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public String getType(int fieldIndex) throws Exception
	{
		return m_rowSet.getTypeOfField(fieldIndex);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(value = "null, _ -> param2", pure = true)
	private static String checkString(@Nullable String currentValue, @Nullable String defaultValue)
	{
		if(currentValue != null)
		{
			String tempValue = currentValue.trim();

			if(!tempValue.isEmpty() && !"@NULL".equalsIgnoreCase(tempValue))
			{
				return currentValue;
			}
		}

		return defaultValue;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getValue(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_rowSet.m_numberOfFields)
		{
			throw new Exception("field index out of range");
		}

		return checkString(m_values[fieldIndex], "@NULL");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public String getValue(int fieldIndex, @Nullable String defaultValue) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_rowSet.m_numberOfFields)
		{
			throw new Exception("field index out of range");
		}

		return checkString(m_values[fieldIndex], defaultValue);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Boolean getValue(int fieldIndex, @Nullable Boolean defaultValue) throws Exception
	{
		try
		{
			return Bool.valueOf(getValue(fieldIndex, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Integer getValue(int fieldIndex, @Nullable Integer defaultValue) throws Exception
	{
		try
		{
			return Integer.valueOf(getValue(fieldIndex, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Float getValue(int fieldIndex, @Nullable Float defaultValue) throws Exception
	{
		try
		{
			return Float.valueOf(getValue(fieldIndex, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Double getValue(int fieldIndex, @Nullable Double defaultValue) throws Exception
	{
		try
		{
			return Double.valueOf(getValue(fieldIndex, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getValue(String name) throws Exception
	{
		if(!m_rowSet.m_labelIndices.containsKey(name))
		{
			if(!m_rowSet.m_nameIndices.containsKey(name))
			{
				throw new Exception("field label/name `" + name + "` not in row");
			}
			else
			{
				return checkString(m_values[m_rowSet.m_nameIndices.get(name)], "@NULL");
			}
		}
		else
		{
			return checkString(m_values[m_rowSet.m_labelIndices.get(name)], "@NULL");
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public String getValue(String name, @Nullable String defaultValue) throws Exception
	{
		if(!m_rowSet.m_labelIndices.containsKey(name))
		{
			if(!m_rowSet.m_nameIndices.containsKey(name))
			{
				throw new Exception("field label/name `" + name + "` not in row");
			}
			else
			{
				return checkString(m_values[m_rowSet.m_nameIndices.get(name)], defaultValue);
			}
		}
		else
		{
			return checkString(m_values[m_rowSet.m_labelIndices.get(name)], defaultValue);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Boolean getValue(String name, @Nullable Boolean defaultValue) throws Exception
	{
		try
		{
			return Bool.valueOf(getValue(name, (String) null));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Integer getValue(String name, @Nullable Integer defaultValue) throws Exception
	{
		try
		{
			return Integer.valueOf(getValue(name, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Float getValue(String name, @Nullable Float defaultValue) throws Exception
	{
		try
		{
			return Float.valueOf(getValue(name, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract(value = "_, !null -> !null", pure = true)
	public Double getValue(String name, @Nullable Double defaultValue) throws Exception
	{
		try
		{
			return Double.valueOf(getValue(name, ""));
		}
		catch(NumberFormatException e)
		{
			return defaultValue;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder(@Nullable String type)
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<row").append(type != null ? " type=\"" + Utility.escapeHTML(type) + "\"" : "").append(">");

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < m_rowSet.m_numberOfFields; i++)
		{
			result.append("<field name=\"").append(Utility.escapeHTML(m_rowSet.m_fieldLabels[i])).append("\"><![CDATA[").append(m_values[i]).append("]]>")
			      .append(m_rowSet.processWebLink(i, this))
			      .append("</field>")
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("</row>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
