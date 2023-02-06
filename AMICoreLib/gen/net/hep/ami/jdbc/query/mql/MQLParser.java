// Generated from java-escape by ANTLR 4.11.1
package net.hep.ami.jdbc.query.mql;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class MQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, SELECT=13, DISTINCT=14, AS=15, WHERE=16, 
		GROUP=17, ORDER=18, BY=19, ASC=20, DESC=21, LIMIT=22, OFFSET=23, INSERT=24, 
		UPDATE=25, VALUES=26, DELETE=27, OR=28, XOR=29, AND=30, COMP=31, NOT=32, 
		LIKE=33, REGEXP=34, BETWEEN=35, IN=36, IS=37, PLUS=38, MINUS=39, MUL=40, 
		DIV=41, MOD=42, FUNCTION=43, NULL=44, CURRENT_TIMESTAMP=45, STRING=46, 
		PARAMETER=47, NUMBER=48, ID=49, COMMENT=50, WS=51;
	public static final int
		RULE_mqlQuery = 0, RULE_selectStatement = 1, RULE_insertStatement = 2, 
		RULE_updateStatement = 3, RULE_deleteStatement = 4, RULE_columnList = 5, 
		RULE_aColumn = 6, RULE_qIdList = 7, RULE_aQId = 8, RULE_expressionTuple = 9, 
		RULE_qIdTuple = 10, RULE_literalTuple = 11, RULE_expressionOr = 12, RULE_expressionXor = 13, 
		RULE_expressionAnd = 14, RULE_expressionNot = 15, RULE_expressionComp = 16, 
		RULE_expressionAddSub = 17, RULE_expressionMulDiv = 18, RULE_expressionPlusMinus = 19, 
		RULE_expressionX = 20, RULE_function = 21, RULE_qId = 22, RULE_constraintQId = 23, 
		RULE_basicQId = 24, RULE_literal = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"mqlQuery", "selectStatement", "insertStatement", "updateStatement", 
			"deleteStatement", "columnList", "aColumn", "qIdList", "aQId", "expressionTuple", 
			"qIdTuple", "literalTuple", "expressionOr", "expressionXor", "expressionAnd", 
			"expressionNot", "expressionComp", "expressionAddSub", "expressionMulDiv", 
			"expressionPlusMinus", "expressionX", "function", "qId", "constraintQId", 
			"basicQId", "literal"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'('", "')'", "'['", "']'", "'{'", "'|'", "'}'", 
			"'!'", "'#'", "'.'", null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "'+'", "'-'", "'*'", "'/'", "'%'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "SELECT", "DISTINCT", "AS", "WHERE", "GROUP", "ORDER", "BY", "ASC", 
			"DESC", "LIMIT", "OFFSET", "INSERT", "UPDATE", "VALUES", "DELETE", "OR", 
			"XOR", "AND", "COMP", "NOT", "LIKE", "REGEXP", "BETWEEN", "IN", "IS", 
			"PLUS", "MINUS", "MUL", "DIV", "MOD", "FUNCTION", "NULL", "CURRENT_TIMESTAMP", 
			"STRING", "PARAMETER", "NUMBER", "ID", "COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MqlQueryContext extends ParserRuleContext {
		public SelectStatementContext m_select;
		public InsertStatementContext m_insert;
		public UpdateStatementContext m_update;
		public DeleteStatementContext m_delete;
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public InsertStatementContext insertStatement() {
			return getRuleContext(InsertStatementContext.class,0);
		}
		public UpdateStatementContext updateStatement() {
			return getRuleContext(UpdateStatementContext.class,0);
		}
		public DeleteStatementContext deleteStatement() {
			return getRuleContext(DeleteStatementContext.class,0);
		}
		public MqlQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mqlQuery; }
	}

	public final MqlQueryContext mqlQuery() throws RecognitionException {
		MqlQueryContext _localctx = new MqlQueryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_mqlQuery);
		int _la;
		try {
			setState(62);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(52);
				match(T__0);
				}
				break;
			case SELECT:
			case INSERT:
			case UPDATE:
			case DELETE:
				enterOuterAlt(_localctx, 2);
				{
				setState(57);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SELECT:
					{
					setState(53);
					((MqlQueryContext)_localctx).m_select = selectStatement();
					}
					break;
				case INSERT:
					{
					setState(54);
					((MqlQueryContext)_localctx).m_insert = insertStatement();
					}
					break;
				case UPDATE:
					{
					setState(55);
					((MqlQueryContext)_localctx).m_update = updateStatement();
					}
					break;
				case DELETE:
					{
					setState(56);
					((MqlQueryContext)_localctx).m_delete = deleteStatement();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(60);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(59);
					match(T__0);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectStatementContext extends ParserRuleContext {
		public Token m_distinct;
		public ColumnListContext m_columns;
		public ExpressionOrContext m_expression;
		public QIdListContext m_groupBy;
		public QIdListContext m_orderBy;
		public Token m_orderWay;
		public Token m_limit;
		public Token m_offset;
		public TerminalNode SELECT() { return getToken(MQLParser.SELECT, 0); }
		public ColumnListContext columnList() {
			return getRuleContext(ColumnListContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(MQLParser.WHERE, 0); }
		public TerminalNode GROUP() { return getToken(MQLParser.GROUP, 0); }
		public List<TerminalNode> BY() { return getTokens(MQLParser.BY); }
		public TerminalNode BY(int i) {
			return getToken(MQLParser.BY, i);
		}
		public TerminalNode ORDER() { return getToken(MQLParser.ORDER, 0); }
		public TerminalNode LIMIT() { return getToken(MQLParser.LIMIT, 0); }
		public TerminalNode DISTINCT() { return getToken(MQLParser.DISTINCT, 0); }
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public List<QIdListContext> qIdList() {
			return getRuleContexts(QIdListContext.class);
		}
		public QIdListContext qIdList(int i) {
			return getRuleContext(QIdListContext.class,i);
		}
		public List<TerminalNode> NUMBER() { return getTokens(MQLParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(MQLParser.NUMBER, i);
		}
		public TerminalNode OFFSET() { return getToken(MQLParser.OFFSET, 0); }
		public TerminalNode ASC() { return getToken(MQLParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(MQLParser.DESC, 0); }
		public SelectStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStatement; }
	}

	public final SelectStatementContext selectStatement() throws RecognitionException {
		SelectStatementContext _localctx = new SelectStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(SELECT);
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISTINCT) {
				{
				setState(65);
				((SelectStatementContext)_localctx).m_distinct = match(DISTINCT);
				}
			}

			setState(68);
			((SelectStatementContext)_localctx).m_columns = columnList();
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(69);
				match(WHERE);
				setState(70);
				((SelectStatementContext)_localctx).m_expression = expressionOr();
				}
			}

			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP) {
				{
				setState(73);
				match(GROUP);
				setState(74);
				match(BY);
				setState(75);
				((SelectStatementContext)_localctx).m_groupBy = qIdList();
				}
			}

			setState(84);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(78);
				match(ORDER);
				setState(79);
				match(BY);
				setState(80);
				((SelectStatementContext)_localctx).m_orderBy = qIdList();
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASC || _la==DESC) {
					{
					setState(81);
					((SelectStatementContext)_localctx).m_orderWay = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==ASC || _la==DESC) ) {
						((SelectStatementContext)_localctx).m_orderWay = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				}
			}

			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(86);
				match(LIMIT);
				setState(87);
				((SelectStatementContext)_localctx).m_limit = match(NUMBER);
				setState(90);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OFFSET) {
					{
					setState(88);
					match(OFFSET);
					setState(89);
					((SelectStatementContext)_localctx).m_offset = match(NUMBER);
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InsertStatementContext extends ParserRuleContext {
		public QIdTupleContext m_qIds;
		public ExpressionTupleContext m_expressions;
		public TerminalNode INSERT() { return getToken(MQLParser.INSERT, 0); }
		public TerminalNode VALUES() { return getToken(MQLParser.VALUES, 0); }
		public QIdTupleContext qIdTuple() {
			return getRuleContext(QIdTupleContext.class,0);
		}
		public ExpressionTupleContext expressionTuple() {
			return getRuleContext(ExpressionTupleContext.class,0);
		}
		public InsertStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insertStatement; }
	}

	public final InsertStatementContext insertStatement() throws RecognitionException {
		InsertStatementContext _localctx = new InsertStatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_insertStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			match(INSERT);
			setState(95);
			((InsertStatementContext)_localctx).m_qIds = qIdTuple();
			setState(96);
			match(VALUES);
			setState(97);
			((InsertStatementContext)_localctx).m_expressions = expressionTuple();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UpdateStatementContext extends ParserRuleContext {
		public QIdTupleContext m_qIds;
		public ExpressionTupleContext m_expressions;
		public ExpressionOrContext m_expression;
		public TerminalNode UPDATE() { return getToken(MQLParser.UPDATE, 0); }
		public TerminalNode VALUES() { return getToken(MQLParser.VALUES, 0); }
		public QIdTupleContext qIdTuple() {
			return getRuleContext(QIdTupleContext.class,0);
		}
		public ExpressionTupleContext expressionTuple() {
			return getRuleContext(ExpressionTupleContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(MQLParser.WHERE, 0); }
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public UpdateStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_updateStatement; }
	}

	public final UpdateStatementContext updateStatement() throws RecognitionException {
		UpdateStatementContext _localctx = new UpdateStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_updateStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(UPDATE);
			setState(100);
			((UpdateStatementContext)_localctx).m_qIds = qIdTuple();
			setState(101);
			match(VALUES);
			setState(102);
			((UpdateStatementContext)_localctx).m_expressions = expressionTuple();
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(103);
				match(WHERE);
				setState(104);
				((UpdateStatementContext)_localctx).m_expression = expressionOr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeleteStatementContext extends ParserRuleContext {
		public ExpressionOrContext m_expression;
		public TerminalNode DELETE() { return getToken(MQLParser.DELETE, 0); }
		public TerminalNode WHERE() { return getToken(MQLParser.WHERE, 0); }
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public DeleteStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deleteStatement; }
	}

	public final DeleteStatementContext deleteStatement() throws RecognitionException {
		DeleteStatementContext _localctx = new DeleteStatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_deleteStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(DELETE);
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(108);
				match(WHERE);
				setState(109);
				((DeleteStatementContext)_localctx).m_expression = expressionOr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnListContext extends ParserRuleContext {
		public AColumnContext aColumn;
		public List<AColumnContext> m_columns = new ArrayList<AColumnContext>();
		public List<AColumnContext> aColumn() {
			return getRuleContexts(AColumnContext.class);
		}
		public AColumnContext aColumn(int i) {
			return getRuleContext(AColumnContext.class,i);
		}
		public ColumnListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnList; }
	}

	public final ColumnListContext columnList() throws RecognitionException {
		ColumnListContext _localctx = new ColumnListContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_columnList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			((ColumnListContext)_localctx).aColumn = aColumn();
			((ColumnListContext)_localctx).m_columns.add(((ColumnListContext)_localctx).aColumn);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(113);
				match(T__1);
				setState(114);
				((ColumnListContext)_localctx).aColumn = aColumn();
				((ColumnListContext)_localctx).m_columns.add(((ColumnListContext)_localctx).aColumn);
				}
				}
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AColumnContext extends ParserRuleContext {
		public ExpressionOrContext m_expression;
		public Token m_alias;
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public TerminalNode AS() { return getToken(MQLParser.AS, 0); }
		public TerminalNode ID() { return getToken(MQLParser.ID, 0); }
		public AColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aColumn; }
	}

	public final AColumnContext aColumn() throws RecognitionException {
		AColumnContext _localctx = new AColumnContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_aColumn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			((AColumnContext)_localctx).m_expression = expressionOr();
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(121);
				match(AS);
				setState(122);
				((AColumnContext)_localctx).m_alias = match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QIdListContext extends ParserRuleContext {
		public AQIdContext aQId;
		public List<AQIdContext> m_aQIds = new ArrayList<AQIdContext>();
		public List<AQIdContext> aQId() {
			return getRuleContexts(AQIdContext.class);
		}
		public AQIdContext aQId(int i) {
			return getRuleContext(AQIdContext.class,i);
		}
		public QIdListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qIdList; }
	}

	public final QIdListContext qIdList() throws RecognitionException {
		QIdListContext _localctx = new QIdListContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_qIdList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			((QIdListContext)_localctx).aQId = aQId();
			((QIdListContext)_localctx).m_aQIds.add(((QIdListContext)_localctx).aQId);
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(126);
				match(T__1);
				setState(127);
				((QIdListContext)_localctx).aQId = aQId();
				((QIdListContext)_localctx).m_aQIds.add(((QIdListContext)_localctx).aQId);
				}
				}
				setState(132);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AQIdContext extends ParserRuleContext {
		public QIdContext m_qId;
		public QIdContext qId() {
			return getRuleContext(QIdContext.class,0);
		}
		public AQIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aQId; }
	}

	public final AQIdContext aQId() throws RecognitionException {
		AQIdContext _localctx = new AQIdContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_aQId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			((AQIdContext)_localctx).m_qId = qId();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionTupleContext extends ParserRuleContext {
		public ExpressionOrContext expressionOr;
		public List<ExpressionOrContext> m_expressions = new ArrayList<ExpressionOrContext>();
		public List<ExpressionOrContext> expressionOr() {
			return getRuleContexts(ExpressionOrContext.class);
		}
		public ExpressionOrContext expressionOr(int i) {
			return getRuleContext(ExpressionOrContext.class,i);
		}
		public ExpressionTupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionTuple; }
	}

	public final ExpressionTupleContext expressionTuple() throws RecognitionException {
		ExpressionTupleContext _localctx = new ExpressionTupleContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_expressionTuple);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(T__2);
			setState(136);
			((ExpressionTupleContext)_localctx).expressionOr = expressionOr();
			((ExpressionTupleContext)_localctx).m_expressions.add(((ExpressionTupleContext)_localctx).expressionOr);
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(137);
				match(T__1);
				setState(138);
				((ExpressionTupleContext)_localctx).expressionOr = expressionOr();
				((ExpressionTupleContext)_localctx).m_expressions.add(((ExpressionTupleContext)_localctx).expressionOr);
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(144);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QIdTupleContext extends ParserRuleContext {
		public QIdContext qId;
		public List<QIdContext> m_qIds = new ArrayList<QIdContext>();
		public List<QIdContext> qId() {
			return getRuleContexts(QIdContext.class);
		}
		public QIdContext qId(int i) {
			return getRuleContext(QIdContext.class,i);
		}
		public QIdTupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qIdTuple; }
	}

	public final QIdTupleContext qIdTuple() throws RecognitionException {
		QIdTupleContext _localctx = new QIdTupleContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_qIdTuple);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(T__2);
			setState(147);
			((QIdTupleContext)_localctx).qId = qId();
			((QIdTupleContext)_localctx).m_qIds.add(((QIdTupleContext)_localctx).qId);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(148);
				match(T__1);
				setState(149);
				((QIdTupleContext)_localctx).qId = qId();
				((QIdTupleContext)_localctx).m_qIds.add(((QIdTupleContext)_localctx).qId);
				}
				}
				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(155);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralTupleContext extends ParserRuleContext {
		public LiteralContext literal;
		public List<LiteralContext> m_literals = new ArrayList<LiteralContext>();
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public LiteralTupleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalTuple; }
	}

	public final LiteralTupleContext literalTuple() throws RecognitionException {
		LiteralTupleContext _localctx = new LiteralTupleContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_literalTuple);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(T__2);
			setState(158);
			((LiteralTupleContext)_localctx).literal = literal();
			((LiteralTupleContext)_localctx).m_literals.add(((LiteralTupleContext)_localctx).literal);
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(159);
				match(T__1);
				setState(160);
				((LiteralTupleContext)_localctx).literal = literal();
				((LiteralTupleContext)_localctx).m_literals.add(((LiteralTupleContext)_localctx).literal);
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(166);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionOrContext extends ParserRuleContext {
		public List<ExpressionXorContext> expressionXor() {
			return getRuleContexts(ExpressionXorContext.class);
		}
		public ExpressionXorContext expressionXor(int i) {
			return getRuleContext(ExpressionXorContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(MQLParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(MQLParser.OR, i);
		}
		public ExpressionOrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionOr; }
	}

	public final ExpressionOrContext expressionOr() throws RecognitionException {
		ExpressionOrContext _localctx = new ExpressionOrContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_expressionOr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			expressionXor();
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(169);
				match(OR);
				setState(170);
				expressionXor();
				}
				}
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionXorContext extends ParserRuleContext {
		public List<ExpressionAndContext> expressionAnd() {
			return getRuleContexts(ExpressionAndContext.class);
		}
		public ExpressionAndContext expressionAnd(int i) {
			return getRuleContext(ExpressionAndContext.class,i);
		}
		public List<TerminalNode> XOR() { return getTokens(MQLParser.XOR); }
		public TerminalNode XOR(int i) {
			return getToken(MQLParser.XOR, i);
		}
		public ExpressionXorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionXor; }
	}

	public final ExpressionXorContext expressionXor() throws RecognitionException {
		ExpressionXorContext _localctx = new ExpressionXorContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_expressionXor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			expressionAnd();
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==XOR) {
				{
				{
				setState(177);
				match(XOR);
				setState(178);
				expressionAnd();
				}
				}
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionAndContext extends ParserRuleContext {
		public List<ExpressionNotContext> expressionNot() {
			return getRuleContexts(ExpressionNotContext.class);
		}
		public ExpressionNotContext expressionNot(int i) {
			return getRuleContext(ExpressionNotContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(MQLParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(MQLParser.AND, i);
		}
		public ExpressionAndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionAnd; }
	}

	public final ExpressionAndContext expressionAnd() throws RecognitionException {
		ExpressionAndContext _localctx = new ExpressionAndContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_expressionAnd);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			expressionNot();
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(185);
				match(AND);
				setState(186);
				expressionNot();
				}
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionNotContext extends ParserRuleContext {
		public ExpressionCompContext expressionComp() {
			return getRuleContext(ExpressionCompContext.class,0);
		}
		public List<TerminalNode> NOT() { return getTokens(MQLParser.NOT); }
		public TerminalNode NOT(int i) {
			return getToken(MQLParser.NOT, i);
		}
		public ExpressionNotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionNot; }
	}

	public final ExpressionNotContext expressionNot() throws RecognitionException {
		ExpressionNotContext _localctx = new ExpressionNotContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expressionNot);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NOT) {
				{
				{
				setState(192);
				match(NOT);
				}
				}
				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(198);
			expressionComp();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionCompContext extends ParserRuleContext {
		public List<ExpressionAddSubContext> expressionAddSub() {
			return getRuleContexts(ExpressionAddSubContext.class);
		}
		public ExpressionAddSubContext expressionAddSub(int i) {
			return getRuleContext(ExpressionAddSubContext.class,i);
		}
		public TerminalNode COMP() { return getToken(MQLParser.COMP, 0); }
		public TerminalNode IS() { return getToken(MQLParser.IS, 0); }
		public TerminalNode NULL() { return getToken(MQLParser.NULL, 0); }
		public TerminalNode BETWEEN() { return getToken(MQLParser.BETWEEN, 0); }
		public TerminalNode AND() { return getToken(MQLParser.AND, 0); }
		public TerminalNode IN() { return getToken(MQLParser.IN, 0); }
		public LiteralTupleContext literalTuple() {
			return getRuleContext(LiteralTupleContext.class,0);
		}
		public TerminalNode NOT() { return getToken(MQLParser.NOT, 0); }
		public TerminalNode LIKE() { return getToken(MQLParser.LIKE, 0); }
		public TerminalNode REGEXP() { return getToken(MQLParser.REGEXP, 0); }
		public ExpressionCompContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionComp; }
	}

	public final ExpressionCompContext expressionComp() throws RecognitionException {
		ExpressionCompContext _localctx = new ExpressionCompContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_expressionComp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			expressionAddSub();
			setState(222);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMP:
				{
				setState(201);
				match(COMP);
				setState(202);
				expressionAddSub();
				}
				break;
			case NOT:
			case LIKE:
			case REGEXP:
			case BETWEEN:
			case IN:
				{
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(203);
					match(NOT);
					}
				}

				setState(215);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case BETWEEN:
					{
					setState(206);
					match(BETWEEN);
					setState(207);
					expressionAddSub();
					setState(208);
					match(AND);
					setState(209);
					expressionAddSub();
					}
					break;
				case LIKE:
				case REGEXP:
					{
					setState(211);
					_la = _input.LA(1);
					if ( !(_la==LIKE || _la==REGEXP) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(212);
					expressionAddSub();
					}
					break;
				case IN:
					{
					setState(213);
					match(IN);
					setState(214);
					literalTuple();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case IS:
				{
				setState(217);
				match(IS);
				setState(219);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(218);
					match(NOT);
					}
				}

				setState(221);
				match(NULL);
				}
				break;
			case EOF:
			case T__0:
			case T__1:
			case T__3:
			case T__5:
			case AS:
			case WHERE:
			case GROUP:
			case ORDER:
			case LIMIT:
			case OR:
			case XOR:
			case AND:
				break;
			default:
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionAddSubContext extends ParserRuleContext {
		public List<ExpressionMulDivContext> expressionMulDiv() {
			return getRuleContexts(ExpressionMulDivContext.class);
		}
		public ExpressionMulDivContext expressionMulDiv(int i) {
			return getRuleContext(ExpressionMulDivContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(MQLParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(MQLParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(MQLParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(MQLParser.MINUS, i);
		}
		public ExpressionAddSubContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionAddSub; }
	}

	public final ExpressionAddSubContext expressionAddSub() throws RecognitionException {
		ExpressionAddSubContext _localctx = new ExpressionAddSubContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_expressionAddSub);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			expressionMulDiv();
			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				{
				setState(225);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(226);
				expressionMulDiv();
				}
				}
				setState(231);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionMulDivContext extends ParserRuleContext {
		public List<ExpressionPlusMinusContext> expressionPlusMinus() {
			return getRuleContexts(ExpressionPlusMinusContext.class);
		}
		public ExpressionPlusMinusContext expressionPlusMinus(int i) {
			return getRuleContext(ExpressionPlusMinusContext.class,i);
		}
		public List<TerminalNode> MUL() { return getTokens(MQLParser.MUL); }
		public TerminalNode MUL(int i) {
			return getToken(MQLParser.MUL, i);
		}
		public List<TerminalNode> DIV() { return getTokens(MQLParser.DIV); }
		public TerminalNode DIV(int i) {
			return getToken(MQLParser.DIV, i);
		}
		public List<TerminalNode> MOD() { return getTokens(MQLParser.MOD); }
		public TerminalNode MOD(int i) {
			return getToken(MQLParser.MOD, i);
		}
		public ExpressionMulDivContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionMulDiv; }
	}

	public final ExpressionMulDivContext expressionMulDiv() throws RecognitionException {
		ExpressionMulDivContext _localctx = new ExpressionMulDivContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_expressionMulDiv);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			expressionPlusMinus();
			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((_la) & ~0x3f) == 0 && ((1L << _la) & 7696581394432L) != 0) {
				{
				{
				setState(233);
				_la = _input.LA(1);
				if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 7696581394432L) != 0) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(234);
				expressionPlusMinus();
				}
				}
				setState(239);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionPlusMinusContext extends ParserRuleContext {
		public Token m_operator;
		public ExpressionXContext expressionX() {
			return getRuleContext(ExpressionXContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(MQLParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(MQLParser.MINUS, 0); }
		public ExpressionPlusMinusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionPlusMinus; }
	}

	public final ExpressionPlusMinusContext expressionPlusMinus() throws RecognitionException {
		ExpressionPlusMinusContext _localctx = new ExpressionPlusMinusContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_expressionPlusMinus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(240);
				((ExpressionPlusMinusContext)_localctx).m_operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
					((ExpressionPlusMinusContext)_localctx).m_operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(243);
			expressionX();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionXContext extends ParserRuleContext {
		public ExpressionXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionX; }
	 
		public ExpressionXContext() { }
		public void copyFrom(ExpressionXContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionStdGroupContext extends ExpressionXContext {
		public ExpressionOrContext m_expression;
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public ExpressionStdGroupContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionFunctionContext extends ExpressionXContext {
		public FunctionContext m_function;
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ExpressionFunctionContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionIsoGroupContext extends ExpressionXContext {
		public ExpressionOrContext m_expression;
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public ExpressionIsoGroupContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionQIdContext extends ExpressionXContext {
		public QIdContext m_qId;
		public QIdContext qId() {
			return getRuleContext(QIdContext.class,0);
		}
		public ExpressionQIdContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionLiteralContext extends ExpressionXContext {
		public LiteralContext m_literal;
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ExpressionLiteralContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}

	public final ExpressionXContext expressionX() throws RecognitionException {
		ExpressionXContext _localctx = new ExpressionXContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_expressionX);
		try {
			setState(256);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				_localctx = new ExpressionStdGroupContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				match(T__2);
				setState(246);
				((ExpressionStdGroupContext)_localctx).m_expression = expressionOr();
				setState(247);
				match(T__3);
				}
				break;
			case T__4:
				_localctx = new ExpressionIsoGroupContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(249);
				match(T__4);
				setState(250);
				((ExpressionIsoGroupContext)_localctx).m_expression = expressionOr();
				setState(251);
				match(T__5);
				}
				break;
			case FUNCTION:
				_localctx = new ExpressionFunctionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(253);
				((ExpressionFunctionContext)_localctx).m_function = function();
				}
				break;
			case T__10:
			case MUL:
			case ID:
				_localctx = new ExpressionQIdContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(254);
				((ExpressionQIdContext)_localctx).m_qId = qId();
				}
				break;
			case NULL:
			case CURRENT_TIMESTAMP:
			case STRING:
			case PARAMETER:
			case NUMBER:
				_localctx = new ExpressionLiteralContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(255);
				((ExpressionLiteralContext)_localctx).m_literal = literal();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionContext extends ParserRuleContext {
		public Token m_functionName;
		public Token m_distinct;
		public ExpressionOrContext expressionOr;
		public List<ExpressionOrContext> m_expressions = new ArrayList<ExpressionOrContext>();
		public TerminalNode FUNCTION() { return getToken(MQLParser.FUNCTION, 0); }
		public List<ExpressionOrContext> expressionOr() {
			return getRuleContexts(ExpressionOrContext.class);
		}
		public ExpressionOrContext expressionOr(int i) {
			return getRuleContext(ExpressionOrContext.class,i);
		}
		public TerminalNode DISTINCT() { return getToken(MQLParser.DISTINCT, 0); }
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			((FunctionContext)_localctx).m_functionName = match(FUNCTION);
			setState(259);
			match(T__2);
			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISTINCT) {
				{
				setState(260);
				((FunctionContext)_localctx).m_distinct = match(DISTINCT);
				}
			}

			setState(263);
			((FunctionContext)_localctx).expressionOr = expressionOr();
			((FunctionContext)_localctx).m_expressions.add(((FunctionContext)_localctx).expressionOr);
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(264);
				match(T__1);
				setState(265);
				((FunctionContext)_localctx).expressionOr = expressionOr();
				((FunctionContext)_localctx).m_expressions.add(((FunctionContext)_localctx).expressionOr);
				}
				}
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(271);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QIdContext extends ParserRuleContext {
		public BasicQIdContext m_basicQId;
		public ConstraintQIdContext constraintQId;
		public List<ConstraintQIdContext> m_constraintQIds = new ArrayList<ConstraintQIdContext>();
		public BasicQIdContext basicQId() {
			return getRuleContext(BasicQIdContext.class,0);
		}
		public List<ConstraintQIdContext> constraintQId() {
			return getRuleContexts(ConstraintQIdContext.class);
		}
		public ConstraintQIdContext constraintQId(int i) {
			return getRuleContext(ConstraintQIdContext.class,i);
		}
		public QIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qId; }
	}

	public final QIdContext qId() throws RecognitionException {
		QIdContext _localctx = new QIdContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_qId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			((QIdContext)_localctx).m_basicQId = basicQId();
			setState(285);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(274);
				match(T__6);
				setState(275);
				((QIdContext)_localctx).constraintQId = constraintQId();
				((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1 || _la==T__7) {
					{
					{
					setState(276);
					_la = _input.LA(1);
					if ( !(_la==T__1 || _la==T__7) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(277);
					((QIdContext)_localctx).constraintQId = constraintQId();
					((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
					}
					}
					setState(282);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(283);
				match(T__8);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstraintQIdContext extends ParserRuleContext {
		public Token m_op;
		public QIdContext m_qId;
		public QIdContext qId() {
			return getRuleContext(QIdContext.class,0);
		}
		public ConstraintQIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraintQId; }
	}

	public final ConstraintQIdContext constraintQId() throws RecognitionException {
		ConstraintQIdContext _localctx = new ConstraintQIdContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_constraintQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(287);
				((ConstraintQIdContext)_localctx).m_op = match(T__9);
				}
			}

			setState(290);
			((ConstraintQIdContext)_localctx).m_qId = qId();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BasicQIdContext extends ParserRuleContext {
		public Token ID;
		public List<Token> m_ids = new ArrayList<Token>();
		public Token s40;
		public Token s11;
		public Token _tset731;
		public Token _tset744;
		public List<TerminalNode> ID() { return getTokens(MQLParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(MQLParser.ID, i);
		}
		public List<TerminalNode> MUL() { return getTokens(MQLParser.MUL); }
		public TerminalNode MUL(int i) {
			return getToken(MQLParser.MUL, i);
		}
		public BasicQIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicQId; }
	}

	public final BasicQIdContext basicQId() throws RecognitionException {
		BasicQIdContext _localctx = new BasicQIdContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_basicQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			((BasicQIdContext)_localctx)._tset731 = _input.LT(1);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 564049465051136L) != 0) ) {
				((BasicQIdContext)_localctx)._tset731 = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset731);
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(293);
				match(T__11);
				setState(294);
				((BasicQIdContext)_localctx)._tset744 = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 564049465051136L) != 0) ) {
					((BasicQIdContext)_localctx)._tset744 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset744);
				}
				}
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(MQLParser.NULL, 0); }
		public TerminalNode CURRENT_TIMESTAMP() { return getToken(MQLParser.CURRENT_TIMESTAMP, 0); }
		public TerminalNode STRING() { return getToken(MQLParser.STRING, 0); }
		public TerminalNode PARAMETER() { return getToken(MQLParser.PARAMETER, 0); }
		public TerminalNode NUMBER() { return getToken(MQLParser.NUMBER, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			_la = _input.LA(1);
			if ( !(((_la) & ~0x3f) == 0 && ((1L << _la) & 545357767376896L) != 0) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u00013\u012f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u0000:\b\u0000\u0001\u0000\u0003\u0000=\b\u0000\u0003"+
		"\u0000?\b\u0000\u0001\u0001\u0001\u0001\u0003\u0001C\b\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0003\u0001H\b\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001M\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001S\b\u0001\u0003\u0001U\b\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001[\b\u0001\u0003\u0001]\b\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"j\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004o\b\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0005\u0005t\b\u0005\n\u0005\f\u0005w\t"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006|\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0005\u0007\u0081\b\u0007\n\u0007\f\u0007"+
		"\u0084\t\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0005\t"+
		"\u008c\b\t\n\t\f\t\u008f\t\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0005\n\u0097\b\n\n\n\f\n\u009a\t\n\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u00a2\b\u000b\n\u000b\f\u000b"+
		"\u00a5\t\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0005\f"+
		"\u00ac\b\f\n\f\f\f\u00af\t\f\u0001\r\u0001\r\u0001\r\u0005\r\u00b4\b\r"+
		"\n\r\f\r\u00b7\t\r\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u00bc"+
		"\b\u000e\n\u000e\f\u000e\u00bf\t\u000e\u0001\u000f\u0005\u000f\u00c2\b"+
		"\u000f\n\u000f\f\u000f\u00c5\t\u000f\u0001\u000f\u0001\u000f\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00cd\b\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0003\u0010\u00d8\b\u0010\u0001\u0010\u0001\u0010"+
		"\u0003\u0010\u00dc\b\u0010\u0001\u0010\u0003\u0010\u00df\b\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0005\u0011\u00e4\b\u0011\n\u0011\f\u0011"+
		"\u00e7\t\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00ec\b"+
		"\u0012\n\u0012\f\u0012\u00ef\t\u0012\u0001\u0013\u0003\u0013\u00f2\b\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0003\u0014\u0101\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0003\u0015\u0106\b\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015"+
		"\u010b\b\u0015\n\u0015\f\u0015\u010e\t\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u0117"+
		"\b\u0016\n\u0016\f\u0016\u011a\t\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u011e\b\u0016\u0001\u0017\u0003\u0017\u0121\b\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0005\u0018\u0128\b\u0018\n"+
		"\u0018\f\u0018\u012b\t\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0000"+
		"\u0000\u001a\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*,.02\u0000\u0007\u0001\u0000\u0014\u0015"+
		"\u0001\u0000!\"\u0001\u0000&\'\u0001\u0000(*\u0002\u0000\u0002\u0002\b"+
		"\b\u0003\u0000\u000b\u000b((11\u0001\u0000,0\u0140\u0000>\u0001\u0000"+
		"\u0000\u0000\u0002@\u0001\u0000\u0000\u0000\u0004^\u0001\u0000\u0000\u0000"+
		"\u0006c\u0001\u0000\u0000\u0000\bk\u0001\u0000\u0000\u0000\np\u0001\u0000"+
		"\u0000\u0000\fx\u0001\u0000\u0000\u0000\u000e}\u0001\u0000\u0000\u0000"+
		"\u0010\u0085\u0001\u0000\u0000\u0000\u0012\u0087\u0001\u0000\u0000\u0000"+
		"\u0014\u0092\u0001\u0000\u0000\u0000\u0016\u009d\u0001\u0000\u0000\u0000"+
		"\u0018\u00a8\u0001\u0000\u0000\u0000\u001a\u00b0\u0001\u0000\u0000\u0000"+
		"\u001c\u00b8\u0001\u0000\u0000\u0000\u001e\u00c3\u0001\u0000\u0000\u0000"+
		" \u00c8\u0001\u0000\u0000\u0000\"\u00e0\u0001\u0000\u0000\u0000$\u00e8"+
		"\u0001\u0000\u0000\u0000&\u00f1\u0001\u0000\u0000\u0000(\u0100\u0001\u0000"+
		"\u0000\u0000*\u0102\u0001\u0000\u0000\u0000,\u0111\u0001\u0000\u0000\u0000"+
		".\u0120\u0001\u0000\u0000\u00000\u0124\u0001\u0000\u0000\u00002\u012c"+
		"\u0001\u0000\u0000\u00004?\u0005\u0001\u0000\u00005:\u0003\u0002\u0001"+
		"\u00006:\u0003\u0004\u0002\u00007:\u0003\u0006\u0003\u00008:\u0003\b\u0004"+
		"\u000095\u0001\u0000\u0000\u000096\u0001\u0000\u0000\u000097\u0001\u0000"+
		"\u0000\u000098\u0001\u0000\u0000\u0000:<\u0001\u0000\u0000\u0000;=\u0005"+
		"\u0001\u0000\u0000<;\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000"+
		"=?\u0001\u0000\u0000\u0000>4\u0001\u0000\u0000\u0000>9\u0001\u0000\u0000"+
		"\u0000?\u0001\u0001\u0000\u0000\u0000@B\u0005\r\u0000\u0000AC\u0005\u000e"+
		"\u0000\u0000BA\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CD\u0001"+
		"\u0000\u0000\u0000DG\u0003\n\u0005\u0000EF\u0005\u0010\u0000\u0000FH\u0003"+
		"\u0018\f\u0000GE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HL\u0001"+
		"\u0000\u0000\u0000IJ\u0005\u0011\u0000\u0000JK\u0005\u0013\u0000\u0000"+
		"KM\u0003\u000e\u0007\u0000LI\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000"+
		"\u0000MT\u0001\u0000\u0000\u0000NO\u0005\u0012\u0000\u0000OP\u0005\u0013"+
		"\u0000\u0000PR\u0003\u000e\u0007\u0000QS\u0007\u0000\u0000\u0000RQ\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SU\u0001\u0000\u0000\u0000"+
		"TN\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000U\\\u0001\u0000\u0000"+
		"\u0000VW\u0005\u0016\u0000\u0000WZ\u00050\u0000\u0000XY\u0005\u0017\u0000"+
		"\u0000Y[\u00050\u0000\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000"+
		"\u0000[]\u0001\u0000\u0000\u0000\\V\u0001\u0000\u0000\u0000\\]\u0001\u0000"+
		"\u0000\u0000]\u0003\u0001\u0000\u0000\u0000^_\u0005\u0018\u0000\u0000"+
		"_`\u0003\u0014\n\u0000`a\u0005\u001a\u0000\u0000ab\u0003\u0012\t\u0000"+
		"b\u0005\u0001\u0000\u0000\u0000cd\u0005\u0019\u0000\u0000de\u0003\u0014"+
		"\n\u0000ef\u0005\u001a\u0000\u0000fi\u0003\u0012\t\u0000gh\u0005\u0010"+
		"\u0000\u0000hj\u0003\u0018\f\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000"+
		"\u0000\u0000j\u0007\u0001\u0000\u0000\u0000kn\u0005\u001b\u0000\u0000"+
		"lm\u0005\u0010\u0000\u0000mo\u0003\u0018\f\u0000nl\u0001\u0000\u0000\u0000"+
		"no\u0001\u0000\u0000\u0000o\t\u0001\u0000\u0000\u0000pu\u0003\f\u0006"+
		"\u0000qr\u0005\u0002\u0000\u0000rt\u0003\f\u0006\u0000sq\u0001\u0000\u0000"+
		"\u0000tw\u0001\u0000\u0000\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000"+
		"\u0000\u0000v\u000b\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000"+
		"x{\u0003\u0018\f\u0000yz\u0005\u000f\u0000\u0000z|\u00051\u0000\u0000"+
		"{y\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|\r\u0001\u0000\u0000"+
		"\u0000}\u0082\u0003\u0010\b\u0000~\u007f\u0005\u0002\u0000\u0000\u007f"+
		"\u0081\u0003\u0010\b\u0000\u0080~\u0001\u0000\u0000\u0000\u0081\u0084"+
		"\u0001\u0000\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082\u0083"+
		"\u0001\u0000\u0000\u0000\u0083\u000f\u0001\u0000\u0000\u0000\u0084\u0082"+
		"\u0001\u0000\u0000\u0000\u0085\u0086\u0003,\u0016\u0000\u0086\u0011\u0001"+
		"\u0000\u0000\u0000\u0087\u0088\u0005\u0003\u0000\u0000\u0088\u008d\u0003"+
		"\u0018\f\u0000\u0089\u008a\u0005\u0002\u0000\u0000\u008a\u008c\u0003\u0018"+
		"\f\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008c\u008f\u0001\u0000\u0000"+
		"\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000"+
		"\u0000\u008e\u0090\u0001\u0000\u0000\u0000\u008f\u008d\u0001\u0000\u0000"+
		"\u0000\u0090\u0091\u0005\u0004\u0000\u0000\u0091\u0013\u0001\u0000\u0000"+
		"\u0000\u0092\u0093\u0005\u0003\u0000\u0000\u0093\u0098\u0003,\u0016\u0000"+
		"\u0094\u0095\u0005\u0002\u0000\u0000\u0095\u0097\u0003,\u0016\u0000\u0096"+
		"\u0094\u0001\u0000\u0000\u0000\u0097\u009a\u0001\u0000\u0000\u0000\u0098"+
		"\u0096\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099"+
		"\u009b\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009b"+
		"\u009c\u0005\u0004\u0000\u0000\u009c\u0015\u0001\u0000\u0000\u0000\u009d"+
		"\u009e\u0005\u0003\u0000\u0000\u009e\u00a3\u00032\u0019\u0000\u009f\u00a0"+
		"\u0005\u0002\u0000\u0000\u00a0\u00a2\u00032\u0019\u0000\u00a1\u009f\u0001"+
		"\u0000\u0000\u0000\u00a2\u00a5\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005"+
		"\u0004\u0000\u0000\u00a7\u0017\u0001\u0000\u0000\u0000\u00a8\u00ad\u0003"+
		"\u001a\r\u0000\u00a9\u00aa\u0005\u001c\u0000\u0000\u00aa\u00ac\u0003\u001a"+
		"\r\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000\u00ac\u00af\u0001\u0000\u0000"+
		"\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000"+
		"\u0000\u00ae\u0019\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000"+
		"\u0000\u00b0\u00b5\u0003\u001c\u000e\u0000\u00b1\u00b2\u0005\u001d\u0000"+
		"\u0000\u00b2\u00b4\u0003\u001c\u000e\u0000\u00b3\u00b1\u0001\u0000\u0000"+
		"\u0000\u00b4\u00b7\u0001\u0000\u0000\u0000\u00b5\u00b3\u0001\u0000\u0000"+
		"\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6\u001b\u0001\u0000\u0000"+
		"\u0000\u00b7\u00b5\u0001\u0000\u0000\u0000\u00b8\u00bd\u0003\u001e\u000f"+
		"\u0000\u00b9\u00ba\u0005\u001e\u0000\u0000\u00ba\u00bc\u0003\u001e\u000f"+
		"\u0000\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bc\u00bf\u0001\u0000\u0000"+
		"\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00bd\u00be\u0001\u0000\u0000"+
		"\u0000\u00be\u001d\u0001\u0000\u0000\u0000\u00bf\u00bd\u0001\u0000\u0000"+
		"\u0000\u00c0\u00c2\u0005 \u0000\u0000\u00c1\u00c0\u0001\u0000\u0000\u0000"+
		"\u00c2\u00c5\u0001\u0000\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000"+
		"\u00c3\u00c4\u0001\u0000\u0000\u0000\u00c4\u00c6\u0001\u0000\u0000\u0000"+
		"\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c6\u00c7\u0003 \u0010\u0000\u00c7"+
		"\u001f\u0001\u0000\u0000\u0000\u00c8\u00de\u0003\"\u0011\u0000\u00c9\u00ca"+
		"\u0005\u001f\u0000\u0000\u00ca\u00df\u0003\"\u0011\u0000\u00cb\u00cd\u0005"+
		" \u0000\u0000\u00cc\u00cb\u0001\u0000\u0000\u0000\u00cc\u00cd\u0001\u0000"+
		"\u0000\u0000\u00cd\u00d7\u0001\u0000\u0000\u0000\u00ce\u00cf\u0005#\u0000"+
		"\u0000\u00cf\u00d0\u0003\"\u0011\u0000\u00d0\u00d1\u0005\u001e\u0000\u0000"+
		"\u00d1\u00d2\u0003\"\u0011\u0000\u00d2\u00d8\u0001\u0000\u0000\u0000\u00d3"+
		"\u00d4\u0007\u0001\u0000\u0000\u00d4\u00d8\u0003\"\u0011\u0000\u00d5\u00d6"+
		"\u0005$\u0000\u0000\u00d6\u00d8\u0003\u0016\u000b\u0000\u00d7\u00ce\u0001"+
		"\u0000\u0000\u0000\u00d7\u00d3\u0001\u0000\u0000\u0000\u00d7\u00d5\u0001"+
		"\u0000\u0000\u0000\u00d8\u00df\u0001\u0000\u0000\u0000\u00d9\u00db\u0005"+
		"%\u0000\u0000\u00da\u00dc\u0005 \u0000\u0000\u00db\u00da\u0001\u0000\u0000"+
		"\u0000\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc\u00dd\u0001\u0000\u0000"+
		"\u0000\u00dd\u00df\u0005,\u0000\u0000\u00de\u00c9\u0001\u0000\u0000\u0000"+
		"\u00de\u00cc\u0001\u0000\u0000\u0000\u00de\u00d9\u0001\u0000\u0000\u0000"+
		"\u00de\u00df\u0001\u0000\u0000\u0000\u00df!\u0001\u0000\u0000\u0000\u00e0"+
		"\u00e5\u0003$\u0012\u0000\u00e1\u00e2\u0007\u0002\u0000\u0000\u00e2\u00e4"+
		"\u0003$\u0012\u0000\u00e3\u00e1\u0001\u0000\u0000\u0000\u00e4\u00e7\u0001"+
		"\u0000\u0000\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001"+
		"\u0000\u0000\u0000\u00e6#\u0001\u0000\u0000\u0000\u00e7\u00e5\u0001\u0000"+
		"\u0000\u0000\u00e8\u00ed\u0003&\u0013\u0000\u00e9\u00ea\u0007\u0003\u0000"+
		"\u0000\u00ea\u00ec\u0003&\u0013\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000"+
		"\u00ec\u00ef\u0001\u0000\u0000\u0000\u00ed\u00eb\u0001\u0000\u0000\u0000"+
		"\u00ed\u00ee\u0001\u0000\u0000\u0000\u00ee%\u0001\u0000\u0000\u0000\u00ef"+
		"\u00ed\u0001\u0000\u0000\u0000\u00f0\u00f2\u0007\u0002\u0000\u0000\u00f1"+
		"\u00f0\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2"+
		"\u00f3\u0001\u0000\u0000\u0000\u00f3\u00f4\u0003(\u0014\u0000\u00f4\'"+
		"\u0001\u0000\u0000\u0000\u00f5\u00f6\u0005\u0003\u0000\u0000\u00f6\u00f7"+
		"\u0003\u0018\f\u0000\u00f7\u00f8\u0005\u0004\u0000\u0000\u00f8\u0101\u0001"+
		"\u0000\u0000\u0000\u00f9\u00fa\u0005\u0005\u0000\u0000\u00fa\u00fb\u0003"+
		"\u0018\f\u0000\u00fb\u00fc\u0005\u0006\u0000\u0000\u00fc\u0101\u0001\u0000"+
		"\u0000\u0000\u00fd\u0101\u0003*\u0015\u0000\u00fe\u0101\u0003,\u0016\u0000"+
		"\u00ff\u0101\u00032\u0019\u0000\u0100\u00f5\u0001\u0000\u0000\u0000\u0100"+
		"\u00f9\u0001\u0000\u0000\u0000\u0100\u00fd\u0001\u0000\u0000\u0000\u0100"+
		"\u00fe\u0001\u0000\u0000\u0000\u0100\u00ff\u0001\u0000\u0000\u0000\u0101"+
		")\u0001\u0000\u0000\u0000\u0102\u0103\u0005+\u0000\u0000\u0103\u0105\u0005"+
		"\u0003\u0000\u0000\u0104\u0106\u0005\u000e\u0000\u0000\u0105\u0104\u0001"+
		"\u0000\u0000\u0000\u0105\u0106\u0001\u0000\u0000\u0000\u0106\u0107\u0001"+
		"\u0000\u0000\u0000\u0107\u010c\u0003\u0018\f\u0000\u0108\u0109\u0005\u0002"+
		"\u0000\u0000\u0109\u010b\u0003\u0018\f\u0000\u010a\u0108\u0001\u0000\u0000"+
		"\u0000\u010b\u010e\u0001\u0000\u0000\u0000\u010c\u010a\u0001\u0000\u0000"+
		"\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d\u010f\u0001\u0000\u0000"+
		"\u0000\u010e\u010c\u0001\u0000\u0000\u0000\u010f\u0110\u0005\u0004\u0000"+
		"\u0000\u0110+\u0001\u0000\u0000\u0000\u0111\u011d\u00030\u0018\u0000\u0112"+
		"\u0113\u0005\u0007\u0000\u0000\u0113\u0118\u0003.\u0017\u0000\u0114\u0115"+
		"\u0007\u0004\u0000\u0000\u0115\u0117\u0003.\u0017\u0000\u0116\u0114\u0001"+
		"\u0000\u0000\u0000\u0117\u011a\u0001\u0000\u0000\u0000\u0118\u0116\u0001"+
		"\u0000\u0000\u0000\u0118\u0119\u0001\u0000\u0000\u0000\u0119\u011b\u0001"+
		"\u0000\u0000\u0000\u011a\u0118\u0001\u0000\u0000\u0000\u011b\u011c\u0005"+
		"\t\u0000\u0000\u011c\u011e\u0001\u0000\u0000\u0000\u011d\u0112\u0001\u0000"+
		"\u0000\u0000\u011d\u011e\u0001\u0000\u0000\u0000\u011e-\u0001\u0000\u0000"+
		"\u0000\u011f\u0121\u0005\n\u0000\u0000\u0120\u011f\u0001\u0000\u0000\u0000"+
		"\u0120\u0121\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000\u0000"+
		"\u0122\u0123\u0003,\u0016\u0000\u0123/\u0001\u0000\u0000\u0000\u0124\u0129"+
		"\u0007\u0005\u0000\u0000\u0125\u0126\u0005\f\u0000\u0000\u0126\u0128\u0007"+
		"\u0005\u0000\u0000\u0127\u0125\u0001\u0000\u0000\u0000\u0128\u012b\u0001"+
		"\u0000\u0000\u0000\u0129\u0127\u0001\u0000\u0000\u0000\u0129\u012a\u0001"+
		"\u0000\u0000\u0000\u012a1\u0001\u0000\u0000\u0000\u012b\u0129\u0001\u0000"+
		"\u0000\u0000\u012c\u012d\u0007\u0006\u0000\u0000\u012d3\u0001\u0000\u0000"+
		"\u0000$9<>BGLRTZ\\inu{\u0082\u008d\u0098\u00a3\u00ad\u00b5\u00bd\u00c3"+
		"\u00cc\u00d7\u00db\u00de\u00e5\u00ed\u00f1\u0100\u0105\u010c\u0118\u011d"+
		"\u0120\u0129";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}