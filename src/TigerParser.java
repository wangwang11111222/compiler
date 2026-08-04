// Generated from Tiger.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TigerParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ARRAY=1, BEGIN=2, BREAK=3, DO=4, ELSE=5, END=6, ENDDO=7, ENDIF=8, FLOAT=9, 
		FOR=10, FUNCTION=11, IF=12, INT=13, LET=14, OF=15, PROGRAM=16, RETURN=17, 
		STATIC=18, THEN=19, TO=20, TYPE=21, VAR=22, WHILE=23, COMMA=24, COLON=25, 
		SEMI=26, LPAREN=27, RPAREN=28, LBRACK=29, RBRACK=30, ADD=31, SUB=32, MUL=33, 
		DIV=34, POW=35, EQ=36, NE=37, LT=38, GT=39, LE=40, GE=41, AND=42, OR=43, 
		ASSIGN=44, TASSIGN=45, ID=46, INTLIT=47, FLOATLIT=48, COMMENT=49, WS=50;
	public static final int
		RULE_tiger_program = 0, RULE_declaration_segment = 1, RULE_type_declaration_list = 2, 
		RULE_var_declaration_list = 3, RULE_funct_list = 4, RULE_type_declaration = 5, 
		RULE_type = 6, RULE_base_type = 7, RULE_var_declaration = 8, RULE_storage_class = 9, 
		RULE_id_list = 10, RULE_optional_init = 11, RULE_funct = 12, RULE_param_list = 13, 
		RULE_param_list_tail = 14, RULE_ret_type = 15, RULE_param = 16, RULE_stat_seq = 17, 
		RULE_stat = 18, RULE_optreturn = 19, RULE_optprefix = 20, RULE_expr = 21, 
		RULE_constant = 22, RULE_expr_list = 23, RULE_expr_list_tail = 24, RULE_value = 25, 
		RULE_value_tail = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"tiger_program", "declaration_segment", "type_declaration_list", "var_declaration_list", 
			"funct_list", "type_declaration", "type", "base_type", "var_declaration", 
			"storage_class", "id_list", "optional_init", "funct", "param_list", "param_list_tail", 
			"ret_type", "param", "stat_seq", "stat", "optreturn", "optprefix", "expr", 
			"constant", "expr_list", "expr_list_tail", "value", "value_tail"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'array'", "'begin'", "'break'", "'do'", "'else'", "'end'", "'enddo'", 
			"'endif'", "'float'", "'for'", "'function'", "'if'", "'int'", "'let'", 
			"'of'", "'program'", "'return'", "'static'", "'then'", "'to'", "'type'", 
			"'var'", "'while'", "','", "':'", "';'", "'('", "')'", "'['", "']'", 
			"'+'", "'-'", "'*'", "'/'", "'**'", "'=='", "'!='", "'<'", "'>'", "'<='", 
			"'>='", "'&'", "'|'", "':='", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ARRAY", "BEGIN", "BREAK", "DO", "ELSE", "END", "ENDDO", "ENDIF", 
			"FLOAT", "FOR", "FUNCTION", "IF", "INT", "LET", "OF", "PROGRAM", "RETURN", 
			"STATIC", "THEN", "TO", "TYPE", "VAR", "WHILE", "COMMA", "COLON", "SEMI", 
			"LPAREN", "RPAREN", "LBRACK", "RBRACK", "ADD", "SUB", "MUL", "DIV", "POW", 
			"EQ", "NE", "LT", "GT", "LE", "GE", "AND", "OR", "ASSIGN", "TASSIGN", 
			"ID", "INTLIT", "FLOATLIT", "COMMENT", "WS"
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
	public String getGrammarFileName() { return "Tiger.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TigerParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Tiger_programContext extends ParserRuleContext {
		public TerminalNode PROGRAM() { return getToken(TigerParser.PROGRAM, 0); }
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TerminalNode LET() { return getToken(TigerParser.LET, 0); }
		public Declaration_segmentContext declaration_segment() {
			return getRuleContext(Declaration_segmentContext.class,0);
		}
		public TerminalNode BEGIN() { return getToken(TigerParser.BEGIN, 0); }
		public Funct_listContext funct_list() {
			return getRuleContext(Funct_listContext.class,0);
		}
		public TerminalNode END() { return getToken(TigerParser.END, 0); }
		public TerminalNode EOF() { return getToken(TigerParser.EOF, 0); }
		public Tiger_programContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tiger_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterTiger_program(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitTiger_program(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitTiger_program(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Tiger_programContext tiger_program() throws RecognitionException {
		Tiger_programContext _localctx = new Tiger_programContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_tiger_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(PROGRAM);
			setState(55);
			match(ID);
			setState(56);
			match(LET);
			setState(57);
			declaration_segment();
			setState(58);
			match(BEGIN);
			setState(59);
			funct_list();
			setState(60);
			match(END);
			setState(61);
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
	public static class Declaration_segmentContext extends ParserRuleContext {
		public Type_declaration_listContext type_declaration_list() {
			return getRuleContext(Type_declaration_listContext.class,0);
		}
		public Var_declaration_listContext var_declaration_list() {
			return getRuleContext(Var_declaration_listContext.class,0);
		}
		public Declaration_segmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration_segment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterDeclaration_segment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitDeclaration_segment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitDeclaration_segment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Declaration_segmentContext declaration_segment() throws RecognitionException {
		Declaration_segmentContext _localctx = new Declaration_segmentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declaration_segment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			type_declaration_list();
			setState(64);
			var_declaration_list();
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
	public static class Type_declaration_listContext extends ParserRuleContext {
		public Type_declarationContext type_declaration() {
			return getRuleContext(Type_declarationContext.class,0);
		}
		public Type_declaration_listContext type_declaration_list() {
			return getRuleContext(Type_declaration_listContext.class,0);
		}
		public Type_declaration_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_declaration_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterType_declaration_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitType_declaration_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitType_declaration_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_declaration_listContext type_declaration_list() throws RecognitionException {
		Type_declaration_listContext _localctx = new Type_declaration_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type_declaration_list);
		try {
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TYPE:
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				type_declaration();
				setState(67);
				type_declaration_list();
				}
				break;
			case BEGIN:
			case STATIC:
			case VAR:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class Var_declaration_listContext extends ParserRuleContext {
		public Var_declarationContext var_declaration() {
			return getRuleContext(Var_declarationContext.class,0);
		}
		public Var_declaration_listContext var_declaration_list() {
			return getRuleContext(Var_declaration_listContext.class,0);
		}
		public Var_declaration_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_declaration_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterVar_declaration_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitVar_declaration_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitVar_declaration_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_declaration_listContext var_declaration_list() throws RecognitionException {
		Var_declaration_listContext _localctx = new Var_declaration_listContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_var_declaration_list);
		try {
			setState(76);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STATIC:
			case VAR:
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				var_declaration();
				setState(73);
				var_declaration_list();
				}
				break;
			case BEGIN:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class Funct_listContext extends ParserRuleContext {
		public FunctContext funct() {
			return getRuleContext(FunctContext.class,0);
		}
		public Funct_listContext funct_list() {
			return getRuleContext(Funct_listContext.class,0);
		}
		public Funct_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funct_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterFunct_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitFunct_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitFunct_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Funct_listContext funct_list() throws RecognitionException {
		Funct_listContext _localctx = new Funct_listContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_funct_list);
		try {
			setState(82);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FUNCTION:
				enterOuterAlt(_localctx, 1);
				{
				setState(78);
				funct();
				setState(79);
				funct_list();
				}
				break;
			case END:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class Type_declarationContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(TigerParser.TYPE, 0); }
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TerminalNode TASSIGN() { return getToken(TigerParser.TASSIGN, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(TigerParser.SEMI, 0); }
		public Type_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterType_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitType_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitType_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_declarationContext type_declaration() throws RecognitionException {
		Type_declarationContext _localctx = new Type_declarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_type_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(TYPE);
			setState(85);
			match(ID);
			setState(86);
			match(TASSIGN);
			setState(87);
			type();
			setState(88);
			match(SEMI);
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
	public static class TypeContext extends ParserRuleContext {
		public Base_typeContext base_type() {
			return getRuleContext(Base_typeContext.class,0);
		}
		public TerminalNode ARRAY() { return getToken(TigerParser.ARRAY, 0); }
		public TerminalNode LBRACK() { return getToken(TigerParser.LBRACK, 0); }
		public TerminalNode INTLIT() { return getToken(TigerParser.INTLIT, 0); }
		public TerminalNode RBRACK() { return getToken(TigerParser.RBRACK, 0); }
		public TerminalNode OF() { return getToken(TigerParser.OF, 0); }
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_type);
		try {
			setState(98);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLOAT:
			case INT:
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				base_type();
				}
				break;
			case ARRAY:
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				match(ARRAY);
				setState(92);
				match(LBRACK);
				setState(93);
				match(INTLIT);
				setState(94);
				match(RBRACK);
				setState(95);
				match(OF);
				setState(96);
				base_type();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(97);
				match(ID);
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
	public static class Base_typeContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(TigerParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(TigerParser.FLOAT, 0); }
		public Base_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterBase_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitBase_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitBase_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Base_typeContext base_type() throws RecognitionException {
		Base_typeContext _localctx = new Base_typeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_base_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			_la = _input.LA(1);
			if ( !(_la==FLOAT || _la==INT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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
	public static class Var_declarationContext extends ParserRuleContext {
		public Storage_classContext storage_class() {
			return getRuleContext(Storage_classContext.class,0);
		}
		public Id_listContext id_list() {
			return getRuleContext(Id_listContext.class,0);
		}
		public TerminalNode COLON() { return getToken(TigerParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Optional_initContext optional_init() {
			return getRuleContext(Optional_initContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(TigerParser.SEMI, 0); }
		public Var_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterVar_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitVar_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitVar_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Var_declarationContext var_declaration() throws RecognitionException {
		Var_declarationContext _localctx = new Var_declarationContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_var_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			storage_class();
			setState(103);
			id_list();
			setState(104);
			match(COLON);
			setState(105);
			type();
			setState(106);
			optional_init();
			setState(107);
			match(SEMI);
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
	public static class Storage_classContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(TigerParser.VAR, 0); }
		public TerminalNode STATIC() { return getToken(TigerParser.STATIC, 0); }
		public Storage_classContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_storage_class; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterStorage_class(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitStorage_class(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitStorage_class(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Storage_classContext storage_class() throws RecognitionException {
		Storage_classContext _localctx = new Storage_classContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_storage_class);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_la = _input.LA(1);
			if ( !(_la==STATIC || _la==VAR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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
	public static class Id_listContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TerminalNode COMMA() { return getToken(TigerParser.COMMA, 0); }
		public Id_listContext id_list() {
			return getRuleContext(Id_listContext.class,0);
		}
		public Id_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterId_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitId_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitId_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Id_listContext id_list() throws RecognitionException {
		Id_listContext _localctx = new Id_listContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_id_list);
		try {
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(111);
				match(ID);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				match(ID);
				setState(113);
				match(COMMA);
				setState(114);
				id_list();
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
	public static class Optional_initContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(TigerParser.ASSIGN, 0); }
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public Optional_initContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optional_init; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterOptional_init(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitOptional_init(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitOptional_init(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Optional_initContext optional_init() throws RecognitionException {
		Optional_initContext _localctx = new Optional_initContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_optional_init);
		try {
			setState(120);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ASSIGN:
				enterOuterAlt(_localctx, 1);
				{
				setState(117);
				match(ASSIGN);
				setState(118);
				constant();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class FunctContext extends ParserRuleContext {
		public TerminalNode FUNCTION() { return getToken(TigerParser.FUNCTION, 0); }
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TerminalNode LPAREN() { return getToken(TigerParser.LPAREN, 0); }
		public Param_listContext param_list() {
			return getRuleContext(Param_listContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TigerParser.RPAREN, 0); }
		public Ret_typeContext ret_type() {
			return getRuleContext(Ret_typeContext.class,0);
		}
		public TerminalNode BEGIN() { return getToken(TigerParser.BEGIN, 0); }
		public Stat_seqContext stat_seq() {
			return getRuleContext(Stat_seqContext.class,0);
		}
		public TerminalNode END() { return getToken(TigerParser.END, 0); }
		public FunctContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterFunct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitFunct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitFunct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctContext funct() throws RecognitionException {
		FunctContext _localctx = new FunctContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_funct);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(FUNCTION);
			setState(123);
			match(ID);
			setState(124);
			match(LPAREN);
			setState(125);
			param_list();
			setState(126);
			match(RPAREN);
			setState(127);
			ret_type();
			setState(128);
			match(BEGIN);
			setState(129);
			stat_seq();
			setState(130);
			match(END);
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
	public static class Param_listContext extends ParserRuleContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public Param_list_tailContext param_list_tail() {
			return getRuleContext(Param_list_tailContext.class,0);
		}
		public Param_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterParam_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitParam_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitParam_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Param_listContext param_list() throws RecognitionException {
		Param_listContext _localctx = new Param_listContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_param_list);
		try {
			setState(136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(132);
				param();
				setState(133);
				param_list_tail();
				}
				break;
			case RPAREN:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class Param_list_tailContext extends ParserRuleContext {
		public TerminalNode COMMA() { return getToken(TigerParser.COMMA, 0); }
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public Param_list_tailContext param_list_tail() {
			return getRuleContext(Param_list_tailContext.class,0);
		}
		public Param_list_tailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_list_tail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterParam_list_tail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitParam_list_tail(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitParam_list_tail(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Param_list_tailContext param_list_tail() throws RecognitionException {
		Param_list_tailContext _localctx = new Param_list_tailContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_param_list_tail);
		try {
			setState(143);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMMA:
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				match(COMMA);
				setState(139);
				param();
				setState(140);
				param_list_tail();
				}
				break;
			case RPAREN:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class Ret_typeContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(TigerParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Ret_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ret_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterRet_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitRet_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitRet_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ret_typeContext ret_type() throws RecognitionException {
		Ret_typeContext _localctx = new Ret_typeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_ret_type);
		try {
			setState(148);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLON:
				enterOuterAlt(_localctx, 1);
				{
				setState(145);
				match(COLON);
				setState(146);
				type();
				}
				break;
			case BEGIN:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class ParamContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TerminalNode COLON() { return getToken(TigerParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(ID);
			setState(151);
			match(COLON);
			setState(152);
			type();
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
	public static class Stat_seqContext extends ParserRuleContext {
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public Stat_seqContext stat_seq() {
			return getRuleContext(Stat_seqContext.class,0);
		}
		public Stat_seqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat_seq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterStat_seq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitStat_seq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitStat_seq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Stat_seqContext stat_seq() throws RecognitionException {
		Stat_seqContext _localctx = new Stat_seqContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_stat_seq);
		try {
			setState(158);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				stat();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
				stat();
				setState(156);
				stat_seq();
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
	public static class StatContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(TigerParser.ASSIGN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(TigerParser.SEMI, 0); }
		public TerminalNode IF() { return getToken(TigerParser.IF, 0); }
		public TerminalNode THEN() { return getToken(TigerParser.THEN, 0); }
		public List<Stat_seqContext> stat_seq() {
			return getRuleContexts(Stat_seqContext.class);
		}
		public Stat_seqContext stat_seq(int i) {
			return getRuleContext(Stat_seqContext.class,i);
		}
		public TerminalNode ENDIF() { return getToken(TigerParser.ENDIF, 0); }
		public TerminalNode ELSE() { return getToken(TigerParser.ELSE, 0); }
		public TerminalNode WHILE() { return getToken(TigerParser.WHILE, 0); }
		public TerminalNode DO() { return getToken(TigerParser.DO, 0); }
		public TerminalNode ENDDO() { return getToken(TigerParser.ENDDO, 0); }
		public TerminalNode FOR() { return getToken(TigerParser.FOR, 0); }
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public TerminalNode TO() { return getToken(TigerParser.TO, 0); }
		public OptprefixContext optprefix() {
			return getRuleContext(OptprefixContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TigerParser.LPAREN, 0); }
		public Expr_listContext expr_list() {
			return getRuleContext(Expr_listContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TigerParser.RPAREN, 0); }
		public TerminalNode BREAK() { return getToken(TigerParser.BREAK, 0); }
		public TerminalNode RETURN() { return getToken(TigerParser.RETURN, 0); }
		public OptreturnContext optreturn() {
			return getRuleContext(OptreturnContext.class,0);
		}
		public TerminalNode LET() { return getToken(TigerParser.LET, 0); }
		public Declaration_segmentContext declaration_segment() {
			return getRuleContext(Declaration_segmentContext.class,0);
		}
		public TerminalNode BEGIN() { return getToken(TigerParser.BEGIN, 0); }
		public TerminalNode END() { return getToken(TigerParser.END, 0); }
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitStat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		StatContext _localctx = new StatContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_stat);
		try {
			setState(218);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(160);
				value();
				setState(161);
				match(ASSIGN);
				setState(162);
				expr(0);
				setState(163);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				match(IF);
				setState(166);
				expr(0);
				setState(167);
				match(THEN);
				setState(168);
				stat_seq();
				setState(169);
				match(ENDIF);
				setState(170);
				match(SEMI);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(172);
				match(IF);
				setState(173);
				expr(0);
				setState(174);
				match(THEN);
				setState(175);
				stat_seq();
				setState(176);
				match(ELSE);
				setState(177);
				stat_seq();
				setState(178);
				match(ENDIF);
				setState(179);
				match(SEMI);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(181);
				match(WHILE);
				setState(182);
				expr(0);
				setState(183);
				match(DO);
				setState(184);
				stat_seq();
				setState(185);
				match(ENDDO);
				setState(186);
				match(SEMI);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(188);
				match(FOR);
				setState(189);
				match(ID);
				setState(190);
				match(ASSIGN);
				setState(191);
				expr(0);
				setState(192);
				match(TO);
				setState(193);
				expr(0);
				setState(194);
				match(DO);
				setState(195);
				stat_seq();
				setState(196);
				match(ENDDO);
				setState(197);
				match(SEMI);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(199);
				optprefix();
				setState(200);
				match(ID);
				setState(201);
				match(LPAREN);
				setState(202);
				expr_list();
				setState(203);
				match(RPAREN);
				setState(204);
				match(SEMI);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(206);
				match(BREAK);
				setState(207);
				match(SEMI);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(208);
				match(RETURN);
				setState(209);
				optreturn();
				setState(210);
				match(SEMI);
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(212);
				match(LET);
				setState(213);
				declaration_segment();
				setState(214);
				match(BEGIN);
				setState(215);
				stat_seq();
				setState(216);
				match(END);
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
	public static class OptreturnContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public OptreturnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optreturn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterOptreturn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitOptreturn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitOptreturn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptreturnContext optreturn() throws RecognitionException {
		OptreturnContext _localctx = new OptreturnContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_optreturn);
		try {
			setState(222);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
			case ID:
			case INTLIT:
			case FLOATLIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(220);
				expr(0);
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class OptprefixContext extends ParserRuleContext {
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(TigerParser.ASSIGN, 0); }
		public OptprefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optprefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterOptprefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitOptprefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitOptprefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptprefixContext optprefix() throws RecognitionException {
		OptprefixContext _localctx = new OptprefixContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_optprefix);
		try {
			setState(228);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(224);
				value();
				setState(225);
				match(ASSIGN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class ExprContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(TigerParser.LPAREN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(TigerParser.RPAREN, 0); }
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode POW() { return getToken(TigerParser.POW, 0); }
		public TerminalNode MUL() { return getToken(TigerParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(TigerParser.DIV, 0); }
		public TerminalNode ADD() { return getToken(TigerParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(TigerParser.SUB, 0); }
		public TerminalNode EQ() { return getToken(TigerParser.EQ, 0); }
		public TerminalNode NE() { return getToken(TigerParser.NE, 0); }
		public TerminalNode LT() { return getToken(TigerParser.LT, 0); }
		public TerminalNode GT() { return getToken(TigerParser.GT, 0); }
		public TerminalNode LE() { return getToken(TigerParser.LE, 0); }
		public TerminalNode GE() { return getToken(TigerParser.GE, 0); }
		public TerminalNode AND() { return getToken(TigerParser.AND, 0); }
		public TerminalNode OR() { return getToken(TigerParser.OR, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				setState(231);
				match(LPAREN);
				setState(232);
				expr(0);
				setState(233);
				match(RPAREN);
				}
				break;
			case INTLIT:
			case FLOATLIT:
				{
				setState(235);
				constant();
				}
				break;
			case ID:
				{
				setState(236);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(259);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(257);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(239);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(240);
						match(POW);
						setState(241);
						expr(6);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(242);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(243);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(244);
						expr(6);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(245);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(246);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(247);
						expr(5);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(248);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(249);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 4329327034368L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(250);
						expr(4);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(251);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(252);
						match(AND);
						setState(253);
						expr(3);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(254);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(255);
						match(OR);
						setState(256);
						expr(2);
						}
						break;
					}
					} 
				}
				setState(261);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstantContext extends ParserRuleContext {
		public TerminalNode INTLIT() { return getToken(TigerParser.INTLIT, 0); }
		public TerminalNode FLOATLIT() { return getToken(TigerParser.FLOATLIT, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			_la = _input.LA(1);
			if ( !(_la==INTLIT || _la==FLOATLIT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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
	public static class Expr_listContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Expr_list_tailContext expr_list_tail() {
			return getRuleContext(Expr_list_tailContext.class,0);
		}
		public Expr_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterExpr_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitExpr_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitExpr_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expr_listContext expr_list() throws RecognitionException {
		Expr_listContext _localctx = new Expr_listContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_expr_list);
		try {
			setState(268);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
			case ID:
			case INTLIT:
			case FLOATLIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(264);
				expr(0);
				setState(265);
				expr_list_tail();
				}
				break;
			case RPAREN:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class Expr_list_tailContext extends ParserRuleContext {
		public TerminalNode COMMA() { return getToken(TigerParser.COMMA, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Expr_list_tailContext expr_list_tail() {
			return getRuleContext(Expr_list_tailContext.class,0);
		}
		public Expr_list_tailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr_list_tail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterExpr_list_tail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitExpr_list_tail(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitExpr_list_tail(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Expr_list_tailContext expr_list_tail() throws RecognitionException {
		Expr_list_tailContext _localctx = new Expr_list_tailContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_expr_list_tail);
		try {
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMMA:
				enterOuterAlt(_localctx, 1);
				{
				setState(270);
				match(COMMA);
				setState(271);
				expr(0);
				setState(272);
				expr_list_tail();
				}
				break;
			case RPAREN:
				enterOuterAlt(_localctx, 2);
				{
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
	public static class ValueContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TigerParser.ID, 0); }
		public Value_tailContext value_tail() {
			return getRuleContext(Value_tailContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(ID);
			setState(278);
			value_tail();
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
	public static class Value_tailContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(TigerParser.LBRACK, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RBRACK() { return getToken(TigerParser.RBRACK, 0); }
		public Value_tailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value_tail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).enterValue_tail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TigerListener ) ((TigerListener)listener).exitValue_tail(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TigerVisitor ) return ((TigerVisitor<? extends T>)visitor).visitValue_tail(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Value_tailContext value_tail() throws RecognitionException {
		Value_tailContext _localctx = new Value_tailContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_value_tail);
		try {
			setState(285);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(280);
				match(LBRACK);
				setState(281);
				expr(0);
				setState(282);
				match(RBRACK);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 21:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		case 4:
			return precpred(_ctx, 2);
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u00012\u0120\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002G\b\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0003\u0003M\b\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u0004S\b\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0003\u0006c\b\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0003\nt\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"y\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0089\b\r\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u0090"+
		"\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0095\b\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0003\u0011\u009f\b\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u00db\b\u0012\u0001\u0013\u0001\u0013"+
		"\u0003\u0013\u00df\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0003\u0014\u00e5\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00ee\b\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015"+
		"\u0102\b\u0015\n\u0015\f\u0015\u0105\t\u0015\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u010d\b\u0017\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u0114"+
		"\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u011e\b\u001a\u0001\u001a\u0000"+
		"\u0001*\u001b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(*,.024\u0000\u0006\u0002\u0000\t\t\r\r\u0002"+
		"\u0000\u0012\u0012\u0016\u0016\u0001\u0000!\"\u0001\u0000\u001f \u0001"+
		"\u0000$)\u0001\u0000/0\u0124\u00006\u0001\u0000\u0000\u0000\u0002?\u0001"+
		"\u0000\u0000\u0000\u0004F\u0001\u0000\u0000\u0000\u0006L\u0001\u0000\u0000"+
		"\u0000\bR\u0001\u0000\u0000\u0000\nT\u0001\u0000\u0000\u0000\fb\u0001"+
		"\u0000\u0000\u0000\u000ed\u0001\u0000\u0000\u0000\u0010f\u0001\u0000\u0000"+
		"\u0000\u0012m\u0001\u0000\u0000\u0000\u0014s\u0001\u0000\u0000\u0000\u0016"+
		"x\u0001\u0000\u0000\u0000\u0018z\u0001\u0000\u0000\u0000\u001a\u0088\u0001"+
		"\u0000\u0000\u0000\u001c\u008f\u0001\u0000\u0000\u0000\u001e\u0094\u0001"+
		"\u0000\u0000\u0000 \u0096\u0001\u0000\u0000\u0000\"\u009e\u0001\u0000"+
		"\u0000\u0000$\u00da\u0001\u0000\u0000\u0000&\u00de\u0001\u0000\u0000\u0000"+
		"(\u00e4\u0001\u0000\u0000\u0000*\u00ed\u0001\u0000\u0000\u0000,\u0106"+
		"\u0001\u0000\u0000\u0000.\u010c\u0001\u0000\u0000\u00000\u0113\u0001\u0000"+
		"\u0000\u00002\u0115\u0001\u0000\u0000\u00004\u011d\u0001\u0000\u0000\u0000"+
		"67\u0005\u0010\u0000\u000078\u0005.\u0000\u000089\u0005\u000e\u0000\u0000"+
		"9:\u0003\u0002\u0001\u0000:;\u0005\u0002\u0000\u0000;<\u0003\b\u0004\u0000"+
		"<=\u0005\u0006\u0000\u0000=>\u0005\u0000\u0000\u0001>\u0001\u0001\u0000"+
		"\u0000\u0000?@\u0003\u0004\u0002\u0000@A\u0003\u0006\u0003\u0000A\u0003"+
		"\u0001\u0000\u0000\u0000BC\u0003\n\u0005\u0000CD\u0003\u0004\u0002\u0000"+
		"DG\u0001\u0000\u0000\u0000EG\u0001\u0000\u0000\u0000FB\u0001\u0000\u0000"+
		"\u0000FE\u0001\u0000\u0000\u0000G\u0005\u0001\u0000\u0000\u0000HI\u0003"+
		"\u0010\b\u0000IJ\u0003\u0006\u0003\u0000JM\u0001\u0000\u0000\u0000KM\u0001"+
		"\u0000\u0000\u0000LH\u0001\u0000\u0000\u0000LK\u0001\u0000\u0000\u0000"+
		"M\u0007\u0001\u0000\u0000\u0000NO\u0003\u0018\f\u0000OP\u0003\b\u0004"+
		"\u0000PS\u0001\u0000\u0000\u0000QS\u0001\u0000\u0000\u0000RN\u0001\u0000"+
		"\u0000\u0000RQ\u0001\u0000\u0000\u0000S\t\u0001\u0000\u0000\u0000TU\u0005"+
		"\u0015\u0000\u0000UV\u0005.\u0000\u0000VW\u0005-\u0000\u0000WX\u0003\f"+
		"\u0006\u0000XY\u0005\u001a\u0000\u0000Y\u000b\u0001\u0000\u0000\u0000"+
		"Zc\u0003\u000e\u0007\u0000[\\\u0005\u0001\u0000\u0000\\]\u0005\u001d\u0000"+
		"\u0000]^\u0005/\u0000\u0000^_\u0005\u001e\u0000\u0000_`\u0005\u000f\u0000"+
		"\u0000`c\u0003\u000e\u0007\u0000ac\u0005.\u0000\u0000bZ\u0001\u0000\u0000"+
		"\u0000b[\u0001\u0000\u0000\u0000ba\u0001\u0000\u0000\u0000c\r\u0001\u0000"+
		"\u0000\u0000de\u0007\u0000\u0000\u0000e\u000f\u0001\u0000\u0000\u0000"+
		"fg\u0003\u0012\t\u0000gh\u0003\u0014\n\u0000hi\u0005\u0019\u0000\u0000"+
		"ij\u0003\f\u0006\u0000jk\u0003\u0016\u000b\u0000kl\u0005\u001a\u0000\u0000"+
		"l\u0011\u0001\u0000\u0000\u0000mn\u0007\u0001\u0000\u0000n\u0013\u0001"+
		"\u0000\u0000\u0000ot\u0005.\u0000\u0000pq\u0005.\u0000\u0000qr\u0005\u0018"+
		"\u0000\u0000rt\u0003\u0014\n\u0000so\u0001\u0000\u0000\u0000sp\u0001\u0000"+
		"\u0000\u0000t\u0015\u0001\u0000\u0000\u0000uv\u0005,\u0000\u0000vy\u0003"+
		",\u0016\u0000wy\u0001\u0000\u0000\u0000xu\u0001\u0000\u0000\u0000xw\u0001"+
		"\u0000\u0000\u0000y\u0017\u0001\u0000\u0000\u0000z{\u0005\u000b\u0000"+
		"\u0000{|\u0005.\u0000\u0000|}\u0005\u001b\u0000\u0000}~\u0003\u001a\r"+
		"\u0000~\u007f\u0005\u001c\u0000\u0000\u007f\u0080\u0003\u001e\u000f\u0000"+
		"\u0080\u0081\u0005\u0002\u0000\u0000\u0081\u0082\u0003\"\u0011\u0000\u0082"+
		"\u0083\u0005\u0006\u0000\u0000\u0083\u0019\u0001\u0000\u0000\u0000\u0084"+
		"\u0085\u0003 \u0010\u0000\u0085\u0086\u0003\u001c\u000e\u0000\u0086\u0089"+
		"\u0001\u0000\u0000\u0000\u0087\u0089\u0001\u0000\u0000\u0000\u0088\u0084"+
		"\u0001\u0000\u0000\u0000\u0088\u0087\u0001\u0000\u0000\u0000\u0089\u001b"+
		"\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u0018\u0000\u0000\u008b\u008c"+
		"\u0003 \u0010\u0000\u008c\u008d\u0003\u001c\u000e\u0000\u008d\u0090\u0001"+
		"\u0000\u0000\u0000\u008e\u0090\u0001\u0000\u0000\u0000\u008f\u008a\u0001"+
		"\u0000\u0000\u0000\u008f\u008e\u0001\u0000\u0000\u0000\u0090\u001d\u0001"+
		"\u0000\u0000\u0000\u0091\u0092\u0005\u0019\u0000\u0000\u0092\u0095\u0003"+
		"\f\u0006\u0000\u0093\u0095\u0001\u0000\u0000\u0000\u0094\u0091\u0001\u0000"+
		"\u0000\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0095\u001f\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0005.\u0000\u0000\u0097\u0098\u0005\u0019\u0000"+
		"\u0000\u0098\u0099\u0003\f\u0006\u0000\u0099!\u0001\u0000\u0000\u0000"+
		"\u009a\u009f\u0003$\u0012\u0000\u009b\u009c\u0003$\u0012\u0000\u009c\u009d"+
		"\u0003\"\u0011\u0000\u009d\u009f\u0001\u0000\u0000\u0000\u009e\u009a\u0001"+
		"\u0000\u0000\u0000\u009e\u009b\u0001\u0000\u0000\u0000\u009f#\u0001\u0000"+
		"\u0000\u0000\u00a0\u00a1\u00032\u0019\u0000\u00a1\u00a2\u0005,\u0000\u0000"+
		"\u00a2\u00a3\u0003*\u0015\u0000\u00a3\u00a4\u0005\u001a\u0000\u0000\u00a4"+
		"\u00db\u0001\u0000\u0000\u0000\u00a5\u00a6\u0005\f\u0000\u0000\u00a6\u00a7"+
		"\u0003*\u0015\u0000\u00a7\u00a8\u0005\u0013\u0000\u0000\u00a8\u00a9\u0003"+
		"\"\u0011\u0000\u00a9\u00aa\u0005\b\u0000\u0000\u00aa\u00ab\u0005\u001a"+
		"\u0000\u0000\u00ab\u00db\u0001\u0000\u0000\u0000\u00ac\u00ad\u0005\f\u0000"+
		"\u0000\u00ad\u00ae\u0003*\u0015\u0000\u00ae\u00af\u0005\u0013\u0000\u0000"+
		"\u00af\u00b0\u0003\"\u0011\u0000\u00b0\u00b1\u0005\u0005\u0000\u0000\u00b1"+
		"\u00b2\u0003\"\u0011\u0000\u00b2\u00b3\u0005\b\u0000\u0000\u00b3\u00b4"+
		"\u0005\u001a\u0000\u0000\u00b4\u00db\u0001\u0000\u0000\u0000\u00b5\u00b6"+
		"\u0005\u0017\u0000\u0000\u00b6\u00b7\u0003*\u0015\u0000\u00b7\u00b8\u0005"+
		"\u0004\u0000\u0000\u00b8\u00b9\u0003\"\u0011\u0000\u00b9\u00ba\u0005\u0007"+
		"\u0000\u0000\u00ba\u00bb\u0005\u001a\u0000\u0000\u00bb\u00db\u0001\u0000"+
		"\u0000\u0000\u00bc\u00bd\u0005\n\u0000\u0000\u00bd\u00be\u0005.\u0000"+
		"\u0000\u00be\u00bf\u0005,\u0000\u0000\u00bf\u00c0\u0003*\u0015\u0000\u00c0"+
		"\u00c1\u0005\u0014\u0000\u0000\u00c1\u00c2\u0003*\u0015\u0000\u00c2\u00c3"+
		"\u0005\u0004\u0000\u0000\u00c3\u00c4\u0003\"\u0011\u0000\u00c4\u00c5\u0005"+
		"\u0007\u0000\u0000\u00c5\u00c6\u0005\u001a\u0000\u0000\u00c6\u00db\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c8\u0003(\u0014\u0000\u00c8\u00c9\u0005.\u0000"+
		"\u0000\u00c9\u00ca\u0005\u001b\u0000\u0000\u00ca\u00cb\u0003.\u0017\u0000"+
		"\u00cb\u00cc\u0005\u001c\u0000\u0000\u00cc\u00cd\u0005\u001a\u0000\u0000"+
		"\u00cd\u00db\u0001\u0000\u0000\u0000\u00ce\u00cf\u0005\u0003\u0000\u0000"+
		"\u00cf\u00db\u0005\u001a\u0000\u0000\u00d0\u00d1\u0005\u0011\u0000\u0000"+
		"\u00d1\u00d2\u0003&\u0013\u0000\u00d2\u00d3\u0005\u001a\u0000\u0000\u00d3"+
		"\u00db\u0001\u0000\u0000\u0000\u00d4\u00d5\u0005\u000e\u0000\u0000\u00d5"+
		"\u00d6\u0003\u0002\u0001\u0000\u00d6\u00d7\u0005\u0002\u0000\u0000\u00d7"+
		"\u00d8\u0003\"\u0011\u0000\u00d8\u00d9\u0005\u0006\u0000\u0000\u00d9\u00db"+
		"\u0001\u0000\u0000\u0000\u00da\u00a0\u0001\u0000\u0000\u0000\u00da\u00a5"+
		"\u0001\u0000\u0000\u0000\u00da\u00ac\u0001\u0000\u0000\u0000\u00da\u00b5"+
		"\u0001\u0000\u0000\u0000\u00da\u00bc\u0001\u0000\u0000\u0000\u00da\u00c7"+
		"\u0001\u0000\u0000\u0000\u00da\u00ce\u0001\u0000\u0000\u0000\u00da\u00d0"+
		"\u0001\u0000\u0000\u0000\u00da\u00d4\u0001\u0000\u0000\u0000\u00db%\u0001"+
		"\u0000\u0000\u0000\u00dc\u00df\u0003*\u0015\u0000\u00dd\u00df\u0001\u0000"+
		"\u0000\u0000\u00de\u00dc\u0001\u0000\u0000\u0000\u00de\u00dd\u0001\u0000"+
		"\u0000\u0000\u00df\'\u0001\u0000\u0000\u0000\u00e0\u00e1\u00032\u0019"+
		"\u0000\u00e1\u00e2\u0005,\u0000\u0000\u00e2\u00e5\u0001\u0000\u0000\u0000"+
		"\u00e3\u00e5\u0001\u0000\u0000\u0000\u00e4\u00e0\u0001\u0000\u0000\u0000"+
		"\u00e4\u00e3\u0001\u0000\u0000\u0000\u00e5)\u0001\u0000\u0000\u0000\u00e6"+
		"\u00e7\u0006\u0015\uffff\uffff\u0000\u00e7\u00e8\u0005\u001b\u0000\u0000"+
		"\u00e8\u00e9\u0003*\u0015\u0000\u00e9\u00ea\u0005\u001c\u0000\u0000\u00ea"+
		"\u00ee\u0001\u0000\u0000\u0000\u00eb\u00ee\u0003,\u0016\u0000\u00ec\u00ee"+
		"\u00032\u0019\u0000\u00ed\u00e6\u0001\u0000\u0000\u0000\u00ed\u00eb\u0001"+
		"\u0000\u0000\u0000\u00ed\u00ec\u0001\u0000\u0000\u0000\u00ee\u0103\u0001"+
		"\u0000\u0000\u0000\u00ef\u00f0\n\u0006\u0000\u0000\u00f0\u00f1\u0005#"+
		"\u0000\u0000\u00f1\u0102\u0003*\u0015\u0006\u00f2\u00f3\n\u0005\u0000"+
		"\u0000\u00f3\u00f4\u0007\u0002\u0000\u0000\u00f4\u0102\u0003*\u0015\u0006"+
		"\u00f5\u00f6\n\u0004\u0000\u0000\u00f6\u00f7\u0007\u0003\u0000\u0000\u00f7"+
		"\u0102\u0003*\u0015\u0005\u00f8\u00f9\n\u0003\u0000\u0000\u00f9\u00fa"+
		"\u0007\u0004\u0000\u0000\u00fa\u0102\u0003*\u0015\u0004\u00fb\u00fc\n"+
		"\u0002\u0000\u0000\u00fc\u00fd\u0005*\u0000\u0000\u00fd\u0102\u0003*\u0015"+
		"\u0003\u00fe\u00ff\n\u0001\u0000\u0000\u00ff\u0100\u0005+\u0000\u0000"+
		"\u0100\u0102\u0003*\u0015\u0002\u0101\u00ef\u0001\u0000\u0000\u0000\u0101"+
		"\u00f2\u0001\u0000\u0000\u0000\u0101\u00f5\u0001\u0000\u0000\u0000\u0101"+
		"\u00f8\u0001\u0000\u0000\u0000\u0101\u00fb\u0001\u0000\u0000\u0000\u0101"+
		"\u00fe\u0001\u0000\u0000\u0000\u0102\u0105\u0001\u0000\u0000\u0000\u0103"+
		"\u0101\u0001\u0000\u0000\u0000\u0103\u0104\u0001\u0000\u0000\u0000\u0104"+
		"+\u0001\u0000\u0000\u0000\u0105\u0103\u0001\u0000\u0000\u0000\u0106\u0107"+
		"\u0007\u0005\u0000\u0000\u0107-\u0001\u0000\u0000\u0000\u0108\u0109\u0003"+
		"*\u0015\u0000\u0109\u010a\u00030\u0018\u0000\u010a\u010d\u0001\u0000\u0000"+
		"\u0000\u010b\u010d\u0001\u0000\u0000\u0000\u010c\u0108\u0001\u0000\u0000"+
		"\u0000\u010c\u010b\u0001\u0000\u0000\u0000\u010d/\u0001\u0000\u0000\u0000"+
		"\u010e\u010f\u0005\u0018\u0000\u0000\u010f\u0110\u0003*\u0015\u0000\u0110"+
		"\u0111\u00030\u0018\u0000\u0111\u0114\u0001\u0000\u0000\u0000\u0112\u0114"+
		"\u0001\u0000\u0000\u0000\u0113\u010e\u0001\u0000\u0000\u0000\u0113\u0112"+
		"\u0001\u0000\u0000\u0000\u01141\u0001\u0000\u0000\u0000\u0115\u0116\u0005"+
		".\u0000\u0000\u0116\u0117\u00034\u001a\u0000\u01173\u0001\u0000\u0000"+
		"\u0000\u0118\u0119\u0005\u001d\u0000\u0000\u0119\u011a\u0003*\u0015\u0000"+
		"\u011a\u011b\u0005\u001e\u0000\u0000\u011b\u011e\u0001\u0000\u0000\u0000"+
		"\u011c\u011e\u0001\u0000\u0000\u0000\u011d\u0118\u0001\u0000\u0000\u0000"+
		"\u011d\u011c\u0001\u0000\u0000\u0000\u011e5\u0001\u0000\u0000\u0000\u0013"+
		"FLRbsx\u0088\u008f\u0094\u009e\u00da\u00de\u00e4\u00ed\u0101\u0103\u010c"+
		"\u0113\u011d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}