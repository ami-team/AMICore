package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

/**
 * A 1-tuple.
 */

public class Tuple1<A> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -5819375594899075829L;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * x
	 */

	public final A x;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 */

	@org.jetbrains.annotations.Contract(pure = true)
	public Tuple1(A _x)
	{
		x = _x;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return new StringBuilder().append("[")
		                          .append(Utility.object2json(x))
		                          .append("]")
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
