// Generated from /home/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/mql/QId.g4 by ANTLR 4.7.2
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
public class QIdParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, ID=8, WS=9;
	public static final int
		RULE_qId = 0, RULE_constraintQId = 1, RULE_basicQId = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"qId", "constraintQId", "basicQId"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "','", "'}'", "'!'", "'*'", "'#'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "ID", "WS"
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
	public String getGrammarFileName() { return "QId.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QIdParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
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
		enterRule(_localctx, 0, RULE_qId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			((QIdContext)_localctx).m_basicQId = basicQId();
			setState(18);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(7);
				match(T__0);
				setState(8);
				((QIdContext)_localctx).constraintQId = constraintQId();
				((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
				setState(13);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(9);
					match(T__1);
					setState(10);
					((QIdContext)_localctx).constraintQId = constraintQId();
					((QIdContext)_localctx).m_constraintQIds.add(((QIdContext)_localctx).constraintQId);
					}
					}
					setState(15);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(16);
				match(T__2);
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
		enterRule(_localctx, 2, RULE_constraintQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(20);
				((ConstraintQIdContext)_localctx).m_op = match(T__3);
				}
			}

			setState(23);
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
		public Token s5;
		public Token s6;
		public Token _tset81;
		public Token _tset94;
		public List<TerminalNode> ID() { return getTokens(QIdParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(QIdParser.ID, i);
		}
		public BasicQIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicQId; }
	}

	public final BasicQIdContext basicQId() throws RecognitionException {
		BasicQIdContext _localctx = new BasicQIdContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_basicQId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			((BasicQIdContext)_localctx)._tset81 = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << ID))) != 0)) ) {
				((BasicQIdContext)_localctx)._tset81 = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset81);
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(26);
				match(T__6);
				setState(27);
				((BasicQIdContext)_localctx)._tset94 = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << ID))) != 0)) ) {
					((BasicQIdContext)_localctx)._tset94 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				((BasicQIdContext)_localctx).m_ids.add(((BasicQIdContext)_localctx)._tset94);
				}
				}
				setState(32);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13$\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\2\7\2\16\n\2\f\2\16\2\21\13\2\3\2\3\2\5\2"+
		"\25\n\2\3\3\5\3\30\n\3\3\3\3\3\3\4\3\4\3\4\7\4\37\n\4\f\4\16\4\"\13\4"+
		"\3\4\2\2\5\2\4\6\2\3\4\2\7\b\n\n\2$\2\b\3\2\2\2\4\27\3\2\2\2\6\33\3\2"+
		"\2\2\b\24\5\6\4\2\t\n\7\3\2\2\n\17\5\4\3\2\13\f\7\4\2\2\f\16\5\4\3\2\r"+
		"\13\3\2\2\2\16\21\3\2\2\2\17\r\3\2\2\2\17\20\3\2\2\2\20\22\3\2\2\2\21"+
		"\17\3\2\2\2\22\23\7\5\2\2\23\25\3\2\2\2\24\t\3\2\2\2\24\25\3\2\2\2\25"+
		"\3\3\2\2\2\26\30\7\6\2\2\27\26\3\2\2\2\27\30\3\2\2\2\30\31\3\2\2\2\31"+
		"\32\5\2\2\2\32\5\3\2\2\2\33 \t\2\2\2\34\35\7\t\2\2\35\37\t\2\2\2\36\34"+
		"\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!\7\3\2\2\2\" \3\2\2\2\6\17"+
		"\24\27 ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}