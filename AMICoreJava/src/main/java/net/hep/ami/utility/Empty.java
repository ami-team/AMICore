package net.hep.ami.utility;

import org.jetbrains.annotations.*;

public class Empty
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(value = "null -> true", pure = true)
	public static boolean isEmpty(@Nullable String s)
	{
		return s == null || s.isEmpty();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(value = "null -> true", pure = true)
	public static boolean isBlankEmpty(@Nullable String s)
	{
		if(s != null)
		{
			s = s.trim();

			return s.isEmpty() /*--------------------------*/;
		}
		else
		{
			return true;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(value = "null -> true", pure = true)
	public static boolean isBlankEmptyNull(@Nullable String s)
	{
		if(s != null)
		{
			s = s.trim();

			return s.isEmpty() || "@NULL".equalsIgnoreCase(s);
		}
		else
		{
			return true;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
