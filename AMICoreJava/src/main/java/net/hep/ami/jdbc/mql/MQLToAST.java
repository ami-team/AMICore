package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.mql.antlr.*;

public class MQLToAST
{
	/*---------------------------------------------------------------------*/

	private int m_cnt;

	private final String m_catalog;

	/*---------------------------------------------------------------------*/

	public MQLToAST(String catalog)
	{
		m_cnt = 0;

		m_catalog = catalog;
	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, String catalog) throws Exception
	{
		/*-----------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy());

		/*-----------------------------------------------------------------*/

		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();

		new MQLToAST(catalog).visit(nodes, edges, Arrays.asList(parser.getRuleNames()), 0, parser.selectStatement());

		return new StringBuilder().append("digraph ast {\n")
		                          .append("\trankdir=TB;\n")
		                          .append(nodes)
		                          .append(edges)
		                          .append("}")
		                          .toString()
		;

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private void visit(StringBuilder nodes, StringBuilder edges, List<String> foo, int oldId, ParseTree ctx)
	{
		int newId;

		final int nb = ctx.getChildCount();

		/*-----------------------------------------------------------------*/

		if(ctx instanceof TerminalNode)
		{
			newId = ++m_cnt;

			if(oldId > 0)
			{
				edges.append("\tnode").append(oldId).append(" -> node").append(newId).append(";\n");
			}

			nodes.append("\tnode").append(newId).append(" [label=\"").append(Trees.getNodeText(ctx, foo).replace("\"", "\\\"")).append("\"];\n");
		}
		else
		{
			if(nb > 1)
			{
				newId = ++m_cnt;

				if(oldId > 0)
				{
					edges.append("\tnode").append(oldId).append(" -> node").append(newId).append(";\n");
				}

				nodes.append("\tnode").append(newId).append(" [label=\"#").append(Trees.getNodeText(ctx, foo).replace("\"", "\\\"")).append("\"];\n");
			}
			else
			{
				newId = oldId;
			}
		}

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < nb; i++)
		{
			visit(nodes, edges, foo, newId, ctx.getChild(i));
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
