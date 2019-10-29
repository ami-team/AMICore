package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

/**
 * A 5-tuple.
 */

public class Tuple5<A, B, C, D, E> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -6018601306968105631L;

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

	/**
	 * u
	 */

	public final E u;

	/*----------------------------------------------------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 * @param _z z
	 * @param _t t
	 * @param _u u
	 */

	@Contract(pure = true)
	public Tuple5(A _x, B _y, C _z, D _t, E _u)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
		u = _u;
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
		                          .append(",")
		                          .append(Utility.object2json(u))
		                          .append("]")
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
