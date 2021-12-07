/*lex code to determine whether input is an identifier or not*/
%{
#include <stdio.h>

%}

%%
^[a-zA-Z_][a-zA-Z0-9_]* {printf("Valid Identifier");}

^[^a-zA-Z_] {printf("Invalid Identifier");}
.;
%%

void main()
{
	yylex();
}
