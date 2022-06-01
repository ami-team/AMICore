package net.hep.ami.jdbc;

import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class Update
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private final Integer m_nbOfUpdatedRows;

	private final String m_generatedKey;

	private final String m_sql;
	private final String m_mql;
	private final String m_ast;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public Update(int nbOfUpdatedRows)
	{
		this(nbOfUpdatedRows, null, null, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public Update(int nbOfUpdatedRows, @Nullable String sql, @Nullable String mql, @Nullable String ast)
	{
		m_nbOfUpdatedRows = nbOfUpdatedRows;

		m_generatedKey = /**/null/**/;

		m_sql = sql != null ? sql : "";
		m_mql = mql != null ? mql : "";
		m_ast = ast != null ? ast : "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public Update(int nbOfUpdatedRows, @Nullable String generatedKey, @Nullable String sql, @Nullable String mql, @Nullable String ast)
	{
		m_nbOfUpdatedRows = nbOfUpdatedRows;

		m_generatedKey = generatedKey;

		m_sql = sql != null ? sql : "";
		m_mql = mql != null ? mql : "";
		m_ast = ast != null ? ast : "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public int getNbOfUpdatedRows()
	{
		return m_nbOfUpdatedRows;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	public String getGeneratedKey()
	{
		return m_generatedKey;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getSQL()
	{
		return m_sql;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getMQL()
	{
		return m_mql;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String getAST()
	{
		return m_ast;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public StringBuilder toStringBuilder()
	{
		StringBuilder result = new StringBuilder().append("<sql><![CDATA[").append(m_sql).append("]]></sql>")
		                                          .append("<mql><![CDATA[").append(m_mql).append("]]></mql>")
		                                          .append("<ast><![CDATA[").append(m_ast).append("]]></ast>")
		                                          .append("<info><![CDATA[").append(m_nbOfUpdatedRows).append(" element(s) updated with success]]></info>")
		                                          .append("<rowset>")
		                                          .append("<row>")
		                                          .append("<field name=\"nbOfUpdatedRows\"><![CDATA[").append(m_nbOfUpdatedRows).append("]]></field>")
		                                          .append(m_generatedKey != null ? "<field name=\"generatedKey\"><![CDATA[" + m_generatedKey + "]]></field>" : "")
		                                          .append("</row>")
		                                          .append("</rowset>")
		;

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
