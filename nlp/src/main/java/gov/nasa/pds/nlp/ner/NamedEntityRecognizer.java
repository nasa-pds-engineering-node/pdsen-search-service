package gov.nasa.pds.nlp.ner;

import java.util.ArrayList;
import java.util.List;
import gov.nasa.pds.nlp.ner.dic.NerDictionary;


public class NamedEntityRecognizer
{
    private NerDictionary dic;
    
    
    public NamedEntityRecognizer(NerDictionary dic)
    {
       this.dic = dic;
    }
    
    
    private static class MWResult
    {
        public int lastTokenIndex;
        public NerToken token;

        public MWResult(int idx, NerToken token)
        {
            this.lastTokenIndex = idx;
            this.token = token;
        }
    }

    
    private MWResult processMultiWord(List<String> tokens, int currentTokenIndex, NerToken token)
    {
        NerToken lastKnownToken = token;
        int lastKnownTokenIndex = currentTokenIndex;
        String currentKey = token.getKey();
        
        int j = currentTokenIndex + 1;
        for(; j < tokens.size(); j++)
        {
            String nextWord = tokens.get(j);
            currentKey = currentKey +  " " + nextWord;
            token = dic.get(currentKey);
            
            if(token == null)
            {
                break;
            }
            else
            {
                if(token.getType() != 0)
                {
                    lastKnownToken = token;
                    lastKnownTokenIndex = j;
                }
                
                if(!token.hasNext()) break;
            }
        }
        
        return new MWResult(lastKnownTokenIndex, lastKnownToken);
    }
    
    
    public List<NerToken> parse(List<String> tokens)
    {
        List<NerToken> results = new ArrayList<>();
        
        for(int i = 0; i < tokens.size(); i++)
        {
            String word = tokens.get(i);
            
            // LID
            if(word.startsWith("urn:nasa:pds:"))
            {
                results.add(new NerToken(word, NerToken.TYPE_LID));
                continue;
            }

            // Dictionary lookup
            NerToken token = dic.get(word);            
            if(token == null)
            {
                results.add(new NerToken(word, NerToken.TYPE_UNKNOWN));
            }
            else
            {
                // Multiple word
                if(token.hasNext())
                {
                    MWResult res = processMultiWord(tokens, i, token);
                    if(res != null)
                    {
                        results.add(res.token);
                        i = res.lastTokenIndex;
                        continue;
                    }
                }
                // Single word
                else
                {
                    results.add(token);
                }
            }
        }
        
        return results;
    }
}
