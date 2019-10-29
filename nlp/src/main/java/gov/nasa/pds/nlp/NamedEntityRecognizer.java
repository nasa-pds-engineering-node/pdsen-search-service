package gov.nasa.pds.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NamedEntityRecognizer
{
    private MultiWordDictionary dic;
    
    
    public NamedEntityRecognizer(MultiWordDictionary dic)
    {
       this.dic = dic;
    }
    
    
    private static class MWResult
    {
        public int lastTokenIndex;
        public Token token;
    }

    
    private MWResult processMultiWord(List<String> tokens, int currentTokenIndex, MultiWordTermInfo mwti)
    {
        int j = currentTokenIndex + 1;
        for(; j < tokens.size(); j++)
        {
            MultiWordTermInfo prevMwti = mwti;
            String nextWord = tokens.get(j);
            mwti = mwti.getTermInfo(nextWord);
            if(mwti == null)
            {
                if(prevMwti.type != 0)
                {
                    MWResult res = new MWResult();                    
                    res.lastTokenIndex = j-1;
                    res.token = new Token(prevMwti.id, prevMwti.type);
                    return res;
                }
                else
                {
                    // Not found
                    return null;
                }
            }
        }
        
        // Last word in tokens list
        if(mwti.type != 0)
        {
            MWResult res = new MWResult();
            res.lastTokenIndex = j;
            res.token = new Token(mwti.id, mwti.type);
            return res;
        }
        
        return null;
    }
    
    
    public List<Token> parse(String sentence)
    {
        List<Token> results = new ArrayList<>();
        
        sentence = sentence.toLowerCase();
        String[] tmp = sentence.split(" ");
        List<String> tokens = Arrays.asList(tmp);
        
        for(int i = 0; i < tokens.size(); i++)
        {
            String word = tokens.get(i);
            MultiWordTermInfo mwti = dic.findMultiWordTerm(word);
            
            // Process multi-word term
            if(mwti != null)
            {
                MWResult res = processMultiWord(tokens, i, mwti);
                if(res != null)
                {
                    results.add(res.token);
                    i = res.lastTokenIndex;
                    continue;
                }
            }
            
            SingleWordTermInfo swti = dic.findSingleWordTerm(word);
            // Process single word term
            if(swti != null)
            {
                results.add(new Token(swti.id, swti.type));
            }
            else
            {
                results.add(new Token(word, Token.TYPE_UNKNOWN));
            }            
        }
        
        return results;
    }
}
