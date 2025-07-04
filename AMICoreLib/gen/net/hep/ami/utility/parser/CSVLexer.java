// Generated from java-escape by ANTLR 4.11.1
package net.hep.ami.utility.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CSVLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, STRING=2, VALUE=3, BREAK=4, COMMENT=5, WS=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "STRING", "VALUE", "BREAK", "COMMENT", "WS", "ESC", "HEX"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "STRING", "VALUE", "BREAK", "COMMENT", "WS"
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


	public CSVLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CSV.g4"; }

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
		"\u0004\u0000\u0006Q\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0005\u0001\u0019\b\u0001\n\u0001\f\u0001\u001c"+
		"\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0005\u0001$\b\u0001\n\u0001\f\u0001\'\t\u0001\u0001\u0001\u0003"+
		"\u0001*\b\u0001\u0001\u0002\u0004\u0002-\b\u0002\u000b\u0002\f\u0002."+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u00034\b\u0003\u0001\u0004"+
		"\u0001\u0004\u0005\u00048\b\u0004\n\u0004\f\u0004;\t\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0004\u0005@\b\u0005\u000b\u0005\f\u0005A\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006N\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0000\u0000\b\u0001\u0001\u0003\u0002\u0005\u0003\u0007"+
		"\u0004\t\u0005\u000b\u0006\r\u0000\u000f\u0000\u0001\u0000\u0007\u0004"+
		"\u0000\n\n\r\r\"\"\\\\\u0004\u0000\n\n\r\r\'\'\\\\\u0006\u0000\t\n\r\r"+
		"  \"#\'\',,\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \t\u0000\"\"\'\'//\\"+
		"\\bbffnnrrtt\u0003\u000009AFafZ\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0001\u0011\u0001\u0000\u0000\u0000\u0003)\u0001"+
		"\u0000\u0000\u0000\u0005,\u0001\u0000\u0000\u0000\u00073\u0001\u0000\u0000"+
		"\u0000\t5\u0001\u0000\u0000\u0000\u000b?\u0001\u0000\u0000\u0000\rE\u0001"+
		"\u0000\u0000\u0000\u000fO\u0001\u0000\u0000\u0000\u0011\u0012\u0005,\u0000"+
		"\u0000\u0012\u0002\u0001\u0000\u0000\u0000\u0013\u001a\u0005\"\u0000\u0000"+
		"\u0014\u0019\u0003\r\u0006\u0000\u0015\u0016\u0005\"\u0000\u0000\u0016"+
		"\u0019\u0005\"\u0000\u0000\u0017\u0019\b\u0000\u0000\u0000\u0018\u0014"+
		"\u0001\u0000\u0000\u0000\u0018\u0015\u0001\u0000\u0000\u0000\u0018\u0017"+
		"\u0001\u0000\u0000\u0000\u0019\u001c\u0001\u0000\u0000\u0000\u001a\u0018"+
		"\u0001\u0000\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u001d"+
		"\u0001\u0000\u0000\u0000\u001c\u001a\u0001\u0000\u0000\u0000\u001d*\u0005"+
		"\"\u0000\u0000\u001e%\u0005\'\u0000\u0000\u001f$\u0003\r\u0006\u0000 "+
		"!\u0005\'\u0000\u0000!$\u0005\'\u0000\u0000\"$\b\u0001\u0000\u0000#\u001f"+
		"\u0001\u0000\u0000\u0000# \u0001\u0000\u0000\u0000#\"\u0001\u0000\u0000"+
		"\u0000$\'\u0001\u0000\u0000\u0000%#\u0001\u0000\u0000\u0000%&\u0001\u0000"+
		"\u0000\u0000&(\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000(*\u0005"+
		"\'\u0000\u0000)\u0013\u0001\u0000\u0000\u0000)\u001e\u0001\u0000\u0000"+
		"\u0000*\u0004\u0001\u0000\u0000\u0000+-\b\u0002\u0000\u0000,+\u0001\u0000"+
		"\u0000\u0000-.\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001"+
		"\u0000\u0000\u0000/\u0006\u0001\u0000\u0000\u000001\u0005\r\u0000\u0000"+
		"14\u0005\n\u0000\u000024\u0007\u0003\u0000\u000030\u0001\u0000\u0000\u0000"+
		"32\u0001\u0000\u0000\u00004\b\u0001\u0000\u0000\u000059\u0005#\u0000\u0000"+
		"68\b\u0003\u0000\u000076\u0001\u0000\u0000\u00008;\u0001\u0000\u0000\u0000"+
		"97\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:<\u0001\u0000\u0000"+
		"\u0000;9\u0001\u0000\u0000\u0000<=\u0006\u0004\u0000\u0000=\n\u0001\u0000"+
		"\u0000\u0000>@\u0007\u0004\u0000\u0000?>\u0001\u0000\u0000\u0000@A\u0001"+
		"\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000"+
		"BC\u0001\u0000\u0000\u0000CD\u0006\u0005\u0000\u0000D\f\u0001\u0000\u0000"+
		"\u0000EM\u0005\\\u0000\u0000FN\u0007\u0005\u0000\u0000GH\u0005u\u0000"+
		"\u0000HI\u0003\u000f\u0007\u0000IJ\u0003\u000f\u0007\u0000JK\u0003\u000f"+
		"\u0007\u0000KL\u0003\u000f\u0007\u0000LN\u0001\u0000\u0000\u0000MF\u0001"+
		"\u0000\u0000\u0000MG\u0001\u0000\u0000\u0000N\u000e\u0001\u0000\u0000"+
		"\u0000OP\u0007\u0006\u0000\u0000P\u0010\u0001\u0000\u0000\u0000\u000b"+
		"\u0000\u0018\u001a#%).39AM\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}