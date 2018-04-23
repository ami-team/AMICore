package net.hep.ami.utility;

import java.io.*;

public class Tuple7<A, B, C, D, E, F, G> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -6030498746014971545L;

	/*---------------------------------------------------------------------*/

	public final A x;
	public final B y;
	public final C z;
	public final D t;
	public final E u;
	public final F v;
	public final G w;

	/*---------------------------------------------------------------------*/

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
}
