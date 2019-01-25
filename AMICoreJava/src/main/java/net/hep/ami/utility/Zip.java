package net.hep.ami.utility;

import java.util.*;
import java.util.stream.*;

public class Zip
{
	/*---------------------------------------------------------------------*/

	public static <U, V> Stream<Tuple2<U, V>> asStream(U[] o1, V[] o2)
	{
		return IntStream.range(0, Math.min(o1.length, o2.length)).mapToObj(i -> new Tuple2<>(o1[i], o2[i]));
	}

	/*---------------------------------------------------------------------*/

	public static <U, V> Stream<Tuple2<U, V>> asStream(AbstractList<U> o1, AbstractList<V> o2)
	{
		return IntStream.range(0, Math.min(o1.size(), o2.size())).mapToObj(i -> new Tuple2<>(o1.get(i), o2.get(i)));
	}

	/*---------------------------------------------------------------------*/
}
