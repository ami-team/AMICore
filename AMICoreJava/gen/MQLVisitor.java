// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/MQL.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MQLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MQLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MQLParser#mqlQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMqlQuery(MQLParser.MqlQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectStatement(MQLParser.SelectStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#insertStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsertStatement(MQLParser.InsertStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#updateStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdateStatement(MQLParser.UpdateStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#deleteStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeleteStatement(MQLParser.DeleteStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#columnList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnList(MQLParser.ColumnListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#aColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAColumn(MQLParser.AColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#qIdList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQIdList(MQLParser.QIdListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#aQId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAQId(MQLParser.AQIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionTuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionTuple(MQLParser.ExpressionTupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#qIdTuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQIdTuple(MQLParser.QIdTupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#literalTuple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralTuple(MQLParser.LiteralTupleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionOr(MQLParser.ExpressionOrContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionXor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionXor(MQLParser.ExpressionXorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionAnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionAnd(MQLParser.ExpressionAndContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionNot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionNot(MQLParser.ExpressionNotContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionComp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionComp(MQLParser.ExpressionCompContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionAddSub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionAddSub(MQLParser.ExpressionAddSubContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionMulDiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionMulDiv(MQLParser.ExpressionMulDivContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#expressionPlusMinus}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionPlusMinus(MQLParser.ExpressionPlusMinusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionStdGroup}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStdGroup(MQLParser.ExpressionStdGroupContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionIsoGroup}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionIsoGroup(MQLParser.ExpressionIsoGroupContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionFunction}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionFunction(MQLParser.ExpressionFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionQId}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionQId(MQLParser.ExpressionQIdContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionLiteral}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionLiteral(MQLParser.ExpressionLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(MQLParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#qId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQId(MQLParser.QIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#constraintQId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraintQId(MQLParser.ConstraintQIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#basicQId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasicQId(MQLParser.BasicQIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link MQLParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(MQLParser.LiteralContext ctx);
}