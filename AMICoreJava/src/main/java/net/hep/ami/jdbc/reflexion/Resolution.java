package net.hep.ami.jdbc.reflexion;

import java.util.*;

import net.hep.ami.jdbc.query.*;

public class Resolution
{
	/*---------------------------------------------------------------------*/

	private QId m_resolvedQId = null;

	/*---------------------------------------------------------------------*/

	private List<SchemaSingleton.FrgnKeys> m_resolvedPaths = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public Resolution check(QId givenQId) throws Exception
	{
		/*-----------------------------------------------------------------*/

		if(m_resolvedQId == null)
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

	public Resolution addPath(QId givenQId, QId resolvedQId, Vector<SchemaSingleton.FrgnKey> path) throws Exception
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

		m_resolvedPaths.add(new SchemaSingleton.FrgnKeys(path));

		/*-----------------------------------------------------------------*/

		return this;
	}

	/*---------------------------------------------------------------------*/

	public QId getQId()
	{
		return m_resolvedQId;
	}

	/*---------------------------------------------------------------------*/

	public List<SchemaSingleton.FrgnKeys> getPaths()
	{
		return m_resolvedPaths;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int hashCode()
	{
		return Objects.hash(m_resolvedQId, "@", m_resolvedPaths);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return m_resolvedQId.toString() + "@" + m_resolvedPaths.toString();
	}

	/*---------------------------------------------------------------------*/
}
