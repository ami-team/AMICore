package net.hep.ami.utility;

import java.util.*;
import java.util.regex.*;

public class CommandParser {
	/*---------------------------------------------------------------------*/

	public static class CommandParserTuple {

		public String command;

		public Map<String, String> arguments;

		public CommandParserTuple(String _command, Map<String, String> _arguments) {

			command = _command;

			arguments = _arguments;
		}
	}

	/*---------------------------------------------------------------------*/

	private static final Pattern m_stringPattern = Pattern.compile("^[_a-zA-Z0-9]+$");

	/*---------------------------------------------------------------------*/

	private static final Character[] m_spaces = {
		' ', '\t', '\n'
	};

	private static final String[] m_kwords = {
		"-", "/", "="
	};

	private static final String[] m_quotes = {
		"\'", "\""
	};

	/*---------------------------------------------------------------------*/

	private static final int DASH = 0;
	private static final int EQUAL = 1;
	private static final int STRING = 2;
	private static final int ERR = 9;

	private static final int CMD = 0;
	private static final int ARG = 1;
	private static final int VAL = 2;
	private static final int NIL = 9;

	/*---------------------------------------------------------------------*/

	private static final int TRANSITIONS[][] = {
		/*	DASH	EQUAL	STRING  */
		{	ERR,	ERR,	  1		},
		{	  2,	ERR,	  3		},
		{	  2,	ERR,	  3		},
		{	  2,	  4,	  3		},
		{	ERR,	ERR,	  1		},
	};

	private static final int OPERATIONS[][] = {
		/*	DASH	EQUAL	STRING  */
		{	NIL,	NIL,	CMD		},
		{	NIL,	NIL,	ARG		},
		{	NIL,	NIL,	ARG		},
		{	NIL,	NIL,	ARG		},
		{	NIL,	NIL,	VAL		},
	};

	/*---------------------------------------------------------------------*/

	private static final Integer FINALS[] = {1, 3};

	/*---------------------------------------------------------------------*/

	public static CommandParserTuple parse(String s) throws Exception {
		/*-----------------------------------------------------------------*/
		/* TOKENIZE COMMAND                                                */
		/*-----------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(s, m_spaces, m_kwords, m_quotes);

		if(tokens.size() == 0) {
			throw new Exception("empty command");
		}

		/*-----------------------------------------------------------------*/
		/* PARSE COMMAND                                                   */
		/*-----------------------------------------------------------------*/

		String command = "";

		Map<String, String> arguments = new HashMap<String, String>();

		/*-----------------------------------------------------------------*/

		int idx = 0;

		int cur_state = 0;
		int new_state = 0;
		int operation = 0;

		String param = "";

		for(String token: tokens) {

			/****/ if(token.equals("-")
			          ||
			          token.equals("/")
			 ) {
				idx = DASH;
			} else if(token.equals("=")) {
				idx = EQUAL;
			} else {
				idx = STRING;

				if(token.charAt(0) == '\''
				   ||
				   token.charAt(0) == '\"'
				 ) {
					token = token.substring(1, token.length() - 1);
				} else {

					if(m_stringPattern.matcher(token).find() == false) {
						throw new Exception("syntax error, unexpected token `" + token + "`");
					}
				}
			}

			new_state = TRANSITIONS[cur_state][idx];
			operation = OPERATIONS[cur_state][idx];

			if(new_state == ERR) {
				throw new Exception("syntax error, unexpected token `" + token + "`");
			}

			switch(operation) {
				case CMD:
					command = token;
					break;

				case ARG:
					param = token;
					arguments.put(param, "");
					break;

				case VAL:
					token = unescape(token);
					arguments.put(param, token);
					break;
			}

			cur_state = new_state;
		}

		/*-----------------------------------------------------------------*/
		/* CHECK FINAL STATE                                               */
		/*-----------------------------------------------------------------*/

		boolean isOk = false;

		for(int the_state: FINALS) {

			if(the_state == cur_state) {
				isOk = true;
				break;
			}
		}

		/*-----------------------------------------------------------------*/

		if(isOk == false) {
			throw new Exception("syntax error, truncated command");
		}

		/*-----------------------------------------------------------------*/
		/* RETURN RESULT                                                   */
		/*-----------------------------------------------------------------*/

		return new CommandParserTuple(command, arguments);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String unescape(String s) {

		StringBuilder result = new StringBuilder(s.length());

		/*-----------------------------------------------------------------*/

		char c;
		int i = 0;
		String code;

		final int length = s.length();

		while(i < length) {
			c = s.charAt(i++);

			if(c == '\\') {
				c = s.charAt(i++);

				switch(c) {
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

						if(length - i < 4) {
							c = 'u';
							break;
						}

						code = s.substring(i + 0, i + 4);
						i += 4;

						try { 
							result.append(Character.toChars(Integer.parseInt(code, 16)));
						} catch(Exception e) {
							result.append(/******************/ '?' /******************/);
						}

						continue;
				}
			}

			result.append(c);
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
