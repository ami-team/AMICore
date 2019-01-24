package net.hep.ami.jdbc.driver.sql;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.utility.*;

@DriverMetadata(
	type = DriverMetadata.Type.SQL,
	proto = "jdbc:postgresql",
	clazz = "org.postgresql.Driver"
)

public class PostgreSQLDriver extends AbstractDriver
{
	/*---------------------------------------------------------------------*/

	private static final int IDX_SELECT = 0;
	private static final int IDX_INSERT = 1;
	private static final int IDX_UPDATE = 2;
	private static final int IDX_DELETE = 3;
	private static final int IDX_SET = 4;
	private static final int IDX_PARENT = 5;
	private static final int IDX_FROM = 6;
	private static final int IDX_WHERE = 7;
	private static final int IDX_GROUP = 8;
	private static final int IDX_ORDER = 9;
	private static final int IDX_BY = 10;
	private static final int IDX_DOT = 11;
	private static final int IDX_ID = 12;
	private static final int IDX_ELSE = 13;

	/*---------------------------------------------------------------------*/

	private static final int OP_1 = 0;
	private static final int OP_2 = 1;

	/*---------------------------------------------------------------------*/

	public PostgreSQLDriver(@Nullable String externalCatalog, String internalCatalog, String jdbcUrl, String user, String pass, String AMIUser, boolean isAdmin) throws Exception
	{
		super(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, isAdmin);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void setDB(String db) throws Exception
	{
		/* DO NOTHING */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public String patchSQL(String sql) throws Exception
	{
		/*-----------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql);

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < tokens.size(); i++)
		{
			tokens.set(i, Tokenizer.backQuotesToDoubleQuotes(tokens.get(i)));
		}

		/*-----------------------------------------------------------------*/

		CatalogSingleton.Tuple tuple;

		try
		{
			tuple = CatalogSingleton.getTuple(m_externalCatalog);
		}
		catch(Exception e)
		{
			tuple = /*------------------------*/ null /*------------------------*/;
		}

		if(tuple != null)
		{
			tokens = patch(tokens, CatalogSingleton.getTuple(m_externalCatalog).z);
		}

		/*-----------------------------------------------------------------*/

		return String.join("", tokens);
	}

	/*---------------------------------------------------------------------*/

	static final int[][] s_hhh = new int[][] {
		/*         SELECT	INSERT	UPDATE	DELETE	SET		PARENT	FROM	WHERE	GROUP	ORDER	BY		DOT		ID		ELSE		*/
		new int[] {1,		7,		7,		7,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		},
		new int[] {1,		-1,		-1,		-1,		-1,		1,		7,		1,		11,		11,		-1,		1,		2,		1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		1,		7,		2,		-1,		-1,		-1,		3,		2,		1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		4,		1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		7,		-1,		11,		11,		-1,		5,		2,		1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		6,		1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		7,		-1,		-1,		-1,		-1,		-1,		2,		1,		},
		new int[] {-1,		-1,		-1,		-1,		1,		1,		7,		1,		11,		11,		-1,		-1,		8,		7,		},
		new int[] {-1,		-1,		-1,		-1,		1,		1,		-1,		1,		11,		11,		-1,		9,		8,		7,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		11,		11,		-1,		-1,		10,		7,		},
		new int[] {-1,		-1,		-1,		-1,		1,		1,		-1,		1,		11,		11,		-1,		-1,		8,		7,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		1,		-1,		-1, 	-1,		},
	};

	static final int[][] s_iii = new int[][] {
		/*         SELECT	INSERT	UPDATE	DELETE	SET		PARENT	FROM	WHERE	GROUP	ORDER	BY		DOT		ID		ELSE		*/
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		OP_2,	-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		OP_1,	-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
	};

	/*---------------------------------------------------------------------*/

	public static List<String> patch(List<String> tokens, String schema) throws Exception
	{
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		int idx;

		int old_state = 0;
		int new_state = 0;
		int operation = 0;

		for(String token: tokens)
		{
			/*-------------------------------------------------------------*/

			/**/ if("FROM_UNIXTIME".equalsIgnoreCase(token))
			{
				token = "TO_TIMESTAMP";
			}

			/*-------------------------------------------------------------*/

			result.add(token);

			if(token.matches("\\s+"))
			{
				continue;
			}

			/*-------------------------------------------------------------*/

			/**/ if("SELECT".equalsIgnoreCase(token)) {
				idx = IDX_SELECT;
			}
			else if("INSERT".equalsIgnoreCase(token)) {
				idx = IDX_INSERT;
			}
			else if("UPDATE".equalsIgnoreCase(token)) {
				idx = IDX_UPDATE;
			}
			else if("DELETE".equalsIgnoreCase(token)) {
				idx = IDX_DELETE;
			}
			else if("SET".equalsIgnoreCase(token)) {
				idx = IDX_SET;
			}
			else if("(".equalsIgnoreCase(token)) {
				idx = IDX_PARENT;
			}
			else if("FROM".equalsIgnoreCase(token)) {
				idx = IDX_FROM;
			}
			else if("WHERE".equalsIgnoreCase(token)) {
				idx = IDX_WHERE;
			}
			else if("GROUP".equalsIgnoreCase(token)) {
				idx = IDX_GROUP;
			}
			else if("ORDER".equalsIgnoreCase(token)) {
				idx = IDX_ORDER;
			}
			else if("BY".equalsIgnoreCase(token)) {
				idx = IDX_BY;
			}
			else if(".".equalsIgnoreCase(token)) {
				idx = IDX_DOT;
			}
			else
			{
				if(token.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")
				   ||
				   token.startsWith("\"")
				   ||
				   token.startsWith("`")
				 ) {
					idx = IDX_ID;
				}
				else {
					idx = IDX_ELSE;
				}
			}

			/*-------------------------------------------------------------*/

			new_state = s_hhh[old_state][idx];
			operation = s_iii[old_state][idx];

			/*-------------------------------------------------------------*/

			if(new_state == -1)
			{
				throw new Exception("syntax error near token `" + token + "`");
			}

			/*-------------------------------------------------------------*/

			switch(operation)
			{
				case OP_1:
					result.set(result.size() - 1, ".\"" + schema + "\"" + result.get(result.size() - 1));
					break;

				case OP_2:
					result.set(result.size() - 2, "\"" + schema + "\"." + result.get(result.size() - 2));
					break;
			}

			/*-------------------------------------------------------------*/

			old_state = new_state;

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		return result;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
