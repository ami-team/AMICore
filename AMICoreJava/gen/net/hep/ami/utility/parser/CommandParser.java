// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/Command.g4 by ANTLR 4.9.1
package net.hep.ami.utility.parser;

	import java.util.*;

	import net.hep.ami.utility.*;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommandParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, IDENTIFIER=3, STRING=4, COMMENT=5, WS=6;
	public static final int
		RULE_command = 0, RULE_parameterList = 1, RULE_parameter = 2, RULE_identifier = 3, 
		RULE_string = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"command", "parameterList", "parameter", "identifier", "string"
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

	@Override
	public String getGrammarFileName() { return "Command.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }



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

	public CommandParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class CommandContext extends ParserRuleContext {
		public Command.CommandTuple commandTuple;
		public IdentifierContext identifier;
		public ParameterListContext parameterList;
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ParameterListContext parameterList() {
			return getRuleContext(ParameterListContext.class,0);
		}
		public TerminalNode EOF() { return getToken(CommandParser.EOF, 0); }
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_command);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			((CommandContext)_localctx).identifier = identifier();
			setState(11);
			((CommandContext)_localctx).parameterList = parameterList();
			setState(12);
			match(EOF);
			 ((CommandContext)_localctx).commandTuple =  new Command.CommandTuple(((CommandContext)_localctx).identifier.v, ((CommandContext)_localctx).parameterList.v); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterListContext extends ParserRuleContext {
		public Map<String, String> v;
		public ParameterContext parameter;
		public List<ParameterContext> parameter() {
			return getRuleContexts(ParameterContext.class);
		}
		public ParameterContext parameter(int i) {
			return getRuleContext(ParameterContext.class,i);
		}
		public ParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterList; }
	}

	public final ParameterListContext parameterList() throws RecognitionException {
		ParameterListContext _localctx = new ParameterListContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_parameterList);
		 ((ParameterListContext)_localctx).v =  new LinkedHashMap<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(15);
				((ParameterListContext)_localctx).parameter = parameter();
				 _localctx.v.put(((ParameterListContext)_localctx).parameter.v.x, ((ParameterListContext)_localctx).parameter.v.y); 
				}
				}
				setState(22);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParameterContext extends ParserRuleContext {
		public Pair v;
		public IdentifierContext identifier;
		public StringContext string;
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_parameter);
		int _la;
		try {
			setState(41);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(24); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(23);
					match(T__0);
					}
					}
					setState(26); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(28);
				((ParameterContext)_localctx).identifier = identifier();
				setState(29);
				match(T__1);
				setState(30);
				((ParameterContext)_localctx).string = string();
				 ((ParameterContext)_localctx).v =  new Pair(((ParameterContext)_localctx).identifier.v, Utility.javaStringToText(((ParameterContext)_localctx).string.v)); 
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(34); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(33);
					match(T__0);
					}
					}
					setState(36); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
				setState(38);
				((ParameterContext)_localctx).identifier = identifier();
				 ((ParameterContext)_localctx).v =  new Pair(((ParameterContext)_localctx).identifier.v, ""); 
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public String v;
		public Token IDENTIFIER;
		public TerminalNode IDENTIFIER() { return getToken(CommandParser.IDENTIFIER, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			((IdentifierContext)_localctx).IDENTIFIER = match(IDENTIFIER);
			 ((IdentifierContext)_localctx).v =  (((IdentifierContext)_localctx).IDENTIFIER!=null?((IdentifierContext)_localctx).IDENTIFIER.getText():null); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringContext extends ParserRuleContext {
		public String v;
		public Token STRING;
		public TerminalNode STRING() { return getToken(CommandParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			((StringContext)_localctx).STRING = match(STRING);
			 ((StringContext)_localctx).v =  (((StringContext)_localctx).STRING!=null?((StringContext)_localctx).STRING.getText():null); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\b\64\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\7\3\25\n"+
		"\3\f\3\16\3\30\13\3\3\4\6\4\33\n\4\r\4\16\4\34\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\6\4%\n\4\r\4\16\4&\3\4\3\4\3\4\5\4,\n\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\2"+
		"\2\7\2\4\6\b\n\2\2\2\62\2\f\3\2\2\2\4\26\3\2\2\2\6+\3\2\2\2\b-\3\2\2\2"+
		"\n\60\3\2\2\2\f\r\5\b\5\2\r\16\5\4\3\2\16\17\7\2\2\3\17\20\b\2\1\2\20"+
		"\3\3\2\2\2\21\22\5\6\4\2\22\23\b\3\1\2\23\25\3\2\2\2\24\21\3\2\2\2\25"+
		"\30\3\2\2\2\26\24\3\2\2\2\26\27\3\2\2\2\27\5\3\2\2\2\30\26\3\2\2\2\31"+
		"\33\7\3\2\2\32\31\3\2\2\2\33\34\3\2\2\2\34\32\3\2\2\2\34\35\3\2\2\2\35"+
		"\36\3\2\2\2\36\37\5\b\5\2\37 \7\4\2\2 !\5\n\6\2!\"\b\4\1\2\",\3\2\2\2"+
		"#%\7\3\2\2$#\3\2\2\2%&\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\'(\3\2\2\2()\5\b\5"+
		"\2)*\b\4\1\2*,\3\2\2\2+\32\3\2\2\2+$\3\2\2\2,\7\3\2\2\2-.\7\5\2\2./\b"+
		"\5\1\2/\t\3\2\2\2\60\61\7\6\2\2\61\62\b\6\1\2\62\13\3\2\2\2\6\26\34&+";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}