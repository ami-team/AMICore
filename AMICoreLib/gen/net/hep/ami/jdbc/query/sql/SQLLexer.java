// Generated from java-escape by ANTLR 4.11.1
package net.hep.ami.jdbc.query.sql;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SQLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACES=1, SPECIAL=2, STRING=3, PARAMETER=4, OTHERS=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"SPACES", "SPECIAL", "STRING", "PARAMETER", "OTHERS", "INT"
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


	public SQLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SQL.g4"; }

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
		"\u0004\u0000\u0005s\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0004\u0000\u000f\b\u0000"+
		"\u000b\u0000\f\u0000\u0010\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0004\u0001\u0017\b\u0001\u000b\u0001\f\u0001\u0018\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0004\u0002\u001f\b\u0002\u000b\u0002\f"+
		"\u0002 \u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\'"+
		"\b\u0002\n\u0002\f\u0002*\t\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0004\u00021\b\u0002\u000b\u0002\f\u00022\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0004\u0002:\b"+
		"\u0002\u000b\u0002\f\u0002;\u0001\u0002\u0003\u0002?\b\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0004\u0003F\b\u0003"+
		"\u000b\u0003\f\u0003G\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0004\u0003Q\b\u0003\u000b\u0003"+
		"\f\u0003R\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0004\u0003[\b\u0003\u000b\u0003\f\u0003\\\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003k\b"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0004\u0005p\b\u0005\u000b"+
		"\u0005\f\u0005q\u0000\u0000\u0006\u0001\u0001\u0003\u0002\u0005\u0003"+
		"\u0007\u0004\t\u0005\u000b\u0000\u0001\u0000\b\u0003\u0000\t\n\r\r  \u0001"+
		"\u0000%%\u0004\u000009AZ__az\u0001\u0000\'\'\u0001\u0000``\u0001\u0000"+
		"\"\"\u0001\u0000>>\u0001\u000009\u0086\u0000\u0001\u0001\u0000\u0000\u0000"+
		"\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000"+
		"\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0001"+
		"\u000e\u0001\u0000\u0000\u0000\u0003\u0012\u0001\u0000\u0000\u0000\u0005"+
		">\u0001\u0000\u0000\u0000\u0007j\u0001\u0000\u0000\u0000\tl\u0001\u0000"+
		"\u0000\u0000\u000bo\u0001\u0000\u0000\u0000\r\u000f\u0007\u0000\u0000"+
		"\u0000\u000e\r\u0001\u0000\u0000\u0000\u000f\u0010\u0001\u0000\u0000\u0000"+
		"\u0010\u000e\u0001\u0000\u0000\u0000\u0010\u0011\u0001\u0000\u0000\u0000"+
		"\u0011\u0002\u0001\u0000\u0000\u0000\u0012\u0013\u0005{\u0000\u0000\u0013"+
		"\u0014\u0005%\u0000\u0000\u0014\u0016\u0001\u0000\u0000\u0000\u0015\u0017"+
		"\b\u0001\u0000\u0000\u0016\u0015\u0001\u0000\u0000\u0000\u0017\u0018\u0001"+
		"\u0000\u0000\u0000\u0018\u0016\u0001\u0000\u0000\u0000\u0018\u0019\u0001"+
		"\u0000\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u001b\u0005"+
		"%\u0000\u0000\u001b\u001c\u0005}\u0000\u0000\u001c\u0004\u0001\u0000\u0000"+
		"\u0000\u001d\u001f\u0007\u0002\u0000\u0000\u001e\u001d\u0001\u0000\u0000"+
		"\u0000\u001f \u0001\u0000\u0000\u0000 \u001e\u0001\u0000\u0000\u0000 "+
		"!\u0001\u0000\u0000\u0000!?\u0001\u0000\u0000\u0000\"(\u0005\'\u0000\u0000"+
		"#$\u0005\'\u0000\u0000$\'\u0005\'\u0000\u0000%\'\b\u0003\u0000\u0000&"+
		"#\u0001\u0000\u0000\u0000&%\u0001\u0000\u0000\u0000\'*\u0001\u0000\u0000"+
		"\u0000(&\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)+\u0001\u0000"+
		"\u0000\u0000*(\u0001\u0000\u0000\u0000+?\u0005\'\u0000\u0000,0\u0005`"+
		"\u0000\u0000-.\u0005`\u0000\u0000.1\u0005`\u0000\u0000/1\b\u0004\u0000"+
		"\u00000-\u0001\u0000\u0000\u00000/\u0001\u0000\u0000\u000012\u0001\u0000"+
		"\u0000\u000020\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000034\u0001"+
		"\u0000\u0000\u00004?\u0005`\u0000\u000059\u0005\"\u0000\u000067\u0005"+
		"\"\u0000\u00007:\u0005\"\u0000\u00008:\b\u0005\u0000\u000096\u0001\u0000"+
		"\u0000\u000098\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;9\u0001"+
		"\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000"+
		"=?\u0005\"\u0000\u0000>\u001e\u0001\u0000\u0000\u0000>\"\u0001\u0000\u0000"+
		"\u0000>,\u0001\u0000\u0000\u0000>5\u0001\u0000\u0000\u0000?\u0006\u0001"+
		"\u0000\u0000\u0000@A\u0005?\u0000\u0000AB\u0005#\u0000\u0000BC\u0005<"+
		"\u0000\u0000CE\u0001\u0000\u0000\u0000DF\b\u0006\u0000\u0000ED\u0001\u0000"+
		"\u0000\u0000FG\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001"+
		"\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0005>\u0000\u0000Jk\u0003"+
		"\u000b\u0005\u0000KL\u0005?\u0000\u0000LM\u0005^\u0000\u0000MN\u0005<"+
		"\u0000\u0000NP\u0001\u0000\u0000\u0000OQ\b\u0006\u0000\u0000PO\u0001\u0000"+
		"\u0000\u0000QR\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000\u0000RS\u0001"+
		"\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TU\u0005>\u0000\u0000Uk\u0003"+
		"\u000b\u0005\u0000VW\u0005?\u0000\u0000WX\u0005<\u0000\u0000XZ\u0001\u0000"+
		"\u0000\u0000Y[\b\u0006\u0000\u0000ZY\u0001\u0000\u0000\u0000[\\\u0001"+
		"\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000"+
		"]^\u0001\u0000\u0000\u0000^_\u0005>\u0000\u0000_k\u0003\u000b\u0005\u0000"+
		"`a\u0005?\u0000\u0000ab\u0005#\u0000\u0000bc\u0001\u0000\u0000\u0000c"+
		"k\u0003\u000b\u0005\u0000de\u0005?\u0000\u0000ef\u0005^\u0000\u0000fg"+
		"\u0001\u0000\u0000\u0000gk\u0003\u000b\u0005\u0000hi\u0005?\u0000\u0000"+
		"ik\u0003\u000b\u0005\u0000j@\u0001\u0000\u0000\u0000jK\u0001\u0000\u0000"+
		"\u0000jV\u0001\u0000\u0000\u0000j`\u0001\u0000\u0000\u0000jd\u0001\u0000"+
		"\u0000\u0000jh\u0001\u0000\u0000\u0000k\b\u0001\u0000\u0000\u0000lm\t"+
		"\u0000\u0000\u0000m\n\u0001\u0000\u0000\u0000np\u0007\u0007\u0000\u0000"+
		"on\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000"+
		"\u0000qr\u0001\u0000\u0000\u0000r\f\u0001\u0000\u0000\u0000\u0010\u0000"+
		"\u0010\u0018 &(029;>GR\\jq\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}