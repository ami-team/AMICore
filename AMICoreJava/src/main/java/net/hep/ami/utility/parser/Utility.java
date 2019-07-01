package net.hep.ami.utility.parser;

import net.hep.ami.utility.*;

public class Utility
{
	/*---------------------------------------------------------------------*/
	/* JSON STRING                                                         */
	/*---------------------------------------------------------------------*/

	public static String escapeJSONString(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

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

				default:
					result.append(c);
					break;
			}
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String unescapeJSONString(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

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

	public static String jsonStringToText(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		String tmp = s.trim();

		/*-----------------------------------------------------------------*/

		final int l = tmp.length();

		if(l >= 2)
		{
			if((tmp.charAt(0 + 0) == '\"' && tmp.charAt(l - 1) == '\"')
			   ||
			   (tmp.charAt(0 + 0) == '\'' && tmp.charAt(l - 1) == '\'')
			 ) {
				s = unescapeJSONString(tmp.substring(0 + 1, l - 1));
			}
		}

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public static String textToJSONString(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		return "'" + escapeJSONString(s) + "'"; /* DON'T CHECK IF ALREADY A STRING */
	}

	/*---------------------------------------------------------------------*/
	/* JAVA STRING                                                         */
	/*---------------------------------------------------------------------*/

	public static String escapeJavaString(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

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

	public static String unescapeJavaString(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

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

	public static String kjavaStringToText(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		String tmp = s.trim();

		/*-----------------------------------------------------------------*/

		final int l = tmp.length();

		if(l >= 2)
		{
			if((tmp.charAt(0 + 0) == '\"' && tmp.charAt(l - 1) == '\"')
			   ||
			   (tmp.charAt(0 + 0) == '\'' && tmp.charAt(l - 1) == '\'')
			 ) {
				s = unescapeJavaString(tmp.substring(0 + 1, l - 1));
			}
		}

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public static String textToJavaString(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		return "'" + escapeJavaString(s) + "'"; /* DON'T CHECK IF ALREADY A STRING */
	}

	/*---------------------------------------------------------------------*/
	/* XQL STRING                                                          */
	/*---------------------------------------------------------------------*/

	public static String sqlIdToText(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		String tmp = s.trim();

		/*-----------------------------------------------------------------*/

		final int l = tmp.length();

		if(l >= 2)
		{
			/**/ if(tmp.charAt(0 + 0) == '`'
			        &&
			        tmp.charAt(l - 1) == '`'
			 ) {
				s = tmp.substring(0 + 1, l - 1).trim().replace("``", "`");
			}
			else if(tmp.charAt(0 + 0) == '"'
			        &&
			        tmp.charAt(l - 1) == '"'
			 ) {
				s = tmp.substring(0 + 1, l - 1).trim().replace("\"\"", "\"");
			}
		}

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public static String textToSqlId(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		String tmp = s.trim();

		/*-----------------------------------------------------------------*/

		final int l = tmp.length();

		if(l >= 1)
		{
			if((tmp.charAt(0 + 0) != '`' && tmp.charAt(l - 1) != '`')
			   &&
			   (tmp.charAt(0 + 0) != '"' && tmp.charAt(l - 1) != '"')
			 ) {
				s = "`" + tmp.replace("`", "``") + "`";
			}
		}

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public static String sqlValToText(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		String tmp = s.trim();

		/*-----------------------------------------------------------------*/

		/**/ if("NULL".equalsIgnoreCase(tmp))
		{
			return "@NULL";
		}
		else if("CURRENT_TIMESTAMP".equalsIgnoreCase(tmp))
		{
			return "@CURRENT_TIMESTAMP";
		}

		/*-----------------------------------------------------------------*/

		final int l = tmp.length();

		if(l >= 2)
		{
			if(tmp.charAt(0 + 0) == '\''
			   &&
			   tmp.charAt(l - 1) == '\''
			 ) {
				s = tmp.substring(0 + 1, l - 1).trim().replace("''", "'");
			}
		}

		/*-----------------------------------------------------------------*/

		return s;
	}

	/*---------------------------------------------------------------------*/

	public static String textToSqlVal(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		String tmp = s.trim();

		/*-----------------------------------------------------------------*/

		/**/ if("@NULL".equalsIgnoreCase(tmp))
		{
			return "NULL";
		}
		else if("@CURRENT_TIMESTAMP".equalsIgnoreCase(tmp))
		{
			return "CURRENT_TIMESTAMP";
		}

		/*-----------------------------------------------------------------*/

		return "'" + s.replace("'", "''") + "'"; /* DON'T CHECK IF ALREADY A STRING */

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
	/* HTML STRING                                                         */
	/*---------------------------------------------------------------------*/

	public static String escapeHTML(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		final int l = s.length();

		for(int i = 0; i < l; i++)
		{
			char c = s.charAt(i);

			/**/ if(c == '<') {
				result.append("&lt;");
			}
			else if(c == '>') {
				result.append("&gt;");
			}
			else if(c == '&') {
				result.append("&amp;");
			}
			else if(c == '"') {
				result.append("&quot;");
			}
			else if(c > 127) {
				result.append("&#")
				      .append((int) c)
				      .append(";")
				;
			}
			else {
				result.append(c);
			}
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}

