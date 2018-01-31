package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

public class QId
{
	/*---------------------------------------------------------------------*/

	public enum Deepness
	{
		CATALOG,
		TABLE,
		COLUMN
	}

	/*---------------------------------------------------------------------*/

	private final String m_catalog;
	private final String m_table;
	private final String m_column;

	/*---------------------------------------------------------------------*/

	private final List<QId> m_qIds = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public QId(String qId)
	{
		this(qId, Deepness.COLUMN);
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId, Deepness deepness)
	{
		/*-----------------------------------------------------------------*/

		int idx1 = qId.indexOf('{');
		int idx2 = qId.indexOf('}');

		if(idx1 > 0 && idx1 < idx2)
		{
			String[] parts = qId.substring(idx1 + 1, idx2 + 0).split(",", -1);

			for(String part: parts) m_qIds.add(new QId(part));

			qId = qId.substring(0, idx1);
		}

		/*-----------------------------------------------------------------*/

		String[] parts = qId.split("\\.", -1);

		switch(deepness)
		{
			/*-------------------------------------------------------------*/

			case CATALOG:
				switch(parts.length)
				{
					case 1:
						m_catalog = unquote(parts[0]);
						m_table = null;
						m_column = null;
						break;

					default:
						m_catalog = null;
						m_table = null;
						m_column = null;
						break;
				}
				break;

			/*-------------------------------------------------------------*/

			case TABLE:
				switch(parts.length)
				{
					case 1:
						m_catalog = null;
						m_table = unquote(parts[0]);
						m_column = null;
						break;

					case 2:
						m_catalog = unquote(parts[0]);
						m_table = unquote(parts[1]);
						m_column = null;
						break;

					default:
						m_catalog = null;
						m_table = null;
						m_column = null;
						break;
				}
				break;

			/*-------------------------------------------------------------*/

			case COLUMN:
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
						break;
				}
				break;

			/*-------------------------------------------------------------*/

			default:
				m_catalog = null;
				m_table = null;
				m_column = null;
				break;

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String table, @Nullable String column)
	{
		this(catalog, table, column, null);
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String table, @Nullable String column, @Nullable List<QId> qIds)
	{
		m_catalog = catalog != null ? unquote(catalog) : null;
		m_table = table != null ? unquote(table) : null;
		m_column = column != null ? unquote(column) : null;

		if(m_qIds != null)
		{
			m_qIds.addAll(qIds);
		}
	}

	/*---------------------------------------------------------------------*/

	public static String unquote(String s)
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

	public static String quote(String s)
	{
		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		final int l = s.length() - 1;

		if(s.charAt(0) != '`'
		   ||
		   s.charAt(l) != '`'
		 ) {
			s = '`' + s.replace("`", "``") + '`';
		}

		/*-----------------------------------------------------------------*/

		s = s.trim();

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public boolean equals(QId qId)
	{
		return this == qId || (this.m_catalog == qId.m_catalog && this.m_table == qId.m_table && this.m_column == qId.m_column && this.m_qIds.equals(qId.m_qIds));
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

	public List<QId> getQIds()
	{
		return m_qIds;
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
					result.add(quote(m_catalog));
				}
				break;

			/*-------------------------------------------------------------*/

			case TABLE:
				if(m_catalog != null) {
					result.add(quote(m_catalog));
				}
				if(m_table != null) {
					result.add(quote(m_table));
				}
				break;

			/*-------------------------------------------------------------*/

			case COLUMN:
				if(m_catalog != null) {
					result.add(quote(m_catalog));
				}
				if(m_table != null) {
					result.add(quote(m_table));
				}
				if(m_column != null) {
					result.add(quote(m_column));
				}
				break;

			/*-------------------------------------------------------------*/
		}

		return String.join(".", result);
	}

	/*---------------------------------------------------------------------*/
}
