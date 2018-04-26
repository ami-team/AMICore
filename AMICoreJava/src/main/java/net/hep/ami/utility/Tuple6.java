package net.hep.ami.utility;

import java.io.*;

/**
 * A 6-tuple.
 */

public class Tuple6<A, B, C, D, E, F> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 4847864711156867793L;

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

	/**
	 * v
	 */

	public final F v;

	/*---------------------------------------------------------------------*/

	public Tuple6(A _x, B _y, C _z, D _t, E _u, F _v)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
		u = _u;
		v = _v;
	}

	/*---------------------------------------------------------------------*/
}
