// Generated from java-escape by ANTLR 4.11.1
package net.hep.ami.utility.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CSVParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.11.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, STRING=2, VALUE=3, BREAK=4, COMMENT=5, WS=6;
	public static final int
		RULE_file = 0, RULE_row = 1, RULE_field = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "row", "field"
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

	@Override
	public String getGrammarFileName() { return "java-escape"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CSVParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public List<List<String>> v;
		public RowContext row;
		public TerminalNode EOF() { return getToken(CSVParser.EOF, 0); }
		public List<RowContext> row() {
			return getRuleContexts(RowContext.class);
		}
		public RowContext row(int i) {
			return getRuleContext(RowContext.class,i);
		}
		public List<TerminalNode> BREAK() { return getTokens(CSVParser.BREAK); }
		public TerminalNode BREAK(int i) {
			return getToken(CSVParser.BREAK, i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		 ((FileContext)_localctx).v =  new ArrayList<List<String>>(); 
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(13);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(11);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case BREAK:
						{
						setState(6);
						match(BREAK);
						}
						break;
					case T__0:
					case STRING:
					case VALUE:
						{
						setState(7);
						((FileContext)_localctx).row = row();
						 _localctx.v.add(((FileContext)_localctx).row.v); 
						setState(9);
						match(BREAK);
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(15);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			setState(21);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EOF:
				{
				setState(16);
				match(EOF);
				}
				break;
			case T__0:
			case STRING:
			case VALUE:
				{
				setState(17);
				((FileContext)_localctx).row = row();
				 _localctx.v.add(((FileContext)_localctx).row.v); 
				setState(19);
				match(EOF);
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	@SuppressWarnings("CheckReturnValue")
	public static class RowContext extends ParserRuleContext {
		public List<String> v;
		public FieldContext field;
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public RowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_row; }
	}

	public final RowContext row() throws RecognitionException {
		RowContext _localctx = new RowContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_row);
		 ((RowContext)_localctx).v =  new ArrayList<String>(); 
		int _la;
		try {
			setState(49);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
			case VALUE:
				enterOuterAlt(_localctx, 1);
				{
				setState(23);
				((RowContext)_localctx).field = field();
				 _localctx.v.add(((RowContext)_localctx).field.v); 
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__0) {
					{
					{
					setState(25);
					match(T__0);
					setState(30);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case STRING:
					case VALUE:
						{
						setState(26);
						((RowContext)_localctx).field = field();
						 _localctx.v.add(((RowContext)_localctx).field.v); 
						}
						break;
					case EOF:
					case T__0:
					case BREAK:
						{
						 _localctx.v.add(""); 
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					}
					setState(36);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				 _localctx.v.add(   ""   ); 
				setState(45); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(38);
					match(T__0);
					setState(43);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case STRING:
					case VALUE:
						{
						setState(39);
						((RowContext)_localctx).field = field();
						 _localctx.v.add(((RowContext)_localctx).field.v); 
						}
						break;
					case EOF:
					case T__0:
					case BREAK:
						{
						 _localctx.v.add(""); 
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					}
					setState(47); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__0 );
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
	public static class FieldContext extends ParserRuleContext {
		public String v;
		public Token STRING;
		public Token VALUE;
		public TerminalNode STRING() { return getToken(CSVParser.STRING, 0); }
		public TerminalNode VALUE() { return getToken(CSVParser.VALUE, 0); }
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_field);
		try {
			setState(55);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(51);
				((FieldContext)_localctx).STRING = match(STRING);
				 ((FieldContext)_localctx).v =  Utility.javaStringToText((((FieldContext)_localctx).STRING!=null?((FieldContext)_localctx).STRING.getText():null)); 
				}
				break;
			case VALUE:
				enterOuterAlt(_localctx, 2);
				{
				setState(53);
				((FieldContext)_localctx).VALUE = match(VALUE);
				 ((FieldContext)_localctx).v =  Utility.javaStringToText((((FieldContext)_localctx).VALUE!=null?((FieldContext)_localctx).VALUE.getText():null)); 
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
		"\u0004\u0001\u0006:\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0005\u0000\f\b\u0000\n\u0000\f\u0000\u000f\t\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u0016\b\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u001f\b\u0001\u0005\u0001!\b\u0001\n\u0001\f"+
		"\u0001$\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0003\u0001,\b\u0001\u0004\u0001.\b\u0001\u000b\u0001"+
		"\f\u0001/\u0003\u00012\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0003\u00028\b\u0002\u0001\u0002\u0000\u0000\u0003\u0000\u0002"+
		"\u0004\u0000\u0000?\u0000\r\u0001\u0000\u0000\u0000\u00021\u0001\u0000"+
		"\u0000\u0000\u00047\u0001\u0000\u0000\u0000\u0006\f\u0005\u0004\u0000"+
		"\u0000\u0007\b\u0003\u0002\u0001\u0000\b\t\u0006\u0000\uffff\uffff\u0000"+
		"\t\n\u0005\u0004\u0000\u0000\n\f\u0001\u0000\u0000\u0000\u000b\u0006\u0001"+
		"\u0000\u0000\u0000\u000b\u0007\u0001\u0000\u0000\u0000\f\u000f\u0001\u0000"+
		"\u0000\u0000\r\u000b\u0001\u0000\u0000\u0000\r\u000e\u0001\u0000\u0000"+
		"\u0000\u000e\u0015\u0001\u0000\u0000\u0000\u000f\r\u0001\u0000\u0000\u0000"+
		"\u0010\u0016\u0005\u0000\u0000\u0001\u0011\u0012\u0003\u0002\u0001\u0000"+
		"\u0012\u0013\u0006\u0000\uffff\uffff\u0000\u0013\u0014\u0005\u0000\u0000"+
		"\u0001\u0014\u0016\u0001\u0000\u0000\u0000\u0015\u0010\u0001\u0000\u0000"+
		"\u0000\u0015\u0011\u0001\u0000\u0000\u0000\u0016\u0001\u0001\u0000\u0000"+
		"\u0000\u0017\u0018\u0003\u0004\u0002\u0000\u0018\"\u0006\u0001\uffff\uffff"+
		"\u0000\u0019\u001e\u0005\u0001\u0000\u0000\u001a\u001b\u0003\u0004\u0002"+
		"\u0000\u001b\u001c\u0006\u0001\uffff\uffff\u0000\u001c\u001f\u0001\u0000"+
		"\u0000\u0000\u001d\u001f\u0006\u0001\uffff\uffff\u0000\u001e\u001a\u0001"+
		"\u0000\u0000\u0000\u001e\u001d\u0001\u0000\u0000\u0000\u001f!\u0001\u0000"+
		"\u0000\u0000 \u0019\u0001\u0000\u0000\u0000!$\u0001\u0000\u0000\u0000"+
		"\" \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#2\u0001\u0000\u0000"+
		"\u0000$\"\u0001\u0000\u0000\u0000%-\u0006\u0001\uffff\uffff\u0000&+\u0005"+
		"\u0001\u0000\u0000\'(\u0003\u0004\u0002\u0000()\u0006\u0001\uffff\uffff"+
		"\u0000),\u0001\u0000\u0000\u0000*,\u0006\u0001\uffff\uffff\u0000+\'\u0001"+
		"\u0000\u0000\u0000+*\u0001\u0000\u0000\u0000,.\u0001\u0000\u0000\u0000"+
		"-&\u0001\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/-\u0001\u0000\u0000"+
		"\u0000/0\u0001\u0000\u0000\u000002\u0001\u0000\u0000\u00001\u0017\u0001"+
		"\u0000\u0000\u00001%\u0001\u0000\u0000\u00002\u0003\u0001\u0000\u0000"+
		"\u000034\u0005\u0002\u0000\u000048\u0006\u0002\uffff\uffff\u000056\u0005"+
		"\u0003\u0000\u000068\u0006\u0002\uffff\uffff\u000073\u0001\u0000\u0000"+
		"\u000075\u0001\u0000\u0000\u00008\u0005\u0001\u0000\u0000\u0000\t\u000b"+
		"\r\u0015\u001e\"+/17";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}