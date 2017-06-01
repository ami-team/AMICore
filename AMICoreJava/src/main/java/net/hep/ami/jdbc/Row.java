package net.hep.ami.jdbc;

import java.sql.SQLException;

public class Row
{
	/*---------------------------------------------------------------------*/

	private final RowSet m_rowSet;

	private final String[] m_values;

	/*---------------------------------------------------------------------*/

	protected Row(RowSet rowSet) throws SQLException
	{
		m_values = (m_rowSet = rowSet).getCurrentValue();
	}

	/*---------------------------------------------------------------------*/

	public String getFieldCatalog(int columnIndex) throws Exception
	{
		return m_rowSet.getCatalogOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldEntity(int columnIndex) throws Exception
	{
		return m_rowSet.getEntityOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldName(int columnIndex) throws Exception
	{
		return m_rowSet.getNameOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getFieldType(int columnIndex) throws Exception
	{
		return m_rowSet.getTypeOfField(columnIndex);
	}

	/*---------------------------------------------------------------------*/

	public String getValue(int fieldIndex) throws Exception
	{
		if(fieldIndex < 0 || fieldIndex >= m_rowSet.m_numberOfFields)
		{
			throw new Exception("index out of range");
		}

		return m_values[
			fieldIndex
		];
	}

	/*---------------------------------------------------------------------*/

	public String getValue(String fieldName) throws Exception
	{
		if(m_rowSet.m_fieldIndices.containsKey(fieldName) == false)
		{
			throw new Exception("field not in row");
		}

		return m_values[
			m_rowSet.m_fieldIndices.get(fieldName)
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
