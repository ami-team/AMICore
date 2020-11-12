// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/QId.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QIdParser}.
 */
public interface QIdListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QIdParser#qId}.
	 * @param ctx the parse tree
	 */
	void enterQId(QIdParser.QIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link QIdParser#qId}.
	 * @param ctx the parse tree
	 */
	void exitQId(QIdParser.QIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link QIdParser#constraintQId}.
	 * @param ctx the parse tree
	 */
	void enterConstraintQId(QIdParser.ConstraintQIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link QIdParser#constraintQId}.
	 * @param ctx the parse tree
	 */
	void exitConstraintQId(QIdParser.ConstraintQIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link QIdParser#basicQId}.
	 * @param ctx the parse tree
	 */
	void enterBasicQId(QIdParser.BasicQIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link QIdParser#basicQId}.
	 * @param ctx the parse tree
	 */
	void exitBasicQId(QIdParser.BasicQIdContext ctx);
}