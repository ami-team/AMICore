package net.hep.ami.jdbc;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class Row
{
	/*---------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	private final String[] m_values;

	/*---------------------------------------------------------------------*/

	protected Row(RowSet rowSet) throws Exception
	{
		m_values = (m_rowSet = rowSet).getCurrentRow();
	}

	/*---------------------------------------------------------------------*/

	public RowSet getRowSet()
	{
		return m_rowSet;
	}

	/*---------------------------------------------------------------------*/

	public String getCatalog(int fieldIndex) throws Exception
	{
		return m_rowSet.getCatalogOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getEntity(int fieldIndex) throws Exception
	{
		return m_rowSet.getEntityOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getName(int fieldIndex) throws Exception
	{
		return m_rowSet.getNameOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getLabel(int fieldIndex) throws Exception
	{
		return m_rowSet.getLabelOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getType(int fieldIndex) throws Exception
	{
		return m_rowSet.getTypeOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getValue(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_rowSet.m_numberOfFields)
		{
			throw new Exception("field index out of range");
		}

		return m_values[fieldIndex];
	}

	/*---------------------------------------------------------------------*/

	public Boolean getValue(int fieldIndex, @Nullable Boolean defaultValue) throws Exception
	{
		Boolean result;

		String tmpValue = getValue(fieldIndex).trim().toLowerCase();

		/**/ if("1".equals(tmpValue)
		        ||
		        "on".equals(tmpValue)
		        ||
		        "yes".equals(tmpValue)
		        ||
		        "true".equals(tmpValue)
		 ) {
			result = true;
		}
		else if("0".equals(tmpValue)
		        ||
		        "off".equals(tmpValue)
		        ||
		        "no".equals(tmpValue)
		        ||
		        "false".equals(tmpValue)
		 ) {
			result = false;
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Integer getValue(int fieldIndex, @Nullable Integer defaultValue) throws Exception
	{
		Integer result;

		String tmpValue = getValue(fieldIndex);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			try
			{
				result = Integer.parseInt(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Float getValue(int fieldIndex, @Nullable Float defaultValue) throws Exception
	{
		Float result;

		String tmpValue = getValue(fieldIndex);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			try
			{
				result = Float.parseFloat(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Double getValue(int fieldIndex, @Nullable Double defaultValue) throws Exception
	{
		Double result;

		String tmpValue = getValue(fieldIndex);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			try
			{
				result = Double.parseDouble(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public String getValue(String name) throws Exception
	{
		if(m_rowSet.m_labelIndices.containsKey(name) == false)
		{
			if(m_rowSet.m_nameIndices.containsKey(name) == false)
			{
				throw new Exception("field label/name `" + name + "` not in row");
			}
			else
			{
				return m_values[m_rowSet.m_nameIndices.get(name)];
			}
		}
		else
		{
			return m_values[m_rowSet.m_labelIndices.get(name)];
		}
	}

	/*---------------------------------------------------------------------*/

	public Boolean getValue(String name, @Nullable Boolean defaultValue) throws Exception
	{
		Boolean result;

		String tmpValue = getValue(name);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			tmpValue = tmpValue.trim().toLowerCase();

			/**/ if("1".equals(tmpValue)
			        ||
			        "on".equals(tmpValue)
			        ||
			        "yes".equals(tmpValue)
			        ||
			        "true".equals(tmpValue)
			 ) {
				result = true;
			}
			else if("0".equals(tmpValue)
			        ||
			        "off".equals(tmpValue)
			        ||
			        "no".equals(tmpValue)
			        ||
			        "false".equals(tmpValue)
			 ) {
				result = false;
			}
			else
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Integer getValue(String name, @Nullable Integer defaultValue) throws Exception
	{
		Integer result;

		String tmpValue = getValue(name);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			try
			{
				result = Integer.parseInt(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Float getValue(String name, @Nullable Float defaultValue) throws Exception
	{
		Float result;

		String tmpValue = getValue(name);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			try
			{
				result = Float.parseFloat(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Double getValue(String name, @Nullable Double defaultValue) throws Exception
	{
		Double result;

		String tmpValue = getValue(name);

		if(tmpValue.isEmpty() == false && "@NULL".equals(tmpValue) == false)
		{
			try
			{
				result = Double.parseDouble(tmpValue);
			}
			catch(NumberFormatException e)
			{
				result = defaultValue;
			}
		}
		else
		{
			result = defaultValue;
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(@Nullable String type)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(type == null)
		{
			result.append("<row>");
		}
		else
		{
			result.append("<row type=\"").append(Utility.escapeHTML(type)).append("\">");
		}

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_rowSet.m_numberOfFields; i++)
		{
			result.append("<field name=\"").append(Utility.escapeHTML(m_rowSet.m_fieldLabels[i])).append("\"><![CDATA[").append(m_values[i]).append("]]>")
			      .append(m_rowSet.processWebLink(i, this))
			      .append("</field>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
