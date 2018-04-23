package net.hep.ami.utility;

import java.io.*;

public class Tuple5<A, B, C, D, E> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -6018601306968105631L;

	/*---------------------------------------------------------------------*/

	public final A x;
	public final B y;
	public final C z;
	public final D t;
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
