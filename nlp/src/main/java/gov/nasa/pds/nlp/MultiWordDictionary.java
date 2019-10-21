package gov.nasa.pds.nlp;

import java.util.Map;
import java.util.TreeMap;

public class MultiWordDictionary
{
    private Map<String, SingleWordTermInfo> singleWords;
    private MultiWordTermInfo multiWords;
    
    
    public MultiWordDictionary()
    {
        singleWords = new TreeMap<>();
        multiWords = new MultiWordTermInfo();
    }
    
    
    public void add(String name, String type, String id)
    {
        name = name.toLowerCase();
    
        // Single word
        if(name.indexOf(' ') < 0)
        {
            singleWords.put(name, new SingleWordTermInfo(id, type));
        }
        // Multiple words
        else
        {
            String[] tokens = name.split(" ");
            multiWords.add(tokens, type, id);
        }
    }
    
    
    public MultiWordTermInfo findMultiWordTerm(String firstWord)
    {
        return multiWords.getTermInfo(firstWord);
    }
    
    
    public SingleWordTermInfo findSingleWordTerm(String word)
    {
        return singleWords.get(word);
    }
    
}
