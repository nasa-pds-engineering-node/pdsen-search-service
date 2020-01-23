package gov.nasa.pds.nlp.lex;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class PdsLexer
{
    public PdsLexer()
    {        
    }
    
    public List<String> parse(String sentence) throws IOException
    {
        if(sentence == null) return null;
        
        sentence = AsciiUtils.toAscii(sentence);
        sentence = sentence.toLowerCase();

        PdsLexerImpl impl = new PdsLexerImpl(new StringReader(sentence));
        
        List<String> tokens = new ArrayList<>();
        
        int tokenType;
        while((tokenType = impl.getNextToken()) != PdsLexerImpl.YYEOF)
        {
            tokens.add(impl.yytext());
        }
        
        impl.yyclose();

        return tokens;
    }
}
