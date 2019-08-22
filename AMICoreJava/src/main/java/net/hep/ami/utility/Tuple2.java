package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

/**
 * A 2-tuple.
 */

public class Tuple2<A, B> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -483227104742113803L;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * x
	 */

	public final A x;

	/**
	 * y
	 */

	public final B y;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 */

	@org.jetbrains.annotations.Contract(pure = true)
	public Tuple2(A _x, B _y)
	{
		x = _x;
		y = _y;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return new StringBuilder().append("[")
		                          .append(Utility.object2json(x))
		                          .append(",")
		                          .append(Utility.object2json(y))
		                          .append("]")
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
