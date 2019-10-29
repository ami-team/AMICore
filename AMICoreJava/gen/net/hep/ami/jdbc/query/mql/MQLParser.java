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
		VALUES=25, DELETE=26, OR=27, XOR=28, AND=29, COMP=30, NOT=31, LIKE=32, 
		REGEXP=33, BETWEEN=34, IN=35, IS=36, PLUS=37, MINUS=38, MUL=39, DIV=40, 
		MOD=41, FUNCTION=42, NULL=43, CURRENT_TIMESTAMP=44, STRING=45, PARAMETER=46, 
		NUMBER=47, ID=48, COMMENT=49, WS=50;
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
			null, "';'", "','", "'('", "')'", "'['", "']'", "'{'", "'}'", "'!'", 
			"'#'", "'.'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "'+'", "'-'", "'*'", "'/'", "'%'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"SELECT", "DISTINCT", "AS", "WHERE", "GROUP", "ORDER", "BY", "ASC", "DESC", 
			"LIMIT", "OFFSET", "INSERT", "UPDATE", "VALUES", "DELETE", "OR", "XOR", 
			"AND", "COMP", "NOT", "LIKE", "REGEXP", "BETWEEN", "IN", "IS", "PLUS", 
			"MINUS", "MUL", "DIV", "MOD", "FUNCTION", "NULL", "CURRENT_TIMESTAMP", 
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) {
				{
				{
				setState(233);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) ) {
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
		public FunctionContext m_function;
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
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
			case T__9:
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
				while (_la==T__1) {
					{
					{
					setState(276);
					match(T__1);
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
		enterRule(_localctx, 46, RULE_constraintQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(287);
				((ConstraintQIdContext)_localctx).m_op = match(T__8);
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

	public static class BasicQIdContext extends ParserRuleContext {
		public Token ID;
		public List<Token> m_ids = new ArrayList<Token>();
		public Token s39;
		public Token s10;
		public Token _tset725;
		public Token _tset738;
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
			((BasicQIdContext)_localctx)._tset725 = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << MUL) | (1L << ID))) != 0)) ) {
				((BasicQIdContext)_localctx)._tset725 = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset725);
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__10) {
				{
				{
				setState(293);
				match(T__10);
				setState(294);
				((BasicQIdContext)_localctx)._tset738 = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << MUL) | (1L << ID))) != 0)) ) {
					((BasicQIdContext)_localctx)._tset738 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset738);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u0131\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\3\2\3\2\3\2\3\2\5\2<\n\2\3\2\5\2?\n\2\5\2A\n"+
		"\2\3\3\3\3\5\3E\n\3\3\3\3\3\3\3\5\3J\n\3\3\3\3\3\3\3\5\3O\n\3\3\3\3\3"+
		"\3\3\3\3\5\3U\n\3\5\3W\n\3\3\3\3\3\3\3\3\3\5\3]\n\3\5\3_\n\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5l\n\5\3\6\3\6\3\6\5\6q\n\6\3\7\3"+
		"\7\3\7\7\7v\n\7\f\7\16\7y\13\7\3\b\3\b\3\b\5\b~\n\b\3\t\3\t\3\t\7\t\u0083"+
		"\n\t\f\t\16\t\u0086\13\t\3\n\3\n\3\13\3\13\3\13\3\13\7\13\u008e\n\13\f"+
		"\13\16\13\u0091\13\13\3\13\3\13\3\f\3\f\3\f\3\f\7\f\u0099\n\f\f\f\16\f"+
		"\u009c\13\f\3\f\3\f\3\r\3\r\3\r\3\r\7\r\u00a4\n\r\f\r\16\r\u00a7\13\r"+
		"\3\r\3\r\3\16\3\16\3\16\7\16\u00ae\n\16\f\16\16\16\u00b1\13\16\3\17\3"+
		"\17\3\17\7\17\u00b6\n\17\f\17\16\17\u00b9\13\17\3\20\3\20\3\20\7\20\u00be"+
		"\n\20\f\20\16\20\u00c1\13\20\3\21\7\21\u00c4\n\21\f\21\16\21\u00c7\13"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\5\22\u00cf\n\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u00da\n\22\3\22\3\22\5\22\u00de\n\22\3"+
		"\22\5\22\u00e1\n\22\3\23\3\23\3\23\7\23\u00e6\n\23\f\23\16\23\u00e9\13"+
		"\23\3\24\3\24\3\24\7\24\u00ee\n\24\f\24\16\24\u00f1\13\24\3\25\5\25\u00f4"+
		"\n\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\5\26\u0103\n\26\3\27\3\27\3\27\5\27\u0108\n\27\3\27\3\27\3\27\7\27\u010d"+
		"\n\27\f\27\16\27\u0110\13\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\7\30\u0119"+
		"\n\30\f\30\16\30\u011c\13\30\3\30\3\30\5\30\u0120\n\30\3\31\5\31\u0123"+
		"\n\31\3\31\3\31\3\32\3\32\3\32\7\32\u012a\n\32\f\32\16\32\u012d\13\32"+
		"\3\33\3\33\3\33\2\2\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,"+
		".\60\62\64\2\b\3\2\25\26\3\2\"#\3\2\'(\3\2)+\5\2\f\f))\62\62\3\2-\61\2"+
		"\u0142\2@\3\2\2\2\4B\3\2\2\2\6`\3\2\2\2\be\3\2\2\2\nm\3\2\2\2\fr\3\2\2"+
		"\2\16z\3\2\2\2\20\177\3\2\2\2\22\u0087\3\2\2\2\24\u0089\3\2\2\2\26\u0094"+
		"\3\2\2\2\30\u009f\3\2\2\2\32\u00aa\3\2\2\2\34\u00b2\3\2\2\2\36\u00ba\3"+
		"\2\2\2 \u00c5\3\2\2\2\"\u00ca\3\2\2\2$\u00e2\3\2\2\2&\u00ea\3\2\2\2(\u00f3"+
		"\3\2\2\2*\u0102\3\2\2\2,\u0104\3\2\2\2.\u0113\3\2\2\2\60\u0122\3\2\2\2"+
		"\62\u0126\3\2\2\2\64\u012e\3\2\2\2\66A\7\3\2\2\67<\5\4\3\28<\5\6\4\29"+
		"<\5\b\5\2:<\5\n\6\2;\67\3\2\2\2;8\3\2\2\2;9\3\2\2\2;:\3\2\2\2<>\3\2\2"+
		"\2=?\7\3\2\2>=\3\2\2\2>?\3\2\2\2?A\3\2\2\2@\66\3\2\2\2@;\3\2\2\2A\3\3"+
		"\2\2\2BD\7\16\2\2CE\7\17\2\2DC\3\2\2\2DE\3\2\2\2EF\3\2\2\2FI\5\f\7\2G"+
		"H\7\21\2\2HJ\5\32\16\2IG\3\2\2\2IJ\3\2\2\2JN\3\2\2\2KL\7\22\2\2LM\7\24"+
		"\2\2MO\5\20\t\2NK\3\2\2\2NO\3\2\2\2OV\3\2\2\2PQ\7\23\2\2QR\7\24\2\2RT"+
		"\5\20\t\2SU\t\2\2\2TS\3\2\2\2TU\3\2\2\2UW\3\2\2\2VP\3\2\2\2VW\3\2\2\2"+
		"W^\3\2\2\2XY\7\27\2\2Y\\\7\61\2\2Z[\7\30\2\2[]\7\61\2\2\\Z\3\2\2\2\\]"+
		"\3\2\2\2]_\3\2\2\2^X\3\2\2\2^_\3\2\2\2_\5\3\2\2\2`a\7\31\2\2ab\5\26\f"+
		"\2bc\7\33\2\2cd\5\24\13\2d\7\3\2\2\2ef\7\32\2\2fg\5\26\f\2gh\7\33\2\2"+
		"hk\5\24\13\2ij\7\21\2\2jl\5\32\16\2ki\3\2\2\2kl\3\2\2\2l\t\3\2\2\2mp\7"+
		"\34\2\2no\7\21\2\2oq\5\32\16\2pn\3\2\2\2pq\3\2\2\2q\13\3\2\2\2rw\5\16"+
		"\b\2st\7\4\2\2tv\5\16\b\2us\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x\r\3"+
		"\2\2\2yw\3\2\2\2z}\5\32\16\2{|\7\20\2\2|~\7\62\2\2}{\3\2\2\2}~\3\2\2\2"+
		"~\17\3\2\2\2\177\u0084\5\22\n\2\u0080\u0081\7\4\2\2\u0081\u0083\5\22\n"+
		"\2\u0082\u0080\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0082\3\2\2\2\u0084\u0085"+
		"\3\2\2\2\u0085\21\3\2\2\2\u0086\u0084\3\2\2\2\u0087\u0088\5.\30\2\u0088"+
		"\23\3\2\2\2\u0089\u008a\7\5\2\2\u008a\u008f\5\32\16\2\u008b\u008c\7\4"+
		"\2\2\u008c\u008e\5\32\16\2\u008d\u008b\3\2\2\2\u008e\u0091\3\2\2\2\u008f"+
		"\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0092\3\2\2\2\u0091\u008f\3\2"+
		"\2\2\u0092\u0093\7\6\2\2\u0093\25\3\2\2\2\u0094\u0095\7\5\2\2\u0095\u009a"+
		"\5.\30\2\u0096\u0097\7\4\2\2\u0097\u0099\5.\30\2\u0098\u0096\3\2\2\2\u0099"+
		"\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3\2"+
		"\2\2\u009c\u009a\3\2\2\2\u009d\u009e\7\6\2\2\u009e\27\3\2\2\2\u009f\u00a0"+
		"\7\5\2\2\u00a0\u00a5\5\64\33\2\u00a1\u00a2\7\4\2\2\u00a2\u00a4\5\64\33"+
		"\2\u00a3\u00a1\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6"+
		"\3\2\2\2\u00a6\u00a8\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a8\u00a9\7\6\2\2\u00a9"+
		"\31\3\2\2\2\u00aa\u00af\5\34\17\2\u00ab\u00ac\7\35\2\2\u00ac\u00ae\5\34"+
		"\17\2\u00ad\u00ab\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00b0\33\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b7\5\36\20"+
		"\2\u00b3\u00b4\7\36\2\2\u00b4\u00b6\5\36\20\2\u00b5\u00b3\3\2\2\2\u00b6"+
		"\u00b9\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\35\3\2\2"+
		"\2\u00b9\u00b7\3\2\2\2\u00ba\u00bf\5 \21\2\u00bb\u00bc\7\37\2\2\u00bc"+
		"\u00be\5 \21\2\u00bd\u00bb\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2"+
		"\2\2\u00bf\u00c0\3\2\2\2\u00c0\37\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00c4"+
		"\7!\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c5"+
		"\u00c6\3\2\2\2\u00c6\u00c8\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c8\u00c9\5\""+
		"\22\2\u00c9!\3\2\2\2\u00ca\u00e0\5$\23\2\u00cb\u00cc\7 \2\2\u00cc\u00e1"+
		"\5$\23\2\u00cd\u00cf\7!\2\2\u00ce\u00cd\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf"+
		"\u00d9\3\2\2\2\u00d0\u00d1\7$\2\2\u00d1\u00d2\5$\23\2\u00d2\u00d3\7\37"+
		"\2\2\u00d3\u00d4\5$\23\2\u00d4\u00da\3\2\2\2\u00d5\u00d6\t\3\2\2\u00d6"+
		"\u00da\5$\23\2\u00d7\u00d8\7%\2\2\u00d8\u00da\5\30\r\2\u00d9\u00d0\3\2"+
		"\2\2\u00d9\u00d5\3\2\2\2\u00d9\u00d7\3\2\2\2\u00da\u00e1\3\2\2\2\u00db"+
		"\u00dd\7&\2\2\u00dc\u00de\7!\2\2\u00dd\u00dc\3\2\2\2\u00dd\u00de\3\2\2"+
		"\2\u00de\u00df\3\2\2\2\u00df\u00e1\7-\2\2\u00e0\u00cb\3\2\2\2\u00e0\u00ce"+
		"\3\2\2\2\u00e0\u00db\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1#\3\2\2\2\u00e2"+
		"\u00e7\5&\24\2\u00e3\u00e4\t\4\2\2\u00e4\u00e6\5&\24\2\u00e5\u00e3\3\2"+
		"\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8"+
		"%\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00ef\5(\25\2\u00eb\u00ec\t\5\2\2"+
		"\u00ec\u00ee\5(\25\2\u00ed\u00eb\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed"+
		"\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\'\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2"+
		"\u00f4\t\4\2\2\u00f3\u00f2\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f5\3\2"+
		"\2\2\u00f5\u00f6\5*\26\2\u00f6)\3\2\2\2\u00f7\u00f8\7\5\2\2\u00f8\u00f9"+
		"\5\32\16\2\u00f9\u00fa\7\6\2\2\u00fa\u0103\3\2\2\2\u00fb\u00fc\7\7\2\2"+
		"\u00fc\u00fd\5\32\16\2\u00fd\u00fe\7\b\2\2\u00fe\u0103\3\2\2\2\u00ff\u0103"+
		"\5,\27\2\u0100\u0103\5.\30\2\u0101\u0103\5\64\33\2\u0102\u00f7\3\2\2\2"+
		"\u0102\u00fb\3\2\2\2\u0102\u00ff\3\2\2\2\u0102\u0100\3\2\2\2\u0102\u0101"+
		"\3\2\2\2\u0103+\3\2\2\2\u0104\u0105\7,\2\2\u0105\u0107\7\5\2\2\u0106\u0108"+
		"\7\17\2\2\u0107\u0106\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u0109\3\2\2\2"+
		"\u0109\u010e\5\32\16\2\u010a\u010b\7\4\2\2\u010b\u010d\5\32\16\2\u010c"+
		"\u010a\3\2\2\2\u010d\u0110\3\2\2\2\u010e\u010c\3\2\2\2\u010e\u010f\3\2"+
		"\2\2\u010f\u0111\3\2\2\2\u0110\u010e\3\2\2\2\u0111\u0112\7\6\2\2\u0112"+
		"-\3\2\2\2\u0113\u011f\5\62\32\2\u0114\u0115\7\t\2\2\u0115\u011a\5\60\31"+
		"\2\u0116\u0117\7\4\2\2\u0117\u0119\5\60\31\2\u0118\u0116\3\2\2\2\u0119"+
		"\u011c\3\2\2\2\u011a\u0118\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u011d\3\2"+
		"\2\2\u011c\u011a\3\2\2\2\u011d\u011e\7\n\2\2\u011e\u0120\3\2\2\2\u011f"+
		"\u0114\3\2\2\2\u011f\u0120\3\2\2\2\u0120/\3\2\2\2\u0121\u0123\7\13\2\2"+
		"\u0122\u0121\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0124\3\2\2\2\u0124\u0125"+
		"\5.\30\2\u0125\61\3\2\2\2\u0126\u012b\t\6\2\2\u0127\u0128\7\r\2\2\u0128"+
		"\u012a\t\6\2\2\u0129\u0127\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2"+
		"\2\2\u012b\u012c\3\2\2\2\u012c\63\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u012f"+
		"\t\7\2\2\u012f\65\3\2\2\2&;>@DINTV\\^kpw}\u0084\u008f\u009a\u00a5\u00af"+
		"\u00b7\u00bf\u00c5\u00ce\u00d9\u00dd\u00e0\u00e7\u00ef\u00f3\u0102\u0107"+
		"\u010e\u011a\u011f\u0122\u012b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}