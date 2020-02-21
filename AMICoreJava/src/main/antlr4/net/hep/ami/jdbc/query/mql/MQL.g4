/*--------------------------------------------------------------------------------------------------------------------*/

grammar MQL;

/*--------------------------------------------------------------------------------------------------------------------*/

options {
	language = Java;
}

/*--------------------------------------------------------------------------------------------------------------------*/
/* MQL PARSER                                                                                                         */
/*--------------------------------------------------------------------------------------------------------------------*/

mqlQuery
	: ';'
	| (m_select=selectStatement | m_insert=insertStatement | m_update=updateStatement | m_delete=deleteStatement) ';'?
	;

/*-------------------------------------------------------------------------*/

selectStatement
	: SELECT (m_distinct=DISTINCT)? m_columns=columnList (WHERE m_expression=expressionOr)? (GROUP BY m_groupBy=qIdList)? (ORDER BY m_orderBy=qIdList (m_orderWay=(ASC|DESC))?)? (LIMIT m_limit=NUMBER (OFFSET m_offset=NUMBER)?)?
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
/* QID_LIST                  */
/*---------------------------*/

qIdList
	: m_aQIds+=aQId (',' m_aQIds+=aQId)*
	;

aQId
	: m_qId=qId
	;

/*---------------------------*/
/* EXPRESSION_TUPLE          */
/*---------------------------*/

expressionTuple
	: '(' m_expressions+=expressionOr (',' m_expressions+=expressionOr)* ')'
	;

/*---------------------------*/
/* QID_TUPLE                 */
/*---------------------------*/

qIdTuple
	: '(' m_qIds+=qId (',' m_qIds+=qId)* ')'
	;

/*---------------------------*/
/* LITERAL_TUPLE             */
/*---------------------------*/

literalTuple
	: '(' m_literals+=literal (',' m_literals+=literal)* ')'
	;

/*---------------------------*/
/* EXPRESSION                */
/*---------------------------*/

expressionOr
	: expressionXor (OR expressionXor)*
	;

expressionXor
	: expressionAnd (XOR expressionAnd)*
	;

expressionAnd
	: expressionNot (AND expressionNot)*
	;

expressionNot
	: NOT* expressionComp
	;

expressionComp
	: expressionAddSub (
		  COMP expressionAddSub
		| NOT? (
			  BETWEEN expressionAddSub AND expressionAddSub
			| (LIKE | REGEXP) expressionAddSub
			| IN literalTuple
		  )
		| IS NOT? NULL
	  )?
	;

expressionAddSub
	: expressionMulDiv ((PLUS | MINUS) expressionMulDiv)*
	;

expressionMulDiv
	: expressionPlusMinus ((MUL | DIV | MOD) expressionPlusMinus)*
	;

expressionPlusMinus
	: m_operator=(PLUS | MINUS)? expressionX
	;

expressionX
	: '(' m_expression=expressionOr ')'    # ExpressionStdGroup
	| '[' m_expression=expressionOr ']'    # ExpressionIsoGroup
	| m_function=function                  # ExpressionFunction
	| m_qId=qId                            # ExpressionQId
	| m_literal=literal                    # ExpressionLiteral
	;

/*---------------------------*/
/* FUNCTION                  */
/*---------------------------*/

function
    : m_functionName=FUNCTION '(' (m_distinct=DISTINCT)? m_expressions+=expressionOr (',' m_expressions+=expressionOr)* ')'
    ;

/*---------------------------*/
/* QID                       */
/*---------------------------*/

qId
	: m_basicQId=basicQId ('{' m_constraintQIds+=constraintQId ((',' | '|') m_constraintQIds+=constraintQId)* '}')?
	;

/*---------------------------*/

constraintQId
	: m_op='!'? m_qId=qId
	;

/*---------------------------*/

basicQId
	: m_ids+=(ID|'*'|'#') ('.' m_ids+=(ID|'*'|'#'))*
	;

/*---------------------------*/
/* LITERAL                   */
/*---------------------------*/

literal
	: NULL | CURRENT_TIMESTAMP | STRING | PARAMETER | NUMBER
	;

/*--------------------------------------------------------------------------------------------------------------------*/
/* MQL LEXER                                                                                                          */
/*--------------------------------------------------------------------------------------------------------------------*/

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

UPDATE
	: U P D A T E
	;

VALUES
	: V A L U E S
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

XOR
	: X O R
	| '^^' { setText("XOR"); }
	;

AND
	: A N D
	| '&&' { setText("AND"); }
	;

COMP
	: '=' | '<=>' | '!=' | '^=' { setText("!="); } | '<>' { setText("!="); }
	| '<' | '>' | '<=' | '>='
	;

NOT
	: N O T
	;

LIKE
	: L I K E
	;

REGEXP
	: R E G E X P
	;

BETWEEN
	: B E T W E E N
	;

IN
	: I N
	;

IS
	: I S
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

FUNCTION
	: A M I '_' T I M E S T A M P | A M I '_' D A T E | A M I '_' T I M E
	| A B S | A V G | C O N C A T | C O S | C O U N T | L E N G T H
	| L O G | L O W E R | M A X | M I N | M O D | P O W | R A N D
	| R O U N D | S I N | S Q R T | S T D D E V | S U B S T R
	| S U M | T I M E S T A M P | U P P E R
	| J S O N '_' [a-zA-Z_]+
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

PARAMETER
	: '?#<' (~'>')+ '>' INT		/* TYPED CRYPTED LABELED PARAMETER */
	| '?<' (~'>')+ '>' INT		/* TYPED LABELED PARAMETER */
	| '?#' INT					/* CRYPTED LABELED PARAMETER */
	| '?' INT					/* LABELED PARAMETER */
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

/*---------------------------*/

COMMENT
	: '-' '-' ~[\n\r]* -> skip
	;

WS
	: [ \t\n\r]+ -> skip
	;

/*---------------------------*/

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

/*--------------------------------------------------------------------------------------------------------------------*/
