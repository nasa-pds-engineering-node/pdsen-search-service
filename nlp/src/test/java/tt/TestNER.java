package tt;

import java.io.File;
import java.util.List;

import gov.nasa.pds.nlp.lex.PdsLexer;
import gov.nasa.pds.nlp.ner.NamedEntityRecognizer;
import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.dic.NerDictionary;
import gov.nasa.pds.nlp.ner.dic.NerDictionaryHashMap;


public class TestNER
{

    public static void main(String[] args) throws Exception
    {
        NerDictionary dic = new NerDictionaryHashMap();        
        dic.load(new File("src/test/data/ner/"));
        
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        
        String sent1 = "MRO CRISM TRDRs over Gale crater on Mars"; 
        String sent2 = "Mars Reconnaissance Orbiter CRISM TRDRs over Gale crater on Mars";
        String sent3 = "Mars Science Laboratory";
        String sent4 = "Mars Science";
        String sent5 = "2001 Mars Odyssey instruments";
        String sent6 = "Mars Reconnaissance Test";
                
        PdsLexer lexer = new PdsLexer();
        List<String> lexTokens = lexer.parse(sent1);
        
        List<NerToken> nerTokens = ner.parse(lexTokens);
        for(NerToken token: nerTokens)
        {
            System.out.println(token.getKey() + " --> " + token.getType() + " (" + token.getId() + ")");
        }
    }
    
}
