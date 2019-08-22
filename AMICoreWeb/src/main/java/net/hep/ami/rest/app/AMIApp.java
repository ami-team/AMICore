package net.hep.ami.rest.app;

import java.util.*;

import javax.ws.rs.core.*;

public class AMIApp extends Application
{
	/*---------------------------------------------------------------------*/

	@Override
	public Set<Class<?>> getClasses()
	{
		final Set<Class<?>> classes = new HashSet<>();

		classes.add(net.hep.ami.rest.filter.AMIFilter.class);

		return classes;
	}

	/*---------------------------------------------------------------------*/
}
