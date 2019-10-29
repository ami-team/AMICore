package net.hep.ami.utility.parser;

import java.util.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

public class Utility
{
	/*----------------------------------------------------------------------------------------------------------------*/
	/* JAVA & JSON STRINGS                                                                                            */
	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null -> null; !null -> !null")
	public static String escapeJavaString(@Nullable String s)
	{
		return escapeJSONString(s, true);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null, _ -> null; !null, _ -> !null")
	public static String escapeJSONString(@Nullable String s, boolean simpleQuotes)
	{
		if(s == null)
		{
			return null;
		}

		StringBuilder result = new StringBuilder(s.length());

		/*------------------------------------------------------------------------------------------------------------*/

		/*-*/ int i = 0x00000000;
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
					result.append(simpleQuotes ? "\\\'" : "\'");
					break;

				default:
					result.append(c);
					break;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null -> null; !null -> !null")
	public static String unescapeJavaString(@Nullable String s) throws RuntimeException
	{
		return unescapeJSONString(s, true);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null, _ -> null; !null, _ -> !null")
	public static String unescapeJSONString(@Nullable String s, boolean simpleQuotes) throws RuntimeException
	{
		if(s == null)
		{
			return null;
		}

		StringBuilder result = new StringBuilder(s.length());

		/*------------------------------------------------------------------------------------------------------------*/

		/*-*/ int i = 0x00000000;
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
						if(!simpleQuotes)
						{
							throw new RuntimeException("invalid escape sequence \"\\\'\"");
						}
						c = '\'';
						break;

					case 'u':
						/*--------------------------------------------------------------------------------------------*/
						/* UNICODE                                                                                    */
						/*--------------------------------------------------------------------------------------------*/

						int j = Math.min(4, l - i);

						code = s.substring(i, i = i + j);

						/*--------------------------------------------------------------------------------------------*/

						if(j == 4)
						{
							try
							{
								result.append(Character.toChars(Integer.parseInt(code, 16)));
							}
							catch(NumberFormatException e)
							{
								throw new RuntimeException("invalid escape sequence \"\\u" + code + "\"");
							}
						}
						else
						{
							throw new RuntimeException("invalid escape sequence \"\\u" + code + "\"");
						}

						/*--------------------------------------------------------------------------------------------*/

						continue;

					default:
						throw new RuntimeException("invalid escape sequence \"\\" + c + "\"");
				}
			}

			result.append(c);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null -> null; !null -> !null")
	public static String javaStringToText(@Nullable String s) throws RuntimeException
	{
		return jsonStringToText(s, true);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null, _ -> null; !null, _ -> !null")
	public static String jsonStringToText(@Nullable String s, boolean simpleQuotes) throws RuntimeException
	{
		if(s == null)
		{
			return null;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		final String tmp = s.trim();

		final int l = tmp.length();

		if(l >= 2)
		{
			if((/*-----------*/ tmp.charAt(0 + 0) == '\"' && tmp.charAt(l - 1) == '\"')
			   ||
			   (simpleQuotes && tmp.charAt(0 + 0) == '\'' && tmp.charAt(l - 1) == '\'')
			 ){
				s = unescapeJSONString(tmp.substring(0 + 1, l - 1), simpleQuotes);
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return s;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract("null -> null; !null -> !null")
	public static String textToJavaString(@Nullable String s)
	{
		return textToJSONString(s, true);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract("null, _ -> null; !null, _ -> !null")
	public static String textToJSONString(@Nullable String s, boolean simpleQuotes)
	{
		if(s == null)
		{
			return null;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("\"")
		                          .append(escapeJSONString(s, simpleQuotes))
		                          .append("\"")
		                          .toString()
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder object2json(@Nullable Object object)
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/
		/* NULL                                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		/**/ if(object == null)
		{
			result.append("null");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* LIST                                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		else if(List.class.isAssignableFrom(object.getClass()))
		{
			Stream<StringBuilder> stream = ((List<?>) object).stream().map(

				Utility::object2json
			);

			result.append("[")
			      .append(stream.collect(Collectors.joining(",")))
			      .append("]")
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* OBJECT                                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		else if(Map.class.isAssignableFrom(object.getClass()))
		{
			Stream<StringBuilder> stream = ((Map<?, ?>) object).entrySet().stream().map(

				x -> new StringBuilder().append(object2json(x. getKey() ))
				                        .append(":")
				                        .append(object2json(x.getValue()))
			);

			result.append("{")
			      .append(stream.collect(Collectors.joining(",")))
			      .append("}")
			;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* STRING                                                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		else if(object instanceof String)
		{
			result.append(textToJSONString(object.toString(), false));
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ELSE                                                                                                       */
		/*------------------------------------------------------------------------------------------------------------*/

		else
		{
			result.append(object.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* XQL STRINGS                                                                                                    */
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String sqlIdToText(@NotNull String s)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String tmp = Objects.requireNonNull(s).trim();

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

		/*------------------------------------------------------------------------------------------------------------*/

		return s;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String textToSqlId(@NotNull String s)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		String tmp = Objects.requireNonNull(s).trim();

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

		/*------------------------------------------------------------------------------------------------------------*/

		return s;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String sqlValToText(@Nullable String s, boolean javaEscapes) throws RuntimeException
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(s == null)
		{
			return "@NULL";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String tmp = s.trim();

		/**/ if("NULL".equalsIgnoreCase(tmp))
		{
			return "@NULL";
		}
		else if("CURRENT_TIMESTAMP".equalsIgnoreCase(tmp))
		{
			return "@CURRENT_TIMESTAMP";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		final int l = tmp.length();

		if(l >= 2
		   &&
		   tmp.charAt(0 + 0) == '\''
		   &&
		   tmp.charAt(l - 1) == '\''
		 ) {
			s = tmp.substring(0 + 1, l - 1).trim();

			s = javaEscapes ? unescapeJavaString(s): s.replace("''", "'");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return s;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String textToSqlVal(@Nullable String s, boolean javaEscapes)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		if(s == null)
		{
			return "NULL";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String tmp = s.trim();

		/**/ if("@NULL".equalsIgnoreCase(tmp))
		{
			return "NULL";
		}
		else if("@CURRENT_TIMESTAMP".equalsIgnoreCase(tmp))
		{
			return "CURRENT_TIMESTAMP";
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("'")
		                          .append(javaEscapes ? escapeJavaString(s): s.replace("'", "''"))
		                          .append("'")
		                          .toString()
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* HTML STRINGS                                                                                                   */
	/*----------------------------------------------------------------------------------------------------------------*/

	@Nullable
	@Contract("null -> null; !null -> !null")
	public static String escapeHTML(@Nullable String s)
	{
		if(s == null)
		{
			return null;
		}

		StringBuilder result = new StringBuilder(s.length());

		/*------------------------------------------------------------------------------------------------------------*/

		s.codePoints().forEach(c -> {

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
				result.append("&#").append(c).append(";");
			}
			else {
				result.append((char) c);
			}
		});

		/*------------------------------------------------------------------------------------------------------------*/

		return result.toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
