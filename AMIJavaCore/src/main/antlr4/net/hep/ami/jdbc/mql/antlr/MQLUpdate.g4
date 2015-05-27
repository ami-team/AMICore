/*-------------------------------------------------------------------------*/

grammar MQLUpdate;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

/*-------------------------------------------------------------------------*/
/*                                                                         */
/*-------------------------------------------------------------------------*/

statement
  : (createTable | truncateDatabaseStatement | dropDatabaseStatement | dropTableStatement) ';'
  ;

/*---------------------------*/
/*                           */
/*---------------------------*/

createTable
  : CREATE TABLE (IF NOT EXISTS)? ID '(' createTableColumnList ')' (DEFAULT CHARSET '=' ID)? (AUTO_INCREMENT '=' NUMBER)?
  ;

createTableColumnList
  : createTableColumn (',' createTableColumn)*
  ;

createTableColumn
  : ID TYPE ('(' NUMBER ')')? (NOT NULL)? (DEFAULT sqlLiteral)? (AUTO_INCREMENT)?
  ;

/*---------------------------*/
/*                           */
/*---------------------------*/

truncateDatabaseStatement
  : TRUNCATE TABLE ID
  ;

/*---------------------------*/
/*                           */
/*---------------------------*/

dropDatabaseStatement
  : DROP DATABASE (IF EXISTS)? ID
  ;

/*---------------------------*/
/*                           */
/*---------------------------*/

dropTableStatement
  : DROP TABLE (IF EXISTS)? ID
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

AUTO_INCREMENT
  : A U T O '_' I N C R E M E N T
  ;

CHARSET
  : C H A R S E T
  ;

CREATE
  : C R E A T E
  ;

DATABASE
  : D A T A B A S E
  ;

DEFAULT
  : D E F A U L T
  ;

DROP
  : D R O P
  ;

EXISTS
  : E X I S T S
  ;

IF
  : I F
  ;

NOT
  : N O T
  ;

NULL
  : N U L L
  ;

TABLE
  : T A B L E
  ;

TRUNCATE
  : T R U N C A T E
  ;

TYPE
  : I N T | F L O A T | D O U B L E
  | D A T E | T I M E | D A T E T I M E
  | B O O L | V A R C H A R
  | T E X T | B L O B
  ;

/*---------------------------*/
/* LITERALS                  */
/*---------------------------*/

NUMBER
  : DIGIT+
  ;

STRING
  : '\'' (~'\'' | '\'\'')* '\''
  ;

ID
  : [a-zA-Z_] [a-zA-Z_0-9]*
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
