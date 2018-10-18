package net.hep.ami.utility.parser;

import net.hep.ami.utility.*;

public class Utility
{
	/*---------------------------------------------------------------------*/
	/* TEXT                                                                */
	/*---------------------------------------------------------------------*/

	public static String escapeString(String s)
	{
		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x00000000;
		final int l = s.length();

		char c;

		while(i < l)
		{
			c = s.charAt(i++);

			switch(c)
			{
				case '\\':
					result.append("\\\\");
					break;

				case '\b':
					result.append("\\b");
					break;

				case '\f':
					result.append("\\f");
					break;

				case '\n':
					result.append("\\n");
					break;

				case '\r':
					result.append("\\r");
					break;

				case '\t':
					result.append("\\t");
					break;

				case '\"':
					result.append("\\\"");
					break;

				case '\'':
					result.append("\\\'");
					break;

				default:
					result.append(c);
					break;
			}
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String unescapeString(String s)
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

			if(c == '\\' && i < l)
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
						catch(NumberFormatException e)
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

		s = s.trim();

		final int l = s.length();

		return (l >= 2) ? unescapeString(s.substring(0 + 1, l - 1)) : "";
	}

	/*---------------------------------------------------------------------*/
	/* HTML                                                                */
	/*---------------------------------------------------------------------*/

	public static String escapeHTML(String s)
	{
		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		final int l = s.length();

		for(int i = 0; i < l; i++)
		{
			char c = s.charAt(i);

			if(c > 127 || c == '"' || c == '<' || c == '>' || c == '&')
			{
				result.append("&#").append((int) c).append(";");
			}
			else
			{
				result.append((char) c);
			}
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
