package gov.nasa.pds.nlp;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;


public class MultiWordTermInfo
{
    public String id;
    public String type;
    private Map<String, MultiWordTermInfo> terms;
    
    
    public MultiWordTermInfo()
    {
    }

    
    public boolean hasChildren()
    {
        return terms != null;
    }
    
    
    public MultiWordTermInfo getTermInfo(String token)
    {
        if(terms == null) return null;
        
        return terms.get(token.toLowerCase());
    }
    
    
    private MultiWordTermInfo getOrCreateTermInfo(String token)
    {
        if(terms == null) terms = new TreeMap<>();
        
        MultiWordTermInfo term = terms.get(token);
        
        if(term == null)
        {
            term = new MultiWordTermInfo();
            terms.put(token, term);
        }
        
        return term;
    }
    
    
    public void add(String token, String type, String id)
    {
        MultiWordTermInfo term = getOrCreateTermInfo(token);
        
        if(term.id == null)
        {
            term.id = id;
            term.type = type;
        }
        else if(!term.id.equals(id))
        {
            System.out.println("WARNING: Multiple ids: " + term.id + ", " + id);
        }
    }

    
    public void add(String[] tokens, String type, String id)
    {
        if(tokens.length == 1)
        {
            System.out.println("WARNING: Invalid token array length of 1.");
            add(tokens[0], type, id);
            return;
        }
        
        MultiWordTermInfo term = getOrCreateTermInfo(tokens[0]);

        if(tokens.length == 2)
        {
            term.add(tokens[1], type, id);
        }
        else
        {
            String[] childTokens = Arrays.copyOfRange(tokens, 1, tokens.length);
            term.add(childTokens, type, id);
        }
    }
}
