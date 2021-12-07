%{
#include "myscanner.h"
%}

%%
:				 printf("%d\n",COLON);
"for"		 printf("%d\n",FOR);
"while"		 printf("%d\n",WHILE);
"define" printf("%d\n",DEFINE);
"if"		 printf("%d\n",IF);

[a-zA-Z][_a-zA-Z0-9]*	 printf("%d\n",IDENTIFIER);
[1-9][0-9]*			 printf("%d\n",INTEGER);
[ \t\n]					;
.					printf("unexpected character\n");

%%

int yywrap(void)
{
	return 1;
}
