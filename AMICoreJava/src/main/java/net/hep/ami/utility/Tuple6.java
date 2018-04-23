package net.hep.ami.utility;

import java.io.*;

public class Tuple6<A, B, C, D, E, F> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 4847864711156867793L;

	/*---------------------------------------------------------------------*/

	public final A x;
	public final B y;
	public final C z;
	public final D t;
	public final E u;
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
