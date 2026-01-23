/*--------------------------------------------------------------------------------------------------------------------*/
/* Grammaire  Python                                                                                                  */
/*--------------------------------------------------------------------------------------------------------------------*/

grammar PythonDict;

/*--------------------------------------------------------------------------------------------------------------------*/

options {
    language = Java;
}

@header {
    import java.util.*;
}

@members {
    private static class Pair {
        final String key;
        final Object value;

        private Pair(String k, Object v) {
            key = k;
            value = v;
        }
    }

    public boolean simpleQuotes = true;

    private String parseString(String rawString) {
        String content = rawString.substring(1, rawString.length() - 1);

        return content
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\r", "\r")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
            .replace("\\\\", "\\");
    }
}

/*--------------------------------------------------------------------------------------------------------------------*/
/* RÈGLES DU PARSER                                                                                                   */
/*--------------------------------------------------------------------------------------------------------------------*/

file returns [ Object result ]
    : v=value { $result = $v.result; } EOF
    ;

value returns [ Object result ]
    : obj=pythonDict { $result = $obj.result; }
    | arr=pythonList { $result = $arr.result; }
    | t=term { $result = $t.result; }
    ;

pythonDict returns [ Map<String, Object> result ]
    @init {
        $result = new LinkedHashMap<String, Object>();
    }
    : '{' '}'
    | '{'
        p1=pair { $result.put($p1.result.key, $p1.result.value); }
        (
            ',' p2=pair { $result.put($p2.result.key, $p2.result.value); }
        )*
        ','?
      '}'
    ;

pythonList returns [ List<Object> result ]
    @init {
        $result = new ArrayList<Object>();
    }
    : '[' ']'
    | '['
        v1=value { $result.add($v1.result); }
        (
            ',' v2=value { $result.add($v2.result); }
        )*
        ','?
      ']'
    ;

pair returns [ Pair result ]
    : k=STRING ':' v=value {
        $result = new Pair(parseString($k.text), $v.result);
    }
    ;

term returns [ Object result ]
    : STRING {
        $result = parseString($STRING.text);
    }
    | NUMBER {
        String numText = $NUMBER.text;
        if (numText.contains(".") || numText.contains("e") || numText.contains("E")) {
            $result = Double.parseDouble(numText);
        } else {
            try {
                $result = Integer.parseInt(numText);
            } catch (NumberFormatException e) {
                $result = Long.parseLong(numText);
            }
        }
    }
    | 'True'  { $result = true; }
    | 'False' { $result = false; }
    | 'None'  { $result = null; }
    | 'true'  { $result = true; }
    | 'false' { $result = false; }
    | 'null'  { $result = null; }
    ;

/*--------------------------------------------------------------------------------------------------------------------*/
/* RÈGLES DU LEXER                                                                                                    */
/*--------------------------------------------------------------------------------------------------------------------*/

BOOLEAN
    : [tT][rR][uU][eE]
    | [fF][aA][lL][sS][eE]
    ;

STRING
    : '"' (ESC | ~["\\\r\n])* '"'
    | '\'' (ESC | ~['\\\r\n])* '\''
    ;

NUMBER
    : '-'? INT '.' INT+ EXP?
    | '-'? INT EXP
    | '-'? INT
    ;

WS
    : [ \t\n\r]+ -> skip
    ;

COMMENT
    : '#' ~[\r\n]* -> skip
    ;

/*--------------------------------------------------------------------------------------------------------------------*/
/* FRAGMENTS                                                                                                          */
/*--------------------------------------------------------------------------------------------------------------------*/

fragment ESC
    : '\\' (["\\/bfnrt] | 'u' HEX HEX HEX HEX)
    ;

fragment HEX
    : [0-9a-fA-F]
    ;

fragment INT
    : '0'
    | [1-9] [0-9]*
    ;

fragment EXP
    : [eE] [+\-]? INT
    ;

/*--------------------------------------------------------------------------------------------------------------------*/