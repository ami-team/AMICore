// Generated from /home/lambert/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/sql/SQL.g4 by ANTLR 4.9.1
package net.hep.ami.jdbc.query.sql;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\7f\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\6\2\21\n\2\r\2\16\2\22\3\3"+
		"\3\3\3\3\3\3\6\3\31\n\3\r\3\16\3\32\3\3\3\3\3\3\3\4\6\4!\n\4\r\4\16\4"+
		"\"\3\4\3\4\3\4\3\4\7\4)\n\4\f\4\16\4,\13\4\3\4\3\4\3\4\3\4\3\4\6\4\63"+
		"\n\4\r\4\16\4\64\3\4\3\4\3\4\3\4\3\4\6\4<\n\4\r\4\16\4=\3\4\5\4A\n\4\3"+
		"\5\3\5\3\5\3\5\3\5\6\5H\n\5\r\5\16\5I\3\5\3\5\3\5\3\5\3\5\3\5\6\5R\n\5"+
		"\r\5\16\5S\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5^\n\5\3\6\3\6\3\7\6\7c\n"+
		"\7\r\7\16\7d\2\2\b\3\3\5\4\7\5\t\6\13\7\r\2\3\2\n\5\2\13\f\17\17\"\"\3"+
		"\2\'\'\6\2\62;C\\aac|\3\2))\3\2bb\3\2$$\3\2@@\3\2\62;\2v\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\3\20\3\2\2\2\5\24\3\2"+
		"\2\2\7@\3\2\2\2\t]\3\2\2\2\13_\3\2\2\2\rb\3\2\2\2\17\21\t\2\2\2\20\17"+
		"\3\2\2\2\21\22\3\2\2\2\22\20\3\2\2\2\22\23\3\2\2\2\23\4\3\2\2\2\24\25"+
		"\7}\2\2\25\26\7\'\2\2\26\30\3\2\2\2\27\31\n\3\2\2\30\27\3\2\2\2\31\32"+
		"\3\2\2\2\32\30\3\2\2\2\32\33\3\2\2\2\33\34\3\2\2\2\34\35\7\'\2\2\35\36"+
		"\7\177\2\2\36\6\3\2\2\2\37!\t\4\2\2 \37\3\2\2\2!\"\3\2\2\2\" \3\2\2\2"+
		"\"#\3\2\2\2#A\3\2\2\2$*\7)\2\2%&\7)\2\2&)\7)\2\2\')\n\5\2\2(%\3\2\2\2"+
		"(\'\3\2\2\2),\3\2\2\2*(\3\2\2\2*+\3\2\2\2+-\3\2\2\2,*\3\2\2\2-A\7)\2\2"+
		".\62\7b\2\2/\60\7b\2\2\60\63\7b\2\2\61\63\n\6\2\2\62/\3\2\2\2\62\61\3"+
		"\2\2\2\63\64\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\66\3\2\2\2\66A\7b"+
		"\2\2\67;\7$\2\289\7$\2\29<\7$\2\2:<\n\7\2\2;8\3\2\2\2;:\3\2\2\2<=\3\2"+
		"\2\2=;\3\2\2\2=>\3\2\2\2>?\3\2\2\2?A\7$\2\2@ \3\2\2\2@$\3\2\2\2@.\3\2"+
		"\2\2@\67\3\2\2\2A\b\3\2\2\2BC\7A\2\2CD\7%\2\2DE\7>\2\2EG\3\2\2\2FH\n\b"+
		"\2\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JK\3\2\2\2KL\7@\2\2L^\5\r"+
		"\7\2MN\7A\2\2NO\7>\2\2OQ\3\2\2\2PR\n\b\2\2QP\3\2\2\2RS\3\2\2\2SQ\3\2\2"+
		"\2ST\3\2\2\2TU\3\2\2\2UV\7@\2\2V^\5\r\7\2WX\7A\2\2XY\7%\2\2YZ\3\2\2\2"+
		"Z^\5\r\7\2[\\\7A\2\2\\^\5\r\7\2]B\3\2\2\2]M\3\2\2\2]W\3\2\2\2][\3\2\2"+
		"\2^\n\3\2\2\2_`\13\2\2\2`\f\3\2\2\2ac\t\t\2\2ba\3\2\2\2cd\3\2\2\2db\3"+
		"\2\2\2de\3\2\2\2e\16\3\2\2\2\21\2\22\32\"(*\62\64;=@IS]d\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}