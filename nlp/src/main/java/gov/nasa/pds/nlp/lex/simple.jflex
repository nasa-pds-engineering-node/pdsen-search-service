package gov.nasa.pds.nlp.lex;

%%

%class PdsLexerImpl
%unicode 3.0
%integer
%function getNextToken
%pack
%char
%buffer 4096

%{

static final int DIGITS = 1;
static final int ALPHA = 2;
static final int ALPHANUM = 3;

%}

// Number: 24 or 0.5 or 123,234.302
//NUMBER     = {DIGITS} (("."|",") {DIGITS})*

// a sequence of letters and digits
//WORDNUM    = {ALPHA} ({ALPHANUM})*

// Internal apostrophes: O'Reilly, you're, O'Reilly's
//APOSTROPHE =  {ALPHA} ("'" {ALPHA})+

// Internal dashes (X-Ray / Chandrayaan-1)
//DASH =  {ALPHA} ("-" {ALPHANUM})+

// Internal dashes (453-HDBK-GN / 2010-09-16)
//DASHNUM =  {DIGITS} ("-" {ALPHANUM})+

// acronyms: U.S.A., I.B.M., etc.
//ACRONYM    =  {LETTER} "." ({LETTER} ".")+

DIGITS    = ([0-9])+
ALPHA     = ([A-Za-z])+
ALPHANUM  = ([A-Za-z0-9])+

%%

{DIGITS}                 { return DIGITS; }
{ALPHA}                  { return ALPHA; }
{ALPHANUM}               { return ALPHANUM; }

/** Ignore the rest */
[^]                                                            { /* Break so we don't hit fall-through warning: */ break;/* ignore */ }
