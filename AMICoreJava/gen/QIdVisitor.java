// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/QId.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QIdParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QIdVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QIdParser#qId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQId(QIdParser.QIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link QIdParser#constraintQId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraintQId(QIdParser.ConstraintQIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link QIdParser#basicQId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicQId(QIdParser.BasicQIdContext ctx);
}