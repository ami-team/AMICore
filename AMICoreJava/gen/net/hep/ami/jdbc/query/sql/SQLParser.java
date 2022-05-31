// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/sql/SQL.g4 by ANTLR 4.10.1
package net.hep.ami.jdbc.query.sql;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACES=1, SPECIAL=2, STRING=3, PARAMETER=4, OTHERS=5;
	public static final int
		RULE_query = 0, RULE_token = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"query", "token"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "SPACES", "SPECIAL", "STRING", "PARAMETER", "OTHERS"
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
	public String getGrammarFileName() { return "SQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class QueryContext extends ParserRuleContext {
		public List<String> tokens;
		public TokenContext token;
		public List<TokenContext> token() {
			return getRuleContexts(TokenContext.class);
		}
		public TokenContext token(int i) {
			return getRuleContext(TokenContext.class,i);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_query);
		 ((QueryContext)_localctx).tokens =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(9);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SPACES) | (1L << SPECIAL) | (1L << STRING) | (1L << PARAMETER) | (1L << OTHERS))) != 0)) {
				{
				{
				setState(4);
				((QueryContext)_localctx).token = token();
				 _localctx.tokens.add(((QueryContext)_localctx).token.v); 
				}
				}
				setState(11);
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

	public static class TokenContext extends ParserRuleContext {
		public String v;
		public Token SPACES;
		public Token SPECIAL;
		public Token STRING;
		public Token PARAMETER;
		public Token OTHERS;
		public TerminalNode SPACES() { return getToken(SQLParser.SPACES, 0); }
		public TerminalNode SPECIAL() { return getToken(SQLParser.SPECIAL, 0); }
		public TerminalNode STRING() { return getToken(SQLParser.STRING, 0); }
		public TerminalNode PARAMETER() { return getToken(SQLParser.PARAMETER, 0); }
		public TerminalNode OTHERS() { return getToken(SQLParser.OTHERS, 0); }
		public TokenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_token; }
	}

	public final TokenContext token() throws RecognitionException {
		TokenContext _localctx = new TokenContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_token);
		try {
			setState(22);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SPACES:
				enterOuterAlt(_localctx, 1);
				{
				setState(12);
				((TokenContext)_localctx).SPACES = match(SPACES);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).SPACES.getText(); 
				}
				break;
			case SPECIAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(14);
				((TokenContext)_localctx).SPECIAL = match(SPECIAL);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).SPECIAL.getText(); 
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(16);
				((TokenContext)_localctx).STRING = match(STRING);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).STRING.getText(); 
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 4);
				{
				setState(18);
				((TokenContext)_localctx).PARAMETER = match(PARAMETER);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).PARAMETER.getText(); 
				}
				break;
			case OTHERS:
				enterOuterAlt(_localctx, 5);
				{
				setState(20);
				((TokenContext)_localctx).OTHERS = match(OTHERS);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).OTHERS.getText(); 
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

	public static final String _serializedATN =
		"\u0004\u0001\u0005\u0019\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\b\b\u0000\n\u0000\f\u0000"+
		"\u000b\t\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001"+
		"\u0017\b\u0001\u0001\u0001\u0000\u0000\u0002\u0000\u0002\u0000\u0000\u001b"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004"+
		"\u0005\u0003\u0002\u0001\u0000\u0005\u0006\u0006\u0000\uffff\uffff\u0000"+
		"\u0006\b\u0001\u0000\u0000\u0000\u0007\u0004\u0001\u0000\u0000\u0000\b"+
		"\u000b\u0001\u0000\u0000\u0000\t\u0007\u0001\u0000\u0000\u0000\t\n\u0001"+
		"\u0000\u0000\u0000\n\u0001\u0001\u0000\u0000\u0000\u000b\t\u0001\u0000"+
		"\u0000\u0000\f\r\u0005\u0001\u0000\u0000\r\u0017\u0006\u0001\uffff\uffff"+
		"\u0000\u000e\u000f\u0005\u0002\u0000\u0000\u000f\u0017\u0006\u0001\uffff"+
		"\uffff\u0000\u0010\u0011\u0005\u0003\u0000\u0000\u0011\u0017\u0006\u0001"+
		"\uffff\uffff\u0000\u0012\u0013\u0005\u0004\u0000\u0000\u0013\u0017\u0006"+
		"\u0001\uffff\uffff\u0000\u0014\u0015\u0005\u0005\u0000\u0000\u0015\u0017"+
		"\u0006\u0001\uffff\uffff\u0000\u0016\f\u0001\u0000\u0000\u0000\u0016\u000e"+
		"\u0001\u0000\u0000\u0000\u0016\u0010\u0001\u0000\u0000\u0000\u0016\u0012"+
		"\u0001\u0000\u0000\u0000\u0016\u0014\u0001\u0000\u0000\u0000\u0017\u0003"+
		"\u0001\u0000\u0000\u0000\u0002\t\u0016";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}