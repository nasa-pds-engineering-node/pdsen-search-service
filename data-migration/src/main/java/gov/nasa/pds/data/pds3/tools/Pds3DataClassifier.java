package gov.nasa.pds.data.pds3.tools;

import java.io.File;
import java.util.List;

import gov.nasa.pds.nlp.lex.PdsLexer;
import gov.nasa.pds.nlp.ner.NamedEntityRecognizer;
import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.nlp.ner.dic.NerDictionary;
import gov.nasa.pds.nlp.ner.dic.NerDictionaryHashMap;

public class Pds3DataClassifier
{
    private NamedEntityRecognizer ner;
    private PdsLexer lexer;
    
    public Pds3DataClassifier(String dir) throws Exception
    {
        NerDictionary nerDic = new NerDictionaryHashMap();
        nerDic.load(new File(dir));
        ner = new NamedEntityRecognizer(nerDic);
        
        lexer = new PdsLexer();
    }

    
    public String classifyInstrumentType(String text)
    {
        if(text == null) return null;
     
        try
        {
            List<String> lexTokens = lexer.parse(text);
            List<NerToken> nerTokens = ner.parse(lexTokens);
    
            for(NerToken token: nerTokens)
            {
                if(token.getType() != 0)
                {
                    return token.getId();
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
                
        return null;
    }
}
