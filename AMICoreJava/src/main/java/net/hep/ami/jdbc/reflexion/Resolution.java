package net.hep.ami.jdbc.reflexion;

import java.util.*;

public class Resolution
{
	/*---------------------------------------------------------------------*/

	private QId m_resolvedQId = null;

	/*---------------------------------------------------------------------*/

	private List<SchemaSingleton.FrgnKeys> m_paths = new ArrayList<>();

	/*---------------------------------------------------------------------*/

	public Resolution check(QId givenQId) throws Exception
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

		m_paths.add(new SchemaSingleton.FrgnKeys(path));

		/*-----------------------------------------------------------------*/

		return this;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/*---------------------------------------------------------------------*/

	public QId getQId()
	{
		return m_resolvedQId;
	}

	/*---------------------------------------------------------------------*/

	public List<SchemaSingleton.FrgnKeys> getPaths()
	{
		return m_paths;
	}

	/*---------------------------------------------------------------------*/

	public String toString()
	{
		return m_resolvedQId.toString() + "@" + m_paths.toString();
	}

	/*---------------------------------------------------------------------*/
}
