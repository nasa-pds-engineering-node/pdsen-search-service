package tt;

import java.util.List;

import gov.nasa.pds.nlp.MultiWordDictionary;
import gov.nasa.pds.nlp.NamedEntityRecognizer;
import gov.nasa.pds.nlp.Token;


public class TestNER
{

    public static void main(String[] args) throws Exception
    {
        MultiWordDictionary dic = new MultiWordDictionary();        
        dic.load("src/test/data/ner-dic.txt");
        
        NamedEntityRecognizer ner = new NamedEntityRecognizer(dic);
        
        String sent1 = "MRO CRISM TRDRs over Gale crater on Mars"; 
        String sent2 = "Mars Reconnaissance Orbiter CRISM TRDRs over Gale crater on Mars";
        String sent3 = "Mars Science Laboratory";
        String sent4 = "Mars Science";
        
        List<Token> tokens = ner.parse(sent1);
        for(Token token: tokens)
        {
            System.out.println(token.text + " --> " + token.type);
        }
    }
    
}
