package net.hep.ami.jdbc.reflexion.structure;

import java.util.*;

import net.hep.ami.jdbc.reflexion.SchemaSingleton.*;

public class PathList
{
	/*---------------------------------------------------------------------*/

	private QId m_resolvedQId = null;

	/*---------------------------------------------------------------------*/

	private List<List<FrgnKey>> m_paths = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public PathList check(QId givenQId) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(m_resolvedQId == null)
		{
			throw new Exception("could not resolve column name " + givenQId + ": not found");
		}

		/*-----------------------------------------------------------------*/

		Collections.sort(m_paths, new Comparator<List<?>>() {

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

	public PathList addPath(QId givenQId, QId resolvedQId, Vector<FrgnKey> path) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(m_resolvedQId == null)
		{
			m_resolvedQId = resolvedQId;
		}
		else
		{
			if(m_resolvedQId.equals(resolvedQId) == false)
			{
				throw new Exception("could not resolve column name " + givenQId + ": ambiguous resolution");
			}
		}

		/*-----------------------------------------------------------------*/

		m_paths.add(new ArrayList<>(path));

		/*-----------------------------------------------------------------*/

		return this;
	}

	/*---------------------------------------------------------------------*/

	public QId getQId()
	{
		return m_resolvedQId;
	}

	/*---------------------------------------------------------------------*/

	public List<List<FrgnKey>> getPaths()
	{
		return m_paths;
	}

	/*---------------------------------------------------------------------*/
}
