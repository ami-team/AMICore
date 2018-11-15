package net.hep.ami.utility;

import java.io.*;

/**
 * A 7-tuple.
 */

public class Tuple7<A, B, C, D, E, F, G> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -6030498746014971545L;

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

	/**
	 * w
	 */

	public final G w;

	/*---------------------------------------------------------------------*/

	/**
	 * Constructor
	 *
	 * @param _x x
	 * @param _y y
	 * @param _z z
	 * @param _t t
	 * @param _u u
	 * @param _v v
	 * @param _w w
	 */

	public Tuple7(A _x, B _y, C _z, D _t, E _u, F _v, G _w)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
		u = _u;
		v = _v;
		w = _w;
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
		                          .append(",")
		                          .append(u.toString())
		                          .append(",")
		                          .append(v.toString())
		                          .append(",")
		                          .append(w.toString())
		                          .append("]")
		                          .toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
