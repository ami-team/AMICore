/*-------------------------------------------------------------------------*/

grammar MQLSelect;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

/*-------------------------------------------------------------------------*/
/* MQL PARSER                                                              */
/*-------------------------------------------------------------------------*/

selectStatement
	: SELECT (distinct=DISTINCT)? columns=columnList (WHERE expression=expressionOr)? (LIMIT limit=NUMBER (OFFSET offset=NUMBER)?)? ';'?
	;

/*---------------------------*/
/* COLUMN_LIST               */
/*---------------------------*/

columnList
	: column (',' column)*
	;

column
	: expression=expressionOr (AS alias=ID)?
	;

/*---------------------------*/
/* EXPRESSION                */
/*---------------------------*/

expressionOr
  : expressionAnd (OR expressionAnd)*
  ;

expressionAnd
  : expressionComp (AND expressionComp)*
  ;

expressionComp
  : expressionAddSub (operator=COMP expressionAddSub)*
  ;

expressionAddSub
  : expressionMulDiv (operator=('+' | '-') expressionMulDiv)*
  ;

expressionMulDiv
  : expressionNotPlusMinus (operator=('*' | '/' | '%') expressionNotPlusMinus)*
  ;

expressionNotPlusMinus
  : operator=('!' | '-' | '+')? expressionX
  ;

expressionX
  : '(' expression=expressionOr ')'                                          # ExpressionGroup
  | functionName=FUNCTION '('
        (distinct=DISTINCT)? expression=expressionOr
    ')'                                                                      # ExpressionFunction
  | qId=sqlQId LIKE literal=sqlLiteral                                       # ExpressionLike
  | qId=sqlQId                                                               # ExpressionQId
  | literal=sqlLiteral                                                       # ExpressionLiteral
  ;

/*---------------------------*/
/* SQL_QID                   */
/*---------------------------*/

sqlQId
  : tableName=ID '.' columnName=(ID | '*')
  ;

/*---------------------------*/
/* SQL_LITERAL               */
/*---------------------------*/

sqlLiteral
	: NUMBER | STRING | NULL
	;

/*-------------------------------------------------------------------------*/
/* MQL LEXER                                                               */
/*-------------------------------------------------------------------------*/

/*---------------------------*/
/* KEYWORDS                  */
/*---------------------------*/

SELECT
	: S E L E C T
	;

DISTINCT
  : D I S T I N C T
  ;

WHERE
	: W H E R E
	;

LIMIT
  : L I M I T
  ;

OFFSET
  : O F F S E T
  ;

AS
	: A S
	;

OR
	: O R
	| '||'
	;

AND
	: A N D
	| '&&'
	;

LIKE
	: L I K E
	;

IN
	: I N
	;

NULL
	: N U L L
	;

COMP
	: '=' | '!=' | '^='| '<>' | '<' | '>' | '<=' | '>='
	;

FUNCTION
	: A B S | C O S | L O G | M O D | P O W | R N D | S I N | S Q R T
	| C O N C A T | L O W E R | L E N G T H | S U B S T R | U P P E R
	| A V G | C O U N T | M I N | M A X | S U M
	;

/*---------------------------*/
/* LITERALS                  */
/*---------------------------*/

NUMBER
  : DIGIT+ ('.' DIGIT*)? (E [-+]? DIGIT+)?
  | '.' DIGIT+ (E [-+]? DIGIT+)?
  ;

STRING
	: '\'' (~'\'' | '\'\'')* '\''
	;

ID
  : [a-zA-Z_] [a-zA-Z0-9_#$]*
  | '`' (~'`' | '``')+ '`'
  | '"' (~'"' | '""')+ '"'
  ;

/*-------------------------------------------------------------------------*/

WS
	: [ \t\n\r]+ -> channel(HIDDEN)
	;

/*-------------------------------------------------------------------------*/

fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];

fragment DIGIT
	: [0-9]
	;

/*-------------------------------------------------------------------------*/
