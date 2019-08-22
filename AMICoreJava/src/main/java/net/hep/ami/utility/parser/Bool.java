package net.hep.ami.utility.parser;

import net.hep.ami.utility.*;

public class Bool
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract("null -> fail")
	public static Boolean valueOf(@Nullable String s) throws NumberFormatException
	{
		if(s != null)
		{
			s = s.trim().toLowerCase();

			/**/ if("1".equals(s)
			        ||
			        "on".equals(s)
			        ||
			        "yes".equals(s)
			        ||
			        "true".equals(s)
			 ) {
				return true;
			}
			else if("0".equals(s)
			        ||
			        "off".equals(s)
			        ||
			        "no".equals(s)
			        ||
			        "false".equals(s)
			 ) {
				return false;
			}
		}

		throw new NumberFormatException("For input string: \"" + s + "\"");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.jetbrains.annotations.Contract("null -> fail")
	public static boolean parseBool(@Nullable String s) throws NumberFormatException
	{
		if(s != null)
		{
			s = s.trim().toLowerCase();

			/**/ if("1".equals(s)
			        ||
			        "on".equals(s)
			        ||
			        "yes".equals(s)
			        ||
			        "true".equals(s)
			 ) {
				return true;
			}
			else if("0".equals(s)
			        ||
			        "off".equals(s)
			        ||
			        "no".equals(s)
			        ||
			        "false".equals(s)
			 ) {
				return false;
			}
		}

		throw new NumberFormatException("For input string: \"" + s + "\"");
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
