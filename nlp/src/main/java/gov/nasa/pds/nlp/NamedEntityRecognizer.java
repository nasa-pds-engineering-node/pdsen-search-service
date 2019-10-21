package gov.nasa.pds.nlp;

import java.util.Arrays;
import java.util.List;

public class NamedEntityRecognizer
{
    private MultiWordDictionary dic;
    
    
    public NamedEntityRecognizer(MultiWordDictionary dic)
    {
       this.dic = dic;
    }
    
    
    private int processMultiWord(List<String> tokens, int currentTokenIndex, MultiWordTermInfo mwti)
    {
        int j = currentTokenIndex + 1;
        for(; j < tokens.size(); j++)
        {
            MultiWordTermInfo prevMwti = mwti;
            String nextWord = tokens.get(j);
            mwti = mwti.getTermInfo(nextWord);
            if(mwti == null)
            {
                if(prevMwti.type != null)
                {
                    System.out.println(prevMwti.id + "  -->  NNP: " + prevMwti.type);
                    return j-1;
                }
                else
                {
                    // Not found
                    return -1;
                }
            }
        }
        
        // Last word in tokens list
        if(mwti.type != null)
        {
            System.out.println(mwti.id + "  -->  NNP: " + mwti.type);
            return j;
        }
        
        return -1;
    }
    
    
    public void parse(String sentence)
    {
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
                int lastTokenIndex = processMultiWord(tokens, i, mwti);
                if(lastTokenIndex > 0)
                {
                    i = lastTokenIndex;
                    continue;
                }
            }
            
            SingleWordTermInfo swti = dic.findSingleWordTerm(word);
            // Process single word term
            if(swti != null)
            {
                System.out.println(swti.id + "  -->  NNP: " + swti.type);
            }
            else
            {
                System.out.println(word + "  -->  " + "Unknown");
            }            
        }
    }
}
