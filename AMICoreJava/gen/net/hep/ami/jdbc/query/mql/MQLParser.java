// Generated from /Users/jfulach/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/MQL.g4 by ANTLR 4.7.2
package net.hep.ami.jdbc.query.mql;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, SELECT=12, DISTINCT=13, AS=14, WHERE=15, GROUP=16, 
		ORDER=17, BY=18, ASC=19, DESC=20, LIMIT=21, OFFSET=22, INSERT=23, UPDATE=24, 
		VALUES=25, DELETE=26, OR=27, AND=28, COMP=29, NOT=30, IN=31, IS=32, PLUS=33, 
		MINUS=34, MUL=35, DIV=36, MOD=37, FUNCTION=38, NULL=39, CURRENT_TIMESTAMP=40, 
		STRING=41, PARAMETER=42, NUMBER=43, ID=44, COMMENT=45, WS=46;
	public static final int
		RULE_mqlQuery = 0, RULE_selectStatement = 1, RULE_insertStatement = 2, 
		RULE_updateStatement = 3, RULE_deleteStatement = 4, RULE_columnList = 5, 
		RULE_aColumn = 6, RULE_qIdList = 7, RULE_aQId = 8, RULE_expressionTuple = 9, 
		RULE_qIdTuple = 10, RULE_literalTuple = 11, RULE_expressionOr = 12, RULE_expressionAnd = 13, 
		RULE_expressionComp = 14, RULE_expressionNotAddSub = 15, RULE_expressionMulDiv = 16, 
		RULE_expressionPlusMinus = 17, RULE_expressionX = 18, RULE_qId = 19, RULE_constraintQId = 20, 
		RULE_basicQId = 21, RULE_literal = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"mqlQuery", "selectStatement", "insertStatement", "updateStatement", 
			"deleteStatement", "columnList", "aColumn", "qIdList", "aQId", "expressionTuple", 
			"qIdTuple", "literalTuple", "expressionOr", "expressionAnd", "expressionComp", 
			"expressionNotAddSub", "expressionMulDiv", "expressionPlusMinus", "expressionX", 
			"qId", "constraintQId", "basicQId", "literal"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'('", "')'", "'['", "']'", "'{'", "'}'", "'!'", 
			"'#'", "'.'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, "'+'", 
			"'-'", "'*'", "'/'", "'%'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"SELECT", "DISTINCT", "AS", "WHERE", "GROUP", "ORDER", "BY", "ASC", "DESC", 
			"LIMIT", "OFFSET", "INSERT", "UPDATE", "VALUES", "DELETE", "OR", "AND", 
			"COMP", "NOT", "IN", "IS", "PLUS", "MINUS", "MUL", "DIV", "MOD", "FUNCTION", 
			"NULL", "CURRENT_TIMESTAMP", "STRING", "PARAMETER", "NUMBER", "ID", "COMMENT", 
			"WS"
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
	public String getGrammarFileName() { return "MQL.g4"; }

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
			setState(56);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(46);
				match(T__0);
				}
				break;
			case SELECT:
			case INSERT:
			case UPDATE:
			case DELETE:
				enterOuterAlt(_localctx, 2);
				{
				setState(51);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SELECT:
					{
					setState(47);
					((MqlQueryContext)_localctx).m_select = selectStatement();
					}
					break;
				case INSERT:
					{
					setState(48);
					((MqlQueryContext)_localctx).m_insert = insertStatement();
					}
					break;
				case UPDATE:
					{
					setState(49);
					((MqlQueryContext)_localctx).m_update = updateStatement();
					}
					break;
				case DELETE:
					{
					setState(50);
					((MqlQueryContext)_localctx).m_delete = deleteStatement();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(53);
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
			setState(58);
			match(SELECT);
			setState(60);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISTINCT) {
				{
				setState(59);
				((SelectStatementContext)_localctx).m_distinct = match(DISTINCT);
				}
			}

			setState(62);
			((SelectStatementContext)_localctx).m_columns = columnList();
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(63);
				match(WHERE);
				setState(64);
				((SelectStatementContext)_localctx).m_expression = expressionOr();
				}
			}

			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP) {
				{
				setState(67);
				match(GROUP);
				setState(68);
				match(BY);
				setState(69);
				((SelectStatementContext)_localctx).m_groupBy = qIdList();
				}
			}

			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(72);
				match(ORDER);
				setState(73);
				match(BY);
				setState(74);
				((SelectStatementContext)_localctx).m_orderBy = qIdList();
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASC || _la==DESC) {
					{
					setState(75);
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

			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(80);
				match(LIMIT);
				setState(81);
				((SelectStatementContext)_localctx).m_limit = match(NUMBER);
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OFFSET) {
					{
					setState(82);
					match(OFFSET);
					setState(83);
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
			setState(88);
			match(INSERT);
			setState(89);
			((InsertStatementContext)_localctx).m_qIds = qIdTuple();
			setState(90);
			match(VALUES);
			setState(91);
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
			setState(93);
			match(UPDATE);
			setState(94);
			((UpdateStatementContext)_localctx).m_qIds = qIdTuple();
			setState(95);
			match(VALUES);
			setState(96);
			((UpdateStatementContext)_localctx).m_expressions = expressionTuple();
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(97);
				match(WHERE);
				setState(98);
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
			setState(101);
			match(DELETE);
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(102);
				match(WHERE);
				setState(103);
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
			setState(106);
			((ColumnListContext)_localctx).aColumn = aColumn();
			((ColumnListContext)_localctx).m_columns.add(((ColumnListContext)_localctx).aColumn);
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(107);
				match(T__1);
				setState(108);
				((ColumnListContext)_localctx).aColumn = aColumn();
				((ColumnListContext)_localctx).m_columns.add(((ColumnListContext)_localctx).aColumn);
				}
				}
				setState(113);
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
			setState(114);
			((AColumnContext)_localctx).m_expression = expressionOr();
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(115);
				match(AS);
				setState(116);
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
			setState(119);
			((QIdListContext)_localctx).aQId = aQId();
			((QIdListContext)_localctx).m_aQIds.add(((QIdListContext)_localctx).aQId);
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(120);
				match(T__1);
				setState(121);
				((QIdListContext)_localctx).aQId = aQId();
				((QIdListContext)_localctx).m_aQIds.add(((QIdListContext)_localctx).aQId);
				}
				}
				setState(126);
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
			setState(127);
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
			setState(129);
			match(T__2);
			setState(130);
			((ExpressionTupleContext)_localctx).expressionOr = expressionOr();
			((ExpressionTupleContext)_localctx).m_expressions.add(((ExpressionTupleContext)_localctx).expressionOr);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(131);
				match(T__1);
				setState(132);
				((ExpressionTupleContext)_localctx).expressionOr = expressionOr();
				((ExpressionTupleContext)_localctx).m_expressions.add(((ExpressionTupleContext)_localctx).expressionOr);
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138);
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
			setState(140);
			match(T__2);
			setState(141);
			((QIdTupleContext)_localctx).qId = qId();
			((QIdTupleContext)_localctx).m_qIds.add(((QIdTupleContext)_localctx).qId);
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(142);
				match(T__1);
				setState(143);
				((QIdTupleContext)_localctx).qId = qId();
				((QIdTupleContext)_localctx).m_qIds.add(((QIdTupleContext)_localctx).qId);
				}
				}
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(149);
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
			setState(151);
			match(T__2);
			setState(152);
			((LiteralTupleContext)_localctx).literal = literal();
			((LiteralTupleContext)_localctx).m_literals.add(((LiteralTupleContext)_localctx).literal);
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(153);
				match(T__1);
				setState(154);
				((LiteralTupleContext)_localctx).literal = literal();
				((LiteralTupleContext)_localctx).m_literals.add(((LiteralTupleContext)_localctx).literal);
				}
				}
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(160);
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

	public static class ExpressionOrContext extends ParserRuleContext {
		public List<ExpressionAndContext> expressionAnd() {
			return getRuleContexts(ExpressionAndContext.class);
		}
		public ExpressionAndContext expressionAnd(int i) {
			return getRuleContext(ExpressionAndContext.class,i);
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
			setState(162);
			expressionAnd();
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(163);
				match(OR);
				setState(164);
				expressionAnd();
				}
				}
				setState(169);
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

	public static class ExpressionAndContext extends ParserRuleContext {
		public List<ExpressionCompContext> expressionComp() {
			return getRuleContexts(ExpressionCompContext.class);
		}
		public ExpressionCompContext expressionComp(int i) {
			return getRuleContext(ExpressionCompContext.class,i);
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
		enterRule(_localctx, 26, RULE_expressionAnd);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			expressionComp();
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(171);
				match(AND);
				setState(172);
				expressionComp();
				}
				}
				setState(177);
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

	public static class ExpressionCompContext extends ParserRuleContext {
		public List<ExpressionNotAddSubContext> expressionNotAddSub() {
			return getRuleContexts(ExpressionNotAddSubContext.class);
		}
		public ExpressionNotAddSubContext expressionNotAddSub(int i) {
			return getRuleContext(ExpressionNotAddSubContext.class,i);
		}
		public TerminalNode COMP() { return getToken(MQLParser.COMP, 0); }
		public TerminalNode IN() { return getToken(MQLParser.IN, 0); }
		public LiteralTupleContext literalTuple() {
			return getRuleContext(LiteralTupleContext.class,0);
		}
		public TerminalNode IS() { return getToken(MQLParser.IS, 0); }
		public TerminalNode NULL() { return getToken(MQLParser.NULL, 0); }
		public TerminalNode NOT() { return getToken(MQLParser.NOT, 0); }
		public ExpressionCompContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionComp; }
	}

	public final ExpressionCompContext expressionComp() throws RecognitionException {
		ExpressionCompContext _localctx = new ExpressionCompContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_expressionComp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			expressionNotAddSub();
			setState(188);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMP:
				{
				setState(179);
				match(COMP);
				setState(180);
				expressionNotAddSub();
				}
				break;
			case IN:
				{
				setState(181);
				match(IN);
				setState(182);
				literalTuple();
				}
				break;
			case IS:
				{
				setState(183);
				match(IS);
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(184);
					match(NOT);
					}
				}

				setState(187);
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

	public static class ExpressionNotAddSubContext extends ParserRuleContext {
		public List<ExpressionMulDivContext> expressionMulDiv() {
			return getRuleContexts(ExpressionMulDivContext.class);
		}
		public ExpressionMulDivContext expressionMulDiv(int i) {
			return getRuleContext(ExpressionMulDivContext.class,i);
		}
		public List<TerminalNode> NOT() { return getTokens(MQLParser.NOT); }
		public TerminalNode NOT(int i) {
			return getToken(MQLParser.NOT, i);
		}
		public List<TerminalNode> PLUS() { return getTokens(MQLParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(MQLParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(MQLParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(MQLParser.MINUS, i);
		}
		public ExpressionNotAddSubContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionNotAddSub; }
	}

	public final ExpressionNotAddSubContext expressionNotAddSub() throws RecognitionException {
		ExpressionNotAddSubContext _localctx = new ExpressionNotAddSubContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expressionNotAddSub);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			expressionMulDiv();
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NOT) | (1L << PLUS) | (1L << MINUS))) != 0)) {
				{
				{
				setState(191);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NOT) | (1L << PLUS) | (1L << MINUS))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(192);
				expressionMulDiv();
				}
				}
				setState(197);
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
		enterRule(_localctx, 32, RULE_expressionMulDiv);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
			expressionPlusMinus();
			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) {
				{
				{
				setState(199);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(200);
				expressionPlusMinus();
				}
				}
				setState(205);
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
		enterRule(_localctx, 34, RULE_expressionPlusMinus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(206);
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

			setState(209);
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
	public static class ExpressionStdGroupContext extends ExpressionXContext {
		public ExpressionOrContext m_expression;
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public ExpressionStdGroupContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	public static class ExpressionFunctionContext extends ExpressionXContext {
		public Token m_functionName;
		public ExpressionTupleContext m_expressions;
		public TerminalNode FUNCTION() { return getToken(MQLParser.FUNCTION, 0); }
		public ExpressionTupleContext expressionTuple() {
			return getRuleContext(ExpressionTupleContext.class,0);
		}
		public ExpressionFunctionContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	public static class ExpressionIsoGroupContext extends ExpressionXContext {
		public ExpressionOrContext m_expression;
		public ExpressionOrContext expressionOr() {
			return getRuleContext(ExpressionOrContext.class,0);
		}
		public ExpressionIsoGroupContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	public static class ExpressionQIdContext extends ExpressionXContext {
		public QIdContext m_qId;
		public QIdContext qId() {
			return getRuleContext(QIdContext.class,0);
		}
		public ExpressionQIdContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}
	public static class ExpressionLiteralContext extends ExpressionXContext {
		public LiteralContext m_literal;
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public ExpressionLiteralContext(ExpressionXContext ctx) { copyFrom(ctx); }
	}

	public final ExpressionXContext expressionX() throws RecognitionException {
		ExpressionXContext _localctx = new ExpressionXContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_expressionX);
		try {
			setState(223);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				_localctx = new ExpressionStdGroupContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(211);
				match(T__2);
				setState(212);
				((ExpressionStdGroupContext)_localctx).m_expression = expressionOr();
				setState(213);
				match(T__3);
				}
				break;
			case T__4:
				_localctx = new ExpressionIsoGroupContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(215);
				match(T__4);
				setState(216);
				((ExpressionIsoGroupContext)_localctx).m_expression = expressionOr();
				setState(217);
				match(T__5);
				}
				break;
			case FUNCTION:
				_localctx = new ExpressionFunctionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(219);
				((ExpressionFunctionContext)_localctx).m_functionName = match(FUNCTION);
				setState(220);
				((ExpressionFunctionContext)_localctx).m_expressions = expressionTuple();
				}
				break;
			case T__9:
			case MUL:
			case ID:
				_localctx = new ExpressionQIdContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(221);
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
				setState(222);
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
		enterRule(_localctx, 38, RULE_qId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			((QIdContext)_localctx).m_basicQId = basicQId();
			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(226);
				match(T__6);
				setState(227);
				((QIdContext)_localctx).constraintQId = constraintQId();
				((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
				setState(232);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(228);
					match(T__1);
					setState(229);
					((QIdContext)_localctx).constraintQId = constraintQId();
					((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
					}
					}
					setState(234);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(235);
				match(T__7);
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
		enterRule(_localctx, 40, RULE_constraintQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(239);
				((ConstraintQIdContext)_localctx).m_op = match(T__8);
				}
			}

			setState(242);
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

	public static class BasicQIdContext extends ParserRuleContext {
		public Token ID;
		public List<Token> m_ids = new ArrayList<Token>();
		public Token MUL;
		public Token s10;
		public Token _tset636;
		public Token _tset649;
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
		enterRule(_localctx, 42, RULE_basicQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			((BasicQIdContext)_localctx)._tset636 = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << MUL) | (1L << ID))) != 0)) ) {
				((BasicQIdContext)_localctx)._tset636 = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset636);
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__10) {
				{
				{
				setState(245);
				match(T__10);
				setState(246);
				((BasicQIdContext)_localctx)._tset649 = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << MUL) | (1L << ID))) != 0)) ) {
					((BasicQIdContext)_localctx)._tset649 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset649);
				}
				}
				setState(251);
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
		enterRule(_localctx, 44, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NULL) | (1L << CURRENT_TIMESTAMP) | (1L << STRING) | (1L << PARAMETER) | (1L << NUMBER))) != 0)) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\60\u0101\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\3\2\3"+
		"\2\3\2\3\2\5\2\66\n\2\3\2\5\29\n\2\5\2;\n\2\3\3\3\3\5\3?\n\3\3\3\3\3\3"+
		"\3\5\3D\n\3\3\3\3\3\3\3\5\3I\n\3\3\3\3\3\3\3\3\3\5\3O\n\3\5\3Q\n\3\3\3"+
		"\3\3\3\3\3\3\5\3W\n\3\5\3Y\n\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\5\5f\n\5\3\6\3\6\3\6\5\6k\n\6\3\7\3\7\3\7\7\7p\n\7\f\7\16\7s\13\7"+
		"\3\b\3\b\3\b\5\bx\n\b\3\t\3\t\3\t\7\t}\n\t\f\t\16\t\u0080\13\t\3\n\3\n"+
		"\3\13\3\13\3\13\3\13\7\13\u0088\n\13\f\13\16\13\u008b\13\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\7\f\u0093\n\f\f\f\16\f\u0096\13\f\3\f\3\f\3\r\3\r\3\r"+
		"\3\r\7\r\u009e\n\r\f\r\16\r\u00a1\13\r\3\r\3\r\3\16\3\16\3\16\7\16\u00a8"+
		"\n\16\f\16\16\16\u00ab\13\16\3\17\3\17\3\17\7\17\u00b0\n\17\f\17\16\17"+
		"\u00b3\13\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00bc\n\20\3\20\5"+
		"\20\u00bf\n\20\3\21\3\21\3\21\7\21\u00c4\n\21\f\21\16\21\u00c7\13\21\3"+
		"\22\3\22\3\22\7\22\u00cc\n\22\f\22\16\22\u00cf\13\22\3\23\5\23\u00d2\n"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\5\24\u00e2\n\24\3\25\3\25\3\25\3\25\3\25\7\25\u00e9\n\25\f\25\16\25"+
		"\u00ec\13\25\3\25\3\25\5\25\u00f0\n\25\3\26\5\26\u00f3\n\26\3\26\3\26"+
		"\3\27\3\27\3\27\7\27\u00fa\n\27\f\27\16\27\u00fd\13\27\3\30\3\30\3\30"+
		"\2\2\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\b\3\2\25\26"+
		"\4\2  #$\3\2%\'\3\2#$\5\2\f\f%%..\3\2)-\2\u010e\2:\3\2\2\2\4<\3\2\2\2"+
		"\6Z\3\2\2\2\b_\3\2\2\2\ng\3\2\2\2\fl\3\2\2\2\16t\3\2\2\2\20y\3\2\2\2\22"+
		"\u0081\3\2\2\2\24\u0083\3\2\2\2\26\u008e\3\2\2\2\30\u0099\3\2\2\2\32\u00a4"+
		"\3\2\2\2\34\u00ac\3\2\2\2\36\u00b4\3\2\2\2 \u00c0\3\2\2\2\"\u00c8\3\2"+
		"\2\2$\u00d1\3\2\2\2&\u00e1\3\2\2\2(\u00e3\3\2\2\2*\u00f2\3\2\2\2,\u00f6"+
		"\3\2\2\2.\u00fe\3\2\2\2\60;\7\3\2\2\61\66\5\4\3\2\62\66\5\6\4\2\63\66"+
		"\5\b\5\2\64\66\5\n\6\2\65\61\3\2\2\2\65\62\3\2\2\2\65\63\3\2\2\2\65\64"+
		"\3\2\2\2\668\3\2\2\2\679\7\3\2\28\67\3\2\2\289\3\2\2\29;\3\2\2\2:\60\3"+
		"\2\2\2:\65\3\2\2\2;\3\3\2\2\2<>\7\16\2\2=?\7\17\2\2>=\3\2\2\2>?\3\2\2"+
		"\2?@\3\2\2\2@C\5\f\7\2AB\7\21\2\2BD\5\32\16\2CA\3\2\2\2CD\3\2\2\2DH\3"+
		"\2\2\2EF\7\22\2\2FG\7\24\2\2GI\5\20\t\2HE\3\2\2\2HI\3\2\2\2IP\3\2\2\2"+
		"JK\7\23\2\2KL\7\24\2\2LN\5\20\t\2MO\t\2\2\2NM\3\2\2\2NO\3\2\2\2OQ\3\2"+
		"\2\2PJ\3\2\2\2PQ\3\2\2\2QX\3\2\2\2RS\7\27\2\2SV\7-\2\2TU\7\30\2\2UW\7"+
		"-\2\2VT\3\2\2\2VW\3\2\2\2WY\3\2\2\2XR\3\2\2\2XY\3\2\2\2Y\5\3\2\2\2Z[\7"+
		"\31\2\2[\\\5\26\f\2\\]\7\33\2\2]^\5\24\13\2^\7\3\2\2\2_`\7\32\2\2`a\5"+
		"\26\f\2ab\7\33\2\2be\5\24\13\2cd\7\21\2\2df\5\32\16\2ec\3\2\2\2ef\3\2"+
		"\2\2f\t\3\2\2\2gj\7\34\2\2hi\7\21\2\2ik\5\32\16\2jh\3\2\2\2jk\3\2\2\2"+
		"k\13\3\2\2\2lq\5\16\b\2mn\7\4\2\2np\5\16\b\2om\3\2\2\2ps\3\2\2\2qo\3\2"+
		"\2\2qr\3\2\2\2r\r\3\2\2\2sq\3\2\2\2tw\5\32\16\2uv\7\20\2\2vx\7.\2\2wu"+
		"\3\2\2\2wx\3\2\2\2x\17\3\2\2\2y~\5\22\n\2z{\7\4\2\2{}\5\22\n\2|z\3\2\2"+
		"\2}\u0080\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177\21\3\2\2\2\u0080~\3\2\2\2"+
		"\u0081\u0082\5(\25\2\u0082\23\3\2\2\2\u0083\u0084\7\5\2\2\u0084\u0089"+
		"\5\32\16\2\u0085\u0086\7\4\2\2\u0086\u0088\5\32\16\2\u0087\u0085\3\2\2"+
		"\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008c"+
		"\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008d\7\6\2\2\u008d\25\3\2\2\2\u008e"+
		"\u008f\7\5\2\2\u008f\u0094\5(\25\2\u0090\u0091\7\4\2\2\u0091\u0093\5("+
		"\25\2\u0092\u0090\3\2\2\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2\2\2\u0094"+
		"\u0095\3\2\2\2\u0095\u0097\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u0098\7\6"+
		"\2\2\u0098\27\3\2\2\2\u0099\u009a\7\5\2\2\u009a\u009f\5.\30\2\u009b\u009c"+
		"\7\4\2\2\u009c\u009e\5.\30\2\u009d\u009b\3\2\2\2\u009e\u00a1\3\2\2\2\u009f"+
		"\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a2\3\2\2\2\u00a1\u009f\3\2"+
		"\2\2\u00a2\u00a3\7\6\2\2\u00a3\31\3\2\2\2\u00a4\u00a9\5\34\17\2\u00a5"+
		"\u00a6\7\35\2\2\u00a6\u00a8\5\34\17\2\u00a7\u00a5\3\2\2\2\u00a8\u00ab"+
		"\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\33\3\2\2\2\u00ab"+
		"\u00a9\3\2\2\2\u00ac\u00b1\5\36\20\2\u00ad\u00ae\7\36\2\2\u00ae\u00b0"+
		"\5\36\20\2\u00af\u00ad\3\2\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2"+
		"\u00b1\u00b2\3\2\2\2\u00b2\35\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00be"+
		"\5 \21\2\u00b5\u00b6\7\37\2\2\u00b6\u00bf\5 \21\2\u00b7\u00b8\7!\2\2\u00b8"+
		"\u00bf\5\30\r\2\u00b9\u00bb\7\"\2\2\u00ba\u00bc\7 \2\2\u00bb\u00ba\3\2"+
		"\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00bf\7)\2\2\u00be"+
		"\u00b5\3\2\2\2\u00be\u00b7\3\2\2\2\u00be\u00b9\3\2\2\2\u00be\u00bf\3\2"+
		"\2\2\u00bf\37\3\2\2\2\u00c0\u00c5\5\"\22\2\u00c1\u00c2\t\3\2\2\u00c2\u00c4"+
		"\5\"\22\2\u00c3\u00c1\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3\3\2\2\2"+
		"\u00c5\u00c6\3\2\2\2\u00c6!\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c8\u00cd\5"+
		"$\23\2\u00c9\u00ca\t\4\2\2\u00ca\u00cc\5$\23\2\u00cb\u00c9\3\2\2\2\u00cc"+
		"\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce#\3\2\2\2"+
		"\u00cf\u00cd\3\2\2\2\u00d0\u00d2\t\5\2\2\u00d1\u00d0\3\2\2\2\u00d1\u00d2"+
		"\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d4\5&\24\2\u00d4%\3\2\2\2\u00d5"+
		"\u00d6\7\5\2\2\u00d6\u00d7\5\32\16\2\u00d7\u00d8\7\6\2\2\u00d8\u00e2\3"+
		"\2\2\2\u00d9\u00da\7\7\2\2\u00da\u00db\5\32\16\2\u00db\u00dc\7\b\2\2\u00dc"+
		"\u00e2\3\2\2\2\u00dd\u00de\7(\2\2\u00de\u00e2\5\24\13\2\u00df\u00e2\5"+
		"(\25\2\u00e0\u00e2\5.\30\2\u00e1\u00d5\3\2\2\2\u00e1\u00d9\3\2\2\2\u00e1"+
		"\u00dd\3\2\2\2\u00e1\u00df\3\2\2\2\u00e1\u00e0\3\2\2\2\u00e2\'\3\2\2\2"+
		"\u00e3\u00ef\5,\27\2\u00e4\u00e5\7\t\2\2\u00e5\u00ea\5*\26\2\u00e6\u00e7"+
		"\7\4\2\2\u00e7\u00e9\5*\26\2\u00e8\u00e6\3\2\2\2\u00e9\u00ec\3\2\2\2\u00ea"+
		"\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ed\3\2\2\2\u00ec\u00ea\3\2"+
		"\2\2\u00ed\u00ee\7\n\2\2\u00ee\u00f0\3\2\2\2\u00ef\u00e4\3\2\2\2\u00ef"+
		"\u00f0\3\2\2\2\u00f0)\3\2\2\2\u00f1\u00f3\7\13\2\2\u00f2\u00f1\3\2\2\2"+
		"\u00f2\u00f3\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f5\5(\25\2\u00f5+\3"+
		"\2\2\2\u00f6\u00fb\t\6\2\2\u00f7\u00f8\7\r\2\2\u00f8\u00fa\t\6\2\2\u00f9"+
		"\u00f7\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2"+
		"\2\2\u00fc-\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fe\u00ff\t\7\2\2\u00ff/\3\2"+
		"\2\2 \658:>CHNPVXejqw~\u0089\u0094\u009f\u00a9\u00b1\u00bb\u00be\u00c5"+
		"\u00cd\u00d1\u00e1\u00ea\u00ef\u00f2\u00fb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}