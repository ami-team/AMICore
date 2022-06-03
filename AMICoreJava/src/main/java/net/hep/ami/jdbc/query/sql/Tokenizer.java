package net.hep.ami.jdbc.query.sql;

import lombok.*;

import java.util.*;

import org.antlr.v4.runtime.*;

import net.hep.ami.jdbc.query.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

public class Tokenizer
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private Tokenizer() {}

	/*----------------------------------------------------------------------------------------------------------------*/
	/* TOKENIZER                                                                                                      */
	/*----------------------------------------------------------------------------------------------------------------*/

	public static List<String> tokenize(@NotNull String s) throws Exception
	{
		return tokenize(CharStreams.fromString(s.trim()));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static List<String> tokenize(@NotNull CharStream charStream) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		SQLLexer lexer = new SQLLexer(charStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		SQLParser parser = new SQLParser(tokenStream);

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		List<String> result = parser.query().tokens;

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static final String SELECT = "SELECT";
	public static final String FROM = "FROM";
	public static final String WHERE = "WHERE";
	public static final String GROUP = "GROUP";
	public static final String HAVING = "HAVING";
	public static final String ORDER = "ORDER";
	public static final String WAY = "WAY";
	public static final String LIMIT = "LIMIT";
	public static final String OFFSET = "OFFSET";

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@ToString
	public static class XQLParts
	{
		private List<String> select;
		private List<String> from;
		private List<String> where;
		private List<String> group;
		private List<String> having;
		private List<String> order;
		private List<String> way;
		private List<String> limit;
		private List<String> offset;

		public void put(String statement, List<String> list)
		{
			switch(statement)
			{
				case SELECT:
					select = list;
					break;
				case FROM:
					from = list;
					break;
				case WHERE:
					where = list;
					break;
				case GROUP:
					group = list;
					break;
				case HAVING:
					having = list;
					break;
				case ORDER:
					order = list;
					break;
				case WAY:
					way = list;
					break;
				case LIMIT:
					limit = list;
					break;
				case OFFSET:
					offset = list;
					break;
			}
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Set<String> s_xqlRegions = new HashSet<>();

	static
	{
		s_xqlRegions.add(SELECT);
		s_xqlRegions.add(FROM);
		s_xqlRegions.add(WHERE);
		s_xqlRegions.add(GROUP);
		s_xqlRegions.add(HAVING);
		s_xqlRegions.add(ORDER);
		s_xqlRegions.add(LIMIT);
		s_xqlRegions.add(OFFSET);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	/*   `UNION`   NOT SUPPORTED! */
	/* `UNION ALL` NOT SUPPORTED! */
	/* `INTERSECT` NOT SUPPORTED! */

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static XQLParts splitXQL(@NotNull String xql) throws Exception
	{
		XQLParts result = new XQLParts();

		/*------------------------------------------------------------------------------------------------------------*/

		int lock = 0;

		String TOKEN = null;
		String keyword = null;

		List<String> tokens = null;

		try
		{
			for(String token: Tokenizer.tokenize(xql))
			{
				/*----------------------------------------------------------------------------------------------------*/

				TOKEN = token.toUpperCase();

				/*----------------------------------------------------------------------------------------------------*/

				/**/ if("(".equals(token))
				{
					Objects.requireNonNull(tokens).add("(");
					lock++;
				}
				else if(")".equals(token))
				{
					Objects.requireNonNull(tokens).add(")");
					lock--;
				}

				/*----------------------------------------------------------------------------------------------------*/

				else if(lock == 0)
				{
					/*------------------------------------------------------------------------------------------------*/

					if(s_xqlRegions.contains(TOKEN))
					{
						result.put(keyword = TOKEN, tokens = new ArrayList<>());
					}
					else
					{
						/**/ if(ORDER.equals(keyword) && ("ASC".equals(TOKEN) || "DESC".equals(TOKEN)))
						{
							result.put(WAY, Collections.singletonList(TOKEN));
						}
						else if(!(GROUP.equals(keyword) || ORDER.equals(keyword)) || !"BY".equals(TOKEN))
						{
							Objects.requireNonNull(tokens).add(token);
						}
					}

					/*------------------------------------------------------------------------------------------------*/
				}

				/*----------------------------------------------------------------------------------------------------*/

				else
				{
					Objects.requireNonNull(tokens).add(token);
				}

				/*----------------------------------------------------------------------------------------------------*/
			}
		}
		catch(NullPointerException e)
		{
			throw new Exception("invalid SQL syntax");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String backQuotesToDoubleQuotes(@NotNull String token)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class AliasInfo
	{
		@NotNull private final Map<QId, QId> aliasFieldMap;
		@NotNull private final List<Boolean> fieldHasAliasList;
		@NotNull private final Map<QId, QId> rawFieldAliasMap;
		@NotNull private final List<Boolean> tableHasAliasList;
		@NotNull private final Map<QId, QId> rawTableAliasMap;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public static Tokenizer.AliasInfo extractAliasInfo(@NotNull String sql) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* EXTRACT FIELDS AND TABLES                                                                                  */
		/*------------------------------------------------------------------------------------------------------------*/

		int cnt = 0;

		boolean inSelect = false;
		boolean  inFrom  = false;

		List<String> tmp1 = null;
		List<String> tmp2 = null;

		List<List<String>> fields = new ArrayList<>();
		List<List<String>> tables = new ArrayList<>();

		for(String token: Tokenizer.tokenize(sql.trim()))
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
			else if(!"DISTINCT".equalsIgnoreCase(token)
			        &&
			        !((("AS"))).equalsIgnoreCase(token)
			 ) {
				/**/ if(inSelect)
				{
					if(!",".equals(token))
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
					if(!",".equals(token))
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

		/*------------------------------------------------------------------------------------------------------------*/
		/* BUILD FIELD-ALIAS AND TABLE-ALIAS MAPS                                                                     */
		/*------------------------------------------------------------------------------------------------------------*/

		int l;
		int idx;

		String tmp;

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		List<Boolean> tableHasAliasList = new ArrayList<>();

		Map<QId, QId> rawTableAliasMap = new HashMap<>();

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

					rawTableAliasMap.put(
						QId.parseQId(tmp.substring(idx + 1, l), QId.Type.ENTITY, QId.Type.NONE),
						QId.parseQId(tmp.substring(0, idx + 0), QId.Type.ENTITY, QId.Type.NONE)
					);
				}
				else
				{
					tableHasAliasList.add(false);

					rawTableAliasMap.put(
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

		/*------------------------------------------------------------------------------------------------------------*/
		/*                                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		Map<QId, QId> aliasFieldMap = new HashMap<>();

		for(Map.Entry<QId, QId> entry: rawFieldAliasMap.entrySet())
		{
			if(entry.getValue().is(QId.MASK_ENTITY_FIELD))
			{
				QId table = rawTableAliasMap.get(new QId(
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

		/*------------------------------------------------------------------------------------------------------------*/

		return new AliasInfo(
			aliasFieldMap,
			fieldHasAliasList,
			rawFieldAliasMap,
			tableHasAliasList,
			rawTableAliasMap
		);
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
