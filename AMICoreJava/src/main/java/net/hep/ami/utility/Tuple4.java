package net.hep.ami.utility;

import java.io.*;

/**
 * A 4-tuple.
 */

public class Tuple4<A, B, C, D> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -8715309231303053521L;

	/*---------------------------------------------------------------------*/

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

	/*---------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 * @param _z z
	 * @param _t t
	 */

	public Tuple4(A _x, B _y, C _z, D _t)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String toString()
	{
		return new StringBuilder().append("[")
		                          .append(x.toString())
		                          .append(",")
		                          .append(y.toString())
		                          .append(",")
		                          .append(z.toString())
		                          .append(",")
		                          .append(t.toString())
		                          .append("]")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
