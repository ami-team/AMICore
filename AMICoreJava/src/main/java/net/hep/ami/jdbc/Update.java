package net.hep.ami.jdbc;

import net.hep.ami.utility.*;

public class Update
{
	/*---------------------------------------------------------------------*/

	private final int m_nbOfUpdatedRows;

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
		return new StringBuilder().append("<sql><![CDATA[").append(m_sql).append("]]></sql>")
		                          .append("<mql><![CDATA[").append(m_mql).append("]]></mql>")
		                          .append("<ast><![CDATA[").append(m_ast).append("]]></ast>")
		                          .append("<info><![CDATA[").append(m_nbOfUpdatedRows).append(" element(s) updated with success]]></info>")
		                          .append("<rowset><row><field name=\"nbOfUpdatedRows\"><![CDATA[").append(m_nbOfUpdatedRows).append("]]></field></row></rowset>")
		;
	}

	/*---------------------------------------------------------------------*/
}
