package net.hep.ami.utility;

import java.util.*;

public class Tokenizer {
	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String s, Character[] spaces, String[] kwords, String[] quotes) throws Exception {

		return tokenize(s, spaces, kwords, quotes, "\\");
	}

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String s, Character[] spaces, String[] kwords, String[] quotes, String escape) throws Exception {

		Set<Character> spaceSet = new HashSet<Character>();

		List<String> result = new ArrayList<String>();

		for(Character space: spaces) {
			spaceSet.add(space);
		}

		/***/ int i = 0x00000000;
		final int l = s.length();

		String word = "";
		boolean found;

		while(i < l) {
			/*-------------------------------------------------------------*/
			/* EAT SPACES                                                  */
			/*-------------------------------------------------------------*/

			if(spaceSet.contains(s.charAt(i))) {

				if(word.isEmpty() == false) {
					result.add(word);
					word = "";
				}

				i++;

				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT KWORDS                                                  */
			/*-------------------------------------------------------------*/

			found = false;

			for(String kword: kwords) {

				if(s.substring(i).startsWith(kword)) {

					if(word.isEmpty() == false) {
						result.add(word);
						word = "";
					}

					int j = i + kword.length();
					result.add(s.substring(i, j));
					i = j;

					found = true;
					break;
				}
			}

			if(found) {
				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT STRINGS                                                 */
			/*-------------------------------------------------------------*/

			found = false;

			for(String quote: quotes) {

				if(s.substring(i).startsWith(quote)) {

					if(word.isEmpty() == false) {
						result.add(word);
						word = "";
					}

					int j = i + shift(s.substring(i), quote, escape);
					result.add(s.substring(i, j));
					i = j;

					found = true;
					break;
				}
			}

			if(found) {
				continue;
			}

			/*-------------------------------------------------------------*/
			/* EAT REMAINING CHARACTERES                                   */
			/*-------------------------------------------------------------*/

			word += s.charAt(i++);

			/*-------------------------------------------------------------*/
		}

		if(word.isEmpty() == false) {
			result.add(word);
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	private static int shift(String s, String quote, String escape) throws Exception {

		final int l = s.length();
		final int m = quote.length();
		final int n = escape.length();

		int i = m;
		int cnt = 0;

		while(i < l) {

			/*  */ if(s.substring(i).startsWith(quote)) {
				i += m;
				if(cnt % 2 == 0) return i;
				cnt = 0;
			} else if(s.substring(i).startsWith(escape)) {
				i += n;
/*				if(0x00001 == 0) return i;
 */				cnt += 1;
			} else {
				i += 1;
/*				if(0x00001 == 0) return i;
 */				cnt = 0;
			}
		}

		throw new Exception("syntax error, missing token `" + quote + "`");
	}

	/*---------------------------------------------------------------------*/
}
