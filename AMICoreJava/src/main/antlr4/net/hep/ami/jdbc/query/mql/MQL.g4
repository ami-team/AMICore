/*-------------------------------------------------------------------------*/

grammar MQL;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

/*-------------------------------------------------------------------------*/
/* MQL PARSER                                                              */
/*-------------------------------------------------------------------------*/

mqlQuery
	: (m_select=selectStatement | m_insert=insertStatement | m_update=updateStatement | m_delete=deleteStatement) ';'?
	;

/*-------------------------------------------------------------------------*/

selectStatement
	: SELECT (m_distinct=DISTINCT)? m_columns=columnList (WHERE m_expression=expressionOr)? (GROUP BY m_groupBy=columnList)? (ORDER BY m_orderBy=qId (m_orderWay=(ASC|DESC))?)? (LIMIT m_limit=NUMBER (OFFSET m_offset=NUMBER)?)?
	;

insertStatement
	: INSERT m_qIds=qIdTuple VALUES m_expressions=expressionTuple /*------------------------------*/
	;

updateStatement
	: UPDATE m_qIds=qIdTuple VALUES m_expressions=expressionTuple (WHERE m_expression=expressionOr)?
	;

deleteStatement
	: DELETE (WHERE m_expression=expressionOr)?
	;

/*---------------------------*/
/* COLUMN_LIST               */
/*---------------------------*/

columnList
	: m_columns+=aColumn (',' m_columns+=aColumn)*
	;

aColumn
	: m_expression=expressionOr (AS m_alias=ID)?
	;

/*---------------------------*/
/* QID_TUPLE                 */
/*---------------------------*/

qIdTuple
	: '(' m_qIds+=qId (',' m_qIds+=qId)* ')'
	;

/*---------------------------*/
/* EXPRESSION_TUPLE          */
/*---------------------------*/

expressionTuple
	: '(' m_expressions+=expressionOr (',' m_expressions+=expressionOr)* ')'
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
	: expressionAddSub (COMP expressionAddSub | IN literalTuple)?
	;

expressionAddSub
	: expressionMulDiv ((PLUS | MINUS) expressionMulDiv)*
	;

expressionMulDiv
	: expressionNotPlusMinus ((MUL | DIV | MOD) expressionNotPlusMinus)*
	;

expressionNotPlusMinus
	: m_operator=(NOT | PLUS | MINUS)? expressionX
	;

expressionX
	: '(' m_expression=expressionOr ')'                                                     # ExpressionGroup
	| '[' m_isoExpression=expressionOr ']'                                                  # ExpressionIsoGroup
	| m_functionName=FUNCTION '(' (m_param1=expressionOr (',' m_param2=expressionOr)?)? ')' # ExpressionFunction
	| m_literal=literal                                                                     # ExpressionLiteral
	| m_qId=qId                                                                             # ExpressionQId
	;

/*---------------------------*/
/* QID                       */
/*---------------------------*/

qId
	: m_basicQId=basicQId ('{' m_constraintQIds+=constraintQId (',' m_constraintQIds+=constraintQId)* '}')?
	;

/*---------------------------*/

constraintQId
	: m_op='!'? m_qId=qId
	;

/*---------------------------*/

basicQId
	: m_ids+=(ID|MUL|'#') ('.' m_ids+=(ID|MUL|'#'))*
	;

/*---------------------------*/
/* LITERAL_TUPLE             */
/*---------------------------*/

literalTuple
	: '(' m_literals+=literal (',' m_literals+=literal)* ')'
	;

/*---------------------------*/
/* LITERAL                   */
/*---------------------------*/

literal
	: STRING | NUMBER | NULL | CURRENT_TIMESTAMP | '?'
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

AS
	: A S
	;

WHERE
	: W H E R E
	;
	
GROUP
	: G R O U P
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

/*---------------------------*/

INSERT
	: I N S E R T
	;

VALUES
	: V A L U E S
	;

/*---------------------------*/

UPDATE
	: U P D A T E
	;

/*---------------------------*/

DELETE
	: D E L E T E
	;

/*---------------------------*/

OR
	: O R
	| '||' { setText("OR"); }
	;

AND
	: A N D
	| '&&' { setText("AND"); }
	;

COMP
	: '=' | '!=' | '^=' { setText("!="); } | '<>' { setText("!="); } | '<' | '>' | '<=' | '>=' | L I K E
	;

IN
	: I N
	;

PLUS
	: '+'
	;

MINUS
	: '-'
	;

MUL
	: '*'
	;

DIV
	: '/'
	;

MOD
	: '%'
	;

NOT
	: '!'
	;

FUNCTION
	: A B S | C O S | L O G | M O D | P O W | R N D | S I N | S Q R T
	| C O N C A T | L O W E R | L E N G T H | S U B S T R | U P P E R
	| A V G | C O U N T | M I N | M A X | S U M
	;

/*---------------------------*/
/* LITERALS                  */
/*---------------------------*/

NULL
	: N U L L
	;

CURRENT_TIMESTAMP
	: C U R R E N T '_' T I M E S T A M P
	;

STRING
	: '\'' ('\'\'' | ~'\'')* '\''
	;

NUMBER
	: '-'? INT '.' INT+ EXP?
	| '-'? INT EXP
	| '-'? INT
	;

ID
	: [a-zA-Z_][a-zA-Z0-9_#$]*
	| '`' ('``' | ~'`')+ '`'
	| '"' ('""' | ~'"')+ '"'
	;

/*-------------------------------------------------------------------------*/

COMMENT
	: '-' '-' ~[\n\r]* -> skip
	;

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
