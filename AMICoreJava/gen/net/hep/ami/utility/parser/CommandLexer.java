// Generated from /home/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/Command.g4 by ANTLR 4.7.2
package net.hep.ami.utility.parser;

	import java.util.*;

	import net.hep.ami.utility.*;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommandLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, IDENTIFIER=3, STRING=4, COMMENT=5, WS=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "IDENTIFIER", "STRING", "COMMENT", "WS", "ESC", "HEX"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'-'", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "IDENTIFIER", "STRING", "COMMENT", "WS"
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
			final String y;

			private Pair(String _x, String _y)
			{
				x = _x;
				y = _y;
			}
		}


	public CommandLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Command.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\bN\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3"+
		"\3\4\3\4\7\4\32\n\4\f\4\16\4\35\13\4\3\5\3\5\3\5\7\5\"\n\5\f\5\16\5%\13"+
		"\5\3\5\3\5\3\5\3\5\7\5+\n\5\f\5\16\5.\13\5\3\5\5\5\61\n\5\3\6\3\6\7\6"+
		"\65\n\6\f\6\16\68\13\6\3\6\3\6\3\7\6\7=\n\7\r\7\16\7>\3\7\3\7\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\5\bK\n\b\3\t\3\t\2\2\n\3\3\5\4\7\5\t\6\13\7\r"+
		"\b\17\2\21\2\3\2\n\4\2C\\c|\5\2\62;C\\c|\6\2\f\f\17\17$$^^\6\2\f\f\17"+
		"\17))^^\4\2\f\f\17\17\5\2\13\f\17\17\"\"\13\2$$))\61\61^^ddhhppttvv\5"+
		"\2\62;CHch\2T\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2"+
		"\2\2\2\r\3\2\2\2\3\23\3\2\2\2\5\25\3\2\2\2\7\27\3\2\2\2\t\60\3\2\2\2\13"+
		"\62\3\2\2\2\r<\3\2\2\2\17B\3\2\2\2\21L\3\2\2\2\23\24\7/\2\2\24\4\3\2\2"+
		"\2\25\26\7?\2\2\26\6\3\2\2\2\27\33\t\2\2\2\30\32\t\3\2\2\31\30\3\2\2\2"+
		"\32\35\3\2\2\2\33\31\3\2\2\2\33\34\3\2\2\2\34\b\3\2\2\2\35\33\3\2\2\2"+
		"\36#\7$\2\2\37\"\5\17\b\2 \"\n\4\2\2!\37\3\2\2\2! \3\2\2\2\"%\3\2\2\2"+
		"#!\3\2\2\2#$\3\2\2\2$&\3\2\2\2%#\3\2\2\2&\61\7$\2\2\',\7)\2\2(+\5\17\b"+
		"\2)+\n\5\2\2*(\3\2\2\2*)\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2\2-/\3\2\2"+
		"\2.,\3\2\2\2/\61\7)\2\2\60\36\3\2\2\2\60\'\3\2\2\2\61\n\3\2\2\2\62\66"+
		"\7%\2\2\63\65\n\6\2\2\64\63\3\2\2\2\658\3\2\2\2\66\64\3\2\2\2\66\67\3"+
		"\2\2\2\679\3\2\2\28\66\3\2\2\29:\b\6\2\2:\f\3\2\2\2;=\t\7\2\2<;\3\2\2"+
		"\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?@\3\2\2\2@A\b\7\2\2A\16\3\2\2\2BJ\7^"+
		"\2\2CK\t\b\2\2DE\7w\2\2EF\5\21\t\2FG\5\21\t\2GH\5\21\t\2HI\5\21\t\2IK"+
		"\3\2\2\2JC\3\2\2\2JD\3\2\2\2K\20\3\2\2\2LM\t\t\2\2M\22\3\2\2\2\f\2\33"+
		"!#*,\60\66>J\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}