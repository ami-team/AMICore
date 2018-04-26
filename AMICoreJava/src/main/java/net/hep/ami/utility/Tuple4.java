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

	public Tuple4(A _x, B _y, C _z, D _t)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
	}

	/*---------------------------------------------------------------------*/
}
