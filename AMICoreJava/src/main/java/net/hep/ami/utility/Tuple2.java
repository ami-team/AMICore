package net.hep.ami.utility;

import java.io.*;

public class Tuple2<A, B> implements Serializable
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -483227104742113803L;

	/*---------------------------------------------------------------------*/

	public final A x;
	public final B y;

	/*---------------------------------------------------------------------*/

	public Tuple2(A _x, B _y)
	{
		x = _x;
		y = _y;
	}

	/*---------------------------------------------------------------------*/
}
