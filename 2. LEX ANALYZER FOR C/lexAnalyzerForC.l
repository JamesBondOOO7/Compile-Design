identifier [a-zA-Z][a-zA-Z0-9]*

%%
#.* {printf("\n%s is a preprocessor directive",yytext);}
int |
float |
char |
double |
while |
for |
struct |
typedef |
do |
if |
break |
continue |
void |
switch |
return |
else |
goto {printf("\n\t%s is a keyword",yytext);}


{identifier}\( {printf("\nFUNCTION \n\t%s",yytext);}
\{  {printf("\n BLOCK BEGINS");}
\}  {printf("BLOCK ENDS ");}

{identifier}(\[[0-9]*\])? {printf("\n\t %s IDENTIFIER",yytext);}

\".*\" {printf("\n\t %s is a STRING",yytext);}
[0-9]+ {printf("\n\t %s is a NUMBER ",yytext);}

= {printf("\n\t %s is an ASSIGNMENT OPERATOR",yytext);}
\<= |
\>= |
\< |
== |
\> {printf("\n\t %s is a RELATIONAL OPERATOR",yytext);}

%%

int main(int argc, char **argv)
{
    yylex();
    printf("\n");
    return(0);
}

int yywrap()
{
    return(1);
}