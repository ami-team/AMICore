// Generated from /home/hmangione/IdeaProjects/AMICore/AMICoreLib/src/main/antlr4/net/hep/ami/utility/parser/PythonDict.g4 by ANTLR 4.13.2
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

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PythonDictLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, BOOLEAN=13, STRING=14, NUMBER=15, WS=16, 
		COMMENT=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "BOOLEAN", "STRING", "NUMBER", "WS", "COMMENT", 
			"ESC", "HEX", "INT", "EXP"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "','", "'['", "']'", "':'", "'True'", "'False'", 
			"'None'", "'true'", "'false'", "'null'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "BOOLEAN", "STRING", "NUMBER", "WS", "COMMENT"
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


	    private static class Pair {
	        final String key;
	        final Object value;

	        private Pair(String k, Object v) {
	            key = k;
	            value = v;
	        }
	    }

	    public boolean simpleQuotes = true;
	    
	    private String parseString(String rawString) {
	        String content = rawString.substring(1, rawString.length() - 1);

	        return content
	            .replace("\\n", "\n")
	            .replace("\\t", "\t")
	            .replace("\\r", "\r")
	            .replace("\\\"", "\"")
	            .replace("\\'", "'")
	            .replace("\\\\", "\\");
	    }


	public PythonDictLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PythonDict.g4"; }

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
		"\u0004\u0000\u0011\u00bb\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0003\fa\b\f\u0001\r\u0001\r\u0001\r"+
		"\u0005\rf\b\r\n\r\f\ri\t\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\ro\b"+
		"\r\n\r\f\rr\t\r\u0001\r\u0003\ru\b\r\u0001\u000e\u0003\u000ex\b\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0004\u000e}\b\u000e\u000b\u000e"+
		"\f\u000e~\u0001\u000e\u0003\u000e\u0082\b\u000e\u0001\u000e\u0003\u000e"+
		"\u0085\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e"+
		"\u008b\b\u000e\u0001\u000e\u0003\u000e\u008e\b\u000e\u0001\u000f\u0004"+
		"\u000f\u0091\b\u000f\u000b\u000f\f\u000f\u0092\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0005\u0010\u0099\b\u0010\n\u0010\f\u0010\u009c"+
		"\t\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00a8"+
		"\b\u0011\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0005"+
		"\u0013\u00af\b\u0013\n\u0013\f\u0013\u00b2\t\u0013\u0003\u0013\u00b4\b"+
		"\u0013\u0001\u0014\u0001\u0014\u0003\u0014\u00b8\b\u0014\u0001\u0014\u0001"+
		"\u0014\u0000\u0000\u0015\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004"+
		"\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017"+
		"\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0000%\u0000\'"+
		"\u0000)\u0000\u0001\u0000\u0011\u0002\u0000TTtt\u0002\u0000RRrr\u0002"+
		"\u0000UUuu\u0002\u0000EEee\u0002\u0000FFff\u0002\u0000AAaa\u0002\u0000"+
		"LLll\u0002\u0000SSss\u0004\u0000\n\n\r\r\"\"\\\\\u0004\u0000\n\n\r\r\'"+
		"\'\\\\\u0003\u0000\t\n\r\r  \u0002\u0000\n\n\r\r\b\u0000\"\"//\\\\bbf"+
		"fnnrrtt\u0003\u000009AFaf\u0001\u000019\u0001\u000009\u0002\u0000++--"+
		"\u00c9\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000"+
		"\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000"+
		"\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000"+
		"\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000"+
		"\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000"+
		"\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000"+
		"\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000"+
		"\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000"+
		"!\u0001\u0000\u0000\u0000\u0001+\u0001\u0000\u0000\u0000\u0003-\u0001"+
		"\u0000\u0000\u0000\u0005/\u0001\u0000\u0000\u0000\u00071\u0001\u0000\u0000"+
		"\u0000\t3\u0001\u0000\u0000\u0000\u000b5\u0001\u0000\u0000\u0000\r7\u0001"+
		"\u0000\u0000\u0000\u000f<\u0001\u0000\u0000\u0000\u0011B\u0001\u0000\u0000"+
		"\u0000\u0013G\u0001\u0000\u0000\u0000\u0015L\u0001\u0000\u0000\u0000\u0017"+
		"R\u0001\u0000\u0000\u0000\u0019`\u0001\u0000\u0000\u0000\u001bt\u0001"+
		"\u0000\u0000\u0000\u001d\u008d\u0001\u0000\u0000\u0000\u001f\u0090\u0001"+
		"\u0000\u0000\u0000!\u0096\u0001\u0000\u0000\u0000#\u009f\u0001\u0000\u0000"+
		"\u0000%\u00a9\u0001\u0000\u0000\u0000\'\u00b3\u0001\u0000\u0000\u0000"+
		")\u00b5\u0001\u0000\u0000\u0000+,\u0005{\u0000\u0000,\u0002\u0001\u0000"+
		"\u0000\u0000-.\u0005}\u0000\u0000.\u0004\u0001\u0000\u0000\u0000/0\u0005"+
		",\u0000\u00000\u0006\u0001\u0000\u0000\u000012\u0005[\u0000\u00002\b\u0001"+
		"\u0000\u0000\u000034\u0005]\u0000\u00004\n\u0001\u0000\u0000\u000056\u0005"+
		":\u0000\u00006\f\u0001\u0000\u0000\u000078\u0005T\u0000\u000089\u0005"+
		"r\u0000\u00009:\u0005u\u0000\u0000:;\u0005e\u0000\u0000;\u000e\u0001\u0000"+
		"\u0000\u0000<=\u0005F\u0000\u0000=>\u0005a\u0000\u0000>?\u0005l\u0000"+
		"\u0000?@\u0005s\u0000\u0000@A\u0005e\u0000\u0000A\u0010\u0001\u0000\u0000"+
		"\u0000BC\u0005N\u0000\u0000CD\u0005o\u0000\u0000DE\u0005n\u0000\u0000"+
		"EF\u0005e\u0000\u0000F\u0012\u0001\u0000\u0000\u0000GH\u0005t\u0000\u0000"+
		"HI\u0005r\u0000\u0000IJ\u0005u\u0000\u0000JK\u0005e\u0000\u0000K\u0014"+
		"\u0001\u0000\u0000\u0000LM\u0005f\u0000\u0000MN\u0005a\u0000\u0000NO\u0005"+
		"l\u0000\u0000OP\u0005s\u0000\u0000PQ\u0005e\u0000\u0000Q\u0016\u0001\u0000"+
		"\u0000\u0000RS\u0005n\u0000\u0000ST\u0005u\u0000\u0000TU\u0005l\u0000"+
		"\u0000UV\u0005l\u0000\u0000V\u0018\u0001\u0000\u0000\u0000WX\u0007\u0000"+
		"\u0000\u0000XY\u0007\u0001\u0000\u0000YZ\u0007\u0002\u0000\u0000Za\u0007"+
		"\u0003\u0000\u0000[\\\u0007\u0004\u0000\u0000\\]\u0007\u0005\u0000\u0000"+
		"]^\u0007\u0006\u0000\u0000^_\u0007\u0007\u0000\u0000_a\u0007\u0003\u0000"+
		"\u0000`W\u0001\u0000\u0000\u0000`[\u0001\u0000\u0000\u0000a\u001a\u0001"+
		"\u0000\u0000\u0000bg\u0005\"\u0000\u0000cf\u0003#\u0011\u0000df\b\b\u0000"+
		"\u0000ec\u0001\u0000\u0000\u0000ed\u0001\u0000\u0000\u0000fi\u0001\u0000"+
		"\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hj\u0001"+
		"\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000ju\u0005\"\u0000\u0000kp\u0005"+
		"\'\u0000\u0000lo\u0003#\u0011\u0000mo\b\t\u0000\u0000nl\u0001\u0000\u0000"+
		"\u0000nm\u0001\u0000\u0000\u0000or\u0001\u0000\u0000\u0000pn\u0001\u0000"+
		"\u0000\u0000pq\u0001\u0000\u0000\u0000qs\u0001\u0000\u0000\u0000rp\u0001"+
		"\u0000\u0000\u0000su\u0005\'\u0000\u0000tb\u0001\u0000\u0000\u0000tk\u0001"+
		"\u0000\u0000\u0000u\u001c\u0001\u0000\u0000\u0000vx\u0005-\u0000\u0000"+
		"wv\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000"+
		"\u0000yz\u0003\'\u0013\u0000z|\u0005.\u0000\u0000{}\u0003\'\u0013\u0000"+
		"|{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~|\u0001\u0000\u0000"+
		"\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0081\u0001\u0000\u0000\u0000"+
		"\u0080\u0082\u0003)\u0014\u0000\u0081\u0080\u0001\u0000\u0000\u0000\u0081"+
		"\u0082\u0001\u0000\u0000\u0000\u0082\u008e\u0001\u0000\u0000\u0000\u0083"+
		"\u0085\u0005-\u0000\u0000\u0084\u0083\u0001\u0000\u0000\u0000\u0084\u0085"+
		"\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0087"+
		"\u0003\'\u0013\u0000\u0087\u0088\u0003)\u0014\u0000\u0088\u008e\u0001"+
		"\u0000\u0000\u0000\u0089\u008b\u0005-\u0000\u0000\u008a\u0089\u0001\u0000"+
		"\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000"+
		"\u0000\u0000\u008c\u008e\u0003\'\u0013\u0000\u008dw\u0001\u0000\u0000"+
		"\u0000\u008d\u0084\u0001\u0000\u0000\u0000\u008d\u008a\u0001\u0000\u0000"+
		"\u0000\u008e\u001e\u0001\u0000\u0000\u0000\u008f\u0091\u0007\n\u0000\u0000"+
		"\u0090\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000"+
		"\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0095\u0006\u000f\u0000\u0000"+
		"\u0095 \u0001\u0000\u0000\u0000\u0096\u009a\u0005#\u0000\u0000\u0097\u0099"+
		"\b\u000b\u0000\u0000\u0098\u0097\u0001\u0000\u0000\u0000\u0099\u009c\u0001"+
		"\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u009b\u0001"+
		"\u0000\u0000\u0000\u009b\u009d\u0001\u0000\u0000\u0000\u009c\u009a\u0001"+
		"\u0000\u0000\u0000\u009d\u009e\u0006\u0010\u0000\u0000\u009e\"\u0001\u0000"+
		"\u0000\u0000\u009f\u00a7\u0005\\\u0000\u0000\u00a0\u00a8\u0007\f\u0000"+
		"\u0000\u00a1\u00a2\u0005u\u0000\u0000\u00a2\u00a3\u0003%\u0012\u0000\u00a3"+
		"\u00a4\u0003%\u0012\u0000\u00a4\u00a5\u0003%\u0012\u0000\u00a5\u00a6\u0003"+
		"%\u0012\u0000\u00a6\u00a8\u0001\u0000\u0000\u0000\u00a7\u00a0\u0001\u0000"+
		"\u0000\u0000\u00a7\u00a1\u0001\u0000\u0000\u0000\u00a8$\u0001\u0000\u0000"+
		"\u0000\u00a9\u00aa\u0007\r\u0000\u0000\u00aa&\u0001\u0000\u0000\u0000"+
		"\u00ab\u00b4\u00050\u0000\u0000\u00ac\u00b0\u0007\u000e\u0000\u0000\u00ad"+
		"\u00af\u0007\u000f\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000\u00af"+
		"\u00b2\u0001\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b4\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b3\u00ab\u0001\u0000\u0000\u0000\u00b3"+
		"\u00ac\u0001\u0000\u0000\u0000\u00b4(\u0001\u0000\u0000\u0000\u00b5\u00b7"+
		"\u0007\u0003\u0000\u0000\u00b6\u00b8\u0007\u0010\u0000\u0000\u00b7\u00b6"+
		"\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8\u00b9"+
		"\u0001\u0000\u0000\u0000\u00b9\u00ba\u0003\'\u0013\u0000\u00ba*\u0001"+
		"\u0000\u0000\u0000\u0013\u0000`egnptw~\u0081\u0084\u008a\u008d\u0092\u009a"+
		"\u00a7\u00b0\u00b3\u00b7\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}