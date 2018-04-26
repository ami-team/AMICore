package net.hep.ami.utility;

import java.io.*;

/**
 * A 3-tuple.
 */

public class Tuple3<A, B, C> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -7680714215487123055L;

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

	/*---------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 * @param _z z
	 */

	public Tuple3(A _x, B _y, C _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}

	/*---------------------------------------------------------------------*/
}
