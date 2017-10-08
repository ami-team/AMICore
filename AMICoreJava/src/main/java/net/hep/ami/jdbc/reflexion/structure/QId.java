package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class QId
{
	/*-----------------------------------------------------------------*/

	public enum Deepness {
		CATALOG,
		TABLE,
		COLUMN
	}

	/*---------------------------------------------------------------------*/

	private final String m_catalog;
	private final String m_table;
	private final String m_column;

	/*---------------------------------------------------------------------*/

	public QId(String qId) //throws Exception
	{
		String[] parts = qId.split("\\.");

		switch(parts.length)
		{
			case 1:
				m_catalog = null;
				m_table = null;
				m_column = unquote(parts[0]);
				break;

			case 2:
				m_catalog = null;
				m_table = unquote(parts[0]);
				m_column = unquote(parts[1]);
				break;

			case 3:
				m_catalog = unquote(parts[0]);
				m_table = unquote(parts[1]);
				m_column = unquote(parts[2]);
				break;

			default:
				m_catalog = null;
				m_table = null;
				m_column = null;
				//throw new Exception("could not parse qId `" + qId + "`");
		}
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String table, @Nullable String column)
	{
		m_catalog = catalog != null ? unquote(catalog) : null;
		m_table = table != null ? unquote(table) : null;
		m_column = column != null ? unquote(column) : null;
	}

	/*---------------------------------------------------------------------*/

	private static String unquote(String s)
	{
		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		final int l = s.length() - 1;
		if(s.charAt(0) == '`'
		   &&
		   s.charAt(l) == '`'
		 ) {
			s = s.substring(1, l).replace("``", "`");
		}

		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public boolean equals(QId qId)
	{
		return this.m_catalog == qId.m_catalog && this.m_table == qId.m_table && this.m_column == qId.m_column;
	}

	/*---------------------------------------------------------------------*/

	public String getCatalog()
	{
		return m_catalog;
	}

	/*---------------------------------------------------------------------*/

	public String getTable()
	{
		return m_table;
	}

	/*---------------------------------------------------------------------*/

	public String getColumn()
	{
		return m_column;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return toString(Deepness.COLUMN);
	}

	/*---------------------------------------------------------------------*/

	public String toString(Deepness deepness)
	{
		List<String> result = new ArrayList<>();

		switch(deepness)
		{
			/*-------------------------------------------------------------*/

			case CATALOG:
				if(m_catalog != null) {
					result.add(m_catalog);
				}
				break;

			/*-------------------------------------------------------------*/

			case TABLE:
				if(m_catalog != null) {
					result.add(m_catalog);
				}
				if(m_table != null) {
					result.add(m_table);
				}
				break;

			/*-------------------------------------------------------------*/

			case COLUMN:
				if(m_catalog != null) {
					result.add(m_catalog);
				}
				if(m_table != null) {
					result.add(m_table);
				}
				if(m_column != null) {
					result.add(m_column);
				}
				break;

			/*-------------------------------------------------------------*/
		}

		return String.join(".", result);
	}

	/*---------------------------------------------------------------------*/
}
