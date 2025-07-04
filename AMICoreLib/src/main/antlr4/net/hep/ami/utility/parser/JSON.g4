/*--------------------------------------------------------------------------------------------------------------------*/

grammar JSON;

/*--------------------------------------------------------------------------------------------------------------------*/

options {
	language = Java;
}

@header {
	import java.util.*;
}

@members {

	private static class Pair
	{
		final String x;
		final Object y;

		private Pair(String _x, Object _y)
		{
			x = _x;
			y = _y;
		}
	}

	public boolean simpleQuotes = false;
}

/*--------------------------------------------------------------------------------------------------------------------*/
/* JSON PARSER                                                                                                        */
/*--------------------------------------------------------------------------------------------------------------------*/

file returns [ Object v ]
	: value { $v = $value.v; } EOF
	;

value returns [ Object v ]
	: object { $v = (Object) $object.v; }
	| array { $v = (Object) $array.v; }
	| term { $v = (Object) $term.v; }
	;

object returns [ Map<String, Object> v ]
	@init { $v = new LinkedHashMap<String, Object>(); }
	: '{' '}'
	| '{' pair { $v.put($pair.v.x, $pair.v.y); } (',' pair { $v.put($pair.v.x, $pair.v.y); })* '}'
	;

array returns [ List<Object> v ]
	@init { $v = new ArrayList<Object>(); }
	: '[' ']'
	| '[' value { $v.add($value.v); } (',' value { $v.add($value.v); })* ']'
	;

pair returns [ Pair v ]
	: x=STRING ':' y=value { $v = new Pair(Utility.jsonStringToText($x.text, simpleQuotes), $y.v); }
	;

term returns [ Object v ]
	: STRING { $v = Utility.jsonStringToText($STRING.text, simpleQuotes); }
	| NUMBER { $v = Double.parseDouble($NUMBER.text); }
	| 'true' { $v = true; }
	| 'false' { $v = false; }
	| 'null' { $v = null; }
	;

/*--------------------------------------------------------------------------------------------------------------------*/
/* JSON LEXER                                                                                                         */
/*--------------------------------------------------------------------------------------------------------------------*/

STRING
	: '"' (ESC | ~["\\\r\n])* '"'
	| '\'' (ESC | ~['\\\r\n])* '\''
	;

NUMBER
	: '-'? INT '.' INT+ EXP?
	| '-'? INT EXP
	| '-'? INT
	;

/*--------------------------------------------------------------------------------------------------------------------*/

WS
	: [ \t\n\r]+ -> skip
	;

/*--------------------------------------------------------------------------------------------------------------------*/

fragment ESC
	: '\\' (["'/\\bfnrt] | 'u' HEX HEX HEX HEX)
	;

fragment HEX
	: [a-fA-F0-9]
	;

fragment INT
	: [0-9]+
	;

fragment EXP
	: [eE] [+\-]? INT
	;

/*--------------------------------------------------------------------------------------------------------------------*/
