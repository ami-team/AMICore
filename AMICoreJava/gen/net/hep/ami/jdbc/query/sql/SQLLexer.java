// Generated from /Users/jfulach/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/jdbc/query/sql/SQL.g4 by ANTLR 4.7.2
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
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACES=1, STRING=2, PARAMETER=3, OTHERS=4;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"SPACES", "STRING", "PARAMETER", "OTHERS", "INT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\6G\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\6\2\17\n\2\r\2\16\2\20\3\3\6\3\24\n"+
		"\3\r\3\16\3\25\3\3\3\3\3\3\3\3\7\3\34\n\3\f\3\16\3\37\13\3\3\3\3\3\3\3"+
		"\3\3\3\3\6\3&\n\3\r\3\16\3\'\3\3\3\3\3\3\3\3\3\3\6\3/\n\3\r\3\16\3\60"+
		"\3\3\5\3\64\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4?\n\4\3\5\3\5\3"+
		"\6\6\6D\n\6\r\6\16\6E\2\2\7\3\3\5\4\7\5\t\6\13\2\3\2\b\5\2\13\f\17\17"+
		"\"\"\6\2\62;C\\aac|\3\2))\3\2bb\3\2$$\3\2\62;\2T\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\3\16\3\2\2\2\5\63\3\2\2\2\7>\3\2\2\2\t@\3\2"+
		"\2\2\13C\3\2\2\2\r\17\t\2\2\2\16\r\3\2\2\2\17\20\3\2\2\2\20\16\3\2\2\2"+
		"\20\21\3\2\2\2\21\4\3\2\2\2\22\24\t\3\2\2\23\22\3\2\2\2\24\25\3\2\2\2"+
		"\25\23\3\2\2\2\25\26\3\2\2\2\26\64\3\2\2\2\27\35\7)\2\2\30\31\7)\2\2\31"+
		"\34\7)\2\2\32\34\n\4\2\2\33\30\3\2\2\2\33\32\3\2\2\2\34\37\3\2\2\2\35"+
		"\33\3\2\2\2\35\36\3\2\2\2\36 \3\2\2\2\37\35\3\2\2\2 \64\7)\2\2!%\7b\2"+
		"\2\"#\7b\2\2#&\7b\2\2$&\n\5\2\2%\"\3\2\2\2%$\3\2\2\2&\'\3\2\2\2\'%\3\2"+
		"\2\2\'(\3\2\2\2()\3\2\2\2)\64\7b\2\2*.\7$\2\2+,\7$\2\2,/\7$\2\2-/\n\6"+
		"\2\2.+\3\2\2\2.-\3\2\2\2/\60\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\62\3"+
		"\2\2\2\62\64\7$\2\2\63\23\3\2\2\2\63\27\3\2\2\2\63!\3\2\2\2\63*\3\2\2"+
		"\2\64\6\3\2\2\2\65\66\7A\2\2\66\67\7%\2\2\678\3\2\2\28?\5\13\6\29:\7A"+
		"\2\2:?\5\13\6\2;<\7A\2\2<?\7%\2\2=?\7A\2\2>\65\3\2\2\2>9\3\2\2\2>;\3\2"+
		"\2\2>=\3\2\2\2?\b\3\2\2\2@A\13\2\2\2A\n\3\2\2\2BD\t\7\2\2CB\3\2\2\2DE"+
		"\3\2\2\2EC\3\2\2\2EF\3\2\2\2F\f\3\2\2\2\16\2\20\25\33\35%\'.\60\63>E\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}