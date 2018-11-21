package net.hep.ami.jdbc.query.sql;

import java.math.*;
import java.util.*;

import org.antlr.v4.runtime.*;

import net.hep.ami.jdbc.obj.*;
import net.hep.ami.jdbc.reflexion.QId;
import net.hep.ami.utility.*;

public class Tokenizer
{
	/*---------------------------------------------------------------------*/

	private Tokenizer() {}

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(String s)
	{
		return tokenize(CharStreams.fromString(s));
	}

	/*---------------------------------------------------------------------*/

	public static List<String> tokenize(CharStream charStream)
	{
		/*-----------------------------------------------------------------*/

		SQLLexer lexer = new SQLLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		SQLParser parser = new SQLParser(tokenStream);

		/*-----------------------------------------------------------------*/

		return parser.query().tokens;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static String format(String sql, Object... args) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		/***/ int i = 0x000000000;
		final int l = args.length;

		if(l == 0)
		{
			return sql;
		}

		/*-----------------------------------------------------------------*/

		Object arg;

		for(String token: Tokenizer.tokenize(sql))
		{
			if("?".equals(token))
			{
				/*---------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*---------------------------------------------------------*/

				arg = args[i++];

				/**/ if(arg == null)
				{
					stringBuilder.append("NULL");
				}
				else if(arg instanceof Float
				        ||
				        arg instanceof Double
				        ||
				        arg instanceof Integer
				        ||
				        arg instanceof BigInteger
				 ) {
					stringBuilder.append(arg);
				}
				else
				{
					stringBuilder.append("'")
					             .append(arg.toString().replace("'", "''"))
					             .append("'")
					;
				}

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/

				stringBuilder.append(token);

				/*---------------------------------------------------------*/
			}
		}

		/*-----------------------------------------------------------------*/

		return stringBuilder.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String backQuotesToDoubleQuotes(String token)
	{
		if(token.startsWith("`")
		   &&
		   token.endsWith("`")
		 ) {
			token = token.replace("\"", "\"\"")
			             .replace("``", "\"\"")
			             .replace("`", "\"")
			;
		}

		return token;
	}

	/*---------------------------------------------------------------------*/

	public static Tuple3<Map<QId, QId>, Set<QId>, Set<QId>> buildLabelToFieldMap(String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		int cnt = 0;

		boolean inSelect = false;
		boolean  inFrom  = false;

		List<String> tmp1 = null;
		List<String> tmp2 = null;

		List<List<String>> fields = new ArrayList<>();
		List<List<String>> tables = new ArrayList<>();

		for(String token: Tokenizer.tokenize(sql))
		{
			/**/ if("(".equals(token)) {
				cnt++;
			}
			else if(")".equals(token)) {
				cnt--;
			}
			else if(cnt == 0 && "SELECT".equalsIgnoreCase(token)) {
				inSelect = true;
				inFrom = false;
			}
			else if(cnt == 0 && "FROM".equalsIgnoreCase(token)) {
				inSelect = false;
				inFrom = true;
			}
			else if(cnt == 0 && "WHERE".equalsIgnoreCase(token)) {
				inSelect = false;
				inFrom = false;
			}
			else if("DISTINCT".equalsIgnoreCase(token) == false
			        &&
			        ((("AS"))).equalsIgnoreCase(token) == false
			 ) {
				/**/ if(inSelect)
				{
					if(",".equals(token) == false)
					{
						if(tmp1 == null)
						{
							tmp1 = new ArrayList<>();
						}

						tmp1.add(token);
					}
					else
					{
						if(tmp1 != null)
						{
							fields.add(tmp1);
							tmp1 = null;
						}
					}
				}
				else if(inFrom)
				{
					if(",".equals(token) == false)
					{
						if(tmp2 == null)
						{
							tmp2 = new ArrayList<>();
						}

						tmp2.add(token);
					}
					else
					{
						if(tmp2 != null)
						{
							tables.add(tmp2);
							tmp2 = null;
						}
					}
				}
			}
		}

		if(tmp1 != null) {
			fields.add(tmp1);
		}

		if(tmp2 != null) {
			tables.add(tmp2);
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		int l;
		int idx;

		String tmp;

		/*-----------------------------------------------------------------*/

		Map<QId, QId> fieldAliasMap = new HashMap<>();

		for(List<String> field: fields)
		{
			tmp = String.join("", field).trim();

			idx = tmp.lastIndexOf(' ');
			if(idx < 0) {
				idx = tmp.lastIndexOf('\t');
			}

			l = tmp.length();

			try
			{
				/**/ if(idx > 0)
				{
					fieldAliasMap.put(
						new QId(tmp.substring(idx + 1, l), QId.Type.FIELD, QId.Type.NONE),
						new QId(tmp.substring(0, idx + 0), QId.Type.FIELD, QId.Type.NONE)
					);
				}
				else
				{
					fieldAliasMap.put(
						new QId(tmp, QId.Type.FIELD, QId.Type.NONE),
						new QId(tmp, QId.Type.FIELD, QId.Type.NONE)
					);
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*-----------------------------------------------------------------*/

		Map<QId, QId> tableAliasMap = new HashMap<>();

		for(List<String> table: tables)
		{
			tmp = String.join("", table).trim();

			idx = tmp.lastIndexOf(' ');
			if(idx < 0) {
				idx = tmp.lastIndexOf('\t');
			}

			l = tmp.length();

			try
			{
				/**/ if(idx > 0)
				{
					tableAliasMap.put(
						new QId(tmp.substring(idx + 1, l), QId.Type.ENTITY, QId.Type.NONE),
						new QId(tmp.substring(0, idx + 0), QId.Type.ENTITY, QId.Type.NONE)
					);
				}
				else
				{
					tableAliasMap.put(
						new QId(tmp, QId.Type.ENTITY, QId.Type.NONE),
						new QId(tmp, QId.Type.ENTITY, QId.Type.NONE)
					);
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		Map<QId, QId> result = new HashMap<>();

		for(Map.Entry<QId, QId> entry: fieldAliasMap.entrySet())
		{
			if(entry.getValue().is(QId.MASK_ENTITY_FIELD))
			{
				QId table = tableAliasMap.get(new QId(
					null,
					entry.getValue().getEntity(),
					null
				));

				if(table != null)
				{
					result.put(
						entry.getKey(),
						new QId(
							table.getCatalog(),
							table.getEntity(),
							entry.getValue().getField()
						)
					);
				}
				else
				{
					result.put(
						entry.getKey(),
						entry.getValue()
					);
				}
			}
			else
			{
				result.put(
					entry.getKey(),
					entry.getValue()
				);
			}
		}

		/*-----------------------------------------------------------------*/

		return new Tuple3<>(
			result,
			tableAliasMap.keySet(),
			fieldAliasMap.keySet()
		);
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		System.out.println(buildLabelToFieldMap("SELECT DISTINCT 1"));
		System.out.println(buildLabelToFieldMap("SELECT `c`.x AS yy, b AS \"toto\" FROM `AA`, ZZ.BB c WHERE titi"));

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
