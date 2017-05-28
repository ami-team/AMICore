package net.hep.ami.jdbc;

public class Row
{
	/*---------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	private final String[] m_values;

	/*---------------------------------------------------------------------*/

	protected Row(RowSet rowSet, String[] values)
	{
		m_rowSet = rowSet;

		m_values = values;
	}

	/*---------------------------------------------------------------------*/

	public String getFieldCatalog(int columnIndex)
	{
		return m_rowSet.getCatalogOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldEntity(int columnIndex)
	{
		return m_rowSet.getEntityOfField(columnIndex);
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
			result.append("<row type=\"").append(type).append("\">");
		}

		/*-----------------------------------------------------------------*/

		final int numberOfValues = m_values.length;

		for(int i = 0; i < numberOfValues; i++)
		{
			result.append("<field catalog=\"")
			      .append(m_rowSet.m_fieldCatalogs[i])
			      .append("\" entity=\"")
			      .append(m_rowSet.m_fieldEntities[i])
			      .append("\" name=\"")
			      .append(m_rowSet.m_fieldNames[i])
			      .append("\" type=\"")
			      .append(m_rowSet.m_fieldTypes[i] + "\"><![CDATA[").append(m_values[i]).append("]]></field>")
			;
		}

		/*-----------------------------------------------------------------*/

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
