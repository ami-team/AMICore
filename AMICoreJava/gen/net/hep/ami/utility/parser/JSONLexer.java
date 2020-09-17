// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/JSON.g4 by ANTLR 4.8
package net.hep.ami.utility.parser;

	import java.util.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JSONLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		STRING=10, NUMBER=11, WS=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"STRING", "NUMBER", "WS", "ESC", "HEX", "INT", "EXP"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "','", "'['", "']'", "':'", "'true'", "'false'", 
			"'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "STRING", 
			"NUMBER", "WS"
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



		private static class Pair
		{
			final String x;
			final Object y;

			private Pair(String _x, Object _y)
			{
				x = _x;
				y = _y;
			}
		}

		public boolean simpleQuotes = false;


	public JSONLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "JSON.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16\u008a\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13C\n\13\f\13\16"+
		"\13F\13\13\3\13\3\13\3\13\3\13\7\13L\n\13\f\13\16\13O\13\13\3\13\5\13"+
		"R\n\13\3\f\5\fU\n\f\3\f\3\f\3\f\6\fZ\n\f\r\f\16\f[\3\f\5\f_\n\f\3\f\5"+
		"\fb\n\f\3\f\3\f\3\f\3\f\5\fh\n\f\3\f\5\fk\n\f\3\r\6\rn\n\r\r\r\16\ro\3"+
		"\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16|\n\16\3\17\3\17\3"+
		"\20\6\20\u0081\n\20\r\20\16\20\u0082\3\21\3\21\5\21\u0087\n\21\3\21\3"+
		"\21\2\2\22\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\2\35\2\37\2!\2\3\2\n\6\2\f\f\17\17$$^^\6\2\f\f\17\17))^^\5\2\13\f\17"+
		"\17\"\"\13\2$$))\61\61^^ddhhppttvv\5\2\62;CHch\3\2\62;\4\2GGgg\4\2--/"+
		"/\2\u0095\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\3#\3\2\2\2\5%\3\2\2\2\7\'\3\2\2\2\t)\3\2\2\2\13"+
		"+\3\2\2\2\r-\3\2\2\2\17/\3\2\2\2\21\64\3\2\2\2\23:\3\2\2\2\25Q\3\2\2\2"+
		"\27j\3\2\2\2\31m\3\2\2\2\33s\3\2\2\2\35}\3\2\2\2\37\u0080\3\2\2\2!\u0084"+
		"\3\2\2\2#$\7}\2\2$\4\3\2\2\2%&\7\177\2\2&\6\3\2\2\2\'(\7.\2\2(\b\3\2\2"+
		"\2)*\7]\2\2*\n\3\2\2\2+,\7_\2\2,\f\3\2\2\2-.\7<\2\2.\16\3\2\2\2/\60\7"+
		"v\2\2\60\61\7t\2\2\61\62\7w\2\2\62\63\7g\2\2\63\20\3\2\2\2\64\65\7h\2"+
		"\2\65\66\7c\2\2\66\67\7n\2\2\678\7u\2\289\7g\2\29\22\3\2\2\2:;\7p\2\2"+
		";<\7w\2\2<=\7n\2\2=>\7n\2\2>\24\3\2\2\2?D\7$\2\2@C\5\33\16\2AC\n\2\2\2"+
		"B@\3\2\2\2BA\3\2\2\2CF\3\2\2\2DB\3\2\2\2DE\3\2\2\2EG\3\2\2\2FD\3\2\2\2"+
		"GR\7$\2\2HM\7)\2\2IL\5\33\16\2JL\n\3\2\2KI\3\2\2\2KJ\3\2\2\2LO\3\2\2\2"+
		"MK\3\2\2\2MN\3\2\2\2NP\3\2\2\2OM\3\2\2\2PR\7)\2\2Q?\3\2\2\2QH\3\2\2\2"+
		"R\26\3\2\2\2SU\7/\2\2TS\3\2\2\2TU\3\2\2\2UV\3\2\2\2VW\5\37\20\2WY\7\60"+
		"\2\2XZ\5\37\20\2YX\3\2\2\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2]"+
		"_\5!\21\2^]\3\2\2\2^_\3\2\2\2_k\3\2\2\2`b\7/\2\2a`\3\2\2\2ab\3\2\2\2b"+
		"c\3\2\2\2cd\5\37\20\2de\5!\21\2ek\3\2\2\2fh\7/\2\2gf\3\2\2\2gh\3\2\2\2"+
		"hi\3\2\2\2ik\5\37\20\2jT\3\2\2\2ja\3\2\2\2jg\3\2\2\2k\30\3\2\2\2ln\t\4"+
		"\2\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2\2pq\3\2\2\2qr\b\r\2\2r\32\3"+
		"\2\2\2s{\7^\2\2t|\t\5\2\2uv\7w\2\2vw\5\35\17\2wx\5\35\17\2xy\5\35\17\2"+
		"yz\5\35\17\2z|\3\2\2\2{t\3\2\2\2{u\3\2\2\2|\34\3\2\2\2}~\t\6\2\2~\36\3"+
		"\2\2\2\177\u0081\t\7\2\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0080"+
		"\3\2\2\2\u0082\u0083\3\2\2\2\u0083 \3\2\2\2\u0084\u0086\t\b\2\2\u0085"+
		"\u0087\t\t\2\2\u0086\u0085\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088\3\2"+
		"\2\2\u0088\u0089\5\37\20\2\u0089\"\3\2\2\2\22\2BDKMQT[^agjo{\u0082\u0086"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}