// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/MQL.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MQLParser}.
 */
public interface MQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MQLParser#mqlQuery}.
	 * @param ctx the parse tree
	 */
	void enterMqlQuery(MQLParser.MqlQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#mqlQuery}.
	 * @param ctx the parse tree
	 */
	void exitMqlQuery(MQLParser.MqlQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(MQLParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(MQLParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#insertStatement}.
	 * @param ctx the parse tree
	 */
	void enterInsertStatement(MQLParser.InsertStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#insertStatement}.
	 * @param ctx the parse tree
	 */
	void exitInsertStatement(MQLParser.InsertStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void enterUpdateStatement(MQLParser.UpdateStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#updateStatement}.
	 * @param ctx the parse tree
	 */
	void exitUpdateStatement(MQLParser.UpdateStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#deleteStatement}.
	 * @param ctx the parse tree
	 */
	void enterDeleteStatement(MQLParser.DeleteStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#deleteStatement}.
	 * @param ctx the parse tree
	 */
	void exitDeleteStatement(MQLParser.DeleteStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#columnList}.
	 * @param ctx the parse tree
	 */
	void enterColumnList(MQLParser.ColumnListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#columnList}.
	 * @param ctx the parse tree
	 */
	void exitColumnList(MQLParser.ColumnListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#aColumn}.
	 * @param ctx the parse tree
	 */
	void enterAColumn(MQLParser.AColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#aColumn}.
	 * @param ctx the parse tree
	 */
	void exitAColumn(MQLParser.AColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#qIdList}.
	 * @param ctx the parse tree
	 */
	void enterQIdList(MQLParser.QIdListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#qIdList}.
	 * @param ctx the parse tree
	 */
	void exitQIdList(MQLParser.QIdListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#aQId}.
	 * @param ctx the parse tree
	 */
	void enterAQId(MQLParser.AQIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#aQId}.
	 * @param ctx the parse tree
	 */
	void exitAQId(MQLParser.AQIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionTuple}.
	 * @param ctx the parse tree
	 */
	void enterExpressionTuple(MQLParser.ExpressionTupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionTuple}.
	 * @param ctx the parse tree
	 */
	void exitExpressionTuple(MQLParser.ExpressionTupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#qIdTuple}.
	 * @param ctx the parse tree
	 */
	void enterQIdTuple(MQLParser.QIdTupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#qIdTuple}.
	 * @param ctx the parse tree
	 */
	void exitQIdTuple(MQLParser.QIdTupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#literalTuple}.
	 * @param ctx the parse tree
	 */
	void enterLiteralTuple(MQLParser.LiteralTupleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#literalTuple}.
	 * @param ctx the parse tree
	 */
	void exitLiteralTuple(MQLParser.LiteralTupleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionOr}.
	 * @param ctx the parse tree
	 */
	void enterExpressionOr(MQLParser.ExpressionOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionOr}.
	 * @param ctx the parse tree
	 */
	void exitExpressionOr(MQLParser.ExpressionOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionXor}.
	 * @param ctx the parse tree
	 */
	void enterExpressionXor(MQLParser.ExpressionXorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionXor}.
	 * @param ctx the parse tree
	 */
	void exitExpressionXor(MQLParser.ExpressionXorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionAnd}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAnd(MQLParser.ExpressionAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionAnd}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAnd(MQLParser.ExpressionAndContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionNot}.
	 * @param ctx the parse tree
	 */
	void enterExpressionNot(MQLParser.ExpressionNotContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionNot}.
	 * @param ctx the parse tree
	 */
	void exitExpressionNot(MQLParser.ExpressionNotContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionComp}.
	 * @param ctx the parse tree
	 */
	void enterExpressionComp(MQLParser.ExpressionCompContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionComp}.
	 * @param ctx the parse tree
	 */
	void exitExpressionComp(MQLParser.ExpressionCompContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionAddSub}.
	 * @param ctx the parse tree
	 */
	void enterExpressionAddSub(MQLParser.ExpressionAddSubContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionAddSub}.
	 * @param ctx the parse tree
	 */
	void exitExpressionAddSub(MQLParser.ExpressionAddSubContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionMulDiv}.
	 * @param ctx the parse tree
	 */
	void enterExpressionMulDiv(MQLParser.ExpressionMulDivContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionMulDiv}.
	 * @param ctx the parse tree
	 */
	void exitExpressionMulDiv(MQLParser.ExpressionMulDivContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#expressionPlusMinus}.
	 * @param ctx the parse tree
	 */
	void enterExpressionPlusMinus(MQLParser.ExpressionPlusMinusContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#expressionPlusMinus}.
	 * @param ctx the parse tree
	 */
	void exitExpressionPlusMinus(MQLParser.ExpressionPlusMinusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionStdGroup}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStdGroup(MQLParser.ExpressionStdGroupContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionStdGroup}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStdGroup(MQLParser.ExpressionStdGroupContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionIsoGroup}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void enterExpressionIsoGroup(MQLParser.ExpressionIsoGroupContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionIsoGroup}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void exitExpressionIsoGroup(MQLParser.ExpressionIsoGroupContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionFunction}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void enterExpressionFunction(MQLParser.ExpressionFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionFunction}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void exitExpressionFunction(MQLParser.ExpressionFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionQId}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void enterExpressionQId(MQLParser.ExpressionQIdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionQId}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void exitExpressionQId(MQLParser.ExpressionQIdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionLiteral}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void enterExpressionLiteral(MQLParser.ExpressionLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionLiteral}
	 * labeled alternative in {@link MQLParser#expressionX}.
	 * @param ctx the parse tree
	 */
	void exitExpressionLiteral(MQLParser.ExpressionLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(MQLParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(MQLParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#qId}.
	 * @param ctx the parse tree
	 */
	void enterQId(MQLParser.QIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#qId}.
	 * @param ctx the parse tree
	 */
	void exitQId(MQLParser.QIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#constraintQId}.
	 * @param ctx the parse tree
	 */
	void enterConstraintQId(MQLParser.ConstraintQIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#constraintQId}.
	 * @param ctx the parse tree
	 */
	void exitConstraintQId(MQLParser.ConstraintQIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#basicQId}.
	 * @param ctx the parse tree
	 */
	void enterBasicQId(MQLParser.BasicQIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#basicQId}.
	 * @param ctx the parse tree
	 */
	void exitBasicQId(MQLParser.BasicQIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link MQLParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(MQLParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link MQLParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(MQLParser.LiteralContext ctx);
}