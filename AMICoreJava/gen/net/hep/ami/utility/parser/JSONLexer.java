// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/JSON.g4 by ANTLR 4.10.1
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
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

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
		"\u0004\u0000\f\u0088\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t"+
		"\u0005\tA\b\t\n\t\f\tD\t\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\tJ\b"+
		"\t\n\t\f\tM\t\t\u0001\t\u0003\tP\b\t\u0001\n\u0003\nS\b\n\u0001\n\u0001"+
		"\n\u0001\n\u0004\nX\b\n\u000b\n\f\nY\u0001\n\u0003\n]\b\n\u0001\n\u0003"+
		"\n`\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\nf\b\n\u0001\n\u0003\ni"+
		"\b\n\u0001\u000b\u0004\u000bl\b\u000b\u000b\u000b\f\u000bm\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0003\fz\b\f\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u007f\b\u000e"+
		"\u000b\u000e\f\u000e\u0080\u0001\u000f\u0001\u000f\u0003\u000f\u0085\b"+
		"\u000f\u0001\u000f\u0001\u000f\u0000\u0000\u0010\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013"+
		"\n\u0015\u000b\u0017\f\u0019\u0000\u001b\u0000\u001d\u0000\u001f\u0000"+
		"\u0001\u0000\b\u0004\u0000\n\n\r\r\"\"\\\\\u0004\u0000\n\n\r\r\'\'\\\\"+
		"\u0003\u0000\t\n\r\r  \t\u0000\"\"\'\'//\\\\bbffnnrrtt\u0003\u000009A"+
		"Faf\u0001\u000009\u0002\u0000EEee\u0002\u0000++--\u0093\u0000\u0001\u0001"+
		"\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001"+
		"\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000"+
		"\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000"+
		"\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0001!\u0001\u0000\u0000\u0000"+
		"\u0003#\u0001\u0000\u0000\u0000\u0005%\u0001\u0000\u0000\u0000\u0007\'"+
		"\u0001\u0000\u0000\u0000\t)\u0001\u0000\u0000\u0000\u000b+\u0001\u0000"+
		"\u0000\u0000\r-\u0001\u0000\u0000\u0000\u000f2\u0001\u0000\u0000\u0000"+
		"\u00118\u0001\u0000\u0000\u0000\u0013O\u0001\u0000\u0000\u0000\u0015h"+
		"\u0001\u0000\u0000\u0000\u0017k\u0001\u0000\u0000\u0000\u0019q\u0001\u0000"+
		"\u0000\u0000\u001b{\u0001\u0000\u0000\u0000\u001d~\u0001\u0000\u0000\u0000"+
		"\u001f\u0082\u0001\u0000\u0000\u0000!\"\u0005{\u0000\u0000\"\u0002\u0001"+
		"\u0000\u0000\u0000#$\u0005}\u0000\u0000$\u0004\u0001\u0000\u0000\u0000"+
		"%&\u0005,\u0000\u0000&\u0006\u0001\u0000\u0000\u0000\'(\u0005[\u0000\u0000"+
		"(\b\u0001\u0000\u0000\u0000)*\u0005]\u0000\u0000*\n\u0001\u0000\u0000"+
		"\u0000+,\u0005:\u0000\u0000,\f\u0001\u0000\u0000\u0000-.\u0005t\u0000"+
		"\u0000./\u0005r\u0000\u0000/0\u0005u\u0000\u000001\u0005e\u0000\u0000"+
		"1\u000e\u0001\u0000\u0000\u000023\u0005f\u0000\u000034\u0005a\u0000\u0000"+
		"45\u0005l\u0000\u000056\u0005s\u0000\u000067\u0005e\u0000\u00007\u0010"+
		"\u0001\u0000\u0000\u000089\u0005n\u0000\u00009:\u0005u\u0000\u0000:;\u0005"+
		"l\u0000\u0000;<\u0005l\u0000\u0000<\u0012\u0001\u0000\u0000\u0000=B\u0005"+
		"\"\u0000\u0000>A\u0003\u0019\f\u0000?A\b\u0000\u0000\u0000@>\u0001\u0000"+
		"\u0000\u0000@?\u0001\u0000\u0000\u0000AD\u0001\u0000\u0000\u0000B@\u0001"+
		"\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CE\u0001\u0000\u0000\u0000"+
		"DB\u0001\u0000\u0000\u0000EP\u0005\"\u0000\u0000FK\u0005\'\u0000\u0000"+
		"GJ\u0003\u0019\f\u0000HJ\b\u0001\u0000\u0000IG\u0001\u0000\u0000\u0000"+
		"IH\u0001\u0000\u0000\u0000JM\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000"+
		"\u0000KL\u0001\u0000\u0000\u0000LN\u0001\u0000\u0000\u0000MK\u0001\u0000"+
		"\u0000\u0000NP\u0005\'\u0000\u0000O=\u0001\u0000\u0000\u0000OF\u0001\u0000"+
		"\u0000\u0000P\u0014\u0001\u0000\u0000\u0000QS\u0005-\u0000\u0000RQ\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000"+
		"TU\u0003\u001d\u000e\u0000UW\u0005.\u0000\u0000VX\u0003\u001d\u000e\u0000"+
		"WV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000"+
		"\u0000YZ\u0001\u0000\u0000\u0000Z\\\u0001\u0000\u0000\u0000[]\u0003\u001f"+
		"\u000f\u0000\\[\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]i\u0001"+
		"\u0000\u0000\u0000^`\u0005-\u0000\u0000_^\u0001\u0000\u0000\u0000_`\u0001"+
		"\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000ab\u0003\u001d\u000e\u0000"+
		"bc\u0003\u001f\u000f\u0000ci\u0001\u0000\u0000\u0000df\u0005-\u0000\u0000"+
		"ed\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000"+
		"\u0000gi\u0003\u001d\u000e\u0000hR\u0001\u0000\u0000\u0000h_\u0001\u0000"+
		"\u0000\u0000he\u0001\u0000\u0000\u0000i\u0016\u0001\u0000\u0000\u0000"+
		"jl\u0007\u0002\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000"+
		"\u0000mk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0001\u0000"+
		"\u0000\u0000op\u0006\u000b\u0000\u0000p\u0018\u0001\u0000\u0000\u0000"+
		"qy\u0005\\\u0000\u0000rz\u0007\u0003\u0000\u0000st\u0005u\u0000\u0000"+
		"tu\u0003\u001b\r\u0000uv\u0003\u001b\r\u0000vw\u0003\u001b\r\u0000wx\u0003"+
		"\u001b\r\u0000xz\u0001\u0000\u0000\u0000yr\u0001\u0000\u0000\u0000ys\u0001"+
		"\u0000\u0000\u0000z\u001a\u0001\u0000\u0000\u0000{|\u0007\u0004\u0000"+
		"\u0000|\u001c\u0001\u0000\u0000\u0000}\u007f\u0007\u0005\u0000\u0000~"+
		"}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080~\u0001"+
		"\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u001e\u0001"+
		"\u0000\u0000\u0000\u0082\u0084\u0007\u0006\u0000\u0000\u0083\u0085\u0007"+
		"\u0007\u0000\u0000\u0084\u0083\u0001\u0000\u0000\u0000\u0084\u0085\u0001"+
		"\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0087\u0003"+
		"\u001d\u000e\u0000\u0087 \u0001\u0000\u0000\u0000\u0010\u0000@BIKORY\\"+
		"_ehmy\u0080\u0084\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}