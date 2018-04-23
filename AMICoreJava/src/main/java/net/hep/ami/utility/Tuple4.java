package net.hep.ami.utility;

import java.io.*;

public class Tuple4<A, B, C, D> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -8715309231303053521L;

	/*---------------------------------------------------------------------*/

	public final A x;
	public final B y;
	public final C z;
	public final D t;

	/*---------------------------------------------------------------------*/

	public Tuple4(A _x, B _y, C _z, D _t)
	{
		x = _x;
		y = _y;
		z = _z;
		t = _t;
	}

	/*---------------------------------------------------------------------*/
}
