package net.hep.ami.utility;

import java.io.*;

import org.jetbrains.annotations.*;

public class AMIStringBuilder implements Serializable, Comparable<StringBuilder>, CharSequence
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final long serialVersionUID = 6188847820981293835L;

	/*----------------------------------------------------------------------------------------------------------------*/

	private final StringBuilder m_underlyingStringBuilder;

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public AMIStringBuilder()
	{
		m_underlyingStringBuilder = new StringBuilder(16);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	public AMIStringBuilder(int capacity)
	{
		m_underlyingStringBuilder = new StringBuilder(capacity);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public AMIStringBuilder(@NotNull String str)
	{
		m_underlyingStringBuilder = new StringBuilder(str.length() + 16);

		m_underlyingStringBuilder.append(str);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public AMIStringBuilder(@NotNull CharSequence seq)
	{
		m_underlyingStringBuilder = new StringBuilder(seq.length() + 16);

		m_underlyingStringBuilder.append(seq);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public int length()
	{
		return m_underlyingStringBuilder.length();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public char charAt(int index)
	{
		return m_underlyingStringBuilder.charAt(index);
	}

	/////////
	public int codePointAt(int index)
	{
		return m_underlyingStringBuilder.codePointAt(index);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/////////
	public CharSequence subSequence(int start)
	{
		return m_underlyingStringBuilder.substring(start);
	}

	@Override
	public CharSequence subSequence(int start, int end)
	{
		return m_underlyingStringBuilder.substring(start, end);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public String substring(int start)
	{
		return m_underlyingStringBuilder.substring(start);
	}

	public String substring(int start, int stop)
	{
		return m_underlyingStringBuilder.substring(start, stop);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public int compareTo(@NotNull StringBuilder o)
	{
		return m_underlyingStringBuilder.compareTo(o);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public AMIStringBuilder append(@Nullable Object o)
	{
		m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(@Nullable String o)
	{
		m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(@Nullable CharSequence o)
	{
		m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(@Nullable StringBuffer o)
	{
		m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(@NotNull char[] o)
	{
		m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(char c)
	{
		m_underlyingStringBuilder.append(c);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(boolean b)
	{
		m_underlyingStringBuilder.append(b);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(int i)
	{
		m_underlyingStringBuilder.append(i);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(long l)
	{
		m_underlyingStringBuilder.append(l);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(float f)
	{
		m_underlyingStringBuilder.append(f);

		return this;
	}

	@NotNull
	public AMIStringBuilder append(double d)
	{
		m_underlyingStringBuilder.append(d);

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull Object... args)
	{
		if(q) for(Object o: args) m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull String... args)
	{
		if(q) for(String o: args) m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull CharSequence... args)
	{
		if(q) for(CharSequence o: args) m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull StringBuffer... args)
	{
		if(q) for(StringBuffer o: args) m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull char[]... args)
	{
		if(q) for(char[] o: args) m_underlyingStringBuilder.append(o);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull char... args)
	{
		if(q) for(char c: args) m_underlyingStringBuilder.append(c);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull boolean... args)
	{
		if(q) for(boolean b: args) m_underlyingStringBuilder.append(b);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull int... args)
	{
		if(q) for(int i: args) m_underlyingStringBuilder.append(i);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull long... args)
	{
		if(q) for(long l: args) m_underlyingStringBuilder.append(l);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull float... args)
	{
		if(q) for(float f: args) m_underlyingStringBuilder.append(f);

		return this;
	}

	@NotNull
	public AMIStringBuilder appendIf(boolean q, @NotNull double... args)
	{
		if(q) for(double d: args) m_underlyingStringBuilder.append(d);

		return this;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	////////
	@Override
	public int hashCode()
	{
		return m_underlyingStringBuilder.hashCode();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public String toString()
	{
		return m_underlyingStringBuilder.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	/////////
	public StringBuilder toStringBuilder()
	{
		return m_underlyingStringBuilder;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
