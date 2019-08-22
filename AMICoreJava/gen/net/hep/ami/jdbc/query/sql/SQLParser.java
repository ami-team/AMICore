// Generated from /Users/jfulach/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/sql/SQL.g4 by ANTLR 4.7.2
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
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACES=1, STRING=2, PARAMETER=3, OTHERS=4;
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
			null, "SPACES", "STRING", "PARAMETER", "OTHERS"
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SPACES) | (1L << STRING) | (1L << PARAMETER) | (1L << OTHERS))) != 0)) {
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
		public Token STRING;
		public Token PARAMETER;
		public Token OTHERS;
		public TerminalNode SPACES() { return getToken(SQLParser.SPACES, 0); }
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
			setState(20);
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
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(14);
				((TokenContext)_localctx).STRING = match(STRING);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).STRING.getText(); 
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 3);
				{
				setState(16);
				((TokenContext)_localctx).PARAMETER = match(PARAMETER);
				 ((TokenContext)_localctx).v =  ((TokenContext)_localctx).PARAMETER.getText(); 
				}
				break;
			case OTHERS:
				enterOuterAlt(_localctx, 4);
				{
				setState(18);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\6\31\4\2\t\2\4\3"+
		"\t\3\3\2\3\2\3\2\7\2\n\n\2\f\2\16\2\r\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\5\3\27\n\3\3\3\2\2\4\2\4\2\2\2\32\2\13\3\2\2\2\4\26\3\2\2\2\6\7\5"+
		"\4\3\2\7\b\b\2\1\2\b\n\3\2\2\2\t\6\3\2\2\2\n\r\3\2\2\2\13\t\3\2\2\2\13"+
		"\f\3\2\2\2\f\3\3\2\2\2\r\13\3\2\2\2\16\17\7\3\2\2\17\27\b\3\1\2\20\21"+
		"\7\4\2\2\21\27\b\3\1\2\22\23\7\5\2\2\23\27\b\3\1\2\24\25\7\6\2\2\25\27"+
		"\b\3\1\2\26\16\3\2\2\2\26\20\3\2\2\2\26\22\3\2\2\2\26\24\3\2\2\2\27\5"+
		"\3\2\2\2\4\13\26";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}