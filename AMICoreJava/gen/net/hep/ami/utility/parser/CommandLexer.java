// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/Command.g4 by ANTLR 4.10.1
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
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

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
		"\u0004\u0000\u0006L\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0005\u0002\u0018\b\u0002\n\u0002\f\u0002\u001b\t\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0005\u0003 \b\u0003\n\u0003\f\u0003#\t"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003)\b"+
		"\u0003\n\u0003\f\u0003,\t\u0003\u0001\u0003\u0003\u0003/\b\u0003\u0001"+
		"\u0004\u0001\u0004\u0005\u00043\b\u0004\n\u0004\f\u00046\t\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0004\u0005;\b\u0005\u000b\u0005\f\u0005"+
		"<\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006I\b\u0006"+
		"\u0001\u0007\u0001\u0007\u0000\u0000\b\u0001\u0001\u0003\u0002\u0005\u0003"+
		"\u0007\u0004\t\u0005\u000b\u0006\r\u0000\u000f\u0000\u0001\u0000\b\u0003"+
		"\u0000AZ__az\u0004\u000009AZ__az\u0004\u0000\n\n\r\r\"\"\\\\\u0004\u0000"+
		"\n\n\r\r\'\'\\\\\u0002\u0000\n\n\r\r\u0003\u0000\t\n\r\r  \t\u0000\"\""+
		"\'\'//\\\\bbffnnrrtt\u0003\u000009AFafR\u0000\u0001\u0001\u0000\u0000"+
		"\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000"+
		"\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000"+
		"\u0000\u000b\u0001\u0000\u0000\u0000\u0001\u0011\u0001\u0000\u0000\u0000"+
		"\u0003\u0013\u0001\u0000\u0000\u0000\u0005\u0015\u0001\u0000\u0000\u0000"+
		"\u0007.\u0001\u0000\u0000\u0000\t0\u0001\u0000\u0000\u0000\u000b:\u0001"+
		"\u0000\u0000\u0000\r@\u0001\u0000\u0000\u0000\u000fJ\u0001\u0000\u0000"+
		"\u0000\u0011\u0012\u0005-\u0000\u0000\u0012\u0002\u0001\u0000\u0000\u0000"+
		"\u0013\u0014\u0005=\u0000\u0000\u0014\u0004\u0001\u0000\u0000\u0000\u0015"+
		"\u0019\u0007\u0000\u0000\u0000\u0016\u0018\u0007\u0001\u0000\u0000\u0017"+
		"\u0016\u0001\u0000\u0000\u0000\u0018\u001b\u0001\u0000\u0000\u0000\u0019"+
		"\u0017\u0001\u0000\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a"+
		"\u0006\u0001\u0000\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000\u001c"+
		"!\u0005\"\u0000\u0000\u001d \u0003\r\u0006\u0000\u001e \b\u0002\u0000"+
		"\u0000\u001f\u001d\u0001\u0000\u0000\u0000\u001f\u001e\u0001\u0000\u0000"+
		"\u0000 #\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000!\"\u0001"+
		"\u0000\u0000\u0000\"$\u0001\u0000\u0000\u0000#!\u0001\u0000\u0000\u0000"+
		"$/\u0005\"\u0000\u0000%*\u0005\'\u0000\u0000&)\u0003\r\u0006\u0000\')"+
		"\b\u0003\u0000\u0000(&\u0001\u0000\u0000\u0000(\'\u0001\u0000\u0000\u0000"+
		"),\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000"+
		"\u0000+-\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000-/\u0005\'\u0000"+
		"\u0000.\u001c\u0001\u0000\u0000\u0000.%\u0001\u0000\u0000\u0000/\b\u0001"+
		"\u0000\u0000\u000004\u0005#\u0000\u000013\b\u0004\u0000\u000021\u0001"+
		"\u0000\u0000\u000036\u0001\u0000\u0000\u000042\u0001\u0000\u0000\u0000"+
		"45\u0001\u0000\u0000\u000057\u0001\u0000\u0000\u000064\u0001\u0000\u0000"+
		"\u000078\u0006\u0004\u0000\u00008\n\u0001\u0000\u0000\u00009;\u0007\u0005"+
		"\u0000\u0000:9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<:\u0001"+
		"\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000"+
		">?\u0006\u0005\u0000\u0000?\f\u0001\u0000\u0000\u0000@H\u0005\\\u0000"+
		"\u0000AI\u0007\u0006\u0000\u0000BC\u0005u\u0000\u0000CD\u0003\u000f\u0007"+
		"\u0000DE\u0003\u000f\u0007\u0000EF\u0003\u000f\u0007\u0000FG\u0003\u000f"+
		"\u0007\u0000GI\u0001\u0000\u0000\u0000HA\u0001\u0000\u0000\u0000HB\u0001"+
		"\u0000\u0000\u0000I\u000e\u0001\u0000\u0000\u0000JK\u0007\u0007\u0000"+
		"\u0000K\u0010\u0001\u0000\u0000\u0000\n\u0000\u0019\u001f!(*.4<H\u0001"+
		"\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}