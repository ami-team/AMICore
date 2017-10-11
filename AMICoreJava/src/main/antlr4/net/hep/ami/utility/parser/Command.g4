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
	/*---------------------------------------------------------------------*/

	private static class ParameterTuple
	{
		final String x;
		final String y;

		public ParameterTuple(String _x, String _y)
		{
			x = _x;
			y = _y;
		}
	}

	/*---------------------------------------------------------------------*/
}

/*-------------------------------------------------------------------------*/
/* COMMAND PARSER                                                          */
/*-------------------------------------------------------------------------*/

command returns [ Command.CommandTuple v ]
	: identifier { $v = new Command.CommandTuple($identifier.v, new LinkedHashMap<>()); }
	;

/*-------------------------------------------------------------------------*/

parameterList returns [ Map<String, String> v ]
	@init { $v = new LinkedHashMap<>(); }
	: (WS+ parameter { $v.put($parameter.v.x, $parameter.v.y); })*
	;

/*-------------------------------------------------------------------------*/

parameter returns [ ParameterTuple v ]
	: '-'* identifier ('=' value)? { $v = new ParameterTuple($identifier.v, $value.v); }
	;

/*-------------------------------------------------------------------------*/

identifier returns [ String v ]
	: ID { System.out.println($ID.text); $v = $ID.text; }
	;

/*-------------------------------------------------------------------------*/

value returns [ String v ]
	: STRING { $v = Utility.parseString($STRING.text); }
	| VALUE { $v = /*---------------*/($VALUE.text); }
	;

/*-------------------------------------------------------------------------*/
/* COMMAND LEXER                                                           */
/*-------------------------------------------------------------------------*/

ID
	: [a-zA-Z_][a-zA-Z0-9_#$]*
	| '`' ('``' | ~'`')+ '`'
	| '"' ('""' | ~'"')+ '"'
	;

STRING
	: '"' (ESC | ~["\\])* '"'
	;

VALUE
	: ~[,"'# \n\r\t]+
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
