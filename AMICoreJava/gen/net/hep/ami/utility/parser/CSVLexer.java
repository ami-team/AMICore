// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/CSV.g4 by ANTLR 4.9.1
package net.hep.ami.utility.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CSVLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\bS\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3"+
		"\3\3\3\3\3\3\7\3\33\n\3\f\3\16\3\36\13\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3&"+
		"\n\3\f\3\16\3)\13\3\3\3\5\3,\n\3\3\4\6\4/\n\4\r\4\16\4\60\3\5\3\5\3\5"+
		"\5\5\66\n\5\3\6\3\6\7\6:\n\6\f\6\16\6=\13\6\3\6\3\6\3\7\6\7B\n\7\r\7\16"+
		"\7C\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bP\n\b\3\t\3\t\2\2\n\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\2\21\2\3\2\t\6\2\f\f\17\17$$^^\6\2\f\f\17\17"+
		"))^^\b\2\13\f\17\17\"\"$%))..\4\2\f\f\17\17\4\2\13\13\"\"\13\2$$))\61"+
		"\61^^ddhhppttvv\5\2\62;CHch\2\\\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\3\23\3\2\2\2\5+\3\2\2\2\7.\3\2\2\2"+
		"\t\65\3\2\2\2\13\67\3\2\2\2\rA\3\2\2\2\17G\3\2\2\2\21Q\3\2\2\2\23\24\7"+
		".\2\2\24\4\3\2\2\2\25\34\7$\2\2\26\33\5\17\b\2\27\30\7$\2\2\30\33\7$\2"+
		"\2\31\33\n\2\2\2\32\26\3\2\2\2\32\27\3\2\2\2\32\31\3\2\2\2\33\36\3\2\2"+
		"\2\34\32\3\2\2\2\34\35\3\2\2\2\35\37\3\2\2\2\36\34\3\2\2\2\37,\7$\2\2"+
		" \'\7)\2\2!&\5\17\b\2\"#\7)\2\2#&\7)\2\2$&\n\3\2\2%!\3\2\2\2%\"\3\2\2"+
		"\2%$\3\2\2\2&)\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2(*\3\2\2\2)\'\3\2\2\2*,\7"+
		")\2\2+\25\3\2\2\2+ \3\2\2\2,\6\3\2\2\2-/\n\4\2\2.-\3\2\2\2/\60\3\2\2\2"+
		"\60.\3\2\2\2\60\61\3\2\2\2\61\b\3\2\2\2\62\63\7\17\2\2\63\66\7\f\2\2\64"+
		"\66\t\5\2\2\65\62\3\2\2\2\65\64\3\2\2\2\66\n\3\2\2\2\67;\7%\2\28:\n\5"+
		"\2\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<>\3\2\2\2=;\3\2\2\2>?\b\6"+
		"\2\2?\f\3\2\2\2@B\t\6\2\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DE\3"+
		"\2\2\2EF\b\7\2\2F\16\3\2\2\2GO\7^\2\2HP\t\7\2\2IJ\7w\2\2JK\5\21\t\2KL"+
		"\5\21\t\2LM\5\21\t\2MN\5\21\t\2NP\3\2\2\2OH\3\2\2\2OI\3\2\2\2P\20\3\2"+
		"\2\2QR\t\b\2\2R\22\3\2\2\2\r\2\32\34%\'+\60\65;CO\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}