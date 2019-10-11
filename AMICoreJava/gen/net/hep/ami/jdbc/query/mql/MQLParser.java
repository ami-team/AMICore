// Generated from /home/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/MQL.g4 by ANTLR 4.7.2
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
		RULE_expressionX = 20, RULE_qId = 21, RULE_constraintQId = 22, RULE_basicQId = 23, 
		RULE_literal = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"mqlQuery", "selectStatement", "insertStatement", "updateStatement", 
			"deleteStatement", "columnList", "aColumn", "qIdList", "aQId", "expressionTuple", 
			"qIdTuple", "literalTuple", "expressionOr", "expressionXor", "expressionAnd", 
			"expressionNot", "expressionComp", "expressionAddSub", "expressionMulDiv", 
			"expressionPlusMinus", "expressionX", "qId", "constraintQId", "basicQId", 
			"literal"
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
			setState(60);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(50);
				match(T__0);
				}
				break;
			case SELECT:
			case INSERT:
			case UPDATE:
			case DELETE:
				enterOuterAlt(_localctx, 2);
				{
				setState(55);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SELECT:
					{
					setState(51);
					((MqlQueryContext)_localctx).m_select = selectStatement();
					}
					break;
				case INSERT:
					{
					setState(52);
					((MqlQueryContext)_localctx).m_insert = insertStatement();
					}
					break;
				case UPDATE:
					{
					setState(53);
					((MqlQueryContext)_localctx).m_update = updateStatement();
					}
					break;
				case DELETE:
					{
					setState(54);
					((MqlQueryContext)_localctx).m_delete = deleteStatement();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(57);
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
			setState(62);
			match(SELECT);
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DISTINCT) {
				{
				setState(63);
				((SelectStatementContext)_localctx).m_distinct = match(DISTINCT);
				}
			}

			setState(66);
			((SelectStatementContext)_localctx).m_columns = columnList();
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(67);
				match(WHERE);
				setState(68);
				((SelectStatementContext)_localctx).m_expression = expressionOr();
				}
			}

			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP) {
				{
				setState(71);
				match(GROUP);
				setState(72);
				match(BY);
				setState(73);
				((SelectStatementContext)_localctx).m_groupBy = qIdList();
				}
			}

			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(76);
				match(ORDER);
				setState(77);
				match(BY);
				setState(78);
				((SelectStatementContext)_localctx).m_orderBy = qIdList();
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASC || _la==DESC) {
					{
					setState(79);
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

			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(84);
				match(LIMIT);
				setState(85);
				((SelectStatementContext)_localctx).m_limit = match(NUMBER);
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OFFSET) {
					{
					setState(86);
					match(OFFSET);
					setState(87);
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
			setState(92);
			match(INSERT);
			setState(93);
			((InsertStatementContext)_localctx).m_qIds = qIdTuple();
			setState(94);
			match(VALUES);
			setState(95);
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
			setState(97);
			match(UPDATE);
			setState(98);
			((UpdateStatementContext)_localctx).m_qIds = qIdTuple();
			setState(99);
			match(VALUES);
			setState(100);
			((UpdateStatementContext)_localctx).m_expressions = expressionTuple();
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(101);
				match(WHERE);
				setState(102);
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
			setState(105);
			match(DELETE);
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(106);
				match(WHERE);
				setState(107);
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
			setState(110);
			((ColumnListContext)_localctx).aColumn = aColumn();
			((ColumnListContext)_localctx).m_columns.add(((ColumnListContext)_localctx).aColumn);
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(111);
				match(T__1);
				setState(112);
				((ColumnListContext)_localctx).aColumn = aColumn();
				((ColumnListContext)_localctx).m_columns.add(((ColumnListContext)_localctx).aColumn);
				}
				}
				setState(117);
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
			setState(118);
			((AColumnContext)_localctx).m_expression = expressionOr();
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(119);
				match(AS);
				setState(120);
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
			setState(123);
			((QIdListContext)_localctx).aQId = aQId();
			((QIdListContext)_localctx).m_aQIds.add(((QIdListContext)_localctx).aQId);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(124);
				match(T__1);
				setState(125);
				((QIdListContext)_localctx).aQId = aQId();
				((QIdListContext)_localctx).m_aQIds.add(((QIdListContext)_localctx).aQId);
				}
				}
				setState(130);
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
			setState(131);
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
			setState(133);
			match(T__2);
			setState(134);
			((ExpressionTupleContext)_localctx).expressionOr = expressionOr();
			((ExpressionTupleContext)_localctx).m_expressions.add(((ExpressionTupleContext)_localctx).expressionOr);
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(135);
				match(T__1);
				setState(136);
				((ExpressionTupleContext)_localctx).expressionOr = expressionOr();
				((ExpressionTupleContext)_localctx).m_expressions.add(((ExpressionTupleContext)_localctx).expressionOr);
				}
				}
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(142);
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
			setState(144);
			match(T__2);
			setState(145);
			((QIdTupleContext)_localctx).qId = qId();
			((QIdTupleContext)_localctx).m_qIds.add(((QIdTupleContext)_localctx).qId);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(146);
				match(T__1);
				setState(147);
				((QIdTupleContext)_localctx).qId = qId();
				((QIdTupleContext)_localctx).m_qIds.add(((QIdTupleContext)_localctx).qId);
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(153);
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
			setState(155);
			match(T__2);
			setState(156);
			((LiteralTupleContext)_localctx).literal = literal();
			((LiteralTupleContext)_localctx).m_literals.add(((LiteralTupleContext)_localctx).literal);
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(157);
				match(T__1);
				setState(158);
				((LiteralTupleContext)_localctx).literal = literal();
				((LiteralTupleContext)_localctx).m_literals.add(((LiteralTupleContext)_localctx).literal);
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
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
			setState(166);
			expressionXor();
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(167);
				match(OR);
				setState(168);
				expressionXor();
				}
				}
				setState(173);
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
			setState(174);
			expressionAnd();
			setState(179);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==XOR) {
				{
				{
				setState(175);
				match(XOR);
				setState(176);
				expressionAnd();
				}
				}
				setState(181);
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
			setState(182);
			expressionNot();
			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(183);
				match(AND);
				setState(184);
				expressionNot();
				}
				}
				setState(189);
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
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NOT) {
				{
				{
				setState(190);
				match(NOT);
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(196);
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
			setState(198);
			expressionAddSub();
			setState(220);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMP:
				{
				setState(199);
				match(COMP);
				setState(200);
				expressionAddSub();
				}
				break;
			case NOT:
			case LIKE:
			case REGEXP:
			case BETWEEN:
			case IN:
				{
				setState(202);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(201);
					match(NOT);
					}
				}

				setState(213);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case BETWEEN:
					{
					setState(204);
					match(BETWEEN);
					setState(205);
					expressionAddSub();
					setState(206);
					match(AND);
					setState(207);
					expressionAddSub();
					}
					break;
				case LIKE:
				case REGEXP:
					{
					setState(209);
					_la = _input.LA(1);
					if ( !(_la==LIKE || _la==REGEXP) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(210);
					expressionAddSub();
					}
					break;
				case IN:
					{
					setState(211);
					match(IN);
					setState(212);
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
				setState(215);
				match(IS);
				setState(217);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(216);
					match(NOT);
					}
				}

				setState(219);
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
			setState(222);
			expressionMulDiv();
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				{
				setState(223);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(224);
				expressionMulDiv();
				}
				}
				setState(229);
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
			setState(230);
			expressionPlusMinus();
			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) {
				{
				{
				setState(231);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(232);
				expressionPlusMinus();
				}
				}
				setState(237);
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
			setState(239);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(238);
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

			setState(241);
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
		enterRule(_localctx, 40, RULE_expressionX);
		try {
			setState(255);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				_localctx = new ExpressionStdGroupContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(243);
				match(T__2);
				setState(244);
				((ExpressionStdGroupContext)_localctx).m_expression = expressionOr();
				setState(245);
				match(T__3);
				}
				break;
			case T__4:
				_localctx = new ExpressionIsoGroupContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(247);
				match(T__4);
				setState(248);
				((ExpressionIsoGroupContext)_localctx).m_expression = expressionOr();
				setState(249);
				match(T__5);
				}
				break;
			case FUNCTION:
				_localctx = new ExpressionFunctionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(251);
				((ExpressionFunctionContext)_localctx).m_functionName = match(FUNCTION);
				setState(252);
				((ExpressionFunctionContext)_localctx).m_expressions = expressionTuple();
				}
				break;
			case T__9:
			case MUL:
			case ID:
				_localctx = new ExpressionQIdContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(253);
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
				setState(254);
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
		enterRule(_localctx, 42, RULE_qId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			((QIdContext)_localctx).m_basicQId = basicQId();
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(258);
				match(T__6);
				setState(259);
				((QIdContext)_localctx).constraintQId = constraintQId();
				((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
				setState(264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(260);
					match(T__1);
					setState(261);
					((QIdContext)_localctx).constraintQId = constraintQId();
					((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
					}
					}
					setState(266);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(267);
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
		enterRule(_localctx, 44, RULE_constraintQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(271);
				((ConstraintQIdContext)_localctx).m_op = match(T__8);
				}
			}

			setState(274);
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
		public Token _tset689;
		public Token _tset702;
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
		enterRule(_localctx, 46, RULE_basicQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(276);
			((BasicQIdContext)_localctx)._tset689 = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << MUL) | (1L << ID))) != 0)) ) {
				((BasicQIdContext)_localctx)._tset689 = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset689);
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__10) {
				{
				{
				setState(277);
				match(T__10);
				setState(278);
				((BasicQIdContext)_localctx)._tset702 = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << MUL) | (1L << ID))) != 0)) ) {
					((BasicQIdContext)_localctx)._tset702 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset702);
				}
				}
				setState(283);
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
		enterRule(_localctx, 48, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u0121\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\3\2\3\2\3\2\3\2\5\2:\n\2\3\2\5\2=\n\2\5\2?\n\2\3\3\3\3"+
		"\5\3C\n\3\3\3\3\3\3\3\5\3H\n\3\3\3\3\3\3\3\5\3M\n\3\3\3\3\3\3\3\3\3\5"+
		"\3S\n\3\5\3U\n\3\3\3\3\3\3\3\3\3\5\3[\n\3\5\3]\n\3\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\5\5j\n\5\3\6\3\6\3\6\5\6o\n\6\3\7\3\7\3\7\7\7"+
		"t\n\7\f\7\16\7w\13\7\3\b\3\b\3\b\5\b|\n\b\3\t\3\t\3\t\7\t\u0081\n\t\f"+
		"\t\16\t\u0084\13\t\3\n\3\n\3\13\3\13\3\13\3\13\7\13\u008c\n\13\f\13\16"+
		"\13\u008f\13\13\3\13\3\13\3\f\3\f\3\f\3\f\7\f\u0097\n\f\f\f\16\f\u009a"+
		"\13\f\3\f\3\f\3\r\3\r\3\r\3\r\7\r\u00a2\n\r\f\r\16\r\u00a5\13\r\3\r\3"+
		"\r\3\16\3\16\3\16\7\16\u00ac\n\16\f\16\16\16\u00af\13\16\3\17\3\17\3\17"+
		"\7\17\u00b4\n\17\f\17\16\17\u00b7\13\17\3\20\3\20\3\20\7\20\u00bc\n\20"+
		"\f\20\16\20\u00bf\13\20\3\21\7\21\u00c2\n\21\f\21\16\21\u00c5\13\21\3"+
		"\21\3\21\3\22\3\22\3\22\3\22\5\22\u00cd\n\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\5\22\u00d8\n\22\3\22\3\22\5\22\u00dc\n\22\3\22\5"+
		"\22\u00df\n\22\3\23\3\23\3\23\7\23\u00e4\n\23\f\23\16\23\u00e7\13\23\3"+
		"\24\3\24\3\24\7\24\u00ec\n\24\f\24\16\24\u00ef\13\24\3\25\5\25\u00f2\n"+
		"\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3"+
		"\26\5\26\u0102\n\26\3\27\3\27\3\27\3\27\3\27\7\27\u0109\n\27\f\27\16\27"+
		"\u010c\13\27\3\27\3\27\5\27\u0110\n\27\3\30\5\30\u0113\n\30\3\30\3\30"+
		"\3\31\3\31\3\31\7\31\u011a\n\31\f\31\16\31\u011d\13\31\3\32\3\32\3\32"+
		"\2\2\33\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\b\3\2"+
		"\25\26\3\2\"#\3\2\'(\3\2)+\5\2\f\f))\62\62\3\2-\61\2\u0131\2>\3\2\2\2"+
		"\4@\3\2\2\2\6^\3\2\2\2\bc\3\2\2\2\nk\3\2\2\2\fp\3\2\2\2\16x\3\2\2\2\20"+
		"}\3\2\2\2\22\u0085\3\2\2\2\24\u0087\3\2\2\2\26\u0092\3\2\2\2\30\u009d"+
		"\3\2\2\2\32\u00a8\3\2\2\2\34\u00b0\3\2\2\2\36\u00b8\3\2\2\2 \u00c3\3\2"+
		"\2\2\"\u00c8\3\2\2\2$\u00e0\3\2\2\2&\u00e8\3\2\2\2(\u00f1\3\2\2\2*\u0101"+
		"\3\2\2\2,\u0103\3\2\2\2.\u0112\3\2\2\2\60\u0116\3\2\2\2\62\u011e\3\2\2"+
		"\2\64?\7\3\2\2\65:\5\4\3\2\66:\5\6\4\2\67:\5\b\5\28:\5\n\6\29\65\3\2\2"+
		"\29\66\3\2\2\29\67\3\2\2\298\3\2\2\2:<\3\2\2\2;=\7\3\2\2<;\3\2\2\2<=\3"+
		"\2\2\2=?\3\2\2\2>\64\3\2\2\2>9\3\2\2\2?\3\3\2\2\2@B\7\16\2\2AC\7\17\2"+
		"\2BA\3\2\2\2BC\3\2\2\2CD\3\2\2\2DG\5\f\7\2EF\7\21\2\2FH\5\32\16\2GE\3"+
		"\2\2\2GH\3\2\2\2HL\3\2\2\2IJ\7\22\2\2JK\7\24\2\2KM\5\20\t\2LI\3\2\2\2"+
		"LM\3\2\2\2MT\3\2\2\2NO\7\23\2\2OP\7\24\2\2PR\5\20\t\2QS\t\2\2\2RQ\3\2"+
		"\2\2RS\3\2\2\2SU\3\2\2\2TN\3\2\2\2TU\3\2\2\2U\\\3\2\2\2VW\7\27\2\2WZ\7"+
		"\61\2\2XY\7\30\2\2Y[\7\61\2\2ZX\3\2\2\2Z[\3\2\2\2[]\3\2\2\2\\V\3\2\2\2"+
		"\\]\3\2\2\2]\5\3\2\2\2^_\7\31\2\2_`\5\26\f\2`a\7\33\2\2ab\5\24\13\2b\7"+
		"\3\2\2\2cd\7\32\2\2de\5\26\f\2ef\7\33\2\2fi\5\24\13\2gh\7\21\2\2hj\5\32"+
		"\16\2ig\3\2\2\2ij\3\2\2\2j\t\3\2\2\2kn\7\34\2\2lm\7\21\2\2mo\5\32\16\2"+
		"nl\3\2\2\2no\3\2\2\2o\13\3\2\2\2pu\5\16\b\2qr\7\4\2\2rt\5\16\b\2sq\3\2"+
		"\2\2tw\3\2\2\2us\3\2\2\2uv\3\2\2\2v\r\3\2\2\2wu\3\2\2\2x{\5\32\16\2yz"+
		"\7\20\2\2z|\7\62\2\2{y\3\2\2\2{|\3\2\2\2|\17\3\2\2\2}\u0082\5\22\n\2~"+
		"\177\7\4\2\2\177\u0081\5\22\n\2\u0080~\3\2\2\2\u0081\u0084\3\2\2\2\u0082"+
		"\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\21\3\2\2\2\u0084\u0082\3\2\2"+
		"\2\u0085\u0086\5,\27\2\u0086\23\3\2\2\2\u0087\u0088\7\5\2\2\u0088\u008d"+
		"\5\32\16\2\u0089\u008a\7\4\2\2\u008a\u008c\5\32\16\2\u008b\u0089\3\2\2"+
		"\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0090"+
		"\3\2\2\2\u008f\u008d\3\2\2\2\u0090\u0091\7\6\2\2\u0091\25\3\2\2\2\u0092"+
		"\u0093\7\5\2\2\u0093\u0098\5,\27\2\u0094\u0095\7\4\2\2\u0095\u0097\5,"+
		"\27\2\u0096\u0094\3\2\2\2\u0097\u009a\3\2\2\2\u0098\u0096\3\2\2\2\u0098"+
		"\u0099\3\2\2\2\u0099\u009b\3\2\2\2\u009a\u0098\3\2\2\2\u009b\u009c\7\6"+
		"\2\2\u009c\27\3\2\2\2\u009d\u009e\7\5\2\2\u009e\u00a3\5\62\32\2\u009f"+
		"\u00a0\7\4\2\2\u00a0\u00a2\5\62\32\2\u00a1\u009f\3\2\2\2\u00a2\u00a5\3"+
		"\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5"+
		"\u00a3\3\2\2\2\u00a6\u00a7\7\6\2\2\u00a7\31\3\2\2\2\u00a8\u00ad\5\34\17"+
		"\2\u00a9\u00aa\7\35\2\2\u00aa\u00ac\5\34\17\2\u00ab\u00a9\3\2\2\2\u00ac"+
		"\u00af\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\33\3\2\2"+
		"\2\u00af\u00ad\3\2\2\2\u00b0\u00b5\5\36\20\2\u00b1\u00b2\7\36\2\2\u00b2"+
		"\u00b4\5\36\20\2\u00b3\u00b1\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5\u00b3\3"+
		"\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\35\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b8"+
		"\u00bd\5 \21\2\u00b9\u00ba\7\37\2\2\u00ba\u00bc\5 \21\2\u00bb\u00b9\3"+
		"\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be"+
		"\37\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0\u00c2\7!\2\2\u00c1\u00c0\3\2\2\2"+
		"\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c6"+
		"\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c7\5\"\22\2\u00c7!\3\2\2\2\u00c8"+
		"\u00de\5$\23\2\u00c9\u00ca\7 \2\2\u00ca\u00df\5$\23\2\u00cb\u00cd\7!\2"+
		"\2\u00cc\u00cb\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00d7\3\2\2\2\u00ce\u00cf"+
		"\7$\2\2\u00cf\u00d0\5$\23\2\u00d0\u00d1\7\37\2\2\u00d1\u00d2\5$\23\2\u00d2"+
		"\u00d8\3\2\2\2\u00d3\u00d4\t\3\2\2\u00d4\u00d8\5$\23\2\u00d5\u00d6\7%"+
		"\2\2\u00d6\u00d8\5\30\r\2\u00d7\u00ce\3\2\2\2\u00d7\u00d3\3\2\2\2\u00d7"+
		"\u00d5\3\2\2\2\u00d8\u00df\3\2\2\2\u00d9\u00db\7&\2\2\u00da\u00dc\7!\2"+
		"\2\u00db\u00da\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00df"+
		"\7-\2\2\u00de\u00c9\3\2\2\2\u00de\u00cc\3\2\2\2\u00de\u00d9\3\2\2\2\u00de"+
		"\u00df\3\2\2\2\u00df#\3\2\2\2\u00e0\u00e5\5&\24\2\u00e1\u00e2\t\4\2\2"+
		"\u00e2\u00e4\5&\24\2\u00e3\u00e1\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3"+
		"\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6%\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8"+
		"\u00ed\5(\25\2\u00e9\u00ea\t\5\2\2\u00ea\u00ec\5(\25\2\u00eb\u00e9\3\2"+
		"\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee"+
		"\'\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0\u00f2\t\4\2\2\u00f1\u00f0\3\2\2\2"+
		"\u00f1\u00f2\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f4\5*\26\2\u00f4)\3"+
		"\2\2\2\u00f5\u00f6\7\5\2\2\u00f6\u00f7\5\32\16\2\u00f7\u00f8\7\6\2\2\u00f8"+
		"\u0102\3\2\2\2\u00f9\u00fa\7\7\2\2\u00fa\u00fb\5\32\16\2\u00fb\u00fc\7"+
		"\b\2\2\u00fc\u0102\3\2\2\2\u00fd\u00fe\7,\2\2\u00fe\u0102\5\24\13\2\u00ff"+
		"\u0102\5,\27\2\u0100\u0102\5\62\32\2\u0101\u00f5\3\2\2\2\u0101\u00f9\3"+
		"\2\2\2\u0101\u00fd\3\2\2\2\u0101\u00ff\3\2\2\2\u0101\u0100\3\2\2\2\u0102"+
		"+\3\2\2\2\u0103\u010f\5\60\31\2\u0104\u0105\7\t\2\2\u0105\u010a\5.\30"+
		"\2\u0106\u0107\7\4\2\2\u0107\u0109\5.\30\2\u0108\u0106\3\2\2\2\u0109\u010c"+
		"\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010d\3\2\2\2\u010c"+
		"\u010a\3\2\2\2\u010d\u010e\7\n\2\2\u010e\u0110\3\2\2\2\u010f\u0104\3\2"+
		"\2\2\u010f\u0110\3\2\2\2\u0110-\3\2\2\2\u0111\u0113\7\13\2\2\u0112\u0111"+
		"\3\2\2\2\u0112\u0113\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\5,\27\2\u0115"+
		"/\3\2\2\2\u0116\u011b\t\6\2\2\u0117\u0118\7\r\2\2\u0118\u011a\t\6\2\2"+
		"\u0119\u0117\3\2\2\2\u011a\u011d\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c"+
		"\3\2\2\2\u011c\61\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u011f\t\7\2\2\u011f"+
		"\63\3\2\2\2$9<>BGLRTZ\\inu{\u0082\u008d\u0098\u00a3\u00ad\u00b5\u00bd"+
		"\u00c3\u00cc\u00d7\u00db\u00de\u00e5\u00ed\u00f1\u0101\u010a\u010f\u0112"+
		"\u011b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}