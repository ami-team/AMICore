package net.hep.ami.utility;

import lombok.*;

import java.util.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

public class Zip
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	@ToString
	public static final class Tuple2<U, V>
	{
		@NotNull private final U x;
		@NotNull private final V y;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static <U, V> Stream<Tuple2<U, V>> asStream(@NotNull U @NotNull [] o1, @NotNull V @NotNull [] o2)
	{
		return IntStream.range(0, Math.min(o1.length, o2.length)).mapToObj(i -> new Tuple2<>(o1[i], o2[i]));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static <U, V> Stream<Tuple2<U, V>> asStream(@NotNull AbstractList<U> o1, @NotNull AbstractList<V> o2)
	{
		return IntStream.range(0, Math.min(o1.size(), o2.size())).mapToObj(i -> new Tuple2<>(o1.get(i), o2.get(i)));
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
