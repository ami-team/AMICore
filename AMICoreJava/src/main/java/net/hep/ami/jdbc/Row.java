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

	public String getFieldCatalog(int fieldIndex) throws Exception
	{
		return m_rowSet.getCatalogOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldEntity(int fieldIndex) throws Exception
	{
		return m_rowSet.getEntityOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldName(int fieldIndex) throws Exception
	{
		return m_rowSet.getNameOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldLabel(int fieldIndex) throws Exception
	{
		return m_rowSet.getLabelOfField(fieldIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldType(int fieldIndex) throws Exception
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

		return m_values[
			/*---------*/ fieldIndex /*---------*/
		];
	}

	/*---------------------------------------------------------------------*/

	public String getValue(String labelName) throws Exception
	{
		if(m_rowSet.m_labelIndices.containsKey(labelName) == false)
		{
			throw new Exception("field label not in row");
		}

		return m_values[
			m_rowSet.m_labelIndices.get(labelName)
		];
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
