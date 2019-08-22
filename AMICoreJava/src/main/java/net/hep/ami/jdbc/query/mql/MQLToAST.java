package net.hep.ami.jdbc.query.mql;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

public class MQLToAST
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private int m_cnt;

	private final String m_catalog;
	private final String m_entity;

	private final List<String> m_ruleNames;

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.jetbrains.annotations.Contract(pure = true)
	public MQLToAST(@NotNull String catalog, @NotNull String entity, @NotNull List<String> ruleNames)
	{
		m_cnt = 0x00;

		m_catalog = catalog;
		m_entity = entity;

		m_ruleNames = ruleNames;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String parse(@NotNull String externalCatalog, @NotNull String entity, @NotNull String AMIUser, boolean isAdmin, @NotNull String query) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		MQLLexer lexer = new MQLLexer(CharStreams.fromString(query));

		MQLParser parser = new MQLParser(new CommonTokenStream(lexer));

		/*------------------------------------------------------------------------------------------------------------*/

		AMIErrorListener listener = AMIErrorListener.setListener(lexer, parser);

		/*------------------------------------------------------------------------------------------------------------*/

		String result = new MQLToAST(externalCatalog, entity, Arrays.asList(parser.getRuleNames())).visitMQLQuery(parser.mqlQuery()).toString();

		if(listener.isError())
		{
			throw new Exception(listener.toString());
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private StringBuilder visitMQLQuery(@NotNull MQLParser.MqlQueryContext context)
	{
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();

		nodes.append("\tnode0 [shape=\"cylinder\", label=\"`").append(m_catalog).append("`.`").append(m_entity).append("`\"];\n");

		visit(nodes, edges, context, 0);

		return new StringBuilder().append("digraph ast {\n")
		                          .append("\trankdir=TB;\n")
		                          .append(nodes)
		                          .append(edges)
		                          .append("}")
		;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private void visit(StringBuilder nodes, StringBuilder edges, @NotNull ParseTree parseTree, int oldId)
	{
		int newId = oldId;

		final int nb = parseTree.getChildCount();

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < nb; i++)
		{
			visit(nodes, edges, parseTree.getChild(i), newId);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
