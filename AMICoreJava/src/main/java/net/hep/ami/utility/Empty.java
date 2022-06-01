package net.hep.ami.utility;

import org.jetbrains.annotations.*;

public class Empty
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static final int STRING_JAVA_NULL = (1 << 0);
	public static final int STRING_AMI_NULL = (1 << 1);
	public static final int STRING_EMPTY = (1 << 2);
	public static final int STRING_BLANK = (1 << 3);

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final int STRING_NULL_EMPTY_BLANK = STRING_JAVA_NULL | STRING_AMI_NULL | STRING_EMPTY | STRING_BLANK;

	/*----------------------------------------------------------------------------------------------------------------*/

	public static boolean is(@Nullable Object o, int mask)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(((mask & STRING_JAVA_NULL) != 0 || (mask & STRING_AMI_NULL) != 0) && o == null)
		{
			return true;
		}

		if(!(o instanceof String))
		{
			return false;
		}

		String s = o.toString();
		String S = s.  trim  ();

		if((mask & STRING_AMI_NULL) != 0 && "@NULL".equalsIgnoreCase(S))
		{
			return true;
		}

		if((mask & STRING_EMPTY) != 0 && s.isEmpty())
		{
			return true;
		}

		if((mask & STRING_BLANK) != 0 && S.isEmpty())
		{
			return true;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
