package net.hep.ami.utility.parser;

public class Utility
{
	/*---------------------------------------------------------------------*/

	public static String unescape(String s)
	{
		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x00000000;
		final int l = s.length();

		String code;
		char c;

		while(i < l)
		{
			c = s.charAt(i++);

			if(c == '\\')
			{
				c = s.charAt(i++);

				switch(c)
				{
					case '\\':
						c = '\\';
						break;

					case 'b':
						c = '\b';
						break;

					case 'f':
						c = '\f';
						break;

					case 'n':
						c = '\n';
						break;

					case 'r':
						c = '\r';
						break;

					case 't':
						c = '\t';
						break;

					case '\"':
						c = '\"';
						break;

					case '\'':
						c = '\'';
						break;

					case 'u':
						/*-------------------------------------------------*/
						/* UNICODE                                         */
						/*-------------------------------------------------*/

						if(l - i < 4)
						{
							c = 'u';
							break;
						}

						code = s.substring(i + 0, i + 4); i += 4;

						try
						{ 
							result.append(Character.toChars(Integer.parseInt(code, 16)));
						}
						catch(java.lang.NumberFormatException e)
						{
							result.append(/******************/ '?' /******************/);
						}

						continue;

						/*-------------------------------------------------*/

						default:
							break;

						/*-------------------------------------------------*/
				}
			}

			result.append(c);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String parseString(@Nullable String s)
	{
		if(s == null)
		{
			return "";
		}

		final int l = s.length();

		return (l >= 2) ? unescape(s.substring(0 + 1, l - 1)) : "";
	}

	/*---------------------------------------------------------------------*/
}
