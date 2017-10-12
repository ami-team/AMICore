/*-------------------------------------------------------------------------*/

grammar Command;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

@header {
	import java.util.*;

	import net.hep.ami.utility.*;
}

@members {

	private static class Pair
	{
		final String x;
		final String y;

		public Pair(String _x, String _y)
		{
			x = _x;
			y = _y;
		}
	}
}

/*-------------------------------------------------------------------------*/
/* COMMAND PARSER                                                          */
/*-------------------------------------------------------------------------*/

command returns [ Command.CommandTuple v ]
	: identifier parameterList EOF { $v = new Command.CommandTuple($identifier.v, $parameterList.v); }
	;

/*-------------------------------------------------------------------------*/

parameterList returns [ Map<String, String> v ]
	@init { $v = new LinkedHashMap<>(); }
	: (parameter { $v.put($parameter.v.x, $parameter.v.y); })*
	;

/*-------------------------------------------------------------------------*/

parameter returns [ Pair v ]
	: '-'+ identifier '=' string { $v = new Pair($identifier.v, $string.v); }
	| '-'+ identifier { $v = new Pair($identifier.v, null); }
	;

/*-------------------------------------------------------------------------*/

identifier returns [ String v ]
	: IDENTIFIER { $v = /*---------------*/($IDENTIFIER.text); }
	;

/*-------------------------------------------------------------------------*/

string returns [ String v ]
	: STRING { $v = Utility.parseString($STRING.text); }
	;

/*-------------------------------------------------------------------------*/
/* COMMAND LEXER                                                           */
/*-------------------------------------------------------------------------*/

IDENTIFIER
	: [a-zA-Z][a-zA-Z0-9]*
	;

STRING
	: '"' (ESC | ~["\\\r\n])* '"'
	| '\'' (ESC | ~['\\\r\n])* '\''
	;

/*-------------------------------------------------------------------------*/

COMMENT
	: '#' ~[\n\r]* -> skip
	;

WS
	: [ \t\n\r]+ -> skip
	;

/*-------------------------------------------------------------------------*/

fragment ESC
	: '\\' (["'/\\bfnrt] | 'u' HEX HEX HEX HEX)
	;

fragment HEX
	: [a-fA-F0-9]
	;

/*-------------------------------------------------------------------------*/
