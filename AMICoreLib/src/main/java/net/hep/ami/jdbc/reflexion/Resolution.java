package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class Resolution
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private Column m_resolvedColumn = null;

	private QId m_resolvedInternalQId = null;
	private QId m_resolvedExternalQId = null;

	/*----------------------------------------------------------------------------------------------------------------*/

	private int m_maxResolvedPathLen = 0;

	private final List<FrgnKeys> m_resolvedPaths = new ArrayList<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	public Resolution addPath(QId unresolvedQId, Column resolvedColumn, List<FrgnKey> path) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(m_resolvedColumn == null)
		{
			m_resolvedColumn = resolvedColumn;

			m_resolvedInternalQId = new QId(resolvedColumn.internalCatalog, resolvedColumn.entity, resolvedColumn.field);
			m_resolvedExternalQId = new QId(resolvedColumn.externalCatalog, resolvedColumn.entity, resolvedColumn.field);
		}
		else
		{
			if(!m_resolvedColumn.equals(resolvedColumn))
			{
				throw new Exception("could not resolve column name " + unresolvedQId + ": ambiguous resolution");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		m_resolvedPaths.add(new FrgnKeys(path));

		if(m_maxResolvedPathLen < path.size())
		{
			m_maxResolvedPathLen = path.size();
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public Resolution finalize(QId unresolvedQId) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(m_resolvedInternalQId == null)
		{
			throw new Exception("could not resolve field " + unresolvedQId + ": not found");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		m_resolvedPaths.sort(Comparator.comparingInt(ArrayList::size));

		/*------------------------------------------------------------------------------------------------------------*/

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public Column getColumn()
	{
		return m_resolvedColumn;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public QId getInternalQId()
	{
		return m_resolvedInternalQId;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public QId getExternalQId()
	{
		return m_resolvedExternalQId;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public int getMaxPathLen()
	{
		return m_maxResolvedPathLen;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public List<FrgnKeys> getPaths()
	{
		return m_resolvedPaths;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

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

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return m_resolvedInternalQId.toString() + "@" + m_resolvedPaths.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
