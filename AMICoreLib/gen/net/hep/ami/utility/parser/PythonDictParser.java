// Generated from /home/hmangione/IdeaProjects/AMICore/AMICoreLib/src/main/antlr4/net/hep/ami/utility/parser/PythonDict.g4 by ANTLR 4.13.2
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

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PythonDictParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, BOOLEAN=13, STRING=14, NUMBER=15, WS=16, 
		COMMENT=17;
	public static final int
		RULE_file = 0, RULE_value = 1, RULE_pythonDict = 2, RULE_pythonList = 3, 
		RULE_pair = 4, RULE_term = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "value", "pythonDict", "pythonList", "pair", "term"
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

	@Override
	public String getGrammarFileName() { return "PythonDict.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


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

	public PythonDictParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public Object result;
		public ValueContext v;
		public TerminalNode EOF() { return getToken(PythonDictParser.EOF, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
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
			((FileContext)_localctx).v = value();
			 ((FileContext)_localctx).result =  ((FileContext)_localctx).v.result; 
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
		public Object result;
		public PythonDictContext obj;
		public PythonListContext arr;
		public TermContext t;
		public PythonDictContext pythonDict() {
			return getRuleContext(PythonDictContext.class,0);
		}
		public PythonListContext pythonList() {
			return getRuleContext(PythonListContext.class,0);
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
				((ValueContext)_localctx).obj = pythonDict();
				 ((ValueContext)_localctx).result =  ((ValueContext)_localctx).obj.result; 
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 2);
				{
				setState(19);
				((ValueContext)_localctx).arr = pythonList();
				 ((ValueContext)_localctx).result =  ((ValueContext)_localctx).arr.result; 
				}
				break;
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case STRING:
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(22);
				((ValueContext)_localctx).t = term();
				 ((ValueContext)_localctx).result =  ((ValueContext)_localctx).t.result; 
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
	public static class PythonDictContext extends ParserRuleContext {
		public Map<String, Object> result;
		public PairContext p1;
		public PairContext p2;
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public PythonDictContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pythonDict; }
	}

	public final PythonDictContext pythonDict() throws RecognitionException {
		PythonDictContext _localctx = new PythonDictContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_pythonDict);

		        ((PythonDictContext)_localctx).result =  new LinkedHashMap<String, Object>();
		    
		int _la;
		try {
			int _alt;
			setState(46);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
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
				((PythonDictContext)_localctx).p1 = pair();
				 _localctx.result.put(((PythonDictContext)_localctx).p1.result.key, ((PythonDictContext)_localctx).p1.result.value); 
				setState(38);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(32);
						match(T__2);
						setState(33);
						((PythonDictContext)_localctx).p2 = pair();
						 _localctx.result.put(((PythonDictContext)_localctx).p2.result.key, ((PythonDictContext)_localctx).p2.result.value); 
						}
						} 
					}
					setState(40);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(41);
					match(T__2);
					}
				}

				setState(44);
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
	public static class PythonListContext extends ParserRuleContext {
		public List<Object> result;
		public ValueContext v1;
		public ValueContext v2;
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public PythonListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pythonList; }
	}

	public final PythonListContext pythonList() throws RecognitionException {
		PythonListContext _localctx = new PythonListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_pythonList);

		        ((PythonListContext)_localctx).result =  new ArrayList<Object>();
		    
		int _la;
		try {
			int _alt;
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(48);
				match(T__3);
				setState(49);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(50);
				match(T__3);
				setState(51);
				((PythonListContext)_localctx).v1 = value();
				 _localctx.result.add(((PythonListContext)_localctx).v1.result); 
				setState(59);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(53);
						match(T__2);
						setState(54);
						((PythonListContext)_localctx).v2 = value();
						 _localctx.result.add(((PythonListContext)_localctx).v2.result); 
						}
						} 
					}
					setState(61);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__2) {
					{
					setState(62);
					match(T__2);
					}
				}

				setState(65);
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
		public Pair result;
		public Token k;
		public ValueContext v;
		public TerminalNode STRING() { return getToken(PythonDictParser.STRING, 0); }
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
			setState(69);
			((PairContext)_localctx).k = match(STRING);
			setState(70);
			match(T__5);
			setState(71);
			((PairContext)_localctx).v = value();

			        ((PairContext)_localctx).result =  new Pair(parseString((((PairContext)_localctx).k!=null?((PairContext)_localctx).k.getText():null)), ((PairContext)_localctx).v.result);
			    
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
		public Object result;
		public Token STRING;
		public Token NUMBER;
		public TerminalNode STRING() { return getToken(PythonDictParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(PythonDictParser.NUMBER, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_term);
		try {
			setState(90);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				((TermContext)_localctx).STRING = match(STRING);

				        ((TermContext)_localctx).result =  parseString((((TermContext)_localctx).STRING!=null?((TermContext)_localctx).STRING.getText():null));
				    
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				((TermContext)_localctx).NUMBER = match(NUMBER);

				        String numText = (((TermContext)_localctx).NUMBER!=null?((TermContext)_localctx).NUMBER.getText():null);
				        // Détecter si c'est un entier ou un float
				        if (numText.contains(".") || numText.contains("e") || numText.contains("E")) {
				            ((TermContext)_localctx).result =  Double.parseDouble(numText);
				        } else {
				            try {
				                ((TermContext)_localctx).result =  Integer.parseInt(numText);
				            } catch (NumberFormatException e) {
				                ((TermContext)_localctx).result =  Long.parseLong(numText);
				            }
				        }
				    
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 3);
				{
				setState(78);
				match(T__6);
				 ((TermContext)_localctx).result =  true; 
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(80);
				match(T__7);
				 ((TermContext)_localctx).result =  false; 
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 5);
				{
				setState(82);
				match(T__8);
				 ((TermContext)_localctx).result =  null; 
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 6);
				{
				setState(84);
				match(T__9);
				 ((TermContext)_localctx).result =  true; 
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 7);
				{
				setState(86);
				match(T__10);
				 ((TermContext)_localctx).result =  false; 
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 8);
				{
				setState(88);
				match(T__11);
				 ((TermContext)_localctx).result =  null; 
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
		"\u0004\u0001\u0011]\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u001a\b\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002%\b\u0002\n\u0002\f\u0002(\t\u0002\u0001"+
		"\u0002\u0003\u0002+\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002/\b\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003:\b\u0003\n\u0003\f\u0003"+
		"=\t\u0003\u0001\u0003\u0003\u0003@\b\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003D\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005[\b"+
		"\u0005\u0001\u0005\u0000\u0000\u0006\u0000\u0002\u0004\u0006\b\n\u0000"+
		"\u0000e\u0000\f\u0001\u0000\u0000\u0000\u0002\u0019\u0001\u0000\u0000"+
		"\u0000\u0004.\u0001\u0000\u0000\u0000\u0006C\u0001\u0000\u0000\u0000\b"+
		"E\u0001\u0000\u0000\u0000\nZ\u0001\u0000\u0000\u0000\f\r\u0003\u0002\u0001"+
		"\u0000\r\u000e\u0006\u0000\uffff\uffff\u0000\u000e\u000f\u0005\u0000\u0000"+
		"\u0001\u000f\u0001\u0001\u0000\u0000\u0000\u0010\u0011\u0003\u0004\u0002"+
		"\u0000\u0011\u0012\u0006\u0001\uffff\uffff\u0000\u0012\u001a\u0001\u0000"+
		"\u0000\u0000\u0013\u0014\u0003\u0006\u0003\u0000\u0014\u0015\u0006\u0001"+
		"\uffff\uffff\u0000\u0015\u001a\u0001\u0000\u0000\u0000\u0016\u0017\u0003"+
		"\n\u0005\u0000\u0017\u0018\u0006\u0001\uffff\uffff\u0000\u0018\u001a\u0001"+
		"\u0000\u0000\u0000\u0019\u0010\u0001\u0000\u0000\u0000\u0019\u0013\u0001"+
		"\u0000\u0000\u0000\u0019\u0016\u0001\u0000\u0000\u0000\u001a\u0003\u0001"+
		"\u0000\u0000\u0000\u001b\u001c\u0005\u0001\u0000\u0000\u001c/\u0005\u0002"+
		"\u0000\u0000\u001d\u001e\u0005\u0001\u0000\u0000\u001e\u001f\u0003\b\u0004"+
		"\u0000\u001f&\u0006\u0002\uffff\uffff\u0000 !\u0005\u0003\u0000\u0000"+
		"!\"\u0003\b\u0004\u0000\"#\u0006\u0002\uffff\uffff\u0000#%\u0001\u0000"+
		"\u0000\u0000$ \u0001\u0000\u0000\u0000%(\u0001\u0000\u0000\u0000&$\u0001"+
		"\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000\'*\u0001\u0000\u0000\u0000"+
		"(&\u0001\u0000\u0000\u0000)+\u0005\u0003\u0000\u0000*)\u0001\u0000\u0000"+
		"\u0000*+\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0005\u0002"+
		"\u0000\u0000-/\u0001\u0000\u0000\u0000.\u001b\u0001\u0000\u0000\u0000"+
		".\u001d\u0001\u0000\u0000\u0000/\u0005\u0001\u0000\u0000\u000001\u0005"+
		"\u0004\u0000\u00001D\u0005\u0005\u0000\u000023\u0005\u0004\u0000\u0000"+
		"34\u0003\u0002\u0001\u00004;\u0006\u0003\uffff\uffff\u000056\u0005\u0003"+
		"\u0000\u000067\u0003\u0002\u0001\u000078\u0006\u0003\uffff\uffff\u0000"+
		"8:\u0001\u0000\u0000\u000095\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000"+
		"\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<?\u0001\u0000"+
		"\u0000\u0000=;\u0001\u0000\u0000\u0000>@\u0005\u0003\u0000\u0000?>\u0001"+
		"\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000"+
		"AB\u0005\u0005\u0000\u0000BD\u0001\u0000\u0000\u0000C0\u0001\u0000\u0000"+
		"\u0000C2\u0001\u0000\u0000\u0000D\u0007\u0001\u0000\u0000\u0000EF\u0005"+
		"\u000e\u0000\u0000FG\u0005\u0006\u0000\u0000GH\u0003\u0002\u0001\u0000"+
		"HI\u0006\u0004\uffff\uffff\u0000I\t\u0001\u0000\u0000\u0000JK\u0005\u000e"+
		"\u0000\u0000K[\u0006\u0005\uffff\uffff\u0000LM\u0005\u000f\u0000\u0000"+
		"M[\u0006\u0005\uffff\uffff\u0000NO\u0005\u0007\u0000\u0000O[\u0006\u0005"+
		"\uffff\uffff\u0000PQ\u0005\b\u0000\u0000Q[\u0006\u0005\uffff\uffff\u0000"+
		"RS\u0005\t\u0000\u0000S[\u0006\u0005\uffff\uffff\u0000TU\u0005\n\u0000"+
		"\u0000U[\u0006\u0005\uffff\uffff\u0000VW\u0005\u000b\u0000\u0000W[\u0006"+
		"\u0005\uffff\uffff\u0000XY\u0005\f\u0000\u0000Y[\u0006\u0005\uffff\uffff"+
		"\u0000ZJ\u0001\u0000\u0000\u0000ZL\u0001\u0000\u0000\u0000ZN\u0001\u0000"+
		"\u0000\u0000ZP\u0001\u0000\u0000\u0000ZR\u0001\u0000\u0000\u0000ZT\u0001"+
		"\u0000\u0000\u0000ZV\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000"+
		"[\u000b\u0001\u0000\u0000\u0000\b\u0019&*.;?CZ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}