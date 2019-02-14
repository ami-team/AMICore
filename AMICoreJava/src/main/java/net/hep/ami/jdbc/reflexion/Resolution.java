package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class Resolution
{
	/*---------------------------------------------------------------------*/

	private Column m_resolvedColumn = null;

	private QId m_resolvedInternalQId = null;
	private QId m_resolvedExternalQId = null;

	/*---------------------------------------------------------------------*/

	private int m_maxResolvedPathLen = 0;

	private List<FrgnKeys> m_resolvedPaths = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public Resolution addPath(QId unresolvedQId, Column resolvedColumn, List<FrgnKey> path) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(m_resolvedColumn == null)
		{
			m_resolvedColumn = resolvedColumn;

			m_resolvedInternalQId = new QId(resolvedColumn.internalCatalog, resolvedColumn.table, resolvedColumn.name);
			m_resolvedExternalQId = new QId(resolvedColumn.externalCatalog, resolvedColumn.table, resolvedColumn.name);
		}
		else
		{
			if(m_resolvedColumn.equals(resolvedColumn) == false)
			{
				throw new Exception("could not resolve column name " + unresolvedQId + ": ambiguous resolution");
			}
		}

		/*-----------------------------------------------------------------*/

		m_resolvedPaths.add(new FrgnKeys(path));

		if(m_maxResolvedPathLen < path.size())
		{
			m_maxResolvedPathLen = path.size();
		}

		/*-----------------------------------------------------------------*/

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Resolution finalize(QId unresolvedQId) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(m_resolvedInternalQId == null)
		{
			throw new Exception("could not resolve field " + unresolvedQId + ": not found");
		}

		/*-----------------------------------------------------------------*/

		Collections.sort(m_resolvedPaths, new Comparator<List<?>>() {

			@Override
			public int compare(List<?> o1, List<?> o2)
			{
				return o1.size() - o2.size();
			}
		});

		/*-----------------------------------------------------------------*/

		return this;
	}

	/*---------------------------------------------------------------------*/

	public Column getColumn()
	{
		return m_resolvedColumn;
	}

	/*---------------------------------------------------------------------*/

	public QId getInternalQId()
	{
		return m_resolvedInternalQId;
	}

	/*---------------------------------------------------------------------*/

	public QId getExternalQId()
	{
		return m_resolvedExternalQId;
	}

	/*---------------------------------------------------------------------*/

	public int getMaxPathLen()
	{
		return m_maxResolvedPathLen;
	}

	/*---------------------------------------------------------------------*/

	public List<FrgnKeys> getPaths()
	{
		return m_resolvedPaths;
	}

	/*---------------------------------------------------------------------*/

	public int getPathHashCode()
	{
		int result = 1;

		for(FrgnKeys frgnKeys: m_resolvedPaths)
		{
			for(FrgnKey frgnKey: /**/ frgnKeys /**/)
			{
				result = 31 * result + frgnKey.hashCode();
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Resolution skip(int nr)
	{
		Resolution result = new Resolution();

		/*-----------------------------------------------------------------*/

		int maxResolvedPathLen = 0;

		for(SchemaSingleton.FrgnKeys frgnKeys: m_resolvedPaths)
		{
			/*-------------------------------------------------------------*/

			int cnt = 0;

			SchemaSingleton.FrgnKeys path = new SchemaSingleton.FrgnKeys();

			for(SchemaSingleton.FrgnKey frgnKey: frgnKeys)
			{
				if(cnt++ >= nr)
				{
					path.add(frgnKey);
				}
			}

			/*-------------------------------------------------------------*/

			result.m_resolvedPaths.add(path);

			if(maxResolvedPathLen < path.size())
			{
				maxResolvedPathLen = path.size();
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		result.m_resolvedColumn = m_resolvedColumn;

		result.m_resolvedInternalQId = m_resolvedInternalQId;
		result.m_resolvedExternalQId = m_resolvedExternalQId;

		result.m_maxResolvedPathLen = maxResolvedPathLen;

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return m_resolvedInternalQId.toString() + "@" + m_resolvedPaths.toString();
	}

	/*---------------------------------------------------------------------*/
}
