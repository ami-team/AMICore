package net.hep.ami;

import java.util.*;

import net.hep.ami.jdbc.query.sql.*;

public class TestGLite
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
	private static final int IDX_DOT = 8;
	private static final int IDX_ID = 9;
	private static final int IDX_ELSE = 10;

	/*---------------------------------------------------------------------*/

	private static final int OP_1 = 0;
	private static final int OP_2 = 1;

	/*---------------------------------------------------------------------*/

	public static String patch(String sql, String schema) throws Exception
	{
		List<String> result = new ArrayList<>();

		/*-----------------------------------------------------------------*/

		List<String> tokens = Tokenizer.tokenize(sql);

		/*-----------------------------------------------------------------*/

		int[][] hhh = new int[][] {
		/*             SELECT	INSERT	UPDATE	DELETE	SET		PARENT	FROM	WHERE	DOT		ID		ELSE	*/
			new int[] {1,		7,		7,		7,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		},
			new int[] {1,		-1,		-1,		-1,		-1,		1,		1,		1,		-1,		2,		1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		1,		7,		-1,		3,		2,		1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		4,		1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		7,		-1,		5,		2,		1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		6,		1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		7,		-1,		-1,		2,		1,		},
			new int[] {-1,		-1,		-1,		-1,		1,		1,		7,		1,		-1,		8,		7,		},
			new int[] {-1,		-1,		-1,		-1,		1,		1,		-1,		1,		9,		8,		7,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		10,		7,		},
			new int[] {-1,		-1,		-1,		-1,		1,		1,		-1,		1,		-1,		8,		7,		},
		};

		int[][] iii = new int[][] {
			/*         SELECT	INSERT	UPDATE	DELETE	SET		PARENT	FROM	WHERE	DOT		ID		ELSE	*/
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		OP_2,	-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		OP_1,	-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
			new int[] {-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1,		-1, 	-1,		},
		};

		/*-----------------------------------------------------------------*/

		int idx;

		int old_state = 0;
		int new_state = 0;
		int operation = 0;

		for(int i = 0; i < tokens.size(); i++)
		{
			tokens.set(i, Tokenizer.backQuotesToDoubleQuotes(tokens.get(i)));
		}

		for(String token: tokens)
		{
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

			new_state = hhh[old_state][idx];
			operation = iii[old_state][idx];

			/*-------------------------------------------------------------*/

			System.out.println("`" + token + "` (" + idx + ") :: " + old_state + " -> " + new_state);

			if(new_state == -1)
			{
				System.out.println("syntax error!");

				return sql;
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

		return String.join("", result);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception
	{
		System.out.println(patch("UPDATE `DATASET` SET NAME = 'toto' WHERE (`radardb`.`DATASET`.`NAME` = 'test')", "public"));

		System.out.println(patch("INSERT INTO `dataset` (`radardb`.`DATASET`.`NAME` ,`radardb`.`DATASET`.`STATUS` ) VALUES ( 'test', 'valid')", "public"));
//		System.out.println(patch("UPDATE `router_catalog` SET `ami_router`.`router_catalog`.`custom` = '{}' WHERE (`ami_router`.`router_catalog`.`externalCatalog` = 'radardb')", "foo"));
/*		System.out.println(patch("SELECT c, b.c, a.b.c, x.c FROM b, a.b, a.b AS x WHERE c = 0 AND b.c = AND a.b.c = 0", "foo"));
		System.out.println(patch("UPDATE a.b SET x = '1', y = '2' WHERE c = 0 AND b.c = AND a.b.c = 0", "foo"));
		System.out.println(patch("DELETE FROM a.b WHERE c = 0 AND b.c = AND a.b.c = 0", "foo"));
		System.out.println(patch("INSERT INTO a.b (x,y) VALUES ('1','2') ", "foo"));
		System.out.println(patch("SELECT c, b.c, a.b.c, x.c FROM b, a.b, a.b AS x WHERE c IN (SELECT c FROM b, a.b, a.b AS x WHERE c = 0 AND b.c = AND a.b.c = 0)", "foo"));
		System.out.println(patch("SELECT c, b.c, a.b.c, x.c FROM b, a.b, a.b AS x WHERE c IN ((SELECT c FROM b, a.b, a.b AS x WHERE c = 0 AND b.c = AND a.b.c = 0) UNION (SELECT c FROM b, a.b, a.b AS x WHERE c = 0 AND b.c = AND a.b.c = 0)))", "foo"));
*/
		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
