package net.hep.ami.utility;

import java.util.*;

public class Misc
{
	/*---------------------------------------------------------------------*/

	private Misc() {}

	/*---------------------------------------------------------------------*/

	public static <E> Iterable<E> toIterable(final Enumeration<E> enumeration)
	{
		if(enumeration == null)
		{
			throw new NullPointerException();
		}

		return () -> new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return enumeration.hasMoreElements();
			}

			@Override
			public E next()
			{
				return enumeration.nextElement();
			}
		};
	}

	/*---------------------------------------------------------------------*/
}
