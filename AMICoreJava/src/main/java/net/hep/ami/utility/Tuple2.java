package net.hep.ami.utility;

import java.io.*;

/**
 * A 2-tuple.
 */

public class Tuple2<A, B> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -483227104742113803L;

	/*---------------------------------------------------------------------*/

	/**
	 * x
	 */

	public final A x;

	/**
	 * y
	 */

	public final B y;

	/*---------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 */

	public Tuple2(A _x, B _y)
	{
		x = _x;
		y = _y;
	}

	/*---------------------------------------------------------------------*/
}
