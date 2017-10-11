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
	: WS* identifier parameterList EOF { $v = new Command.CommandTuple($identifier.v, $parameterList.v); }
	;

/*-------------------------------------------------------------------------*/

parameterList returns [ Map<String, String> v ]
	@init { $v = new LinkedHashMap<>(); }
	: (WS+ parameter { $v.put($parameter.v.x, $parameter.v.y); })*
	;

/*-------------------------------------------------------------------------*/

parameter returns [ Pair v ]
	: '-'* identifier ('=' value)? { $v = new Pair($identifier.v, $value.v); }
	;

/*-------------------------------------------------------------------------*/

identifier returns [ String v ]
	: IDENTIFIER { $v = $IDENTIFIER.text; }
	;

/*-------------------------------------------------------------------------*/

value returns [ String v ]
	: STRING { $v = Utility.parseString($STRING.text); }
	;

/*-------------------------------------------------------------------------*/
/* COMMAND LEXER                                                           */
/*-------------------------------------------------------------------------*/

IDENTIFIER
	: [a-zA-Z][a-zA-Z0-9]*
	;

STRING
	: '"' (ESC | ~["\\])* '"'
	;

/*-------------------------------------------------------------------------*/

COMMENT
	: '#' ~[\n\r]* -> skip
	;

WS
	: [ \t]+
	;

/*-------------------------------------------------------------------------*/

fragment ESC
	: '\\' (["'/\\\b\f\n\r\t] | 'u' HEX HEX HEX HEX)
	;

fragment HEX
	: [a-fA-F0-9]
	;

/*-------------------------------------------------------------------------*/
