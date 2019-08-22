package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

/**
 * A 3-tuple.
 */

public class Tuple3<A, B, C> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -7680714215487123055L;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * x
	 */

	public final A x;

	/**
	 * y
	 */

	public final B y;

	/**
	 * z
	 */

	public final C z;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 * @param _z z
	 */

	@org.jetbrains.annotations.Contract(pure = true)
	public Tuple3(A _x, B _y, C _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return new StringBuilder().append("[")
		                          .append(Utility.object2json(x))
		                          .append(",")
		                          .append(Utility.object2json(y))
		                          .append(",")
		                          .append(Utility.object2json(z))
		                          .append("]")
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
