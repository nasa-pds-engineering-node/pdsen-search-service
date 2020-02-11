package gov.nasa.pds.data.pds3.tools;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    
    public Set<String> extractKeywords(String text)
    {
        if(text == null) return null;
        
        Set<String> keywords = new TreeSet<>();
        
        try
        {
            List<String> lexTokens = lexer.parse(text);
            List<NerToken> nerTokens = ner.parse(lexTokens);
    
            for(NerToken token: nerTokens)
            {
                if(token.getType() != 0)
                {
                    String keyword = token.getId();
                    keywords.add(keyword);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return keywords.isEmpty() ? null : keywords;
    }
}
