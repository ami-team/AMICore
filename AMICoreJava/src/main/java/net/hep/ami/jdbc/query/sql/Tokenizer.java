package net.hep.ami.jdbc.query.sql;

import java.math.*;
import java.util.*;

import org.antlr.v4.runtime.*;

import net.hep.ami.jdbc.query.*;
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

	public static String formatStatement(String sql, Object[] args) throws Exception
	{
		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		final int l = args.length;

		if(l == 0)
		{
			return sql;
		}

		/*-----------------------------------------------------------------*/

		int i = 0;

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

	public static Tuple3<String, List<String>, List<Boolean>> formatPreparedStatement(String sql, Object[] args) throws Exception
	{
		List<String> list1 = new ArrayList<>();
		List<Boolean> list2 = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();

		/*-----------------------------------------------------------------*/

		final int l = args.length;

		if(l == 0)
		{
			return new Tuple3<String, List<String>, List<Boolean>>(sql, list1, list2);
		}

		/*-----------------------------------------------------------------*/

		int i;

		for(String token: Tokenizer.tokenize(sql))
		{
			/**/ if(token.startsWith("?"))
			{
				/*---------------------------------------------------------*/

				i = Integer.parseInt(token.substring(1));

				/*---------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*---------------------------------------------------------*/

				list1.add(args[i].toString());
				list2.add(false);

				stringBuilder.append("?");

				/*---------------------------------------------------------*/
			}
			else if(token.startsWith("AMI_ENCRYPT("))
			{
				/*---------------------------------------------------------*/

				i = Integer.parseInt(token.substring(12, token.length() - 1));

				/*---------------------------------------------------------*/

				if(i >= l)
				{
					throw new Exception("not enough arguments");
				}

				/*---------------------------------------------------------*/

				list1.add(args[i].toString());
				list2.add(true);

				stringBuilder.append("?");

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

		return new Tuple3<String, List<String>, List<Boolean>>(stringBuilder.toString(), list1, list2);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static final String SELECT = "SELECT";
	public static final String FROM = "FROM";
	public static final String WHERE = "WHERE";
	public static final String GROUP = "GROUP";
	public static final String ORDER = "ORDER";
	public static final String WAY = "WAY";
	public static final String LIMIT = "LIMIT";
	public static final String OFFSET = "OFFSET";

	private static final Set<String> s_xqlRegions = new HashSet<>();

	static
	{
		s_xqlRegions.add(SELECT);
		s_xqlRegions.add(FROM);
		s_xqlRegions.add(WHERE);
		s_xqlRegions.add(GROUP);
		s_xqlRegions.add(ORDER);
		s_xqlRegions.add(LIMIT);
		s_xqlRegions.add(OFFSET);
	}

	/*   `UNION`   NOT SUPPORTED! */
	/* `UNION ALL` NOT SUPPORTED! */
	/* `INTERSECT` NOT SUPPORTED! */

	/*---------------------------------------------------------------------*/

	public static Map<String, String> splitXQL(String xql)
	{
		Map<String, String> result = new HashMap<>();

		/*-----------------------------------------------------------------*/

		int lock = 0;

		String TOKEN = "";
		String keyword = "";

		List<String> tokens = new ArrayList<>();

		for(String token: Tokenizer.tokenize(xql.trim()))
		{
			/*-------------------------------------------------------------*/

			TOKEN = token.toUpperCase();

			/*-------------------------------------------------------------*/

			/**/ if("(".equals(token))
			{
				tokens.add("(");
				lock++;
			}
			else if(")".equals(token))
			{
				tokens.add(")");
				lock--;
			}

			/*-------------------------------------------------------------*/

			else if(lock == 0 && s_xqlRegions.contains(TOKEN))
			{
				if(keyword.isEmpty() == false)
				{
					result.put(keyword, String.join("", tokens).trim());
				}

				tokens.clear();
				keyword = TOKEN;
			}

			/*-------------------------------------------------------------*/

			else if(lock == 0)
			{
				/**/ if(s_xqlRegions.contains(TOKEN))
				{
					if(keyword.isEmpty() == false)
					{
						result.put(keyword, String.join("", tokens).trim());
					}

					tokens.clear();
					keyword = TOKEN;
				}
				else if(ORDER.equals(keyword) == true && ("ASC".equals(TOKEN) || "DESC".equals(TOKEN)) == true)
				{
					result.put(WAY, TOKEN);
				}
				else if(ORDER.equals(keyword) == false || (/*-------*/ "BY".equals(TOKEN) /*-------*/) == false)
				{
					tokens.add(token);
				}
			}
			else
			{
				tokens.add(token);
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if(keyword.isEmpty() == false)
		{
			result.put(keyword, String.join("", tokens).trim());
		}

		tokens.clear();
		keyword = null;

		/*-----------------------------------------------------------------*/

		return result;
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

	public static Tuple5<Map<QId, QId>, List<Boolean>, Set<QId>, List<Boolean>, Set<QId>> extractAliasInfo(String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* EXTRACT FIELDS AND TABLES                                       */
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
		/* BUILD FIELD-ALIAS AND TABLE-ALIAS MAPS                          */
		/*-----------------------------------------------------------------*/

		int l;
		int idx;

		String tmp;

		/*-----------------------------------------------------------------*/

		List<Boolean> fieldHasAliasList = new ArrayList<>();

		Map<QId, QId> rawFieldAliasMap = new HashMap<>();


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
					fieldHasAliasList.add(true);

					rawFieldAliasMap.put(
						QId.parseQId(tmp.substring(idx + 1, l), QId.Type.FIELD, QId.Type.NONE),
						QId.parseQId(tmp.substring(0, idx + 0), QId.Type.FIELD, QId.Type.NONE)
					);
				}
				else
				{
					fieldHasAliasList.add(false);

					rawFieldAliasMap.put(
						QId.parseQId(tmp, QId.Type.FIELD, QId.Type.NONE),
						QId.parseQId(tmp, QId.Type.FIELD, QId.Type.NONE)
					);
				}
			}
			catch(Exception e)
			{
				/* IGNORE */
			}
		}

		/*-----------------------------------------------------------------*/

		List<Boolean> tableHasAliasList = new ArrayList<>();

		Map<QId, QId> rawFableAliasMap = new HashMap<>();

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
					tableHasAliasList.add(true);

					rawFableAliasMap.put(
						QId.parseQId(tmp.substring(idx + 1, l), QId.Type.ENTITY, QId.Type.NONE),
						QId.parseQId(tmp.substring(0, idx + 0), QId.Type.ENTITY, QId.Type.NONE)
					);
				}
				else
				{
					tableHasAliasList.add(false);

					rawFableAliasMap.put(
						QId.parseQId(tmp, QId.Type.ENTITY, QId.Type.NONE),
						QId.parseQId(tmp, QId.Type.ENTITY, QId.Type.NONE)
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

		Map<QId, QId> aliasFieldMap = new HashMap<>();

		for(Map.Entry<QId, QId> entry: rawFieldAliasMap.entrySet())
		{
			if(entry.getValue().is(QId.MASK_ENTITY_FIELD))
			{
				QId table = rawFableAliasMap.get(new QId(
					null,
					entry.getValue().getEntity(),
					null
				));

				if(table != null)
				{
					aliasFieldMap.put(
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
					aliasFieldMap.put(
						entry.getKey(),
						entry.getValue()
					);
				}
			}
			else
			{
				aliasFieldMap.put(
					entry.getKey(),
					entry.getValue()
				);
			}
		}

		/*-----------------------------------------------------------------*/

		return new Tuple5<>(
			aliasFieldMap,
			fieldHasAliasList,
			rawFieldAliasMap.keySet(),
			tableHasAliasList,
			rawFableAliasMap.keySet()
		);
	}

	/*---------------------------------------------------------------------*/
}
