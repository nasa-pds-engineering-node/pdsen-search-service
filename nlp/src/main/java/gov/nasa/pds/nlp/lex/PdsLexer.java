package gov.nasa.pds.nlp.lex;

import java.util.Arrays;
import java.util.List;


public class PdsLexer
{
    public PdsLexer()
    {        
    }
    
    public List<String> parse(String sentence)
    {
        sentence = sentence.toLowerCase();
        String[] tmp = sentence.split("\\s+");
        List<String> tokens = Arrays.asList(tmp);
     
        return tokens;
    }
}
