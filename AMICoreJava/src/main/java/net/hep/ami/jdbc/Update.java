package net.hep.ami.jdbc;

import net.hep.ami.utility.*;

public class Update
{
	/*---------------------------------------------------------------------*/

	private final Integer m_nbOfUpdatedRows;

	private final String m_generatedKey;

	private final String m_sql;
	private final String m_mql;
	private final String m_ast;

	/*---------------------------------------------------------------------*/

	public Update(int nbOfUpdatedRows) throws Exception
	{
		this(nbOfUpdatedRows, null, null, null);
	}

	/*---------------------------------------------------------------------*/

	public Update(int nbOfUpdatedRows, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		m_nbOfUpdatedRows = nbOfUpdatedRows;

		m_generatedKey = /**/null/**/;

		m_sql = sql != null ? sql : "";
		m_mql = mql != null ? mql : "";
		m_ast = ast != null ? ast : "";
	}

	/*---------------------------------------------------------------------*/

	public Update(int nbOfUpdatedRows, @Nullable String generatedKey, @Nullable String sql, @Nullable String mql, @Nullable String ast) throws Exception
	{
		m_nbOfUpdatedRows = nbOfUpdatedRows;

		m_generatedKey = generatedKey;

		m_sql = sql != null ? sql : "";
		m_mql = mql != null ? mql : "";
		m_ast = ast != null ? ast : "";
	}

	/*---------------------------------------------------------------------*/

	public int getNbOfUpdatedRows()
	{
		return m_nbOfUpdatedRows;
	}

	/*---------------------------------------------------------------------*/

	public String getGeneratedKey()
	{
		return m_generatedKey;
	}

	/*---------------------------------------------------------------------*/

	public String getSQL()
	{
		return m_sql;
	}

	/*---------------------------------------------------------------------*/

	public String getMQL()
	{
		return m_mql;
	}

	/*---------------------------------------------------------------------*/

	public String getAST()
	{
		return m_ast;
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder() throws Exception
	{
		StringBuilder result = new StringBuilder().append("<sql><![CDATA[").append(m_sql).append("]]></sql>")
		                                          .append("<mql><![CDATA[").append(m_mql).append("]]></mql>")
		                                          .append("<ast><![CDATA[").append(m_ast).append("]]></ast>")
		                                          .append("<info><![CDATA[").append(m_nbOfUpdatedRows).append(" element(s) updated with success]]></info>")
		;

		if(m_generatedKey == null)
		{
			return result.append("<rowset><row>")
			             .append("<field name=\"nbOfUpdatedRows\"><![CDATA[").append(m_nbOfUpdatedRows).append("]]></field>")
			             .append("</row></rowset>")
			;
		}
		else
		{
			return result.append("<rowset><row>")
			             .append("<field name=\"nbOfUpdatedRows\"><![CDATA[").append(m_nbOfUpdatedRows).append("]]></field>")
			             .append("<field name=\"generatedKey\"><![CDATA[").append(m_generatedKey).append("]]></field>")
			             .append("</row></rowset>")
			;
		}
	}

	/*---------------------------------------------------------------------*/
}
