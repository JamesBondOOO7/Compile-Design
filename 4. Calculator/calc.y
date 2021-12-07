%{
void yyerror (char *s);  /* needed when syntactical error */
int yylex();
#include <stdio.h>     /* C declarations used in actions */
#include <stdlib.h>
#include <ctype.h>
int symbols[52];		/* Symbol table, only 52 symbols, [a-zA-Z] */
int symbolVal(char symbol);	/* go to symbol table and look for the value in the symbol */
void updateSymbolVal(char symbol, int val);	/* update symbol table */
%}
/* Yacc definitions */
%union {int num; char id;}	/* union - in order to return tokens of diff. types */
%start line			/* start - Start variable/non-terminal */
%token print			/* token - other tokens from lex */
%token exit_command
%token <num> number
%token <id> identifier
%type <num> line exp term 	/* type - to represent type */
%type <id> assignment

%%

/* descriptions of expected inputs     corresponding actions (in C) */

/* Variable : A {action 1} | B {action 2} | C {action 3} */
line    : assignment ';'		{;}
		| exit_command ';'		{exit(EXIT_SUCCESS);}
		| print exp ';'			{printf("Printing %d\n", $2);}
		| line assignment ';'	{;}
		| line print exp ';'	{printf("Printing %d\n", $3);}
		| line exit_command ';'	{exit(EXIT_SUCCESS);}
        ;

assignment : identifier '=' exp  { updateSymbolVal($1,$3); }
			;
exp    	: term                  {$$ = $1;}
       	| exp '+' term          {$$ = $1 + $3;}
       	| exp '-' term          {$$ = $1 - $3;}
       	| exp '*' term          {$$ = $1 * $3;}
       	| exp '/' term          {$$ = $1 / $3;}
       	;
term   	: number                {$$ = $1;}
		| identifier			{$$ = symbolVal($1);} 
        ;

%%                     /* C code */

int computeSymbolIndex(char token)
{
	int idx = -1;
	if(islower(token)) {
		idx = token - 'a' + 26;
	} else if(isupper(token)) {
		idx = token - 'A';
	}
	return idx;
} 

/* returns the value of a given symbol */
int symbolVal(char symbol)
{
	int bucket = computeSymbolIndex(symbol);
	return symbols[bucket];
}

/* updates the value of a given symbol */
void updateSymbolVal(char symbol, int val)
{
	int bucket = computeSymbolIndex(symbol);
	symbols[bucket] = val;
}

int main (void) {
	/* init symbol table */
	int i;
	for(i=0; i<52; i++) {
		symbols[i] = 0;
	}

	return yyparse ( );
}

void yyerror (char *s) {fprintf (stderr, "%s\n", s);} 

