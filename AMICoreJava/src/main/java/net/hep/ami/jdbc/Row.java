package net.hep.ami.jdbc;

import java.util.*;

import groovy.lang.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class Row
{
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	private static final GroovyShell s_groovyShell = new GroovyShell(Row.class.getClassLoader());

	/*---------------------------------------------------------------------*/

	private static final Map<String, Script> s_groovyScripts = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, false);

	/*---------------------------------------------------------------------*/

	public static String processWebLink(@Nullable String code, String catalog, String entity, String field, String value)
	{
		if(code == null)
		{
			return "";
		}

		/*-----------------------------------------------------------------*/
		/* COMPILE GROOVY SCRIPT                                           */
		/*-----------------------------------------------------------------*/

		Script script = s_groovyScripts.get(code);

		if(script == null)
		{
			script = s_groovyShell.parse(code);

			s_groovyScripts.put(code, script);
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE GROOVY SCRIPT                                           */
		/*-----------------------------------------------------------------*/

		synchronized(Row.class)
		{
			/*-------------------------------------------------------------*/

			Binding binding = new Binding();

			binding.setVariable("catalog", catalog);
			binding.setVariable("entity", entity);
			binding.setVariable("field", field);
			binding.setVariable("value", value);

			script.setBinding(binding);

			/*-------------------------------------------------------------*/

			try
			{
				return ((WebLink) script.run()).toString();
			}
			catch(ClassCastException e)
			{
				return "";
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/
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
			fieldIndex
		];
	}

	/*---------------------------------------------------------------------*/

	public String getValue(String labelName) throws Exception
	{
		if(m_rowSet.m_labelIndices.containsKey(labelName) == false)
		{
			throw new Exception("label not in row");
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
			      .append(processWebLink(
							m_rowSet.m_fieldWebLinkScript[i],
							m_rowSet.m_fieldCatalogs[i],
							m_rowSet.m_fieldEntities[i],
							m_rowSet.m_fieldNames[i],
							m_values[i]
			       ))
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
