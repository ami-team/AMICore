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

		return new Iterable<E>()
		{
			public Iterator<E> iterator()
			{
				return new Iterator<E>()
				{
					public boolean hasNext()
					{
						return enumeration.hasMoreElements();
					}

					public E next()
					{
						return enumeration.nextElement();
					}
				};
			}
		};
	}

	/*---------------------------------------------------------------------*/
}
