/*-------------------------------------------------------------------------*/

grammar GLite;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

/*-------------------------------------------------------------------------*/
/* MQL PARSER                                                              */
/*-------------------------------------------------------------------------*/

selectStatement
	: SELECT columnList (WHERE condition)? ';'?
	;

/*---------------------------*/
/* COLUMN_LIST               */
/*---------------------------*/

columnList
	: column (',' column)*
	;

column
	: ID '.' '*'                                    # ColumnAll
	| expression (AS ID)?                           # ColumnOne
	;

/*---------------------------*/
/* CONDITION                 */
/*---------------------------*/

condition
	: conditionTerm (OR conditionTerm)*
	;

conditionTerm
	: conditionSubTerm (AND conditionSubTerm)*
	;

conditionSubTerm
  : '!' conditionSubSubTerm
  | conditionSubSubTerm
  ;

conditionSubSubTerm
	: '(' condition ')'                             # ConditionSubSubTermGroup
	| expression COMPARISON_OPERATOR expression     # ConditionSubSubTermComparisonOperator
	| expression LIKE sqlLiteral                    # ConditionSubSubTermLike
	| expression IN sqlLiteralList                  # ConditionSubSubTermIn
	;

/*---------------------------*/
/* EXPRESSION                */
/*---------------------------*/

expression
	: expressionTerm (('+' | '-') expressionTerm)*
	;

expressionTerm
	: expressionSubTerm (('*' | '/') expressionSubTerm)*
	;

expressionSubTerm
	: ('+' | '-') expressionSubSubTerm
	| expressionSubSubTerm
	;

expressionSubSubTerm
	: '(' expression ')'                            # ExpressionSubSubTermGroup
	| FUNCTION '(' columnList ')'                   # ExpressionSubSubTermFunction
	| sqlLiteral                                    # ExpressionSubSubTermSqlLiteral
	| ID '.' ID                                     # ExpressionSubSubTermQualifiedId
	;

/*---------------------------*/
/* SQL_LITERAL_LIST          */
/*---------------------------*/

sqlLiteralList
	: '(' sqlLiteral (',' sqlLiteral)* ')'
	;

sqlLiteral
	: STRING | NUMBER | NULL
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

WHERE
	: W H E R E
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

COMPARISON_OPERATOR
	: '=' | '!=' | '^=' | '<>' | '<' | '>' | '<=' | '>='
	;

FUNCTION
	: A B S | C O S | L O G | M O D | P O W | R N D | S I N | S Q R T
	| C O N C A T | L O W E R | L E N G T H | S U B S T R | U P P E R
	| A V G | C O U N T | M I N | M A X | S U M
	;

/*---------------------------*/
/* TOKENS                    */
/*---------------------------*/

ID
	: [a-zA-Z_] [a-zA-Z_0-9]*
	| '`' (~'`' | '``')+ '`'
	| '"' (~'"' | '`"')+ '"'
	;

STRING
	: '\'' (~'\'' | '\'\'')* '\''
	;

NUMBER
	: DIGIT+ ('.' DIGIT*)? (E [-+]? DIGIT+)?
	| '.' DIGIT+ (E [-+]? DIGIT+)?
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
