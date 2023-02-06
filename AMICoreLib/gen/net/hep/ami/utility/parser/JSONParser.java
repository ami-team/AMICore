// Generated from java-escape by ANTLR 4.11.1
package net.hep.ami.utility.parser;

	import java.util.*;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class JSONParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		STRING=10, NUMBER=11, WS=12;
	public static final int
		RULE_file = 0, RULE_value = 1, RULE_object = 2, RULE_array = 3, RULE_pair = 4, 
		RULE_term = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "value", "object", "array", "pair", "term"
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

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }



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

	public JSONParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public Object v;
		public ValueContext value;
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode EOF() { return getToken(JSONParser.EOF, 0); }
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			((FileContext)_localctx).value = value();
			 ((FileContext)_localctx).v =  ((FileContext)_localctx).value.v; 
			setState(14);
			match(EOF);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ValueContext extends ParserRuleContext {
		public Object v;
		public ObjectContext object;
		public ArrayContext array;
		public TermContext term;
		public ObjectContext object() {
			return getRuleContext(ObjectContext.class,0);
		}
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_value);
		try {
			setState(25);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(16);
				((ValueContext)_localctx).object = object();
				 ((ValueContext)_localctx).v =  (Object) ((ValueContext)_localctx).object.v; 
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(19);
				((ValueContext)_localctx).array = array();
				 ((ValueContext)_localctx).v =  (Object) ((ValueContext)_localctx).array.v; 
				}
				break;
			case T__6:
			case T__7:
			case T__8:
			case STRING:
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(22);
				((ValueContext)_localctx).term = term();
				 ((ValueContext)_localctx).v =  (Object) ((ValueContext)_localctx).term.v; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ObjectContext extends ParserRuleContext {
		public Map<String, Object> v;
		public PairContext pair;
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public ObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object; }
	}

	public final ObjectContext object() throws RecognitionException {
		ObjectContext _localctx = new ObjectContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_object);
		 ((ObjectContext)_localctx).v =  new LinkedHashMap<String, Object>(); 
		int _la;
		try {
			setState(43);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(27);
				match(T__0);
				setState(28);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(29);
				match(T__0);
				setState(30);
				((ObjectContext)_localctx).pair = pair();
				 _localctx.v.put(((ObjectContext)_localctx).pair.v.x, ((ObjectContext)_localctx).pair.v.y); 
				setState(38);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(32);
					match(T__2);
					setState(33);
					((ObjectContext)_localctx).pair = pair();
					 _localctx.v.put(((ObjectContext)_localctx).pair.v.x, ((ObjectContext)_localctx).pair.v.y); 
					}
					}
					setState(40);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(41);
				match(T__1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayContext extends ParserRuleContext {
		public List<Object> v;
		public ValueContext value;
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_array);
		 ((ArrayContext)_localctx).v =  new ArrayList<Object>(); 
		int _la;
		try {
			setState(61);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(45);
				match(T__3);
				setState(46);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(47);
				match(T__3);
				setState(48);
				((ArrayContext)_localctx).value = value();
				 _localctx.v.add(((ArrayContext)_localctx).value.v); 
				setState(56);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__2) {
					{
					{
					setState(50);
					match(T__2);
					setState(51);
					((ArrayContext)_localctx).value = value();
					 _localctx.v.add(((ArrayContext)_localctx).value.v); 
					}
					}
					setState(58);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(59);
				match(T__4);
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

	@SuppressWarnings("CheckReturnValue")
	public static class PairContext extends ParserRuleContext {
		public Pair v;
		public Token x;
		public ValueContext y;
		public TerminalNode STRING() { return getToken(JSONParser.STRING, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			((PairContext)_localctx).x = match(STRING);
			setState(64);
			match(T__5);
			setState(65);
			((PairContext)_localctx).y = value();
			 ((PairContext)_localctx).v =  new Pair(Utility.jsonStringToText((((PairContext)_localctx).x!=null?((PairContext)_localctx).x.getText():null), simpleQuotes), ((PairContext)_localctx).y.v); 
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

	@SuppressWarnings("CheckReturnValue")
	public static class TermContext extends ParserRuleContext {
		public Object v;
		public Token STRING;
		public Token NUMBER;
		public TerminalNode STRING() { return getToken(JSONParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(JSONParser.NUMBER, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_term);
		try {
			setState(78);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(68);
				((TermContext)_localctx).STRING = match(STRING);
				 ((TermContext)_localctx).v =  Utility.jsonStringToText((((TermContext)_localctx).STRING!=null?((TermContext)_localctx).STRING.getText():null), simpleQuotes); 
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				((TermContext)_localctx).NUMBER = match(NUMBER);
				 ((TermContext)_localctx).v =  Double.parseDouble((((TermContext)_localctx).NUMBER!=null?((TermContext)_localctx).NUMBER.getText():null)); 
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 3);
				{
				setState(72);
				match(T__6);
				 ((TermContext)_localctx).v =  true; 
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(74);
				match(T__7);
				 ((TermContext)_localctx).v =  false; 
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 5);
				{
				setState(76);
				match(T__8);
				 ((TermContext)_localctx).v =  null; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		"\u0004\u0001\fQ\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u001a\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002%\b\u0002\n\u0002\f\u0002(\t\u0002\u0001"+
		"\u0002\u0001\u0002\u0003\u0002,\b\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0005\u00037\b\u0003\n\u0003\f\u0003:\t\u0003\u0001\u0003\u0001"+
		"\u0003\u0003\u0003>\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003"+
		"\u0005O\b\u0005\u0001\u0005\u0000\u0000\u0006\u0000\u0002\u0004\u0006"+
		"\b\n\u0000\u0000T\u0000\f\u0001\u0000\u0000\u0000\u0002\u0019\u0001\u0000"+
		"\u0000\u0000\u0004+\u0001\u0000\u0000\u0000\u0006=\u0001\u0000\u0000\u0000"+
		"\b?\u0001\u0000\u0000\u0000\nN\u0001\u0000\u0000\u0000\f\r\u0003\u0002"+
		"\u0001\u0000\r\u000e\u0006\u0000\uffff\uffff\u0000\u000e\u000f\u0005\u0000"+
		"\u0000\u0001\u000f\u0001\u0001\u0000\u0000\u0000\u0010\u0011\u0003\u0004"+
		"\u0002\u0000\u0011\u0012\u0006\u0001\uffff\uffff\u0000\u0012\u001a\u0001"+
		"\u0000\u0000\u0000\u0013\u0014\u0003\u0006\u0003\u0000\u0014\u0015\u0006"+
		"\u0001\uffff\uffff\u0000\u0015\u001a\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0003\n\u0005\u0000\u0017\u0018\u0006\u0001\uffff\uffff\u0000\u0018\u001a"+
		"\u0001\u0000\u0000\u0000\u0019\u0010\u0001\u0000\u0000\u0000\u0019\u0013"+
		"\u0001\u0000\u0000\u0000\u0019\u0016\u0001\u0000\u0000\u0000\u001a\u0003"+
		"\u0001\u0000\u0000\u0000\u001b\u001c\u0005\u0001\u0000\u0000\u001c,\u0005"+
		"\u0002\u0000\u0000\u001d\u001e\u0005\u0001\u0000\u0000\u001e\u001f\u0003"+
		"\b\u0004\u0000\u001f&\u0006\u0002\uffff\uffff\u0000 !\u0005\u0003\u0000"+
		"\u0000!\"\u0003\b\u0004\u0000\"#\u0006\u0002\uffff\uffff\u0000#%\u0001"+
		"\u0000\u0000\u0000$ \u0001\u0000\u0000\u0000%(\u0001\u0000\u0000\u0000"+
		"&$\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000\')\u0001\u0000\u0000"+
		"\u0000(&\u0001\u0000\u0000\u0000)*\u0005\u0002\u0000\u0000*,\u0001\u0000"+
		"\u0000\u0000+\u001b\u0001\u0000\u0000\u0000+\u001d\u0001\u0000\u0000\u0000"+
		",\u0005\u0001\u0000\u0000\u0000-.\u0005\u0004\u0000\u0000.>\u0005\u0005"+
		"\u0000\u0000/0\u0005\u0004\u0000\u000001\u0003\u0002\u0001\u000018\u0006"+
		"\u0003\uffff\uffff\u000023\u0005\u0003\u0000\u000034\u0003\u0002\u0001"+
		"\u000045\u0006\u0003\uffff\uffff\u000057\u0001\u0000\u0000\u000062\u0001"+
		"\u0000\u0000\u00007:\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u0000"+
		"89\u0001\u0000\u0000\u00009;\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000"+
		"\u0000;<\u0005\u0005\u0000\u0000<>\u0001\u0000\u0000\u0000=-\u0001\u0000"+
		"\u0000\u0000=/\u0001\u0000\u0000\u0000>\u0007\u0001\u0000\u0000\u0000"+
		"?@\u0005\n\u0000\u0000@A\u0005\u0006\u0000\u0000AB\u0003\u0002\u0001\u0000"+
		"BC\u0006\u0004\uffff\uffff\u0000C\t\u0001\u0000\u0000\u0000DE\u0005\n"+
		"\u0000\u0000EO\u0006\u0005\uffff\uffff\u0000FG\u0005\u000b\u0000\u0000"+
		"GO\u0006\u0005\uffff\uffff\u0000HI\u0005\u0007\u0000\u0000IO\u0006\u0005"+
		"\uffff\uffff\u0000JK\u0005\b\u0000\u0000KO\u0006\u0005\uffff\uffff\u0000"+
		"LM\u0005\t\u0000\u0000MO\u0006\u0005\uffff\uffff\u0000ND\u0001\u0000\u0000"+
		"\u0000NF\u0001\u0000\u0000\u0000NH\u0001\u0000\u0000\u0000NJ\u0001\u0000"+
		"\u0000\u0000NL\u0001\u0000\u0000\u0000O\u000b\u0001\u0000\u0000\u0000"+
		"\u0006\u0019&+8=N";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}