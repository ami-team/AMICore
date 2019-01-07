package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class Resolution
{
	/*---------------------------------------------------------------------*/

	private int m_pathLen = 0;

	private Column m_resolvedColumn = null;

	private QId m_resolvedInternalQId = null;
	private QId m_resolvedExternalQId = null;

	/*---------------------------------------------------------------------*/

	private List<FrgnKeys> m_resolvedPaths = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public Resolution check(QId givenQId) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(m_resolvedInternalQId == null)
		{
			throw new Exception("could not resolve column name " + givenQId + ": not found");
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

	public Resolution addPath(QId givenQId, Column resolvedColumn, Vector<FrgnKey> path) throws Exception
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
				throw new Exception("could not resolve column name " + givenQId + ": ambiguous resolution");
			}
		}

		/*-----------------------------------------------------------------*/

		m_resolvedPaths.add(new FrgnKeys(path));

		if(m_pathLen < path.size())
		{
			m_pathLen = path.size();
		}

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

	public List<FrgnKeys> getPaths()
	{
		return m_resolvedPaths;
	}

	/*---------------------------------------------------------------------*/

	public int getPathLen()
	{
		return m_pathLen;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int hashCode()
	{
		return Objects.hash(m_resolvedInternalQId, "@", m_resolvedPaths);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return m_resolvedInternalQId.toString() + "@" + m_resolvedPaths.toString();
	}

	/*---------------------------------------------------------------------*/
}
