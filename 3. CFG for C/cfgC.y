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
