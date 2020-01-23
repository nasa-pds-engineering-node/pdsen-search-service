package tt;

import java.util.List;

import gov.nasa.pds.nlp.lex.PdsLexer;

public class TestLexer
{

    public static void main(String[] args) throws Exception
    {
        PdsLexer lex = new PdsLexer();
        List<String> tokens = lex.parse("67P/Churyumov-Gerasimenko, (1234) Bennu");
        
        for(String token: tokens)
        {
            System.out.println(token);
        }
    }

}
