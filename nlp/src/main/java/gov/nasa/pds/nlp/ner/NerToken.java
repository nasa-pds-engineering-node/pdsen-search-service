package gov.nasa.pds.nlp.ner;

public class NerToken
{
    private String key;
    private String id;
    
    private int type;
    private int[] types;
    
    private boolean hasNext = false;
    
    
    public NerToken(String key)
    {
        this.key = key;
    }

    
    public NerToken(String key, int type)
    {
        this.key = key;
        this.type = type;
    }

    
    public int getType()
    {
        return type;
    }
    
    
    public int[] getAllTypes()
    {
        return types;
    }
    
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    
    public void setTypes(int[] types)
    {
        if(types == null || types.length == 0) 
        {
            throw new IllegalArgumentException("Types array is null or empty");
        }
        
        if(types.length == 1)
        {
            this.type = types[0];
        }
        else
        {
            this.type = NerTokenType.MULTIPLE;
            this.types = types;
        }
    }

    
    public String getKey()
    {
        return key;
    }
    
    
    public String getId()
    {
        return id;
    }
    
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    
    public boolean hasNext()
    {
        return hasNext;
    }
    
    
    public void setHasNext(boolean b)
    {
        this.hasNext = b;
    }
}
