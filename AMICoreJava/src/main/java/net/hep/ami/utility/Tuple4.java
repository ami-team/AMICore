package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

/**
 * A 4-tuple.
 */

public class Tuple4<A, B, C, D> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -8715309231303053521L;

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

	/**
	 * t
	 */

	public final D t;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 * @param _z z
	 * @param _t t
	 */

	@Contract(pure = true)
	public Tuple4(A _x, B _y, C _z, D _t)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
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
		                          .append(",")
		                          .append(Utility.object2json(t))
		                          .append("]")
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
