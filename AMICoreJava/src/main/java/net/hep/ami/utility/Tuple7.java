package net.hep.ami.utility;

import java.io.*;

import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

/**
 * A 7-tuple.
 */

public class Tuple7<A, B, C, D, E, F, G> implements Serializable
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = -6030498746014971545L;

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
	 */

	@Contract(pure = true)
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
