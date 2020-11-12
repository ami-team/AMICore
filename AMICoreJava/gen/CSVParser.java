// Generated from /Users/jodier/IdeaProjects/AMICore/AMICoreJava/src/main/antlr4/net/hep/ami/utility/parser/CSV.g4 by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CSVParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

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
	public String getGrammarFileName() { return "CSV.g4"; }

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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CSVVisitor ) return ((CSVVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterRow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitRow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CSVVisitor ) return ((CSVVisitor<? extends T>)visitor).visitRow(this);
			else return visitor.visitChildren(this);
		}
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
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).enterField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CSVListener ) ((CSVListener)listener).exitField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CSVVisitor ) return ((CSVVisitor<? extends T>)visitor).visitField(this);
			else return visitor.visitChildren(this);
		}
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\b<\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\2\7\2\16\n\2\f\2\16\2\21\13\2\3\2\3\2\3\2"+
		"\3\2\3\2\5\2\30\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3!\n\3\7\3#\n\3\f\3"+
		"\16\3&\13\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3.\n\3\6\3\60\n\3\r\3\16\3\61\5"+
		"\3\64\n\3\3\4\3\4\3\4\3\4\5\4:\n\4\3\4\2\2\5\2\4\6\2\2\2A\2\17\3\2\2\2"+
		"\4\63\3\2\2\2\69\3\2\2\2\b\16\7\6\2\2\t\n\5\4\3\2\n\13\b\2\1\2\13\f\7"+
		"\6\2\2\f\16\3\2\2\2\r\b\3\2\2\2\r\t\3\2\2\2\16\21\3\2\2\2\17\r\3\2\2\2"+
		"\17\20\3\2\2\2\20\27\3\2\2\2\21\17\3\2\2\2\22\30\7\2\2\3\23\24\5\4\3\2"+
		"\24\25\b\2\1\2\25\26\7\2\2\3\26\30\3\2\2\2\27\22\3\2\2\2\27\23\3\2\2\2"+
		"\30\3\3\2\2\2\31\32\5\6\4\2\32$\b\3\1\2\33 \7\3\2\2\34\35\5\6\4\2\35\36"+
		"\b\3\1\2\36!\3\2\2\2\37!\b\3\1\2 \34\3\2\2\2 \37\3\2\2\2!#\3\2\2\2\"\33"+
		"\3\2\2\2#&\3\2\2\2$\"\3\2\2\2$%\3\2\2\2%\64\3\2\2\2&$\3\2\2\2\'/\b\3\1"+
		"\2(-\7\3\2\2)*\5\6\4\2*+\b\3\1\2+.\3\2\2\2,.\b\3\1\2-)\3\2\2\2-,\3\2\2"+
		"\2.\60\3\2\2\2/(\3\2\2\2\60\61\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\64"+
		"\3\2\2\2\63\31\3\2\2\2\63\'\3\2\2\2\64\5\3\2\2\2\65\66\7\4\2\2\66:\b\4"+
		"\1\2\678\7\5\2\28:\b\4\1\29\65\3\2\2\29\67\3\2\2\2:\7\3\2\2\2\13\r\17"+
		"\27 $-\61\639";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}