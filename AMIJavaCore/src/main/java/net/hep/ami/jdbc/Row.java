package net.hep.ami.jdbc;

public class Row
{
	/*---------------------------------------------------------------------*/

	private RowSet m_rowSet;

	private String[] m_values;

	/*---------------------------------------------------------------------*/

	public Row(RowSet rowSet, String[] values)
	{
		m_rowSet = rowSet;

		m_values = values;
	}

	/*---------------------------------------------------------------------*/

	public String getFieldTable(int columnIndex)
	{
		return m_rowSet.getTableOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldName(int columnIndex)
	{
		return m_rowSet.getNameOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldType(int columnIndex)
	{
		return m_rowSet.getTypeOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getValue(int columnIndex)
	{
		return columnIndex < m_values.length ? m_values[columnIndex]
		                                     : null
		;
	}

	/*---------------------------------------------------------------------*/

	public String getValue(String fieldName)
	{
		return m_rowSet.m_fieldIndices.containsKey(fieldName) ? m_values[m_rowSet.m_fieldIndices.get(fieldName)]
		                                                      : null
		;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(String type)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		if(type == null)
		{
			result.append("<row>");
		}
		else
		{
			result.append("<row type=\"" + type + "\">");
		}

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < m_values.length; i++) /* pas beau le length dans le if */
		{
			result.append("<field table=\"" + m_rowSet.m_fieldTables[i] + "\" name=\"" + m_rowSet.m_fieldNames[i] + "\" type=\"" + m_rowSet.m_fieldTypes[i] + "\"><![CDATA[" + m_values[i] + "]]></field>");
		}

		/*-----------------------------------------------------------------*/

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(null);
	}

	/*---------------------------------------------------------------------*/
}
