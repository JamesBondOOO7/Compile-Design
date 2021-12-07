%{
  #include<stdio.h>
  #include<stdlib.h>
  int cnt=0;
%}
%option yylineno

%%
#.*  {printf("\n");}
[',;(){}/_] {printf("delim ");}
[[]] {printf("delim ");}
"^"|"%"|"^"|"&" { printf("spch ");}
"["|"]" {printf("delim ");}
"="  {printf("as-op ");}
"+"|"-"|"/"|"*" { printf("ar-op ");}
"and"|"or"|"not"|"nand"|"xor"|"nor"|"xnor" {printf("log-op "); cnt++; }
"<="|">="|"++"|"!="|"=="|"<"|">" { printf("rel-op ");}
("int")|("if")|("else")|("while")|("do")|("break")|("continue")|("double")|("float")|("return")|("EOF") { printf("kw ");cnt++;}
("return")|("char")|("case")|("sizeof")|("long")|("short")|("typedef")|("switch")|("unsigned")|("signed") |("void")|("static")|("struct")|("goto")|("main") {printf("kw "); cnt++; }
[a-zA-Z_][a-zA-Z0-9_]* {printf("id ");}
[0-9]*"."[0-9]+ { printf("const ");}
[-+]?[0-9]+ {printf("const ");}
["].*["] {printf("string ");}
"//".*/[\n] {printf("\n");}
"/"(.\n)."/" {printf("\n"); }
[ ] {printf(" ");}
[\t] {printf("\t");}
[\n] {printf("\n");}
. {;}        
%%

int yywrap(void)
{
return 1;
}
int main(void)
{
yylex();
printf("\nTotal Number of Keywords are %d\n",cnt);
}