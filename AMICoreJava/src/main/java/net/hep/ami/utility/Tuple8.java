package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

/**
 * A 8-tuple.
 */

public class Tuple8<A, B, C, D, E, F, G, H> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -2160018170687094433L;

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

	/**
	 * v
	 */

	public final F v;

	/**
	 * w
	 */

	public final G w;

	/**
	 * w
	 */

	public final H a;

	/*----------------------------------------------------------------------------------------------------------------*/

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
	 * @param _a a
	 */

	@org.jetbrains.annotations.Contract(pure = true)
	public Tuple8(A _x, B _y, C _z, D _t, E _u, F _v, G _w, H _a)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
		u = _u;
		v = _v;
		w = _w;
		a = _a;
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
		                          .append(",")
		                          .append(Utility.object2json(v))
		                          .append(",")
		                          .append(Utility.object2json(w))
		                          .append("]")
		                          .toString()
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
