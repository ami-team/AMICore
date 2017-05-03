/*-------------------------------------------------------------------------*/

grammar JSON;

/*-------------------------------------------------------------------------*/

options {
	language = Java;
}

@header {
	import java.util.*;

	import net.hep.ami.utility.*;
}

/*-------------------------------------------------------------------------*/
/* JSON PARSER                                                             */
/*-------------------------------------------------------------------------*/

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
	| '{' pair { $v.put(Utility.parseString($pair.v.x), $pair.v.y); } (',' pair { $v.put(Utility.parseString($pair.v.x), $pair.v.y); })* '}'
	;

array returns [ List<Object> v ]
	@init { $v = new ArrayList<Object>(); }
	: '[' ']'
	| '[' value { $v.add($value.v); } (',' value { $v.add($value.v); })* ']'
	;

pair returns [ Tuple2<String, Object> v ]
	: key=STRING ':' val=value { $v = new Tuple2<>($key.text, $val.v); }
	;

term returns [ Object v ]
	: STRING { $v = Utility.parseString($STRING.text); }
	| NUMBER { $v = Float.parseFloat($NUMBER.text); }
	| 'true' { $v = true; }
	| 'false' { $v = false; }
	| 'null' { $v = null; }
	;

/*-------------------------------------------------------------------------*/
/* JSON LEXER                                                              */
/*-------------------------------------------------------------------------*/

STRING
	: '"' (ESC | ~["\\])* '"'
	| '\'' (ESC | ~['\\])* '\''
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

fragment ESC
	: '\\' (["'/\\\b\f\n\r\t] | 'u' HEX HEX HEX HEX)
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

/*-------------------------------------------------------------------------*/
