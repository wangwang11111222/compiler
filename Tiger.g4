grammar Tiger;


// PARSER RULES



tiger_program
    : PROGRAM ID LET declaration_segment BEGIN funct_list END EOF
    ;


declaration_segment
    : type_declaration_list var_declaration_list
    ;


type_declaration_list
    : type_declaration type_declaration_list
    |
    ;


var_declaration_list
    : var_declaration var_declaration_list
    |
    ;


funct_list
    : funct funct_list
    |
    ;


type_declaration
    : TYPE ID TASSIGN type SEMI
    ;


type
    : base_type | ARRAY LBRACK INTLIT RBRACK OF base_type
    | ID 
    ;


base_type
    : INT
    | FLOAT
    ;


var_declaration
    : storage_class id_list COLON type optional_init SEMI
    ;


storage_class
    : VAR
    | STATIC
    ;


id_list
    : ID
    | ID COMMA id_list
    ;


optional_init
    : ASSIGN constant
    | 
    ;


funct
    : FUNCTION ID LPAREN param_list RPAREN ret_type BEGIN stat_seq END
    ;


param_list
    : param param_list_tail
    |
    ;


param_list_tail
    : COMMA param param_list_tail
    | 
    ;


ret_type
    : COLON type
    | 
    ;


param
    : ID COLON type
    ;



stat_seq
    : stat | stat stat_seq 
    ;

stat
    : value ASSIGN expr SEMI
    | IF expr THEN stat_seq ENDIF SEMI
    | IF expr THEN stat_seq ELSE stat_seq ENDIF SEMI
    | WHILE expr DO stat_seq ENDDO SEMI
    | FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMI
    | optprefix ID LPAREN expr_list RPAREN SEMI
    | BREAK SEMI
    | RETURN optreturn SEMI
    | LET declaration_segment BEGIN stat_seq END
    ;

optreturn
    : expr
    | 
    ;

optprefix
    : value ASSIGN
    | 
    ;

expr
    : LPAREN expr RPAREN                  
    | constant                                  
    | value                                
    | <assoc=right> expr POW expr                           
    | expr (MUL | DIV) expr                   
    | expr (ADD | SUB) expr                   
    | expr (EQ | NE | LT | GT | LE | GE) expr  
    | expr AND expr                            
    | expr OR expr                            
    ;

constant
    : INTLIT
    | FLOATLIT
    ;



expr_list
    : expr expr_list_tail
    | 
    ;

expr_list_tail
    : COMMA expr expr_list_tail
    |
    ;

value
    : ID value_tail
    ;

value_tail
    : LBRACK expr RBRACK
    | 
    ;


// LEXER

ARRAY : 'array';
BEGIN : 'begin';
BREAK : 'break';
DO : 'do';
ELSE : 'else';
END : 'end';
ENDDO : 'enddo';
ENDIF : 'endif';
FLOAT : 'float';
FOR : 'for';
FUNCTION : 'function';
IF : 'if';
INT : 'int';
LET : 'let';
OF : 'of';
PROGRAM : 'program';
RETURN : 'return';
STATIC : 'static';
THEN : 'then';
TO : 'to';
TYPE : 'type';
VAR : 'var';
WHILE : 'while';
COMMA : ',';
COLON : ':';
SEMI : ';';
LPAREN : '(';
RPAREN : ')';
LBRACK : '[';
RBRACK : ']';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
POW : '**';
EQ : '==';
NE : '!=';
LT : '<';
GT : '>';
LE : '<=';
GE : '>=';
AND : '&';
OR : '|';
ASSIGN : ':=';
TASSIGN : '=';
ID : [a-zA-Z] [a-zA-Z0-9_]*;
INTLIT : '0' | [1-9] [0-9]*;
FLOATLIT : ('0' | [1-9] [0-9]*) '.' [0-9]*;
COMMENT : '/*' .*? '*/' -> skip;
WS : [ \t\r\n]+ -> skip;