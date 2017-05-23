package net.hep.ami.jdbc.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.jdbc.mql.antlr.*;

public class MQLToAST
{
	/*---------------------------------------------------------------------*/

	private int m_cnt;

	private List<String> m_ruleNames;

	/*---------------------------------------------------------------------*/

	public MQLToAST(List<String> ruleNames)
	{
		m_cnt = 0;

		m_ruleNames = ruleNames;
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

		nodes.append("\tnode0").append(" [shape=cylinder, label=\"").append(catalog).append("\"];\n");

		new MQLToAST(Arrays.asList(parser.getRuleNames())).visit(nodes, edges, parser.selectStatement(), 0);

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

	private void visit(StringBuilder nodes, StringBuilder edges, ParseTree parseTree, int oldId)
	{
		int newId = oldId;

		final int nb = parseTree.getChildCount();

		/*-----------------------------------------------------------------*/

		if(parseTree instanceof TerminalNode)
		{
			newId = ++m_cnt;

			edges.append("\tnode").append(oldId).append(" -> node").append(newId).append(";\n");

			nodes.append("\tnode").append(newId).append(" [label=\"").append(Trees.getNodeText(parseTree, m_ruleNames).replace("\"", "\\\"")).append("\"];\n");
		}
		else
		{
			if(nb > 1)
			{
				newId = ++m_cnt;

				edges.append("\tnode").append(oldId).append(" -> node").append(newId).append(";\n");

				nodes.append("\tnode").append(newId).append(" [label=\"#").append(Trees.getNodeText(parseTree, m_ruleNames).replace("\"", "\\\"")).append("\"];\n");
			}
		}

		/*-----------------------------------------------------------------*/

		for(int i = 0; i < nb; i++)
		{
			visit(nodes, edges, parseTree.getChild(i), newId);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
