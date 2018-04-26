package net.hep.ami.utility;

import java.io.*;

/**
 * A 5-tuple.
 */

public class Tuple5<A, B, C, D, E> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -6018601306968105631L;

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

	/**
	 * u
	 */

	public final E u;

	/*---------------------------------------------------------------------*/

	public Tuple5(A _x, B _y, C _z, D _t, E _u)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
		u = _u;
	}

	/*---------------------------------------------------------------------*/
}
