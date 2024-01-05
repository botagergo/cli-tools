grammar Query;

@header {
	package task_manager.logic.filter.grammar;
}

query : expr ;

expr : OPEN_PAREN expr CLOSE_PAREN
     | expr OR expr
     | expr AND expr
     | equalsExpr
     | likeExpr ;

equalsExpr : PROPERTY_NAME EQ constant ;

likeExpr : PROPERTY_NAME LIKE string ;

constant : string
     | bool ;

string : SINGLE_STRING
     | DOUBLE_STRING ;

bool : TRUE
     | FALSE ;

fragment SINGLE_QUOT : '\'' ;

fragment DOUBLE_QUOT : '"' ;

TRUE : 't' 'r' 'u' 'e' ;

FALSE : 'f' 'a' 'l' 's' 'e' ;

PROPERTY_NAME : [a-z_]+ ;

SINGLE_STRING : SINGLE_QUOT ~[']* SINGLE_QUOT;

DOUBLE_STRING : DOUBLE_QUOT ~["]* DOUBLE_QUOT;

OPEN_PAREN : '(' ;

CLOSE_PAREN : ')' ;

OR : '|' ;

AND : '&' ;

EQ : '=' ;

LIKE : '~' ;

WS : [ \t\r\n]+ -> skip ;