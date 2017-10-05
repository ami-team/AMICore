/*-------------------------------------------------------------------------*/

grammar MQL;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

/*-------------------------------------------------------------------------*/
/* MQL PARSER                                                              */
/*-------------------------------------------------------------------------*/

selectStatement
	: SELECT (distinct=DISTINCT)? columns=columnList (WHERE expression=expressionOr)? (ORDER BY orderBy=sqlQId (orderWay=(ASC|DESC))?)? (LIMIT limit=NUMBER (OFFSET offset=NUMBER)?)? ';'?
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
	: expressionAddSub (operator=COMP expressionAddSub)?
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
	| literal=sqlLiteral                                                       # ExpressionLiteral
	| qId=sqlQId                                                               # ExpressionQId
	;

/*---------------------------*/
/* SQL_QID                   */
/*---------------------------*/

sqlQId
	: catalogName=ID '.' entityName=ID '.' fieldName=(ID | '*')
	| entityName=ID '.' fieldName=(ID | '*')
	| fieldName=(ID | '*')
	;

/*---------------------------*/
/* SQL_LITERAL               */
/*---------------------------*/

sqlLiteral
	: '?' | STRING | NUMBER | NULL
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

ORDER
	: O R D E R
	;

BY
	: B Y
	;

ASC
	: A S C
	;

DESC
	: D E S C
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
	| '||' { setText("OR"); }
	;

AND
	: A N D
	| '&&' { setText("AND"); }
	;

NULL
	: N U L L
	;

COMP
	: '=' | '!=' | '^=' { setText("!="); } | '<>' { setText("!="); } | '<' | '>' | '<=' | '>=' | L I K E
	;

FUNCTION
	: A B S | C O S | L O G | M O D | P O W | R N D | S I N | S Q R T
	| C O N C A T | L O W E R | L E N G T H | S U B S T R | U P P E R
	| A V G | C O U N T | M I N | M A X | S U M
	;

/*---------------------------*/
/* LITERALS                  */
/*---------------------------*/

STRING
	: '\'' ('\'\'' | ~'\'')* '\''
	;

ID
	: [a-zA-Z_][a-zA-Z0-9_#$]*
	| '`' ('``' | ~'`')+ '`'
	| '"' ('""' | ~'"')+ '"'
	;

NUMBER
	: '-'? INT '.' INT+ EXP?
	| '-'? INT EXP
	| '-'? INT
	;

/*-------------------------------------------------------------------------*/

WS
	: [ \t\n\r]+ -> skip
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

fragment INT
	: [0-9]+
	;

fragment EXP
	: [eE] [+\-]? INT
	;

/*-------------------------------------------------------------------------*/
