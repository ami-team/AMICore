package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;
import java.util.stream.*;

public class QId
{
	/*---------------------------------------------------------------------*/

	public enum Deepness
	{
		CATALOG(1),
		TABLE(2),
		COLUMN(3);

		protected final int length;

		private Deepness(int _length)
		{
			length = _length;
		}
	}

	/*---------------------------------------------------------------------*/

	private final List<String> m_sids = new ArrayList<>();

	private final List<QId> m_path = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public QId(String qId) throws Exception
	{
		this(qId, Deepness.COLUMN);
	}

	/*---------------------------------------------------------------------*/

	public QId(String qId, Deepness deepness) throws Exception
	{
		/*-----------------------------------------------------------------*/

		String tmp;

		int idx1 = qId.  indexOf  ('{');
		int idx2 = qId.lastIndexOf('}');

		if(idx1 > 0x00
		   &&
		   idx2 > idx1
		 ) {
			tmp = qId.substring(idx1 + 1, idx2 + 0);
			qId = qId.substring(0x00 + 0, idx1 + 0);

			for(String part: tmp.split(",", -1))
			{
				m_path.add(new QId(part));
			}

			int idx3 = tmp.  indexOf  ('{');
			int idx4 = tmp.lastIndexOf('}');

			if(idx3 >= 0
			   &&
			   idx4 >= 0
			 ) {
				throw new Exception("invalid QId syntax");
			}
		}
		else
		{
			if(idx1 >= 0
			   ||
			   idx2 >= 0
			 ) {
				throw new Exception("invalid QId syntax");
			}
		}

		/*-----------------------------------------------------------------*/

		String[] parts = qId.split("\\.", -1);

		final int length = Math.min(
			parts.length,
			deepness.length
		);

		if(length > 3)
		{
			throw new Exception("invalid QId syntax");
		}

		/*-----------------------------------------------------------------*/

		for(	int i = 0; i < length; i++)
		{
			m_sids.add(unquote(parts[i]));
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String table, @Nullable String column)
	{
		this(catalog, table, column, null);
	}

	/*---------------------------------------------------------------------*/

	public QId(@Nullable String catalog, @Nullable String table, @Nullable String column, @Nullable List<QId> path)
	{
		if(catalog != null) {
			m_sids.add(unquote(catalog));
		}

		if(catalog != null) {
			m_sids.add(unquote(table));
		}

		if(catalog != null) {
			m_sids.add(unquote(column));
		}

		if(m_path != null) {
			m_path.addAll(path);
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
		return this == qId || (
			this.m_sids.equals(qId.m_sids)
			&&
			this.m_path.equals(qId.m_path)
		);
	}

	/*---------------------------------------------------------------------*/

	public String getCatalog()
	{
		return (m_sids.size() > 0) ? m_sids.get(0) : null;
	}

	/*---------------------------------------------------------------------*/

	public String getTable()
	{
		return (m_sids.size() > 1) ? m_sids.get(1) : null;
	}

	/*---------------------------------------------------------------------*/

	public String getColumn()
	{
		return (m_sids.size() > 2) ? m_sids.get(2) : null;
	}

	/*---------------------------------------------------------------------*/

	public List<QId> getPath()
	{
		return m_path;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return toStringBuilder(Deepness.COLUMN).toString();
	}

	/*---------------------------------------------------------------------*/

	public String toString(Deepness deepness)
	{
		return toStringBuilder(deepness).toString();
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder()
	{
		return toStringBuilder(Deepness.COLUMN);
	}

	/*---------------------------------------------------------------------*/

	public StringBuilder toStringBuilder(Deepness deepness)
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append(m_sids.stream().map(str -> quote(str)).collect(Collectors.joining(".")))
		;

		/*-----------------------------------------------------------------*/

		if(m_path.isEmpty() == false)
		{
			result.append("{")
			      .append(m_path.stream().map(qId -> qId.toString()).collect(Collectors.joining(",")))
			      .append("}")
			;
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/
}
